package it.posteitaliane.df_rds.sections.moneygram.summary

import android.content.DialogInterface
import androidx.core.os.bundleOf
import it.posteitaliane.df_rds.R
import it.posteitaliane.df_rds.RdsConstants.RDS_MONEY_GRAM_MODULE_NAME
import it.posteitaliane.df_rds.RdsConstants.RDS_MULTI_SERVICES_OPERATION_NAME
import it.posteitaliane.df_rds.RdsConstants.RDS_OPERATION_ID
import it.posteitaliane.df_rds.RdsConstants.RDS_START_ANALYTICS
import it.posteitaliane.df_rds.RdsFragmentNavigation
import it.posteitaliane.df_rds.datasource.RDSRepository
import it.posteitaliane.df_rds.model.RDSAddressModel
import it.posteitaliane.df_rds.model.RdsMoneyGramDestinatarioModel
import it.posteitaliane.df_rds.model.Telefono
import it.posteitaliane.df_rds.model.request.moneygram.MoneyGramModule
import it.posteitaliane.df_rds.model.request.moneygram.RDSMoneyGramModuleRequest
import it.posteitaliane.df_rds.model.request.moneygram.RDSMoneyGramServiceRequest
import it.posteitaliane.df_rds.model.request.operation.RDSCreateRequest
import it.posteitaliane.df_rds.utils.RDSConfigurator
import it.posteitaliane.df_rds.utils.extensions.fillWithDecimal
import it.posteitaliane.mvvmtoolkit.view.BaseNavigationViewModel
import kotlinx.coroutines.CoroutineDispatcher
import it.posteitaliane.common.R as RUtils

