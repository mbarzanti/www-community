package it.posteitaliane.df_utils.camera

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import it.posteitaliane.common.R
import it.posteitaliane.df_utils.UtilsConstants
import it.posteitaliane.df_utils.UtilsConstants.CAMERA_FILE_NAME
import it.posteitaliane.df_utils.UtilsConstants.CAMERA_RESULT_KEY
import it.posteitaliane.df_utils.UtilsConstants.PATH_BITMAP_ARRAY_FROM_CAMERA
import it.posteitaliane.df_utils.UtilsConstants.RETURNING_FROM_SCATTO_FRONTE
import it.posteitaliane.df_utils.UtilsConstants.URI_BITMAP_ARRAY_FROM_CAMERA
import it.posteitaliane.df_utils.UtilsConstants.USER_REQUESTED_TO_CLOSE
import it.posteitaliane.common.databinding.FragmentCameraBinding
import it.posteitaliane.df_utils.extensions.saveInCacheDir
import it.posteitaliane.df_utils.extensions.saveInMediaStore
import it.posteitaliane.df_utils.image.ImageUtils
import it.posteitaliane.mvvmtoolkit.navigation.NavigationFragment
import it.posteitaliane.mvvmtoolkit.view.BaseNavigationViewModel
import it.posteitaliane.posteuikit.databinding.PostePannelToolbarViewBinding
import it.posteitaliane.posteuikit.toolbars.PostePannelToolbarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min


