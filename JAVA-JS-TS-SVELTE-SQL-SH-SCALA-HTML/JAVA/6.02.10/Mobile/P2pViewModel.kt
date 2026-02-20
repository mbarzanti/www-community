package it.posteitaliane.df_p2p

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.posteitaliane.df_p2p.P2PCostant.ERROR_NOT_ENOUGH_BALANCE
import it.posteitaliane.df_p2p.P2PCostant.ERROR_SALDO_NEGATIVO
import it.posteitaliane.df_p2p.P2PCostant.ERROR_WALLET_NON_TROVATO
import it.posteitaliane.df_p2p.P2PCostant.P2P_REQUEST_NOT_FOUND_ERROR
import it.posteitaliane.df_p2p.P2PCostant.TY_PAGE_P2P_OP_TYPE
import it.posteitaliane.df_p2p.P2pNavigation.openSaldoNonDisponibileDialog
import it.posteitaliane.df_p2p.datasource.P2pRepository
import it.posteitaliane.df_p2p.model.ChallengeRequest
import it.posteitaliane.df_p2p.model.ChallengeResponse
import it.posteitaliane.df_p2p.model.MoneyTransferRequest
import it.posteitaliane.df_p2p.model.MoneyTransferResponse
import it.posteitaliane.df_p2p.model.PhoneNumbersCheckRequest
import it.posteitaliane.df_p2p.model.RequestP2PMoneyTransferRequest
import it.posteitaliane.df_p2p.uimodel.ChallangeUiModel
import it.posteitaliane.df_p2p.uimodel.ContattiUiModel
import it.posteitaliane.df_p2p.uimodel.RiepilogoUiModel
import it.posteitaliane.df_sessionmanager.ISessionManagerRepository
import it.posteitaliane.df_sessionmanager.analytics.AnalyticsRegion
import it.posteitaliane.df_sessionmanager.application_state.ApplicationStateManager
import it.posteitaliane.df_sessionmanager.application_state.getData
import it.posteitaliane.df_sessionmanager.datasource.notifiche.NotificationsRepository
import it.posteitaliane.df_sessionmanager.datasource.notifiche.model.request.RecuperaAttivitaBodyRequest
import it.posteitaliane.df_sessionmanager.datasource.notifiche.model.request.RecuperaAttivitaRequest
import it.posteitaliane.df_sessionmanager.datasource.notifiche.model.response.RecuperaAttivitaResponse
import it.posteitaliane.df_sessionmanager.datasource.sondaggio.SondaggioUseCase
import it.posteitaliane.df_sessionmanager.datasource.sondaggio.SondaggioUserPage
import it.posteitaliane.df_sessionmanager.sharedpreferences.UserSharedPreferencesContract
import it.posteitaliane.df_sessionmanager.sharedpreferences.UserSharedPreferencesHelper
import it.posteitaliane.df_sessionmanager.statistics.StatisticsContract
import it.posteitaliane.df_sessionmanager.statistics.StatisticsHelper
import it.posteitaliane.df_utils.UtilsConstants
import it.posteitaliane.df_utils.UtilsConstants.COMMAND_RESULT_OK
import it.posteitaliane.df_utils.UtilsConstants.ERROR_NA
import it.posteitaliane.df_utils.chatbot.ChatBotContexts
import it.posteitaliane.df_utils.datasource.addressbook.AddressBookRepositoryContract
import it.posteitaliane.df_utils.datasource.database.OneDatabaseRepository
import it.posteitaliane.df_utils.datasource.database.statistics.StatisticsQuickActions
import it.posteitaliane.df_utils.extensions.getAmountFromHundreds
import it.posteitaliane.df_utils.extensions.getAmountInHundreds
import it.posteitaliane.df_utils.extensions.handleEmptyValue
import it.posteitaliane.df_utils.extensions.isNull
import it.posteitaliane.df_utils.extensions.navigateToHome
import it.posteitaliane.df_utils.extensions.orZero
import it.posteitaliane.df_utils.logger.trackWithPayload
import it.posteitaliane.df_utils.model.addressbook.AddressBookModel
import it.posteitaliane.df_utils.model.base.BaseResponse
import it.posteitaliane.df_utils.model.getParsedError
import it.posteitaliane.df_utils.network.PosteIdErrorCode
import it.posteitaliane.df_utils.payments.model.PaymentMethodUiModel
import it.posteitaliane.df_utils.payments.model.getActiveCard
import it.posteitaliane.df_utils.posteid.PosteIdError
import it.posteitaliane.df_utils.posteid.PosteIdErrorHandlerWithChatbotLegacy
import it.posteitaliane.df_utils.rsa.RsaHelper
import it.posteitaliane.mvvmtoolkit.CloseActivityEnum
import it.posteitaliane.mvvmtoolkit.extensions.postConsumableUnit
import it.posteitaliane.mvvmtoolkit.livedatautils.Consumable
import it.posteitaliane.mvvmtoolkit.livedatautils.Consumable.Companion.asConsumable
import it.posteitaliane.mvvmtoolkit.network.ApiError
import it.posteitaliane.mvvmtoolkit.network.HttpError
import it.posteitaliane.mvvmtoolkit.network.NetworkError
import it.posteitaliane.mvvmtoolkit.view.BaseNavigationViewModel
import it.posteitaliane.mvvmtoolkit.view.PopupUiModel
import it.posteitaliane.poste_remote_logger.annotation.RemoteLoggerApplication
import it.posteitaliane.poste_remote_logger.annotation.RemoteLoggerClassInfo
import it.posteitaliane.poste_remote_logger.annotation.RemoteLoggerInfo
import it.posteitaliane.posteuikit.model.ui.ButtonModel
import it.posteitaliane.posteuikit.model.ui.ButtonType
import it.posteitaliane.posteuikit.model.ui.DialogModel
import it.posteitaliane.posteuikit.model.ui.DismissType
import it.posteitaliane.posteuikit.model.ui.ImageModel
import it.posteitaliane.posteuikit.model.ui.SpanModel
import it.posteitaliane.posteuikit.model.ui.TextModel
import it.posteitaliane.posteuikit.model.ui.TopBarIcons
import it.posteitaliane.posteuikit.model.ui.internal_model.AdditionalThankYouPageModel
import it.posteitaliane.posteuikit.model.ui.tornaAllaHome
import it.posteitaliane.posteuikit.model.ui.tornaInHome
import it.posteitaliane.posteuikit.uimodel.FaqData
import it.posteitaliane.remoteLogger.RemoteLoggerManager
import it.posteitaliane.remoteLogger.error.RemoteLoggableError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import it.posteitaliane.common.R as Utils
import it.posteitaliane.posteuikit.R as RKit


