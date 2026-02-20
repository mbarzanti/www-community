package it.posteitaliane.df_pdw.modalitaconsegna

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import it.posteitaliane.df_maps.refactor.utils.PointType
import it.posteitaliane.df_pdw.PdwViewModel
import it.posteitaliane.df_pdw.R
import it.posteitaliane.df_pdw.databinding.FragmentPdwModalitaConsegnaBinding
import it.posteitaliane.df_pdw.datasource.PdwConstants
import it.posteitaliane.df_pdw.datasource.PdwConstants.DELIVERY_TYPE_HSD
import it.posteitaliane.df_pdw.datasource.PdwConstants.DELIVERY_TYPE_OCP
import it.posteitaliane.df_pdw.datasource.PdwConstants.DELIVERY_TYPE_PBD
import it.posteitaliane.df_pdw.datasource.PdwConstants.DELIVERY_TYPE_RTZ
import it.posteitaliane.df_utils.extensions.visible
import it.posteitaliane.df_utils.uimodel.NormalizedAddressUiModel
import it.posteitaliane.df_utils.uimodel.address.PosteAddressUiModel
import it.posteitaliane.mvvmtoolkit.livedatautils.Consumable.Companion.asConsumable
import it.posteitaliane.mvvmtoolkit.livedatautils.Consumable.Companion.consumeIfAvailable
import it.posteitaliane.mvvmtoolkit.view.BaseFragment
import it.posteitaliane.posteuikit.PosteUiKitConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import it.posteitaliane.common.R as RUtils
import it.posteitaliane.posteuikit.R as RKit