class CameraFragment :
    NavigationFragment<FragmentCameraBinding>(R.layout.fragment_camera),
    PostePannelToolbarHelper.PostePannelToolbarListeners {

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = requireContext().applicationContext
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** [CameraCharacteristics] corresponding to the provided Camera ID */
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraManager.cameraIdList[0])
    }

    /** Readers used as buffers for camera still shots */
    private lateinit var imageReader: ImageReader

    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    /** Performs recording animation of flashing screen */
    private val animationTask: Runnable by lazy {
        Runnable {
            // Flash white animation
            binding.overlay.background = Color.argb(150, 255, 255, 255).toDrawable()
            // Wait for ANIMATION_FAST_MILLIS
            binding.overlay.postDelayed(
                {
                    // Remove white flash animation
                    binding.overlay.background = null
                },
                50L
            )
        }
    }

    /** [HandlerThread] where all buffer reading operations run */
    private val imageReaderThread = HandlerThread("imageReaderThread").apply { start() }

    /** [Handler] corresponding to [imageReaderThread] */
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    /** The [CameraDevice] that will be opened in this fragment */
    private lateinit var camera: CameraDevice

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private lateinit var session: CameraCaptureSession

    private var toolbarTitle: String = ""
    private var isScattoFronte: Boolean = true
    private var imageName: String? = ""
    private var fromWallet: Boolean = false
    private var fromAutolettura: Boolean = false

    override var toolbarHelper: PostePannelToolbarHelper? = null
    private var imagesPath: String? = ""
    private var cameraPreviewDescription: String? = ""

    override val viewModel: BaseNavigationViewModel
        get() = CameraViewModel()

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryUri = it
        val imageBitmapFromGallery= getBitmapFromUri(requireContext().contentResolver,galleryUri )
        val imageBitmapFromGalleryResized = ImageUtils.resizeBitmap(imageBitmapFromGallery!!, 720, 1280)
        val compressedUri =
            imageBitmapFromGalleryResized.let { it1 -> getImageUri(requireContext(), it1) }
        runCatching {
            requireActivity().supportFragmentManager.setFragmentResult(
                CAMERA_RESULT_KEY,
                bundleOf(
                    URI_BITMAP_ARRAY_FROM_CAMERA to compressedUri,
                    RETURNING_FROM_SCATTO_FRONTE to isScattoFronte,
                    CAMERA_FILE_NAME to imageName,
                    PATH_BITMAP_ARRAY_FROM_CAMERA to compressedUri?.path.toString()
                )
            )
        }
    }

    override fun handleArguments(arguments: Bundle) {
        if (!arguments.isEmpty) {
            toolbarTitle = arguments.getString(UtilsConstants.TOOLBAR_TITLE, "")
            isScattoFronte = arguments.getBoolean(UtilsConstants.NEED_TO_CAPTURE_FRONTE)
            imageName = arguments.getString(UtilsConstants.CAMERA_FILE_NAME)
            imagesPath = arguments.getString(UtilsConstants.PATH_FOR_SAVE_MRTD_IMAGES)
            fromWallet = arguments.getBoolean(UtilsConstants.FROM_WALLET, false)
            fromAutolettura = arguments.getBoolean(UtilsConstants.FROM_AUTOLETTURA, false)
            cameraPreviewDescription =
                arguments.getString(UtilsConstants.CAMERA_PREVIEW_DESCRIPTION)
        }
    }

    override fun onViewReady() {
        initToolbar()
        setView()
    }

    override fun initToolbar() {
        val toolbarView: PostePannelToolbarViewBinding =
            binding.toolbar as PostePannelToolbarViewBinding
        toolbarHelper =
            PostePannelToolbarHelper(toolbarView, this, requireActivity() as AppCompatActivity)
        toolbarHelper?.apply {
            showClose()
            showBack()
            disableStepper()
            setPrimaryTitle(toolbarTitle)
        }
        binding.toolbar.posteAppbarLayout.isVisible  = !fromWallet
        binding.toolbar.posteAppbarLayout.isVisible  = !fromAutolettura
    }

    override fun onPostePannelBackClick() {
        navigateBack()
    }

    override fun onPostePannelCloseClick() {
        requireActivity().supportFragmentManager.setFragmentResult(
            USER_REQUESTED_TO_CLOSE,
            bundleOf()
        )
    }

    override fun onPostePannelHelpClick() {
        // ntd
    }

    private fun setView() {
        binding.tvCameraDescription.text = cameraPreviewDescription
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val height = resources.getDimensionPixelSize(R.dimen.camera_height)
        val size = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
            .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        binding.materialCardView.layoutParams = (
            FrameLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT
            )
                .apply { gravity = Gravity.TOP }
            )
        binding.viewFinder.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int,
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {
                // Selects appropriate preview size and configures view finder
                val previewSize = getPreviewOutputSize(
                    binding.viewFinder.display,
                    characteristics,
                    SurfaceHolder::class.java
                )
//                binding.viewFinder.setAspectRatio(
//
//                    previewSize.height,previewSize.width
//                )

                // To ensure that size is set, initialize camera in the view's thread
                view.post { kotlin.runCatching { initializeCamera() } }
            }
        })
    }

    /**
     * Begin all camera operations in a coroutine in the main thread. This function:
     * - Opens the camera
     * - Configures the camera session
     * - Starts the preview by dispatching a repeating capture request
     * - Sets up the still image capture listeners
     */
    private fun initializeCamera() = lifecycleScope.launch(Dispatchers.Main) {

        // Open the selected camera
        camera = openCamera(cameraManager, cameraManager.cameraIdList[0], cameraHandler)

        // Initialize an image reader which will be used to capture still photos
        val size = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!
            .getOutputSizes(ImageFormat.JPEG).maxByOrNull { it.height * it.width }!!
        imageReader = ImageReader.newInstance(
            size.width, size.height, ImageFormat.JPEG, IMAGE_BUFFER_SIZE
        ) // qua va settata la risoluzione per la cattura dell'immagine

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(binding.viewFinder.holder.surface, imageReader.surface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCaptureSession(camera, targets, cameraHandler)

        val captureRequest = kotlin.runCatching {
            camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply { addTarget(binding.viewFinder.holder.surface) }
        }.getOrNull()

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        if (captureRequest != null) session.setRepeatingRequest(captureRequest.build(), null, cameraHandler)

        loadLastImage(binding.loadGallery)
        binding.loadGallery.setOnClickListener{
            galleryLauncher.launch("image/*")
        }
        // Listen to the capture button
        binding.captureButton.setOnClickListener {

            // Disable click listener to prevent multiple requests simultaneously in flight
            it.isEnabled = false

            // Perform I/O heavy operations in a different scope
            lifecycleScope.launch(Dispatchers.IO) {
                takePhoto().use { result ->
                    // Save the result
                    val output = getImageAsByteArray(result)
                    val stream = ByteArrayOutputStream()
                    val bitmap = BitmapFactory.decodeByteArray(output, 0, output.size)
                    val m = Matrix()
                    m.postRotate(90F)
                    val rotated= Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true);
                    val resized = ImageUtils.resizeBitmap(rotated, 720, 1280)
                    resized.compress(Bitmap.CompressFormat.JPEG, 70, stream)

                    val byteArrayCompressed = stream.toByteArray()

                    val file = createFile(imagesPath, imageName, "jpg", byteArrayCompressed)

                    val filePath =  file?.path

                    val uri = if (fromWallet||fromAutolettura) saveImageToFile(imagesPath, UUID.randomUUID().toString(),"jpg",   byteArrayCompressed) else  file?.let {
                        FileProvider.getUriForFile(
                            requireContext(),
                            requireContext().packageName + FILE_PROVIDER_PATH,
                            it
                        )
                    }

                    requireActivity().supportFragmentManager.setFragmentResult(
                        CAMERA_RESULT_KEY,
                        bundleOf(
                            URI_BITMAP_ARRAY_FROM_CAMERA to uri,
                            RETURNING_FROM_SCATTO_FRONTE to isScattoFronte,
                            CAMERA_FILE_NAME to imageName,
                            PATH_BITMAP_ARRAY_FROM_CAMERA to filePath
                        )
                    )

/*                    Log.d(TAG, "Image saved: ${output.absolutePath}")

                    // If the result is a JPEG file, update EXIF metadata with orientation info
                    if (output.extension == "jpg") {
                        val exif = ExifInterface(output.absolutePath)
                        exif.setAttribute(
                            ExifInterface.TAG_ORIENTATION, result.orientation.toString())
                        exif.saveAttributes()
                        Log.d(TAG, "EXIF metadata saved: ${output.absolutePath}")
                    }*/
                }
                // Re-enable click listener after photo is taken
                it.post { it.isEnabled = true }
            }
        }
    }

    /** Opens the camera and returns the opened device (as the result of the suspend coroutine) */
    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null,
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(
            cameraId,
            object : CameraDevice.StateCallback() {
                override fun onOpened(device: CameraDevice) = cont.resume(device)

                override fun onDisconnected(device: CameraDevice) {
                    Log.w(TAG, "Camera $cameraId has been disconnected")
                    requireActivity().finish()
                }

                override fun onError(device: CameraDevice, error: Int) {
                    val msg = when (error) {
                        ERROR_CAMERA_DEVICE -> "Fatal (device)"
                        ERROR_CAMERA_DISABLED -> "Device policy"
                        ERROR_CAMERA_IN_USE -> "Camera in use"
                        ERROR_CAMERA_SERVICE -> "Fatal (service)"
                        ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                        else -> "Unknown"
                    }
                    val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                    Log.e(TAG, exc.message, exc)
                    if (cont.isActive) cont.resumeWithException(exc)
                }
            },
            handler
        )
    }

    /**
     * Starts a [CameraCaptureSession] and returns the configured session (as the result of the
     * suspend coroutine
     */
    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null,
    ): CameraCaptureSession = suspendCoroutine { cont ->

        // Create a capture session using the predefined targets; this also involves defining the
        // session state callback to be notified of when the session is ready
        device.createCaptureSession(
            targets,
            object : CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    val exc = RuntimeException("Camera ${device.id} session configuration failed")
                    Log.e(TAG, exc.message, exc)
                    cont.resumeWithException(exc)
                }
            },
            handler
        )
    }

    /**
     * Helper function used to capture a still image using the [CameraDevice.TEMPLATE_STILL_CAPTURE]
     * template. It performs synchronization between the [CaptureResult] and the [Image] resulting
     * from the single capture, and outputs a [CombinedCaptureResult] object.
     */
    private suspend fun takePhoto():
        CombinedCaptureResult = suspendCoroutine { cont ->

        // Flush any images left in the image reader
        @Suppress("ControlFlowWithEmptyBody")
        while (imageReader.acquireNextImage() != null) {
        }

        // Start a new image queue
        val imageQueue = ArrayBlockingQueue<Image>(IMAGE_BUFFER_SIZE)
        imageReader.setOnImageAvailableListener(
            { reader ->
                val image = reader.acquireNextImage()
                imageQueue.add(image)
            },
            imageReaderHandler
        )

        val captureRequest = session.device.createCaptureRequest(
            CameraDevice.TEMPLATE_STILL_CAPTURE
        ).apply { addTarget(imageReader.surface) }
        session.capture(
            captureRequest.build(),
            object : CameraCaptureSession.CaptureCallback() {

                override fun onCaptureStarted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    timestamp: Long,
                    frameNumber: Long,
                ) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber)
                    binding.viewFinder.post(animationTask)
                }

                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult,
                ) {
                    super.onCaptureCompleted(session, request, result)
                    val resultTimestamp = result.get(CaptureResult.SENSOR_TIMESTAMP)

                    // Set a timeout in case image captured is dropped from the pipeline
                    val exc = TimeoutException("Image dequeuing took too long")
                    val timeoutRunnable = Runnable { cont.resumeWithException(exc) }
                    imageReaderHandler.postDelayed(timeoutRunnable, IMAGE_CAPTURE_TIMEOUT_MILLIS)

                    // Loop in the coroutine's context until an image with matching timestamp comes
                    // We need to launch the coroutine context again because the callback is done in
                    //  the handler provided to the `capture` method, not in our coroutine context
                    @Suppress("BlockingMethodInNonBlockingContext")
                    lifecycleScope.launch(cont.context) {
                        while (true) {

                            // Dequeue images while timestamps don't match
                            val image = imageQueue.take()
                            // if (image.timestamp != resultTimestamp) continue
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                                image.format != ImageFormat.DEPTH_JPEG &&
                                image.timestamp != resultTimestamp
                            ) continue

                            // Unset the image reader listener
                            imageReaderHandler.removeCallbacks(timeoutRunnable)
                            imageReader.setOnImageAvailableListener(null, null)

                            // Clear the queue of images, if there are left
                            while (imageQueue.size > 0) {
                                imageQueue.take().close()
                            }

                            // Compute EXIF orientation metadata
//                        val rotation = relativeOrientation.value ?: 0
                            val rotation = 0
                            val mirrored = characteristics.get(CameraCharacteristics.LENS_FACING) ==
                                CameraCharacteristics.LENS_FACING_FRONT

                            val exifOrientation = computeExifOrientation(rotation, mirrored)

                            // Build the result and resume progress
                            cont.resume(
                                CombinedCaptureResult(
                                    image, result, exifOrientation, imageReader.imageFormat
                                )
                            )

                            // There is no need to break out of the loop, this coroutine will suspend
                        }
                    }
                }
            },
            cameraHandler
        )
    }

    /** Helper function used to save a [CombinedCaptureResult] into a [File] */