@RemoteLoggerApplication(
    it.posteitaliane.common.BuildConfig.APPLICATION_ID,
    it.posteitaliane.common.BuildConfig.BUILD_TYPE,
    useDF = true,
    flavor = it.posteitaliane.common.BuildConfig.FLAVOR
)
@RemoteLoggerClassInfo(BuildConfig.ModulePathP2P)
class P2pViewModel(
    private val p2pRepository: P2pRepository,
    sharedPreferences: SharedPreferences,
    private val addressBookRepository: AddressBookRepositoryContract,
    oneDatabaseRepository: OneDatabaseRepository,
    private val sessionManagerRepository: ISessionManagerRepository,
    private val notificationsRepository: NotificationsRepository,
    private val sondaggioUseCase: SondaggioUseCase,
    val applicationStateManager: ApplicationStateManager,
    coroutineDispatcher: CoroutineDispatcher? = null,
) : BaseNavigationViewModel(coroutineDispatcher),
    UserSharedPreferencesContract by UserSharedPreferencesHelper(sharedPreferences),
    StatisticsContract by StatisticsHelper(
        oneDatabaseRepository,
        sessionManagerRepository,
        sharedPreferences
    ), PosteIdErrorHandlerWithChatbotLegacy {

    override val navigationCoordinator = P2pNavigation

    val listContattiListLiveData = MutableLiveData<Consumable<List<ContattiUiModel>>>()
    val challengeLiveData = MutableLiveData<Consumable<ChallengeResponse>>()
    val moneyTransferLiveData = MutableLiveData<Consumable<MoneyTransferResponse>>()

    private val _showInsufficientBalance = MutableLiveData<Consumable<Unit>>()
    val showInsufficientBalance: LiveData<Consumable<Unit>> = _showInsufficientBalance
    var metodoPagamentoSelected: PaymentMethodUiModel? = null
    var needToShowPencilOnRiepilogo: Boolean = false
    var riepilogoUiModel: RiepilogoUiModel = RiepilogoUiModel()
    var challangeUiModel: ChallangeUiModel? = null
    val analyticsTransactionId = UUID.randomUUID().toString()
    override val _chatBotHelpNeeded = MutableLiveData<Consumable<String>>()
    override val chatBotHelpNeeded: LiveData<Consumable<String>> = _chatBotHelpNeeded
    override var failuresCounter = 0

    var listContact: List<ContattiUiModel> = emptyList()
    var importo: String = ""
    var messaggio: String = ""
    var fromP2pPush: Boolean = false
    var selectedContact = ContattiUiModel()
    var isListFiltered = false
    var selectedP2PFlowType: P2PCostant.P2PFlowType = P2PCostant.P2PFlowType.INVIA
    var areNegaFragment = false
    val showVerificaPosteId = MutableLiveData<Consumable<String>>()
    private var lastAddressBook: ArrayList<AddressBookModel> = arrayListOf()
    var showPresaInCarico = false

    private val _analyticsData: MutableLiveData<Consumable<AnalyticsRegion>> = MutableLiveData()
    val analyticsData: LiveData<Consumable<AnalyticsRegion>> = _analyticsData

    fun isTutored() = applicationStateManager.userState.getData().isTutored()

    fun gotoListaContatti(type: P2PCostant.P2PFlowType = selectedP2PFlowType) {
        selectedP2PFlowType = type
        navigationLiveData.postValue(navigationCoordinator.gotoListaContatti(type))
    }

    fun gotoInvitaAmico(fullName: String) {
        navigationLiveData.postValue(navigationCoordinator.gotoInvitaAmico(fullName))
    }

    fun openLeavePopUp(positiveListener: DialogInterface.OnClickListener) {
        navigationLiveData.postValue(navigationCoordinator.openLeaveDialog(positiveListener))
    }

    fun openWsError() {
        navigationLiveData.postValue(navigationCoordinator.openWsError())
    }

    fun gotoPermessiNegati(type: String) {
        navigationLiveData.postValue(navigationCoordinator.gotoPermessiNegati(type))
    }

    fun gotoRicerca() {
        isListFiltered = false
        navigationLiveData.postValue(navigationCoordinator.gotoRicerca())
    }

    fun goToThankYouPage(dialogModel: DialogModel = getThankYouPageDialogModel()) =
        navigationLiveData.postValue(navigationCoordinator.navigateToComposePage(dialogModel))

    fun goToTransactionFailedErrorPage() {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToComposePage(
                data = DialogModel.ErrorDialogData(
                    buttonList = listOf(
                        ButtonModel(
                            label = tornaAllaHome,
                            modelOnClick = { navigateToHome(isTutored = isTutored()) },
                            buttonType = ButtonType.FULL
                        )
                    ),
                    title = TextModel.ResTextModel(R.string.label_error_title),
                    description = TextModel.ResTextModel(R.string.label_error_first_desc),
                    image = ImageModel.ResImage(RKit.drawable.ic_poste_error_page_profile),
                    additionalData = listOf(AdditionalThankYouPageModel.SurveyDataModel(
                        operationType = TY_PAGE_P2P_OP_TYPE,
                        retrieveCardData = {
                            sondaggioUseCase.invoke(getKoCurrentUserPage())
                        }
                    )),
                )
            ))
    }

    @VisibleForTesting
    fun goToGenericOperationFailedErrorPage() {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToComposePage(
                data = DialogModel.ErrorDialogData(
                    buttonList = listOf(
                        ButtonModel(
                            label = tornaAllaHome,
                            modelOnClick = { navigateToHome(isTutored = isTutored())  },
                            buttonType = ButtonType.FULL
                        )
                    ),
                    title = TextModel.ResTextModel(Utils.string.label_operazione_ko_title),
                    description = TextModel.ResTextModel(Utils.string.label_error_description_without_verify),
                    image = ImageModel.ResImage(RKit.drawable.ic_poste_error_page_profile),
                    additionalData = listOf(AdditionalThankYouPageModel.SurveyDataModel(
                        operationType = TY_PAGE_P2P_OP_TYPE,
                        retrieveCardData = {
                            sondaggioUseCase.invoke(getKoCurrentUserPage())
                        }
                    )),
                )
            ))
    }

    private fun getKoCurrentUserPage() =
        if (metodoPagamentoSelected?.isPaymentMethodAccount == true) {
            SondaggioUserPage.P2P_CONTO_KO
        } else {
            SondaggioUserPage.P2P_CARTA_KO
        }

    private fun getThankYouCurrentUserPage() =
        if (metodoPagamentoSelected?.isPaymentMethodAccount == true) {
            SondaggioUserPage.P2P_CONTO_OK
        } else {
            SondaggioUserPage.P2P_CARTA_OK
        }


    @VisibleForTesting
    fun goToOperationFailedErrorPageTwoCTA(
        riprova: (() -> Unit)? = null,
    ) {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToComposePage(
                data = DialogModel.ErrorDialogData(
                    buttonList = listOf(
                        ButtonModel(
                            label = TextModel.ResTextModel(Utils.string.label_riprova),
                            modelOnClick = { riprova?.invoke() },
                            buttonType = ButtonType.FULL
                        ),

                        ButtonModel(
                            label = tornaAllaHome,
                            modelOnClick = {
                                navigateToHome(isTutored = isTutored())
                            },
                            imageModelStart = ImageModel.ResImage(RKit.drawable.ic_rounded_arrow_back),
                            buttonType = ButtonType.ACTION
                        ),
                    ),

                    dismissType = if(riprova == null) DismissType.All() else DismissType.OnlyAction(),
                    title = TextModel.ResTextModel(Utils.string.label_operazione_ko_title),
                    description = TextModel.ResTextModel(Utils.string.label_error_description_operation_ok),
                    image = ImageModel.ResImage(RKit.drawable.ic_poste_error_page_profile),
                    additionalData = listOf(AdditionalThankYouPageModel.SurveyDataModel(
                        operationType = TY_PAGE_P2P_OP_TYPE,
                        retrieveCardData = {
                            sondaggioUseCase.invoke(getKoCurrentUserPage())
                        }
                    )),
                ),
            )
        )
    }

    fun gotoRichiestaInvioDenaroErrorPage() {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToComposePage(
                data = DialogModel.ErrorDialogData(
                    buttonList = listOf(
                        ButtonModel(
                            label = tornaAllaHome,
                            modelOnClick = { navigateToHome(isTutored = isTutored())  },
                            buttonType = ButtonType.FULL
                        )
                    ),
                    title = TextModel.ResTextModel(R.string.label_richiesta_invio_error_title),
                    description = TextModel.ResTextModel(R.string.label_richiesta_invio_error_description),
                    image = ImageModel.ResImage(R.drawable.ic_nega_richiesta),
                    additionalData = listOf(AdditionalThankYouPageModel.SurveyDataModel(
                        operationType = TY_PAGE_P2P_OP_TYPE,
                        retrieveCardData = {
                            sondaggioUseCase.invoke(getKoCurrentUserPage())
                        }
                    ))
                )
            ))
    }

    @RemoteLoggerInfo
    fun getP2pNumbers(onlyP2p: Boolean) {
        val addressBook = getAddressBook()
        inputOutputCoroutine {
            p2pRepository.getP2pNumbers(
                PhoneNumbersCheckRequest(
                    addressBook.map { it.number }
                )
            ).collect {
                it.handleResponse(
                    succeeded = { response ->
                        listContact = response.toUiModel(addressBook)
                        listContattiListLiveData.postValue(filterContactP2p(onlyP2p).asConsumable())
                        //awaitFrame()
                        postReadyStatus()
                    },
                    failed = { error ->
                        val errorDesc = error.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_getP2pNumbers_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_getP2pNumbers,
                            line = REMOTE_LOGGER_P2pViewModel_getP2pNumbers_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                        openWsError()
                    }
                )
            }
        }
    }

    fun getAddressBook(): ArrayList<AddressBookModel> {
        if (lastAddressBook.isEmpty()) lastAddressBook =
            addressBookRepository.getContactsFromAddressBook()
        return lastAddressBook
    }

    fun canProceedToChallenge(): Boolean? =
        if(metodoPagamentoSelected.isNull()){
            null
        }else if(selectedP2PFlowType == P2PCostant.P2PFlowType.RICEVI){
            true
        }else if (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA && metodoPagamentoSelected != null) {
            val availableBalance = metodoPagamentoSelected?.availableBalance ?: 0
            val totalAmount = with(riepilogoUiModel) {
                importo.getAmountInHundreds().orZero() + commissione.getAmountInHundreds().orZero()
            }
            availableBalance != 0L && availableBalance >= totalAmount
        } else false

    @RemoteLoggerInfo
    fun getP2pChallenge(
        checkOnlyIfAmountIsSufficient: Boolean = false,
        callback: (() -> Unit)? = null,
    ) {

        val isConto = metodoPagamentoSelected?.isPaymentMethodAccount == true
        inputOutputCoroutine {
            p2pRepository.getP2pChallenge(
                ChallengeRequest(
                    if (isConto) {
                        metodoPagamentoSelected?.cards?.getActiveCard()?.alias.orEmpty()
                    } else {
                        metodoPagamentoSelected?.alias.orEmpty()
                    },
                    messaggio,
                    selectedContact.phoneNumber,
                    importo.getAmountInHundreds().toString()
                )
            ).collect { it ->
                it.handleResponse(
                    succeeded = { challengeResponse ->
                        postReadyStatus()
                        if (!handleChatBotErrorCode(
                                challengeResponse.header?.commandResultReason,
                                customContext = ChatBotContexts.CHATBOT_CONTEXT_MESSAGE_P2P_SALDO,
                                callback = {
                                    navigationLiveData.postValue(openSaldoNonDisponibileDialog { dialog, _ ->
                                        dialog.dismiss()
                                    })
                                }
                            )
                            && !checkOnlyIfAmountIsSufficient
                        ) {
                            challangeUiModel = challengeResponse.toChallengeUiModel()
                            callback?.invoke() ?: run {
                                challengeLiveData.postValue(challengeResponse.asConsumable())
                            }
                        }
                    },
                    failed = {
                        val errorDesc = it.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_getP2pChallenge_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_getP2pChallenge,
                            line = REMOTE_LOGGER_P2pViewModel_getP2pChallenge_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                        handleChallengeHttpError(it)
                    },
                    loading = { postLoadingStatus() },
                    timeout = { postFailedStatus() }
                )
            }
        }
    }

    @VisibleForTesting
    fun handleChallengeHttpError(it: ApiError) {
        if (it is HttpError && it.code == UtilsConstants.HTTP_ERROR_CODE_409) {
            (it.body as? ChallengeResponse)?.info?.let {
                RsaHelper.handleOnboardingRSAControls(
                    it.resultCode,
                    it.resultMessage,
                    onSuccess = {},
                    onFailure = {
                        navigationLiveData.postValue(
                            navigationCoordinator.showPopupErrorWithMessage(
                                it
                            )
                        )
                    }
                )
            }
        }
        if (it is NetworkError) {
            openWsError()
        } else {
            when (((it as HttpError).body as? ChallengeResponse)?.info?.resultCode) {
                ERROR_SALDO_NEGATIVO -> {
                    goToOperationFailedErrorPageTwoCTA {
                        getP2pChallenge()
                    }
                }

                ERROR_WALLET_NON_TROVATO -> goToTransactionFailedErrorPage()
                ERROR_NOT_ENOUGH_BALANCE -> {
                            navigationLiveData.postValue(openSaldoNonDisponibileDialog { dialog, _ ->
                                dialog.dismiss()
                            })
                }

                else -> openWsError()
            }
        }
    }

    fun showSaldoNonDisponibilePopup(listeners: DialogInterface.OnClickListener) {
        navigationLiveData.postValue(
            navigationCoordinator.openSaldoNonDisponibileDialog(listeners)
        )
    }

    @VisibleForTesting
    fun handleMoneyTransferHttpError(
        error: ApiError,
        errorResult: String?,
    ) {
        if (error is HttpError && error.code == 409 && error.body is BaseResponse) {
            val body = error.body as BaseResponse
            when (body.info?.resultCode ?: "") {
                ERROR_WALLET_NON_TROVATO -> goToTransactionFailedErrorPage()
                ERROR_SALDO_NEGATIVO -> {
                    goToOperationFailedErrorPageTwoCTA {
                        if (errorResult == P2PCostant.ERROR_RESULT_MONEY_TRANSFER_AFTER_POST_ID) {
                            handlePosteIdErrorCode(PosteIdErrorCode.DISMISS_POSTE_ID.value)
                        }
                    }
                }

                ERROR_NOT_ENOUGH_BALANCE -> {
                    _showInsufficientBalance.postConsumableUnit()
                }

                else -> {
                    goToGenericOperationFailedErrorPage()

                }
            }
        } else {
            goToGenericOperationFailedErrorPage()
        }
    }

    @RemoteLoggerInfo
    fun getP2pMoneyTransfer(signedTransaction: String = "", errorResult: String? = null) {
        inputOutputCoroutine {
            p2pRepository.getP2pMoneyTransfer(
                MoneyTransferRequest(
                    signedTransaction
                )
            ).collect {
                it.handleResponse(
                    succeeded = {
                        _analyticsData.postValue(AnalyticsRegion.P2P_INVIA_DENARO_ESITO.asConsumable())
                        postReadyStatus()
                        showPresaInCarico = !it.result?.status.isNullOrEmpty()
                        moneyTransferLiveData.postValue(it.asConsumable())
                    },
                    failed = { error ->
                        val errorDesc = error.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_getP2pMoneyTransfer_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_getP2pMoneyTransfer,
                            line = REMOTE_LOGGER_P2pViewModel_getP2pMoneyTransfer_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                        handleMoneyTransferHttpError(
                            error,
                            errorResult
                        )
                    },
                    loading = { postLoadingStatus() }
                )
            }
        }
    }

    fun setMetodoDiPagamento(
        metodoDiPagamento: PaymentMethodUiModel,
    ) {
        metodoPagamentoSelected = metodoDiPagamento
    }

    fun setPaymentMethodUiModelFromLastUsed(paymentMethodUiModel: PaymentMethodUiModel?) {
        metodoPagamentoSelected = paymentMethodUiModel
    }

    fun filterContactP2p(isChecked: Boolean): List<ContattiUiModel> {
        return if (isChecked)
            listContact.filter { it.isP2P }
        else
            listContact
    }

    fun updateImporto(value: String) {
        importo = value
    }

    fun updateMessage(value: String) {
        messaggio = value
    }

    fun goToP2PImporto(contatto: ContattiUiModel) {
        selectedContact = contatto
        navigationLiveData.postValue(
            navigationCoordinator.navigateToImportoFragment(contatto)
        )
    }

    fun search(text: String): List<ContattiUiModel> {
        val listFiltered = listContact.filter {
            it.fullName.lowercase()
                .contains(text.lowercase()) || it.phoneNumber.contains(text.lowercase())
        }
        isListFiltered = listFiltered.isNotEmpty()
        return listFiltered
    }

    fun mRiepilogoUiModel(): RiepilogoUiModel {
        riepilogoUiModel = RiepilogoUiModel(
            paymentMethodUiModel = metodoPagamentoSelected,
            importo = importo,
            commissione = challangeUiModel?.commission ?: "0",
            utente = selectedContact.fullName,
            message = messaggio,
            isMultiPaymentMethods = needToShowPencilOnRiepilogo
        )
        return riepilogoUiModel
    }

    fun goToRiepilogo(uiModel: RiepilogoUiModel) {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToRiepilogo(
                uiModel
            )
        )
    }

    // Richiedi --

    @RemoteLoggerInfo
    fun richiediMoneyTransfer(
        body: RequestP2PMoneyTransferRequest = makeMoneyTransferRequest(),
    ) {
        inputOutputCoroutine {
            p2pRepository.requestP2PMoneyTransfer(body).collect {
                it.handleResponse(
                    loading = {
                        postLoadingStatus()
                    },
                    succeeded = {
                        postReadyStatus()
                        _analyticsData.postValue(
                            if (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA) {
                                AnalyticsRegion.P2P_INVIA_DENARO_ESITO
                            } else {
                                AnalyticsRegion.P2P_RICHIEDI_DENARO_ESITO
                            }.asConsumable()
                        )
                        goToThankYouPage()
                    },
                    failed = { error ->
                        this@P2pViewModel.goToOperationFailedErrorPageTwoCTA()
                        val errorDesc = error.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_getP2pChallenge_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_getP2pNumbers,
                            line = REMOTE_LOGGER_P2pViewModel_getP2pNumbers_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                    }
                )
            }
        }
    }

    private fun getThankYouPageDialogModel(): DialogModel {
        val helpButton = TopBarIcons.ActionIcons.HelpIcon(
            faqData = FaqData(
                tagAdobe = when (selectedP2PFlowType) {
                    P2PCostant.P2PFlowType.INVIA -> P2PFaqTag.INVIA_DENARO_RIEPILOGO_SECTION
                    P2PCostant.P2PFlowType.RICEVI -> P2PFaqTag.RICHIEDI_DENARO_RIEPILOGO_SECTION
                }
            )
        )

        return if (!showPresaInCarico) {
            // importo was just copied from the old fragment...
            val importo =
                importo.getAmountInHundreds().toString().getAmountFromHundreds(addEur = true)
                    ?.replace("€", " €").orEmpty()
            val name = selectedContact.fullName
            val description = when (selectedP2PFlowType) {
                P2PCostant.P2PFlowType.INVIA -> {
                    TextModel.ResTextModel(
                        R.string.label_ty_page_card_title_success,
                        arrayOf(importo, name),
                        spanModels = listOf(
                            SpanModel(customStringToCustomize = TextModel.StringTextModel(importo)),
                            SpanModel(customStringToCustomize = TextModel.StringTextModel(name))
                        )
                    )
                }

                P2PCostant.P2PFlowType.RICEVI -> {
                    TextModel.ResTextModel(
                        R.string.label_ty_page_richiedi_card_title,
                        arrayOf(importo, name),
                        spanModels = listOf(
                            SpanModel(customStringToCustomize = TextModel.StringTextModel(importo)),
                            SpanModel(customStringToCustomize = TextModel.StringTextModel(name))
                        )
                    )
                }
            }
            DialogModel.TyDialogData(
                title = TextModel.ResTextModel(RKit.string.ty_page_title),
                description = description,
                image = ImageModel.ResImage(RKit.drawable.ic_illustrazioni_bollettini),
                buttonList = listOf(
                    ButtonModel(
                        label = TextModel.ResTextModel(RKit.string.label_torna_alla_home),
                        buttonType = ButtonType.FULL,
                        modelOnClick = {
                            navigateToHome(isTutored = isTutored())
                        }
                    )
                ),
                iconTopEnd = helpButton.toButtonModel().copy(
                    modelOnClick = {
                        navigationLiveData.postValue(navigationCoordinator.goFaq(helpButton.faqData))
                    }
                ),
                additionalData = if (!isTutored() && (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA && metodoPagamentoSelected != null)) {
                    listOf(
                        AdditionalThankYouPageModel.SurveyDataModel(
                            operationType = TY_PAGE_P2P_OP_TYPE,
                            retrieveCardData = {
                                sondaggioUseCase.invoke(getThankYouCurrentUserPage())
                            }
                        )
                    )
                } else {
                    emptyList()
                }
            )
        } else DialogModel.TyDialogData(
            title = TextModel.ResTextModel(R.string.label_ty_page_title),
            description = TextModel.ResTextModel(R.string.label_ty_page_card_title),
            image = ImageModel.ResImage(it.posteitaliane.common.R.drawable.ic_typage_case_2),
            buttonList = listOf(
                ButtonModel(
                    label = TextModel.ResTextModel(RKit.string.label_torna_alla_home),
                    buttonType = ButtonType.FULL,
                    modelOnClick = {
                        navigateToHome(isTutored = isTutored())
                    }
                )
            ),
            iconTopEnd = helpButton.toButtonModel().copy(
                modelOnClick = {
                    navigationLiveData.postValue(navigationCoordinator.goFaq(helpButton.faqData))
                }
            ),
            additionalData = if (!isTutored() && (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA && metodoPagamentoSelected != null)) {
                listOf(
                    AdditionalThankYouPageModel.SurveyDataModel(
                        operationType = TY_PAGE_P2P_OP_TYPE,
                        retrieveCardData = {
                            sondaggioUseCase.invoke(getThankYouCurrentUserPage())
                        }
                    )
                )
            } else {
                emptyList()
            }
        )
    }

    fun makeMoneyTransferRequest() =
        RequestP2PMoneyTransferRequest(
            debtorPhoneNumber = selectedContact.phoneNumber,
            reason = riepilogoUiModel.message,
            value = riepilogoUiModel.importo.getAmountInHundreds().toString()
        )

    fun addClickStatistic() {
        inputOutputCoroutine {
            addQuickActionStatistic(StatisticsQuickActions.P2P.value)
        }
    }

    fun isLogged() = sessionManagerRepository.isSessionValid()

    @RemoteLoggerInfo
    fun recuperaAttivita(
        idAttivita: String,
        tipoAttivita: String,
        listenerError: () -> Unit,
        callPaymentsMethods: (RecuperaAttivitaResponse) -> Unit,
    ) {
        inputOutputCoroutine {
            val request = RecuperaAttivitaRequest(
                body = RecuperaAttivitaBodyRequest(
                    idAttivita = idAttivita,
                    tipoAttivita = tipoAttivita
                )
            )
            notificationsRepository.recuperaAttivita(request).collect {
                it.handleResponse(
                    succeeded = { recuperaAttivitaResponse ->
                        postReadyStatus()
                        if (recuperaAttivitaResponse.header?.commandResult == COMMAND_RESULT_OK) {
                            recuperaAttivitaResponse.body?.attivita?.importo.toString()
                                .getAmountFromHundreds(true)?.replace(".", ",")
                                ?.let { it1 -> updateImporto(it1) }
                            withContext(Dispatchers.Main) {
                                callPaymentsMethods(recuperaAttivitaResponse)
                            }
                        } else {
                            if (fromP2pPush && selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA) {
                                showCustomPopup(
                                    PopupUiModel(
                                        styleRes = RKit.style.PosteMaterialAlertDialog,
                                        titleRes = RKit.string.label_attenzione,
                                        descriptionString = recuperaAttivitaResponse.header?.commandResultDetails,
                                        cancelable = false,
                                        negativeListener = {_, _ -> navigateToHome() },
                                        closeActivityOnClick = CloseActivityEnum.ALL,
                                        negativeButtonRes = RKit.string.generic_close
                                    )
                                )
                            } else {
                                listenerError()
                            }

                        }
                    },
                    failed = {
                        listenerError()
                        RemoteLoggerManager.trackWithPayload(
                            caller = REMOTE_LOGGER_P2pViewModel_recuperaAttivita_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_recuperaAttivita,
                            line = REMOTE_LOGGER_P2pViewModel_recuperaAttivita_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                    },
                    loading = {
                        postLoadingStatus()
                    },
                    timeout = {
                        listenerError()
                        postFailedStatus()
                    }
                )
            }
        }
    }

    @RemoteLoggerInfo
    fun recuperaRichiestaPagamentoP2P(
        id: String,
        negativeListener: (DialogInterface, Int) -> Unit,
    ) {
        inputOutputCoroutine {
            p2pRepository.recuperaRichiestaPagamentoP2P(id).collect {
                it.handleResponse(
                    succeeded = { richiestaResponse ->
                        postReadyStatus()
                        if (richiestaResponse.header?.commandResult == COMMAND_RESULT_OK) {
                            showVerificaPosteId.postValue(richiestaResponse.body.challenge?.asConsumable())
                        } else {
                            handleErrorNotificaP2P(
                                richiestaResponse.header?.commandResultReason,
                                negativeListener
                            )
                        }
                    },
                    failed = {
                        goToGenericOperationFailedErrorPage()
                        RemoteLoggerManager.trackWithPayload(
                            caller = REMOTE_LOGGER_P2pViewModel_recuperaRichiestaPagamentoP2P_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_recuperaRichiestaPagamentoP2P,
                            line = REMOTE_LOGGER_P2pViewModel_recuperaRichiestaPagamentoP2P_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                    },
                    loading = {
                        postLoadingStatus()
                    },
                    timeout = {
                        goToGenericOperationFailedErrorPage()
                        postFailedStatus()
                    }
                )
            }
        }
    }

    private fun handleErrorNotificaP2P(
        commandResultReason: String?,
        negativeListener: (DialogInterface, Int) -> Unit,
    ) {
        when (commandResultReason) {
            P2P_REQUEST_NOT_FOUND_ERROR -> {
                showCustomPopup(
                    PopupUiModel(
                        styleRes = RKit.style.PosteMaterialAlertDialog,
                        titleRes = RKit.string.label_attenzione,
                        descriptionRes = R.string.error_message_transaction_not_found,
                        cancelable = false,
                        closeActivityOnClick = CloseActivityEnum.ALL,
                        negativeButtonRes = RKit.string.generic_close,
                        negativeListener = negativeListener
                    )
                )

            }

            else -> {
                goToGenericOperationFailedErrorPage()
            }
        }

    }

    @RemoteLoggerInfo
    fun eseguiRichiestaPagamentoP2P(
        signature: String,
        negativeListener: (DialogInterface, Int) -> Unit,
    ) {
        inputOutputCoroutine {
            p2pRepository.eseguiRichiestaPagamentoP2P(signature).collect {
                it.handleResponse(
                    succeeded = { response ->
                        postReadyStatus()
                        if (response.header?.commandResult == "0") {
                            goToThankYouPage()
                            /*if (response.body?.presoInCaricoAlertMessage.isNullOrEmpty() && response.body?.inLavorazioneAlertMessage.isNullOrEmpty()) {
                            selectedP2PFlowType =
                                P2PCostant.P2PFlowType.RICEVI //UI success TYPage
                            gotoTyPageNotifica()
                        } else {
                            selectedP2PFlowType = P2PCostant.P2PFlowType.INVIA //UI preso in carico TYPage
                            gotoTyPageNotifica()
                        }*/
                        } else {
                            handleErrorNotificaP2P(
                                response.header?.commandResultReason,
                                negativeListener
                            )
                        }
                    },
                    failed = { error ->
                        this@P2pViewModel.goToOperationFailedErrorPageTwoCTA()
                        val errorDesc = error.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_eseguiRichiestaPagamentoP2P_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_eseguiRichiestaPagamentoP2P,
                            line = REMOTE_LOGGER_P2pViewModel_eseguiRichiestaPagamentoP2P_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                    },
                    loading = {
                        postLoadingStatus()
                    },
                    timeout = {
                        this@P2pViewModel.goToOperationFailedErrorPageTwoCTA()
                        postFailedStatus()
                    }
                )
            }
        }
    }

    @RemoteLoggerInfo
    fun annullaRichiestaPagamentoP2P(id: String) {
        inputOutputCoroutine {
            p2pRepository.annullaRichiestaPagamentoP2P(id).collect {
                it.handleResponse(
                    succeeded = { response ->
                        postReadyStatus()
                        if (response.header?.commandResult == COMMAND_RESULT_OK) {
                            showNegaError()
                        } else {
                            showCustomPopup(
                                PopupUiModel(
                                    styleRes = RKit.style.PosteMaterialAlertDialog,
                                    titleRes = RKit.string.label_attenzione,
                                    descriptionString = response.header?.commandResultDetails,
                                    cancelable = false,
                                    closeActivityOnClick = CloseActivityEnum.ALL,
                                    negativeButtonRes = RKit.string.generic_close
                                )
                            )
                        }
                    },
                    failed = {
                        openWsError()
                        val errorDesc = it.getParsedError()
                        RemoteLoggerManager.trackWithPayload(
                            errorDescription = errorDesc?.first?.ifEmpty { ERROR_NA },
                            errorCode = errorDesc?.second?.ifEmpty { ERROR_NA },
                            caller = REMOTE_LOGGER_P2pViewModel_annullaRichiestaPagamentoP2P_CALLER,
                            function = REMOTE_LOGGER_P2pViewModel_annullaRichiestaPagamentoP2P,
                            line = REMOTE_LOGGER_P2pViewModel_annullaRichiestaPagamentoP2P_LINE,
                            backend = RemoteLoggableError.Backend.oneApp
                        )
                        postFailedStatus()
                    },
                    loading = {
                        postLoadingStatus()
                    },
                    timeout = {
                        openWsError()
                        postFailedStatus()
                    }
                )
            }
        }
    }

    private fun showNegaError() {

        val title = if (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA) {
            R.string.nega_richiesta_title_invio
        } else {
            R.string.nega_richiesta_title_richiedi
        }.let { TextModel.ResTextModel(it) }

        val description = if (selectedP2PFlowType == P2PCostant.P2PFlowType.INVIA) {
            TextModel.ResTextModel(
                R.string.nega_richiesta_desc_invio,
                params = arrayOf(
                    importo,
                    selectedContact.fullName
                ),
                spanModels = listOf(
                    SpanModel(customStringToCustomize = TextModel.StringTextModel(importo)),
                    SpanModel(customStringToCustomize = TextModel.StringTextModel(selectedContact.fullName))
                )
            )
        } else {
            TextModel.ResTextModel(
                R.string.nega_richiesta_desc_richiedi,
                params = arrayOf(
                    importo,
                    riepilogoUiModel.utente.handleEmptyValue()
                ), spanModels = listOf(
                    SpanModel(customStringToCustomize = TextModel.StringTextModel(importo)),
                    SpanModel(
                        customStringToCustomize = TextModel.StringTextModel(riepilogoUiModel.utente.handleEmptyValue())
                    )
                )
            )
        }

        val model = DialogModel.ErrorDialogData(
            buttonList = listOf(
                ButtonModel(
                    label = tornaInHome,
                    modelOnClick = {
                        navigateToHome(isTutored = isTutored())
                    },
                    buttonType = ButtonType.FULL
                ),
            ),
            title = title,
            description = description,
            image = ImageModel.ResImage(R.drawable.ic_nega_richiesta),
            additionalData = listOf(AdditionalThankYouPageModel.SurveyDataModel(
                operationType = TY_PAGE_P2P_OP_TYPE,
                retrieveCardData = {
                    sondaggioUseCase.invoke(getKoCurrentUserPage())
                }
            )),
        )
        navigationLiveData.postValue(navigationCoordinator.navigateToComposePage(model))
    }

    fun showErrorPopup(
        titleRes: Int,
        messageRes: Int,
        positiveMessageRes: Int,
        positiveListener: DialogInterface.OnClickListener,
    ) {
        showCustomPopup(
            PopupUiModel(
                styleRes = RKit.style.PosteMaterialAlertDialog,
                titleRes = titleRes,
                descriptionRes = messageRes,
                cancelable = true,
                positiveButtonRes = positiveMessageRes,
                positiveListener = positiveListener,
                closeActivityOnBtnClick = false
            )
        )
    }

    fun getToken() =
        if (sessionManagerRepository.isSessionValid()) sessionManagerRepository.getAccessToken() else null

    override val _posteIdErrorLiveData: MutableLiveData<Consumable<PosteIdError>> =
        MutableLiveData()

    override fun showWrongPinPopup() {
        showCustomPopup(
            PopupUiModel(
                styleRes = RKit.style.PosteMaterialAlertDialog,
                descriptionRes = it.posteitaliane.common.R.string.label_error_posteid_not_valid,
                positiveButtonRes = it.posteitaliane.common.R.string.label_riprova,
                autoDismiss = true
            )
        )
    }

    override fun getPosteIdTag(): String = ""
    override fun showGenericError() = goToGenericOperationFailedErrorPage()

}