@FlowPreview
@ExperimentalCoroutinesApi
class ModalitaConsegnaFragment :
    BaseFragment<FragmentPdwModalitaConsegnaBinding>(R.layout.fragment_pdw_modalita_consegna) {

    val pdwViewModel by activityViewModel<PdwViewModel>()
    override val viewModel by viewModel<ModalitaConsegnaViewModel>()
    private var puntoPosteUnavailable = false

    override fun handleArguments(arguments: Bundle) { // Nothing to do here
    }

    override fun handleSavedInstanceState(savedInstanceState: Bundle) { // Nothing to do here
    }

    private val startForResultModificaActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                result.data?.getParcelableExtra<PosteAddressUiModel>(PdwConstants.POSTE_ADDRESS_DESTINATARIO_KEY)
                    ?.let { address ->
                        address.description += address.city
                        pdwViewModel.updateDestinationPosteAddress(address)
                        pdwViewModel.updateIndirizzoDestinatarioFromPosteAddress(address)
                        pdwViewModel.destinatarioPosteAddressUiModel.postValue(address)
                    }
            }
        }

    override fun onViewReady() {
        setView()
        setViewListeners()
        setObservers()
        if (pdwViewModel.isInstantEnabled == false && !pdwViewModel.isInternational()) {
            pdwViewModel.getLimitPP()
        }

        // aggiungere logica per visualizzare textview in futuro e sostituire con valore proveniente da response servizio, al momento non c'è servizio BE
        /* binding.ufficioOPuntoPostePromozioneTv.visibility=View.VISIBLE
        binding.ufficioOPuntoPostePromozioneTv.text= HtmlCompat.fromHtml(getString(R.string.hai_diritto_allo_sconto,"12","%"), HtmlCompat.FROM_HTML_MODE_LEGACY)*/
    }

    private fun setView() {
        lifecycleScope.launchWhenStarted {
            binding.casellaPostaleTl.editText?.textChanges()?.collect { text ->
                pdwViewModel.apply {
                    updateCasellaPostaleNumber(text.toString())
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            binding.infoAggiuntiveDestinatarioTl.editText?.textChanges()?.collect {
                pdwViewModel.getValidateShippingData()
                    .updateInfoAggiuntiveDestinatario(it.toString())
            }
        }
        pdwViewModel.getValidateShippingData().let {
            binding.alSuoIndirizzoTv.text =
                pdwViewModel.getPosteUiModelForPosteAddressFragment(false).toString()
        }

        binding.infoAggiuntiveDestinatarioTl.editText?.setText(
            pdwViewModel.getValidateShippingData()
                .getDestinatarioInfoAggiuntivoAddress()
        )

        pdwViewModel.getValidateShippingData().let {
            binding.casellaPostaleTl.setText(it.getCasellaPostaleNumber())
        }
    }

    private fun setViewListeners() {
        when (pdwViewModel.getValidateShippingData().getDeliveryType()) {
//            DELIVERY_TYPE_RTZ -> updateUI(ufficioOPuntoPoste = true)
            DELIVERY_TYPE_OCP, DELIVERY_TYPE_RTZ, DELIVERY_TYPE_PBD -> updateUI(ufficioOPuntoPoste = true) // posso avre selezionato uno di questi 3 deliveryType a seconda delle casistiche
            DELIVERY_TYPE_HSD -> updateUI(domicilio = true)
        }

        // domicilio
        binding.alSuoDomicilioContainer.setOnClickListener {
            updateUI(domicilio = true, reset = true)
            pdwViewModel.updateDeliveryType(DELIVERY_TYPE_HSD, true)
            pdwViewModel.choosedConsegnaADomicilio.postValue(true.asConsumable()) // per fare update ui e validator nel destinatarioFragment
            resetCampoObbligatorio()
        }

        // ufficio postale o punto poste
        binding.ufficioOPuntoPosteContainer.setOnClickListener {
            updateUI(ufficioOPuntoPoste = true, reset = true)
            pdwViewModel.updateDeliveryType(DELIVERY_TYPE_OCP, true) // RTZ
            pdwViewModel.choosedConsegnaADomicilio.postValue(false.asConsumable()) // per fare update ui e validator nel destinatarioFragment
            resetCampoObbligatorio()
        }
        binding.ufficioOPuntoPosteLayout.headerCardContainer.setOnClickListener {
            openMap(puntoPosteUnavailable)
        }
        binding.ufficioOPuntoPosteMapTv.setOnClickListener {
            openMap(puntoPosteUnavailable)
        }

        binding.ufficioOPuntoPosteLayout.modifyBtn.setOnClickListener {
            openMap(puntoPosteUnavailable)
        }

        binding.alSuoIndirizzoModificaIv.setOnClickListener {
            if (pdwViewModel.isInternational()) {
                pdwViewModel.navigationToPosteAddressFragment(
                    PdwConstants.POSTE_ADDRESS_DESTINATARIO_KEY,
                    getString(RUtils.string.modifica_indirizzo_destinatario),
                    posteAddressUiModel = pdwViewModel.getPosteUiModelForPosteAddressFragment(false),
                    pdwViewModel.isInternational()
                )
            } else {
                pdwViewModel.navigateToPosteModificaIndirizzo(
                    requireContext(),
                    startForResultModificaActivity,
                    pdwViewModel.createBundleForModify(
                        PdwConstants.POSTE_ADDRESS_DESTINATARIO_KEY,
                        getString(R.string.modifica_indirizzo_destinatario)
                    )
                )
            }
        }
    }

    private fun setObservers() {
        pdwViewModel.currentLocation.consumeIfAvailable(this) {
            if (it.postalCode != null && it.locality != null && viewModel.isMapsClicked.value == true) {
                viewModel.isMapsClicked.postValue(false)
            }
        }

        pdwViewModel.checkLimitsPPLiveData.observe(this) {
            if (!it) {
                showConsegnaPuntoPosteNonDisponibile()
            } else {
                puntoPosteUnavailable = false
                binding.ufficioOPuntoPosteConsegnaNonDisponibileTv.visibility = View.GONE
            }
        }

        pdwViewModel.pdwEstimatedShippingCostLiveData.observe(
            viewLifecycleOwner
        ) { response ->
            hideLoader()
            response?.let {
                // upCollection, homeCollection
                it.data.costVasDetail.apply {
                    binding.ufficioOPuntoPosteTv.text =
                        getString(R.string.in_ufficio_postale_o_punto_poste)
                    binding.alSuoDomicilioTv.text = pdwViewModel.getOffertaCost(
                        requireContext(),
                        R.string.al_suo_indirizzo,
                        homeDelivery
                    )
                }
            }
        }

        pdwViewModel.puntoSelectedFromMapLiveData.observe(viewLifecycleOwner) {
            binding.ufficioOPuntoPosteLayout.apply {
                if (pdwViewModel.getValidateShippingData().isPuntoSelezionato()) {
                    pdwViewModel.setCorrectDeliveryType(
                        binding.casellaPostaleTl.getText().toString()
                    )
                    when (pdwViewModel.getValidateShippingData().getSelectedDeliveryType()) {
                        PointType.UFFICIO_POSTALE.id -> {
                            binding.apply {
                                casellaPostaleTl.visible(true)
                                casellaPostaleTl.setText(
                                    pdwViewModel.getValidateShippingData().getCasellaPostaleNumber()
                                )
                                puntoPostePinViaSmsTv.visible(false)
                            }
                        }

                        PointType.PUNTO_POSTE.id -> {
                            binding.apply {
                                casellaPostaleTl.visible(false)
                                puntoPostePinViaSmsTv.visible(true)
                            }
                        }
                    }

                    selectedPointCardContainer.visibility = View.VISIBLE
                    binding.ufficioOPuntoPosteErrorTv.visibility = View.GONE
                    description.text = context?.let { pdwViewModel.getTipoPuntoLabel(it) }
                    tipoCasellaPostale.text =
                        pdwViewModel.getValidateShippingData().getSelectedPointDescription()
                    indirizzoCasellaPostale.text =
                        pdwViewModel.getValidateShippingData().getPointAddress().plus(" - ").plus(
                            pdwViewModel.getValidateShippingData().getSelectedPointCityInfo()?.city
                        )
                    binding.ufficioOPuntoPosteMapTv.visibility = View.GONE
                }
            }
        }

        if (!pdwViewModel.isInstantEnabled)
            requireActivity().supportFragmentManager.setFragmentResultListener(
                PosteUiKitConstants.POSTE_ADDRESS_NORMALIZE_KEY,
                viewLifecycleOwner
            ) { _, bundle ->
                bundle.getParcelable<NormalizedAddressUiModel>(PdwConstants.DESTINATION_ADDRESS)
                    ?.let {
                        pdwViewModel.updateIndirizzoDestinatarioNormalized(it)
                        pdwViewModel.getValidateShippingData().let { validateShippingDataRequest ->
                            binding.alSuoIndirizzoTv.text =
                                validateShippingDataRequest.getIndirizzoDestinatario().plus(", ")
                                    .plus(validateShippingDataRequest.getDestinationCity())
                        }
                        // if everything is ok push next step, same as DestinatarioFragment
                        pdwViewModel.pushDestinatarioNext(requireContext())
                    }
            }

        pdwViewModel.limitsPPErrorLiveData.consumeIfAvailable(this) {
            showConsegnaPuntoPosteNonDisponibile()
        }
    }

    private fun showConsegnaPuntoPosteNonDisponibile() {
        binding.ufficioOPuntoPosteConsegnaNonDisponibileTv.visibility = View.VISIBLE
        binding.titleTv.text = HtmlCompat.fromHtml(
            getString(R.string.consegna_punto_poste_non_disponibile),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        puntoPosteUnavailable = true
    }

    private fun resetAllCards() {
        pdwViewModel.apply {
            updatePuntoAddress("")
            updateCasellaPostaleNumber("")
            getValidateShippingData().updateInfoAggiuntiveMittente("")
        }
        binding.apply {
            ufficioOPuntoPosteLayout.selectedPointCardContainer.visibility = View.GONE
            casellaPostaleTl.visibility = View.GONE
            ufficioOPuntoPosteMapTv.visibility = View.VISIBLE
        }
    }

    private fun resetCampoObbligatorio() {
        binding.ufficioOPuntoPosteErrorTv.visibility = View.GONE
    }

    // lo showDialog dipende dal risultato della validazione del DestinatarioFragment:
    // se è KO ma la validazione in questo fragment è OK allora mostra dialog del DestinatarioFragment
    // (evito così due dialog sovrapposte)
    fun validateStep(needToShowDialog: Boolean): Boolean {
        pdwViewModel.setCorrectDeliveryType(binding.casellaPostaleTl.getText().toString())
        val deliveryType = pdwViewModel.getValidateShippingData().getDeliveryType()
        if (deliveryType.isNotEmpty()) {
            when (deliveryType) {
                DELIVERY_TYPE_OCP, DELIVERY_TYPE_PBD, DELIVERY_TYPE_RTZ -> {
                    return if (activity?.let {
                            viewModel.campoObbligatorioValidation(
                                it,
                                pdwViewModel.getValidateShippingData().isPuntoSelezionato(),
                                showDialog = needToShowDialog
                            )
                        } == true
                    ) {
                        true
                    } else {
                        binding.ufficioOPuntoPosteErrorTv.visibility = View.VISIBLE
                        false
                    }
                }
            }
        }
        return true
    }

    private fun updateUI(
        domicilio: Boolean = false,
        ufficioOPuntoPoste: Boolean = false,
        reset: Boolean = false,
    ) {
        binding.alSuoDomicilioRadio.isChecked = domicilio
        binding.ufficioOPuntoPosteRadio.isChecked = ufficioOPuntoPoste

        if (domicilio) { // nel caso nazionale,se domicilio è checkato, rendi visibile la matita per modificare
            binding.apply {
                alSuoIndirizzoModificaIv.visibility = View.VISIBLE
                alSuoIndirizzoCardview.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        RKit.color.surface_background
                    )
                )
                alSuoDomicilioExpandableLayout.visibility = View.VISIBLE
                casellaPostaleTl.visibility = View.GONE
            }
        } else {
            binding.apply {
                alSuoIndirizzoModificaIv.visibility = View.GONE
                alSuoIndirizzoCardview.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        RKit.color.background_1
                    )
                )
                alSuoDomicilioExpandableLayout.visibility = View.GONE
                puntoPostePinViaSmsTv.visible(false)
            }
        }

        binding.ufficioOPuntoPosteExpandableLayout.visibility =
            if (ufficioOPuntoPoste) View.VISIBLE else View.GONE

        if (reset) {
            resetAllCards()
        }
    }

    private fun openMap(puntoPosteIsUnavailable: Boolean) {
        pdwViewModel.goToConsegnaMapFragment(puntoPosteIsUnavailable)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    @CheckResult
    fun EditText.textChanges(): Flow<CharSequence?> = callbackFlow<CharSequence?> {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrors()

                trySendBlocking(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }.buffer(Channel.CONFLATED)
        .debounce(500L)

    fun updateIndirizzoUi(value: String) {
        binding.alSuoIndirizzoTv.text = value
    }
}