class MoneygramSummaryViewModel(
    private val rdsRepository: RDSRepository,
    coroutineDispatcher: CoroutineDispatcher? = null
) : BaseNavigationViewModel(coroutineDispatcher) {

    override val navigationCoordinator = RdsFragmentNavigation

    fun generateQrCode(rdsMoneygramRequest: RDSMoneyGramServiceRequest) =
        inputOutputCoroutine {
            if (rdsRepository.addModule(createMoneyGramModuleRequest(rdsMoneygramRequest))) {
                createOperation()
            } else {
                showGenericError()
            }
        }

    fun onAddNewServiceClicked(module: RDSMoneyGramServiceRequest) {
        if (rdsRepository.addModule(
                createMoneyGramModuleRequest(module),
                isFromAddNewService = true
            )
        ) {
            navigateToAddService()
        } else {
            showGenericError(R.string.rds_money_gram_summary_operation_max_module_number_reached)
        }
    }

    private fun createOperation() = inputOutputCoroutine {
        rdsRepository.create(obtainRDSCreateRequest()).collect {
            it.handleResponse(
                loading = {
                    postLoadingStatus()
                },
                succeeded = { response ->
                    postReadyStatus()
                    rdsRepository.clearModules()
                    navigateToMultilanguageQrCodeGenerato(response.id ?: "")
                },
                failed = {
                    postFailedStatus()
                    rdsRepository.removeLastModule(true)
                    showGenericError()
                }
            )
        }
    }

    private fun obtainRDSCreateRequest() = RDSCreateRequest(
        name = if (rdsRepository.getAllModules().size > 1) {
            RDS_MULTI_SERVICES_OPERATION_NAME
        } else {
            RDS_MONEY_GRAM_MODULE_NAME
        },
        numeroSecurizzato = false,
        profiloAccesso = RDSConfigurator.loginLevel?.value
            ?: RDSConfigurator.LoginLevel.PRE.value,
        moduli = rdsRepository.getAllModules()
    )

    private fun createMoneyGramModuleRequest(rdsMoneyGramServiceRequest: RDSMoneyGramServiceRequest) =
        RDSMoneyGramModuleRequest(
            modulo = MoneyGramModule(
                paese = rdsMoneyGramServiceRequest.transactionInfo.countryCode,
                paeseDesc = rdsMoneyGramServiceRequest.transactionInfo.country,
                stato = rdsMoneyGramServiceRequest.transactionInfo.stateCode.orEmpty(),
                codiceFiscale = RDSConfigurator.userInfo?.taxCode,
                tipoPagamento = rdsMoneyGramServiceRequest.transactionInfo.sendOption,
                tipoValuta = rdsMoneyGramServiceRequest.transactionInfo.obtainCurrencyCode(),
                receiveCurrency = rdsMoneyGramServiceRequest.transactionInfo.currency,
                importo = rdsMoneyGramServiceRequest.transactionInfo.amount.fillWithDecimal(),
                origineFondi = rdsMoneyGramServiceRequest.transactionInfo.sourceOfFounds?.code,
                scopoTransazione = rdsMoneyGramServiceRequest.transactionInfo.purposeOfTransaction?.code,
                telefonoOrdinante = rdsMoneyGramServiceRequest.senderInfo.phoneNumber,
                relazioneBeneficiario = rdsMoneyGramServiceRequest.senderInfo.relationshipWithReceiver?.code,
                beneficiario = RdsMoneyGramDestinatarioModel(
                    nome = rdsMoneyGramServiceRequest.receiverInfo.firstName,
                    cognome = rdsMoneyGramServiceRequest.receiverInfo.lastName,
                    secondoCognome = rdsMoneyGramServiceRequest.receiverInfo.secondLastName,
                    indirizzo = RDSAddressModel(
                        indirizzo = rdsMoneyGramServiceRequest.receiverInfo.address,
                        citta = rdsMoneyGramServiceRequest.receiverInfo.city,
                        cap = rdsMoneyGramServiceRequest.receiverInfo.cap,
                        paese = rdsMoneyGramServiceRequest.receiverInfo.countryCode.orEmpty(),
                        paeseDesc = rdsMoneyGramServiceRequest.receiverInfo.country.orEmpty(),
                        stato = rdsMoneyGramServiceRequest.receiverInfo.state.orEmpty()
                    ),
                    // controllare se sono mappati bene
                    secondoNome = rdsMoneyGramServiceRequest.receiverInfo.secondName,
                    telefono = Telefono(
                        prefisso = rdsMoneyGramServiceRequest.receiverInfo.prefixC2a,
                        numero = rdsMoneyGramServiceRequest.receiverInfo.telefonoC2A),
                    nomeBanca = rdsMoneyGramServiceRequest.receiverInfo.nomeBancaC2A?.value,
                    nomeBancaDesc = rdsMoneyGramServiceRequest.receiverInfo.nomeBancaC2A?.description,
                    numeroConto = rdsMoneyGramServiceRequest.receiverInfo.numeroContoC2A,
                    numeroID = rdsMoneyGramServiceRequest.receiverInfo.argentinaNumeroId, // solo per argentina
                    motivoTransazione = rdsMoneyGramServiceRequest.receiverInfo.egittoMotivoTransazione?.description, // solo per egitto
                    motivoTransazioneDesc = rdsMoneyGramServiceRequest.receiverInfo.egittoMotivoTransazione?.longDescription, // solo per egitto
                ),
                // controllare il mapping di questi campi
                occupazioneOrdinante = rdsMoneyGramServiceRequest.senderInfo.occupazione?.description,
                occupazioneOrdinanteDesc = rdsMoneyGramServiceRequest.senderInfo.occupazione?.longDescription,
                motivoUtilizzo = rdsMoneyGramServiceRequest.senderInfo.motivoUtilizzo?.description,
                motivoUtilizzoDesc = rdsMoneyGramServiceRequest.senderInfo.motivoUtilizzo?.longDescription,


                )
        )

    fun showGenericError(
        messageErrorRes: Int = RUtils.string.error_loading_message,
        positiveListener: DialogInterface.OnClickListener? = null,
        negativeListener: DialogInterface.OnClickListener? = null,
    ) = showPopup(
        titleRes = RUtils.string.error_loading_title,
        messageRes = messageErrorRes,
        positiveMessageRes = RUtils.string.label_error_generic_reload,
        negativeMessageRes = RUtils.string.close,
        positiveListener = positiveListener,
        negativeListener = negativeListener
    )

    fun showPopup(
        titleRes: Int = 0,
        titleString: String? = null,
        messageRes: Int = 0,
        messageString: String? = null,
        positiveMessageRes: Int = it.posteitaliane.common.R.string.close,
        positiveMessageString: String? = null,
        negativeMessageRes: Int? = null,
        positiveListener: DialogInterface.OnClickListener? = null,
        negativeListener: DialogInterface.OnClickListener? = null,
    ) {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToPopup(
                titleRes = titleRes,
                titleString = titleString,
                messageRes = messageRes,
                messageString = messageString,
                positiveMessageRes = positiveMessageRes,
                positiveMessageString = positiveMessageString,
                negativeMessageRes = negativeMessageRes,
                positiveListener = positiveListener,
                negativeListener = negativeListener,
            )
        )
    }

    private fun navigateToMultilanguageQrCodeGenerato(id: String) {
        navigationLiveData.postValue(
            navigationCoordinator.navigateToMultilanguageQrCodeGenerato(
                bundleOf(
                    RDS_OPERATION_ID to id,
                    RDS_START_ANALYTICS to true
                )
            )
        )
    }

    fun navigateToAddService() {
        navigationLiveData.postValue(navigationCoordinator.navigateToAddService())
    }
}