/*    private suspend fun saveResult(result: CombinedCaptureResult): File = suspendCoroutine { cont ->
        when (result.format) {

            // When the format is JPEG or DEPTH JPEG we can simply save the bytes as-is
            ImageFormat.JPEG, ImageFormat.DEPTH_JPEG -> {
                val buffer = result.image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                try {
                    val output = createFile(path, "jpg")
                    FileOutputStream(output).use { it.write(bytes) }
                    cont.resume(output)
                } catch (exc: IOException) {
                    Log.e(TAG, "Unable to write JPEG image to file", exc)
                    cont.resumeWithException(exc)
                }
            }

            // No other formats are supported by this sample
            else -> {
                val exc = RuntimeException("Unknown image format: ${result.image.format}")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }
    }*/

    private suspend fun getImageAsByteArray(result: CombinedCaptureResult): ByteArray =
        suspendCoroutine { cont ->
            when (result.format) {
                // we can simply save the bytes as-is
                ImageFormat.JPEG -> {
                    val buffer = result.image.planes[0].buffer
                    try {
                        val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
                        cont.resume(bytes)
                    } catch (exc: Exception) {
                        cont.resumeWithException(exc)
                    }
                }

                // No other formats are supported by this sample
                else -> {
                    val exc = RuntimeException("Unknown image format: ${result.image.format}")
                    cont.resumeWithException(exc)
                }
            }
        }

    fun loadLastImage(appCompatImageView: AppCompatImageView) = runCatching{
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
        )
        val cursor = requireContext().contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
            )
        if (cursor!!.moveToFirst()) {
            val imageLocation = cursor.getString(1)
            val imageFile = File(imageLocation)
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                val bm = BitmapFactory.decodeFile(imageLocation)
                appCompatImageView.setImageBitmap(bm)
            }
        }
    }
    override fun onStop() {
        super.onStop()
        try {
            camera.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }

    private fun computeExifOrientation(rotationDegrees: Int, mirrored: Boolean) = when {
        rotationDegrees == 0 && !mirrored -> ExifInterface.ORIENTATION_NORMAL
        rotationDegrees == 0 && mirrored -> ExifInterface.ORIENTATION_FLIP_HORIZONTAL
        rotationDegrees == 180 && !mirrored -> ExifInterface.ORIENTATION_ROTATE_180
        rotationDegrees == 180 && mirrored -> ExifInterface.ORIENTATION_FLIP_VERTICAL
        rotationDegrees == 270 && mirrored -> ExifInterface.ORIENTATION_TRANSVERSE
        rotationDegrees == 90 && !mirrored -> ExifInterface.ORIENTATION_ROTATE_90
        rotationDegrees == 90 && mirrored -> ExifInterface.ORIENTATION_TRANSPOSE
        rotationDegrees == 270 && mirrored -> ExifInterface.ORIENTATION_ROTATE_270
        rotationDegrees == 270 && !mirrored -> ExifInterface.ORIENTATION_TRANSVERSE
        else -> ExifInterface.ORIENTATION_UNDEFINED
    }

    private fun <T> getPreviewOutputSize(
        display: Display,
        characteristics: CameraCharacteristics,
        targetClass: Class<T>,
        format: Int? = null,
    ): Size {
        val SIZE_1080P: SmartSize = SmartSize(1920, 1080)
        // Find which is smaller: screen or 1080p
        val screenSize = getDisplaySmartSize(display)
        val hdScreen = screenSize.long >= SIZE_1080P.long || screenSize.short >= SIZE_1080P.short
        val maxSize = if (hdScreen) SIZE_1080P else screenSize

        // If image format is provided, use it to determine supported sizes; else use target class
        val config = characteristics.get(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )!!

        val allSizes = if (format == null)
            config.getOutputSizes(targetClass) else config.getOutputSizes(format)

        // Get available sizes and sort them by area from largest to smallest
        val validSizes = allSizes
            .sortedWith(compareBy { it.height * it.width })
            .map { SmartSize(it.width, it.height) }.reversed()

        // Then, get the largest output size that is smaller or equal than our max size
        return validSizes.first { it.long <= maxSize.long && it.short <= maxSize.short }.size
    }

    private fun getDisplaySmartSize(display: Display): SmartSize {
        val outPoint = Point()
        display.getRealSize(outPoint)
        return SmartSize(outPoint.x, outPoint.y)
    }

    inner class SmartSize(width: Int, height: Int) {
        var size = Size(width, height)
        var long = max(size.width, size.height)
        var short = min(size.width, size.height)
        override fun toString() = "SmartSize(${long}x$short)"
    }

    private fun getBitmapFromUri(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? = inImage.saveInCacheDir(inContext, compression = 70)


    companion object {
        val TAG = CameraFragment::class.java.simpleName
        const val FILE_PROVIDER_PATH = ".provider"

        /** Maximum number of images that will be held in the reader's buffer */
        private const val IMAGE_BUFFER_SIZE: Int = 3

        /** Maximum time allowed to wait for the result of an image capture */
        private const val IMAGE_CAPTURE_TIMEOUT_MILLIS: Long = 5000

        fun newInstance(arguments: Bundle): CameraFragment {
            val cameraFragment = CameraFragment()
            cameraFragment.setArguments(arguments)
            return cameraFragment
        }

        /** Helper data class used to hold capture metadata with their associated image */
        data class CombinedCaptureResult(
            val image: Image,
            val metadata: CaptureResult,
            val orientation: Int,
            val format: Int,
        ) : Closeable {
            override fun close() = image.close()
        }
        fun saveImageToFile( path: String?,
                             fileName: String?,
                             extension: String,
                             output: ByteArray): Uri? {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(directory, "$fileName.$extension")
            if (!directory.isDirectory) {
                directory.mkdir()
            }
            return if (directory.isDirectory) {
                try {
                    FileOutputStream(imageFile).use { it.write(output) }
                    imageFile.toUri()
                } catch (e: IOException) {
                    Log.d(
                        TAG,
                        " WTF ${e.message}"
                    )
                    null
                }
            } else {
                null
            }
        }

        private fun createFile(
            path: String?,
            fileName: String?,
            extension: String,
            output: ByteArray,
        ): File? {
            try {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val localFileName = fileName ?: "IMG"
                val sdf = SimpleDateFormat("yyyyMMdd", Locale.ITALY)
                if (path != null) {
                    val filePath = File(path)
                    if (!filePath.exists()) {
                        filePath.mkdirs()
                    }
                }

                val file = File(path, "$localFileName${sdf.format(Date())}.$extension")

                FileOutputStream(file).use { it.write(output) }
                return file
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }
    }
}
