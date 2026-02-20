package com.posteitaliane.spim.pfm;

import static com.posteitaliane.spim.pfm.utils.NetworkUtils.multiNullClose;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meniga.sdk.CustomErrorHandler;
import com.meniga.sdk.MenigaSDK;
import com.meniga.sdk.MenigaSettings;
import com.meniga.sdk.helpers.KeyVal;
import com.meniga.sdk.helpers.LogLevel;
import com.meniga.sdk.helpers.LogType;
import com.meniga.sdk.helpers.MenigaDecimal;
import com.meniga.sdk.helpers.Result;
import com.meniga.sdk.models.accounts.MenigaAccount;
import com.meniga.sdk.models.budget.BudgetUpdate;
import com.meniga.sdk.models.budget.FetchBudgetsFilter;
import com.meniga.sdk.models.budget.MenigaBudget;
import com.meniga.sdk.models.budget.MenigaBudgetEntry;
import com.meniga.sdk.models.budget.NewBudget;
import com.meniga.sdk.models.budget.enums.BudgetType;
import com.meniga.sdk.models.categories.MenigaCategory;
import com.meniga.sdk.models.categories.enums.CategoryRequest;
import com.meniga.sdk.models.categories.enums.CategoryType;
import com.meniga.sdk.models.challenges.FetchChallengeFilter;
import com.meniga.sdk.models.challenges.MenigaChallenge;
import com.meniga.sdk.models.transactions.MenigaTransaction;
import com.meniga.sdk.models.transactions.MenigaTransactionPage;
import com.meniga.sdk.models.transactions.MenigaTransactionSeries;
import com.meniga.sdk.models.transactions.Options;
import com.meniga.sdk.models.transactions.SeriesSelector;
import com.meniga.sdk.models.transactions.TransactionsFilter;
import com.meniga.sdk.models.transactions.enums.SeriesOrderBy;
import com.meniga.sdk.models.transactions.enums.TimeResolution;
import com.meniga.sdk.models.user.MenigaUser;
import com.meniga.sdk.models.user.MenigaUserMetaData;
import com.meniga.sdk.models.userevents.MenigaUserEvent;
import com.meniga.sdk.models.userevents.UserEventSetting;
import com.meniga.sdk.models.userevents.UserEventSubscription;
import com.meniga.sdk.models.userevents.enums.UserEventType;
import com.meniga.sdk.providers.tasks.Continuation;
import com.meniga.sdk.providers.tasks.Task;
import com.meniga.sdk.webservices.APIRequest;
import com.meniga.sdk.webservices.HttpMethod;
import com.meniga.sdk.webservices.interceptors.MenigaHttpLogger;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.posteitaliane.spim.network.PfmBaseUrl;
import com.posteitaliane.spim.pfm.model.*;

import it.posteitaliane.df_utils.model.conticarte.MovimentiUiModel;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import com.posteitaliane.spim.pfm.model.movimenti.Movimenti;
import com.posteitaliane.spim.pfm.utils.HostnameHelper;
import com.posteitaliane.spim.pfm.utils.PosteMenigaInterceptor;

/**
 * Porting da BP a ONEAPP
 */
public final class PfmData {

    public static final String CHANNEL_PUSH = "Push";
    //pfm notifiche
    public static final String THRESHOLD_ENTRATA = "DepositAboveThresholdValue";
    public static final String THRESHOLD_USCITA = "ExpensesAboveThresholdValue";
    /**
     * Timeout token.
     */
    private static long DEFAULT_EXP_TIME_IN_MILLS = 3600000; //un'ora
    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-YYYY"); //è giusto giorno mese anno
    /**
     * Instanza del Singleton.
     */
    private static PfmData instance;
    /**
     * Nome della classe per i log.
     */
    private final static String TAG = PfmData.class.getSimpleName();
    /**
     * si auto aggiorna.
     */
    private ArrayList<ParentCategory> allParentCategoryId;

    private ArrayList<RisultatiAndamento> mesiTot = new ArrayList<>();
    //ONEAPP ? private HashMap<DateTime.Property, ArrayList<PfmLeTueSpeseAdapter.LeTueSpeseCard>> hashMonths;
    private HashMap<DateTime.Property, ArrayList<MyMenigaChallenge>> mappaChallenge = new HashMap<>();
    private HashMap<DateTime.Property, ArrayList<MyMenigaBudget>> mappaBudget = new HashMap<>();
    private static List<MenigaAccount> menigaAccountList;
    private List<MenigaCategory> mMenigaCategoryList;
    //    private static List<MenigaUserEvent> eventTypes;
    private MedieTotali medieTotali;
    //LOGIN
    private String accessToken;

    private float defaultStep = 50f;
    private float defaultTargetAmount = 150f;

    private Long sogliaSuggestedCategoryID;
    private String sogliaSuggestedText;
    private float sogliaSuggestedValore;
    private SpesaPiuAlta spesaPiuAlta;
    private float totaleUscite;
    private float totaleEntrate;
    private float totaleNetto;

    private DateTime dataInizioPfm = null;
    private boolean isPfmInfoLoaded = false;
    private DateTime.Property meseSelezionato;
    private boolean erroreComunicazioneMeniga = false;
    private boolean toUpdate = false;
    private boolean updateFeed = false;
    private long accessTokenExp = 0;
    private Result<List<MenigaTransactionSeries>> chiamataGeneraleReal;
    private Result<List<MenigaBudgetEntry>> chiamataBudgetEntryReal;
    private Result<List<MenigaChallenge>> chiamataChallengesReal;
    private Result<MenigaTransactionPage> chiamataSpesaPiuAltaReal;
    private Result<List<MenigaTransactionSeries>> chiamataMesePrecedente;
    private boolean tokenUpdating = false;
    private boolean reloadTab = false;
    private ArrayList<MyMenigaChallenge> ricorrentiMeseCorrente;
    private DateTime.Property meseSelezionatoFeed;
    private boolean pfmEnabled;
    private boolean spegniLeTueSpese = false;
    private boolean categoriesUpdated = false;
    private boolean pfmIsLoading = false;
    private Authenticator myAuthenticator;
    public MutableLiveData<Integer> resultBudget = new MutableLiveData<Integer>();
    public MutableLiveData<Integer> resultGenerale = new MutableLiveData<Integer>();

    public Result<List<MenigaBudgetEntry>> getChiamataBudgetEntryReal() {
        return chiamataBudgetEntryReal;
    }

    public Result<List<MenigaTransactionSeries>> getChiamataGeneraleReal() {
        return chiamataGeneraleReal;
    }

    private PfmData() {
    }

    public static PfmData getInstance() {
        if (instance == null)
            instance = new PfmData();
        return instance;
    }


    private void initMenigaSDK(MenigaInitCallbacks callBack, Application app, String jwtToken) {


        myAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(final Route route, final Response response) throws IOException {
                if (PfmData.getInstance().getAccessToken() == null) {
                    PfmData.getInstance().auth(app, jwtToken, null);
                    return null;
                } else {
//                    if (CodeUtilsBP.isDev(BancoPostaApplication.getApplication()) && response.code() == 401) {
//                        PfmData.getInstance().auth(BancoPostaApplication.getApplication(), null);
//                    }
                    if (PfmData.getInstance().isTokenExpired() && !PfmData.getInstance().isTokenUpdating()) {
                        PfmData.getInstance().auth(app, jwtToken, null);
                    }
                    ArrayList headers = new ArrayList<>();
                    headers.add(new KeyVal<>("Authorization", "Bearer " + PfmData.getInstance().getAccessToken()));
                    return response.request().newBuilder().header("Authorization", "Bearer " + PfmData.getInstance().getAccessToken()).build();
                }
            }
        };

        final MenigaSettings settings;
        if (!skipCertificate()) {
            settings = new MenigaSettings.Builder()
                    .endpoint(new PfmBaseUrl().getBaseUrl())
                    .authenticator(myAuthenticator)
                    .errorHandler(new CustomErrorHandler() {
                        @Override
                        public void reportAndHandle(Throwable throwable) {
                            Log.e(TAG, "Errore connessione Meniga " + throwable.getLocalizedMessage());
                        }
                    })
//ONEAPP PINNING                    .certificatePinner(PosteCertificatePinnerBuilder.getCertificatePinnerBuilder().build())
                    .addHttpInterceptor(new PosteMenigaInterceptor(app, skipCertificate() ? LogLevel.NONE : LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                    //.addHttpInterceptor(new PosteMenigaInterceptor(BancoPostaApplication.getApplication(), LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                    //.addHttpInterceptor(new HeaderInterceptor())
                    .addNetworkInterceptor(new MenigaHttpLogger("LOGMENIGA", LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                    .timeout(30)
                    .build();

            settings.updateCulture("it-IT");
            MenigaSDK.init(settings);
        } else {
            SSLContext sslContext = null;

            HostnameHelper.skipSSL();
            /// IMPOSTAZIONI DEDICATE AL PINNING DEI CERTIFICATI

            try {
                // NOTA: Nel caso sia necessario cambiare protocollo, va verificato che anche
                //          il BE di riferimento (in questo caso Meniga) abbia la compatibilità
                //          pertanto per ora si lascia SSL al posto di "TLSv1.2" o "DTLSv1.2"
                sslContext = SSLContext.getInstance("SSL"); //NOSONAR
                sslContext.init(null, new X509TrustManager[]{PfmData.getInstance().getTrustManager(skipCertificate(), app)}, skipCertificate() ? new SecureRandom() : null);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Exception", e);
            } catch (KeyManagementException e) {
                Log.e(TAG, "Exception", e);
            }
            // FINE IMPOSTAZIONI DEDICATE AL PINNING DEI CERTIFICATI

            if (sslContext != null) {
                settings = new MenigaSettings.Builder()
                        .endpoint(new PfmBaseUrl().getBaseUrl())
                        .authenticator(myAuthenticator)
                        .errorHandler(new CustomErrorHandler() {
                            @Override
                            public void reportAndHandle(Throwable throwable) {
                                Log.e(TAG, "Errore connessione Meniga " + throwable.getLocalizedMessage());
                            }
                        })
                        .useSSLFactory(sslContext.getSocketFactory(), PfmData.getInstance().getTrustManager(skipCertificate(), app)) // PINNING DEI CERTIFICATI
                        .addHttpInterceptor(new PosteMenigaInterceptor(app, skipCertificate() ? LogLevel.NONE : LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                        //.addHttpInterceptor(new PosteMenigaInterceptor(BancoPostaApplication.getApplication(), LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                        //.addHttpInterceptor(new HeaderInterceptor())
                        .addNetworkInterceptor(new MenigaHttpLogger("LOGMENIGA", LogLevel.VERBOSE, LogType.BODY_AND_HEADERS))
                        .timeout(30)
                        .build();

                settings.updateCulture("it-IT");
                MenigaSDK.init(settings);
            }

        }

        if (callBack != null) {
            callBack.onResponse();
        }
    }


    public void setFeedToUpdate(boolean update) {
        updateFeed = update;
    }

    public boolean isFeedToUpdate() {
        if (updateFeed) {
            clearPfmData();
        }
        return updateFeed;
    }

    public int getSpegniLeTueSpese() {
        return spegniLeTueSpese ? 2 : 3;
    }

    private boolean isCategoriesUpdated() {
        return categoriesUpdated;
    }

    private void setCategoriesUpdated(boolean categoriesUpdated) {
        this.categoriesUpdated = categoriesUpdated;
    }

    private static boolean skipCertificate() {
        return BuildConfig.FLAVOR != "prod";
    }


    private void setSpesaPiuAltaMese(long parentId, String originalText, float v, DateTime date, long transactionId) {
        spesaPiuAlta = new SpesaPiuAlta(parentId, originalText, v, date, transactionId);
    }

    public SpesaPiuAlta getSpesaPiuAlta() {
        return spesaPiuAlta;
    }


    boolean isPfmStartDatePresent() {
        return dataInizioPfm != null;
    }

    /**
     * @return profondità dell'app (definisce numero di tab dei mesi, periodo su cui si basa la media)
     * è il momento più vecchio tra la DTPFM e 12 mesi fa
     */
    public DateTime getPfmStartDate() {
        if (dataInizioPfm != null && dataInizioPfm.isBefore(DateTime.now().minusMonths(11))) { //MAX POSTO A 12 MESI. SI ASPETTA CONFERMA
            dataInizioPfm = PfmUtils.getMonthStartDate(DateTime.now().minusMonths(11));
        } /*else { //ONEAPP ? senza DateTimeNow esplode
            setPfmEnabled(false);
            dataInizioPfm = DateTime.now();
        }*/
        return dataInizioPfm;
    }

    private void setPfmStartDate(DateTime dataInizioPfm) {
        this.dataInizioPfm = new DateTime(PfmUtils.getMonthStartDate(dataInizioPfm));
    }

    @Nullable
    public DateTime.Property getMeseSelezionato() {
        return meseSelezionato;
    }

    @Nullable
    public DateTime.Property getMeseSelezionatoFeed() {
        return meseSelezionatoFeed;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean update) {
        toUpdate = update;
        if (update) {
            if (escluseMap != null) {
                escluseMap.clear();
            }
            if (valoriCategorie != null)
                valoriCategorie.clear();
            setFeedToUpdate(true);
        }
    }

    /**
     * @param meseSelezionato passare null per invalidare isDataLoadedTillNow e rifare le chiamate
     */
    public void setMeseSelezionato(@Nullable DateTime.Property meseSelezionato) {
        this.meseSelezionato = meseSelezionato;
    }

    public void setMeseSelezionatoFeed(@Nullable DateTime.Property meseSelezionato) {
        this.meseSelezionatoFeed = meseSelezionato;
    }

    private boolean getErroreComunicazioneMeniga() {
        return erroreComunicazioneMeniga;
    }

    public void setErroreComunicazioneMeniga(boolean erroreComunicazioneMeniga) {
        this.erroreComunicazioneMeniga = erroreComunicazioneMeniga;
    }

    public boolean isTokenExpired() {
        if (accessTokenExp != 0) {
            return accessTokenExp < System.currentTimeMillis();
        } else return true;
    }

    private void setAccessTokenExp(long accessTokenExp) {
        this.accessTokenExp = accessTokenExp;
    }

    public boolean isTokenUpdating() {
        return tokenUpdating;
    }

    private void setTokenUpdating(boolean tokenUpdating) {
        this.tokenUpdating = tokenUpdating;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isPfmInfoLoaded() {
        return isPfmInfoLoaded;
    }

    private void setPfmInfoLoaded(boolean pfmInfoLoaded) {
        this.isPfmInfoLoaded = pfmInfoLoaded;
    }

    public void clearChallenges() {
        mappaChallenge.clear();
    }

    public interface MenigaBECallbacks {
        void onResponse(boolean success);
    }

    public interface MenigaInitCallbacks {
        void onResponse();
    }

    public interface MenigaBECallbacksId {
        void onResponse(boolean success, int id);
    }

    public interface MenigaBECallbackWithData {
        void onResponse(boolean success, MenigaTransaction menigaTransaction, ArrayList<MenigaTransaction> childs);
    }

    public interface MenigaBECallbackWithDataCarosello {
        void onResponse(boolean success, MenigaTransaction menigaTransaction, Movimenti movimento, ArrayList<MenigaTransaction> menigaTransactionsChild);
    }

    public interface MenigaBECallbackWithDataCaroselloNew {
        void onResponse(boolean success, MenigaTransaction menigaTransaction, MovimentiUiModel.ItemMovimentiUiModel movimento, ArrayList<MenigaTransaction> menigaTransactionsChild);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({JWT, BASIC})
    @interface PfmAuthMethodType {
    }

    static final int JWT = 0;
    static final int BASIC = 1;

    private int pfmAuthMethodType = BASIC;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_PFM, PFM_NO_DATI, NO_ATTIVI, PFM_NO_ERROR, PFM_LOADING})
    public @interface MenigaStatus {
    }

    /**
     * PFM DISABILITATO
     */
    public static final int NO_PFM = 0;
    /**
     * PFM IN CARICAMENTO
     */
    public static final int PFM_LOADING = 70;

    /**
     * PFM ATTIVO MA NON HO DATI DISPONIBILI
     */
    public static final int PFM_NO_DATI = 1;

    /**
     * NON HO STRUMENTI MONITORATI SUL PFM
     */
    public static final int NO_ATTIVI = 2;

    /**
     * IL PFM E ATTIVO E NON RIENTRA IN NESSUNO DEI CASI SOPRAINDICATI
     */
    public static final int PFM_NO_ERROR = 3;


    //	I mesi visualizzati nel Feed del PFM sono 11 + mese corrente = 12
    public ArrayList<DateTime.Property> getMonthsForTabs() {
        int monthsBetween = 0;
        if (getPfmStartDate() != null) {
            monthsBetween = Math.abs(Months.monthsBetween(PfmUtils.getMonthStartDate(DateTime.now()), PfmUtils.getMonthStartDate(getPfmStartDate())).getMonths());

        }
        return PfmUtils.getMonths(monthsBetween > 11 ? 11 : monthsBetween);
    }


    public void clearPfmData() {
//ONEAPP ?        if (hashMonths != null) {
//            hashMonths.clear();
//        }
        if (mesiTot != null) {
            mesiTot.clear();
        }
        if (mappaChallenge != null) {
            mappaChallenge.clear();
        }

    }

    public void clearPfmUtente() {

        setAccessTokenExp(0);
        setTokenUpdating(false);
        setAccessToken(null);
        setPfmInfoLoaded(false);


//ONEAPP ?        if (hashMonths != null) {
//            hashMonths.clear();
//        }
        if (mesiTot != null) {
            mesiTot.clear();
        }
        if (mappaChallenge != null) {
            mappaChallenge.clear();
        }
        if (mMenigaCategoryList != null) {
            mMenigaCategoryList.clear();
        }
        if (valoriCategorie != null) {
            valoriCategorie.clear();
        }
        if (medieTotali != null) {
            medieTotali = null;
        }
        if (menigaAccountList != null) {
            menigaAccountList.clear();
        }
        if (escluseMap != null) {
            escluseMap.clear();
        }
        if (mappaBudget != null) {
            mappaBudget.clear();
        }

        sogliaSuggestedText = null;
        mMenigaBudget = null;
        spesaPiuAlta = null;
        setMeseSelezionato(null);
        setMeseSelezionatoFeed(null);
        dataInizioPfm = null;
        categoriesUpdated = false;
    }

    public void clearBudgetEntries() {
        if (mappaBudget != null) {
            mappaBudget.clear();
        }

    }

    public float getTotaleUscite() {
        return totaleUscite;
    }

    public float getTotaleEntrate() {
        return totaleEntrate;
    }

    public float getTotaleNetto() {
        return totaleNetto;
    }

    private boolean isAttivo() {
        return pfmEnabled;
    }

    public void setPfmEnabled(boolean pfmEnabled, Application app, String jwtToken) {
        if (pfmEnabled) {
            setAccessToken(null);
            initMenigaSDK(null, app, jwtToken);
        }
        this.pfmEnabled = pfmEnabled;
    }

    /**
     * necessario per l'empty state di le tue spese
     *
     * @return almeno un meniga account non disabilitato
     */
    private boolean checkStrumentiAttivi() {
        if (menigaAccountList != null && menigaAccountList.size() != 0) {
            for (MenigaAccount m : menigaAccountList) {
                if (!m.isDisabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    @MenigaStatus
    public int getStato() {
        return checkStato();
    }


    private int checkStato() {
        if (pfmIsLoading) {
            return PFM_LOADING;
        } else if (!isAttivo()) {
            setErroreComunicazioneMeniga(false);
            return NO_PFM;
        } else if (!checkStrumentiAttivi()) {
            return NO_ATTIVI;
/*        } else if (getPfmStartDate().isAfter(DateTime.now().minusMonths(3))) {  //per ora no
            return PFM_NO_DATI;*/
        } else if (getErroreComunicazioneMeniga()) {
            return PFM_NO_DATI;
        } else { //QUALCOSA DELL'UTENTE
            setErroreComunicazioneMeniga(false);
            return PFM_NO_ERROR;
        }
    }

    public boolean prodottiValidiPerPfm() {
//ONEAPP ?        for (int i = 0; i < BancoPostaApplication.getApplication().getListaProdotti().size(); i++) {
//            if (BancoPostaApplication.getApplication().getListaProdotti().get(i).isConto() ||
//                    BancoPostaApplication.getApplication().getListaProdotti().get(i).actLikeEvo(true)) {
//                return true;
//
//            }
//        }
        return false;
    }


    public List<Long> getAllParentCategoryId(CategoryType categoryType) {
        ArrayList<Long> filteredCategory = new ArrayList<>();
        if (allParentCategoryId != null) {
            if (categoryType == null) {
                for (int i = 0; i < allParentCategoryId.size(); i++) {
                    filteredCategory.add(allParentCategoryId.get(i).categoryId);
                }
            } else {
                for (int i = 0; i < allParentCategoryId.size(); i++) {
                    if (allParentCategoryId.get(i).categoryType() == categoryType) {
                        filteredCategory.add(allParentCategoryId.get(i).categoryId);
                    }
                }
            }
            return filteredCategory;
        } else {
            return new ArrayList<>();
        }

    }

    private CategoryType getCategoryTypeFromId(long id) {
        for (MenigaCategory menigaCategory : mMenigaCategoryList) {
            if (menigaCategory.getId() == id) {
                return menigaCategory.getCategoryType();
            }
        }
        return null;
    }

    private void addValoriCategorie(ValoriCategorie _valoriCategorie) {
        if (valoriCategorie == null)
            valoriCategorie = new ArrayList<>();
        if (getValoriCategoria(_valoriCategorie.getId()) != null) {
            for (ValoriMese v : _valoriCategorie.getValoriMese()) {
                if (getMesePerCategoria(_valoriCategorie.getId(), v.getMese()) != null) {
                    getValoriCategoria(_valoriCategorie.getId()).getValoriMese().remove(getMesePerCategoria(_valoriCategorie.getId(), v.getMese())); //NOSONAR
                }
                getValoriCategoria(_valoriCategorie.getId()).getValoriMese().add(v); //NOSONAR
            }
        } else {
            valoriCategorie.add(_valoriCategorie);
        }
    }


    public class MesiComparator implements Comparator<ValoriMese> {
        @Override
        public int compare(ValoriMese o1, ValoriMese o2) {
            if (o1.getMese().getDateTime().isBefore(o2.getMese().getDateTime()))
                return -1;
            if (o1.getMese().getDateTime().isAfter(o2.getMese().getDateTime()))
                return 1;
            return 0;
        }
    }


    //------------------START tutti i metodi per ottenere valori per categoria & per mesi------------------

    public ArrayList<ValoriCategorie> getValoriCategorie() {
        return valoriCategorie;
    }

    @Nullable
    private ValoriCategorie getValoriCategoria(long id) {
        for (int i = 0; i < valoriCategorie.size(); i++) {
            if (valoriCategorie.get(i).getId() == id) {
                return valoriCategorie.get(i);
            }
        }
        return null;
    }

    @Nullable
    private ValoriMese getMesePerCategoria(long id, DateTime.Property mese) {
        ValoriCategorie valori = getValoriCategoria(id);
        if (valori != null) {
            for (int i = 0; i < valori.getValoriMese().size(); i++) {
                if (PfmUtils.checkMonthAndYear(mese, valori.getValoriMese().get(i).getMese()))
                    return valori.getValoriMese().get(i);
            }
        }
        return null;
    }

    /**
     * @return media per categoria TOTALE (cioè al mese corrente, su 12 mesi)
     */
    public float getMediaPerCategoria(long id) {
        for (int i = 0; i < valoriCategorie.size(); i++) {
            if (valoriCategorie.get(i).getId() == id) {
                return valoriCategorie.get(i).getMediaTotale().floatValue();
            }
        }
        return 0;
    }

    /**
     * @return media per categoria in un dato mese
     */
    public float getMediaPerCategoriaMese(long id, DateTime.Property mese) {
        for (int i = 0; i < valoriCategorie.size(); i++) {
            if (valoriCategorie.get(i).getId() == id) {
                for (int j = 0; j < valoriCategorie.get(i).getValoriMese().size(); j++) {
                    if (PfmUtils.checkMonthAndYear(valoriCategorie.get(i).getValoriMese().get(j).getMese(), mese))
                        return valoriCategorie.get(i).getValoriMese().get(j).getMedia().floatValue();
                }
            }
        }
        return 0;
    }


    /**
     * @return uscite/ entrate per categoria in un dato mese
     */
    public float getValorePerCategoriaMese(long idCategoria, DateTime.Property mese) {
        if (valoriCategorie != null) {
            for (int i = 0; i < valoriCategorie.size(); i++) {
                if (valoriCategorie.get(i).getId() == idCategoria) {
                    for (int j = 0; j < valoriCategorie.get(i).getValoriMese().size(); j++) {
                        if (PfmUtils.checkMonthAndYear(valoriCategorie.get(i).getValoriMese().get(j).getMese(), mese)) {
                            if (idCategoria == PfmUtils.PFM_ID_ENTRATECATEGORY) {
                                return valoriCategorie.get(i).getValoriMese().get(j).getEntrate().floatValue();
                            } else
                                return valoriCategorie.get(i).getValoriMese().get(j).getUscite().floatValue();
                        }
                    }
                }
            }
        }
        return 0;
    }


    //INIZIO VALORI RELATIVI TOTALE MOVIMENTI
    public RisultatiAndamento getMesiTotMediaByMese(DateTime.Property mese) {
        if (mesiTot != null) {
            for (int i = 0; i < mesiTot.size(); i++) {
                if (PfmUtils.checkMonthAndYear(mesiTot.get(i).getMese(), mese)) {
                    return mesiTot.get(i);
                }
            }
        }
        return null;
    }

    public ArrayList<RisultatiAndamento> getMesiTot() {
        if (mesiTot != null) {
            return mesiTot;
        } else return null;
    }


    private void setMesiTot(MenigaTransactionSeries _mesiTot) {
        mesiTot = new ArrayList<>();
        for (int i = 0; i < _mesiTot.getValues().size(); i++) {
            mesiTot.add(new RisultatiAndamento(_mesiTot.getValues().get(i).getTotalPositive().floatValue(),
                    _mesiTot.getValues().get(i).getTotalNegative().floatValue(),
                    _mesiTot.getValues().get(i).getNettoAmount().floatValue(),
                    _mesiTot.getValues().get(i).getDate().monthOfYear()));
        }

        if (medieTotali == null)
            medieTotali = new MedieTotali(-1, -1, _mesiTot.getStatistics().getAverage().floatValue());
        else medieTotali.setMediaNetta(_mesiTot.getStatistics().getAverage().floatValue());

    }
    //FINE VALORI RELATIVI TOTALE MOVIMENTI


    public MedieTotali getMedieTotali() {
        return medieTotali;
    }

    public class MedieTotali {
        float mediaUscite;
        float mediaEntrate;
        float mediaNetta;


        private MedieTotali(float mediaUscite, float mediaEntrate, float mediaNetta) {
            this.mediaUscite = mediaUscite;
            this.mediaEntrate = mediaEntrate;
            this.mediaNetta = mediaNetta;
        }

        public float getMediaEntrate() {
            return mediaEntrate;
        }

        public float getMediaNetta() {
            return mediaNetta;
        }

        public float getMediaUscite() {
            return mediaUscite;
        }

        void setMediaEntrate(float mediaEntrate) {
            this.mediaEntrate = mediaEntrate;
        }

        void setMediaNetta(float mediaNetta) {
            this.mediaNetta = mediaNetta;
        }

        void setMediaUscite(float mediaUscite) {
            this.mediaUscite = mediaUscite;
        }
    }

//ONEAPP ?    public ArrayList<PfmLeTueSpeseAdapter.LeTueSpeseCard> getCachedMonth(DateTime.Property mese) {
//        if (hashMonths != null) {
//            return hashMonths.get(mese);
//        } else return null;
//
//    }
//
//ONEAPP ?    public void setCachedMonth(DateTime.Property mese, ArrayList<PfmLeTueSpeseAdapter.LeTueSpeseCard> speseCards) {
//        if (hashMonths == null) {
//            hashMonths = new HashMap<>();
//        }
//        hashMonths.put(mese, speseCards);
//    }


    public void saveChallenge(ArrayList<MyMenigaChallenge> _challengeArrayList, DateTime.Property mese) {
        if (mappaChallenge == null) {
            mappaChallenge = new HashMap<>();
        }
        if (_challengeArrayList == null) {
            _challengeArrayList = new ArrayList<>();
        }
        mappaChallenge.put(mese, _challengeArrayList);
    }

    public ArrayList<MyMenigaBudget> getBudgetArrayList(DateTime.Property mese) {
        if (mappaBudget == null) {
            return null;
        }
        return mappaBudget.get(mese);
    }

    public ArrayList<MyMenigaChallenge> getChallengeArrayList(DateTime.Property mese) {
        Log.d(TAG, "getChallengeArrayList " + mese.getAsShortText());
        if (mappaChallenge == null) {
            Log.d(TAG, "mappaChallenge " + mappaChallenge);
            return null;
        }
        return mappaChallenge.get(mese);
    }

    public void saveBudgets(ArrayList<MyMenigaBudget> budgetArrayList, DateTime.Property mese) {
        if (mappaBudget == null) {
            mappaBudget = new HashMap<>();
        }
        mappaBudget.put(mese, budgetArrayList);
    }


    public ArrayList<Object> getChallengeBudgetArrayList(DateTime.Property mese) {
        ArrayList<Object> listaObiettivi = new ArrayList<>();
        if (getBudgetArrayList(mese) != null) listaObiettivi.addAll(getBudgetArrayList(mese));
        if (getChallengeArrayList(mese) != null) listaObiettivi.addAll(getChallengeArrayList(mese));
        return listaObiettivi;
    }


    public float getDefaultTargetAmount() {
        return defaultTargetAmount;
    }

    public float getDefaultStep() {
        return defaultStep;
    }

    @Nullable
    public String getSogliaSuggestedText() {
        return sogliaSuggestedText;
    }

    private void setSogliaSuggested(Long id, String mese, float valore1) {
        sogliaSuggestedCategoryID = id;
        sogliaSuggestedValore = valore1;
        sogliaSuggestedText = "A " + mese + " hai speso " + PfmUtils.valoreFormattatoNoSegno(valore1) + " in più rispetto al solito: prova a spendere di meno in questa categoria!";
    }

    public float getSogliaSuggestedValore() {
        return sogliaSuggestedValore;
    }

    public Long getSogliaSuggestedCategoryID() {
        return sogliaSuggestedCategoryID;
    }


    public X509TrustManager getTrustManager(boolean skip, Context context) {
        if (skip) {
            return HostnameHelper.getSkipTrustManager();
        } else {
            TrustManagerFactory tmf = null;
            // CERTIFICATI PFM
            InputStream certificato = null;
            InputStream certificato2 = null;
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
//ONEAPP                switch (SettingsActivity.getDataProviderPreference(context)) {
//                    case COLLAUDO1:
//                        certificato = context.getResources().openRawResource(R.raw.pfmcol_rete_poste_cer);
//                        break;
//                    case CERTIFICAZIONE:
//                        certificato = context.getResources().openRawResource(R.raw.pfmcert_nokey);
//                        break;
//                    case PRODUZIONE:
//                        certificato = context.getResources().openRawResource(R.raw.pfm_poste_it_2021);
//                        break;
//                }
                Certificate ca = null;
                Certificate ca2 = null;

                ca = cf.generateCertificate(certificato);
                if (certificato2 != null) ca2 = cf.generateCertificate(certificato2);

                // creating a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                if (ca2 != null) keyStore.setCertificateEntry("ca2", ca2);

                // creating a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            } finally {
                multiNullClose(certificato, certificato2);
            }

            return tmf != null ? (X509TrustManager) tmf.getTrustManagers()[0] : null;
        }

    }


    public void auth(final Context context, final String jwtToken, final MenigaBECallbacks menigaBECallbacks) {
        if (!isTokenExpired() && accessToken != null) {
            if (menigaBECallbacks != null) {
                menigaBECallbacks.onResponse(true);
            }
            return;
        }

        pfmAuthMethodType = JWT;

        setTokenUpdating(true);
        setAccessToken(jwtToken);
        setAccessTokenExp(System.currentTimeMillis() + PfmData.DEFAULT_EXP_TIME_IN_MILLS);
//                    } catch (Exception e) {
//                        Log.e(TAG, "Exception", e);
//                        //TENTATIVO PER GESTIRE REDIRECT IN CASO DI FINE SESSIONE
//                        if (response.code() == 302) {
//                            if (menigaBECallbacks != null) menigaBECallbacks.onResponse(false);
////                            BancoPostaApplication.getApplication().clearAllStack();
//                        }
//                    }
        setTokenUpdating(false);
        if (menigaBECallbacks != null) menigaBECallbacks.onResponse(jwtToken != null);
//                }
//
//                @Override
//                public void onFailure(retrofit2.Call call, Throwable t) {
//                    Log.e(TAG, "onFailure");
//                    setTokenUpdating(false);
//                    if (menigaBECallbacks != null) menigaBECallbacks.onResponse(false);
//
//                }
//            });
//        }

    }

    /**
     * Da utilizzare solo una volta!
     */
    public void getCategories(final Context context, final MenigaBECallbacks menigaBECallbacks, Application app, String jwtToken) {
        if (getMenigaCategoryList() != null && getMenigaCategoryList().size() > 0) {
            menigaBECallbacks.onResponse(true);
            if (isCategoriesUpdated()) {
                return;
            }
        }
        Log.v(TAG, "getCategories");
        if (MenigaSDK.getMenigaSettings() == null) {
            initMenigaSDK(new MenigaInitCallbacks() {
                @Override
                public void onResponse() {
                    fetchCategoryTree(new MenigaBECallbacks() {
                        @Override
                        public void onResponse(boolean success) {
                            menigaBECallbacks.onResponse(success);
                        }
                    });
                }
            }, app, jwtToken);
        } else {
            fetchCategoryTree(new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    menigaBECallbacks.onResponse(success);
                }
            });
        }

    }

    private void fetchCategoryTree(final MenigaBECallbacks menigaBECallbacks) {
        MenigaCategory.fetchTree(CategoryRequest.ALL).getTask().continueWith(new Continuation<List<MenigaCategory>, Object>() {
            @Override
            public Object then(Task<List<MenigaCategory>> task) throws Exception {
                // Task object contains list of all root categories. Each root category will contain it's children.
                if (task.isFaulted() || task.getResult() == null || task.getResult().size() == 0) {
                    Log.e(TAG, "task ERROR: " + (task.isFaulted() ? "faulted" : task.getResult() == null ? "null" : "size = 0"));
                    menigaBECallbacks.onResponse(false);
                    return null;
                } else {
                    //verificare se serve il salvatggio
//                    PfmUtils.serializeMenigaCategories(context, task.getResult());
                    setAllParentCategoryId(task.getResult());
                    setMenigaCategoryList(task.getResult());
                    setCategoriesUpdated(true);
//                    SecureSharedPreferencesAdapter.with(context).setMenigaCategory(SharedPreferenceAdapter.with(context).getLoggedUsername(), task.getResult());
                    menigaBECallbacks.onResponse(true);
                }
                return null;
            }
        });
    }

    /**
     * Lista delle categorie disponibili in Meniga.
     *
     * @return List<MenigaCategory>
     */
    @Nullable
    public List<MenigaCategory> getMenigaCategoryList() {
        return mMenigaCategoryList;
    }

    /**
     * @param menigaCategoryList List<MenigaCategory>
     */
    private void setMenigaCategoryList(@NonNull List<MenigaCategory> menigaCategoryList) {
        mMenigaCategoryList = menigaCategoryList;
    }

    private void setAllParentCategoryId(@NonNull List<MenigaCategory> menigaCategories) {
        allParentCategoryId = new ArrayList<>();
        if (menigaCategories != null) {
            for (MenigaCategory menigaCategory : menigaCategories) {
                allParentCategoryId.add(new ParentCategory(menigaCategory.getId(), menigaCategory.getCategoryType()));
            }
        }
    }

    public List<MenigaAccount> getMenigaAccountList() {
        return menigaAccountList;
    }


    private static void setMenigaAccountList(List<MenigaAccount> _menigaAccountList) {
        if (_menigaAccountList == null)
            menigaAccountList = new ArrayList<>();
        else
            menigaAccountList = _menigaAccountList;
    }


    //CHIAMATA PER RICEVE I TIPI DI EVENTI (ANCORA NON UTILIZZATO)
    public void getTransactionByPush(long id, Application app, String jwtToken, final MenigaBECallbacksId menigaBECallbacksId) {
        if (MenigaSDK.getMenigaSettings() == null) {
            initMenigaSDK(new MenigaInitCallbacks() {
                @Override
                public void onResponse() {
                    getPfmUserDataAndCategories(app, new MenigaBECallbacks() {
                        @Override
                        public void onResponse(boolean success) {
                            getTransactionByPushPostCheck(id, menigaBECallbacksId);
                        }
                    }, jwtToken);
                }
            }, app, jwtToken);
        } else {
            getTransactionByPushPostCheck(id, menigaBECallbacksId);
        }
    }    //CHIAMATA PER RICEVE I TIPI DI EVENTI (ANCORA NON UTILIZZATO)

    private void getTransactionByPushPostCheck(long id, final MenigaBECallbacksId menigaBECallbacksId) {
        APIRequest.genericRequest(HttpMethod.GET, new PfmBaseUrl().getBaseUrl() + "/userevents/" + id).getTask().continueWith(new Continuation<Object, Object>() {
            @Override
            public Task then(Task<Object> task) {
                if (task.isFaulted() || task.getError() != null) {
                    menigaBECallbacksId.onResponse(false, -1);
                } else {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(task.getResult());
                        Type dataType = new TypeToken<Userevents>() {
                        }.getType();
                        Userevents data = gson.fromJson(json, dataType);
                        if (data.getData().getDataItem().getTransactionId() != 0)
                            menigaBECallbacksId.onResponse(true, data.getData().getDataItem().getTransactionId());
                        else
                            menigaBECallbacksId.onResponse(false, -2);
                    } catch (Exception e) {
                        menigaBECallbacksId.onResponse(false, -2); //-2 è un codice per dire che non ho ricevuto il codice che speravo ma la chiamata ha avuto successo
                    }

                }
                return null;
            }
        });
    }

    public void getValidAccounts(final Context context, final MenigaBECallbacks menigaBECallbacks) {
        if (getMenigaAccountList() != null && getMenigaAccountList().size() > 0) {
            menigaBECallbacks.onResponse(true);
            return;
        }
        MenigaAccount.fetch(true, true).getTask().continueWith(new Continuation<List<MenigaAccount>, Object>() {
            @Override
            public Object then(Task<List<MenigaAccount>> task) throws Exception {
                if (task.isFaulted()) {
                    if (menigaBECallbacks != null) menigaBECallbacks.onResponse(false);
                }
                if (task.isCompleted()) {
                    if (task.getResult() != null)
                        setMenigaAccountList(task.getResult());
                    //SecureSharedPreferencesAdapter.with(context).setMenigaAccounts(SharedPreferenceAdapter.with(context).getLoggedUsername(), task.getResult());
                    if (menigaBECallbacks != null) menigaBECallbacks.onResponse(true);
                }
                return null;
            }
        });
    }

    private Map<DateTime, ArrayList<MenigaTransactionSeriesForSubCat>> escluseMap;


    /**
     * chiamata composta da una sola series. Richiede i valori con categoryType INCOME e li salva insieme a tutti gli altri valori di categoria
     */
    public void chiamataEntrate(final MenigaBECallbacks chiamataCallback) {
        List<SeriesSelector> series = new ArrayList<>();
        Options options = new Options(TimeResolution.MONTH, true, false, false);

        final TransactionsFilter filterExcluded = new TransactionsFilter.Builder()
                .periodFrom(PfmData.getInstance().getPfmStartDate())
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryType(CategoryType.INCOME)
                .build();

        long parentId = PfmUtils.PFM_ID_ENTRATECATEGORY;
        List<Long> listaCategorie = PfmUtils.getSubcategoriesByParentId(parentId);
        listaCategorie.add(parentId);
        TransactionsFilter filter = new TransactionsFilter.Builder()
                .periodFrom(PfmData.getInstance().getPfmStartDate())
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categories(listaCategorie)
                .build();
        series.add(new SeriesSelector(filter));

        MenigaTransactionSeries.fetch(filterExcluded, options, series).getTask().continueWith(new Continuation<List<MenigaTransactionSeries>, Object>() {
            @Override
            public Object then(Task<List<MenigaTransactionSeries>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "getError " + task.getError().getLocalizedMessage());
                    chiamataCallback.onResponse(false);
                    return null;
                }
                if (task.isCompleted()) {
                    for (int i = 0; i < PfmData.getInstance().getAllParentCategoryId(CategoryType.INCOME).size(); i++) {
                        MenigaDecimal totale = task.getResult().get(i).getStatistics().getTotal();
                        MenigaDecimal mediaTotale = task.getResult().get(i).getStatistics().getAverage();
                        ArrayList<ValoriMese> valoriMeseArrayList = new ArrayList<>();
                        for (int ii = 0; ii < task.getResult().get(i).getValues().size(); ii++) {
                            DateTime.Property mese = task.getResult().get(i).getValues().get(ii).getDate().monthOfYear();
                            MenigaDecimal entrate = task.getResult().get(i).getValues().get(ii).getTotalPositive();
                            MenigaDecimal uscite = task.getResult().get(i).getValues().get(ii).getTotalNegative();
                            MenigaDecimal media;
                            if (PfmUtils.checkMonthAndYear(task.getResult().get(i).getValues().get(ii).getDate().monthOfYear(), DateTime.now().monthOfYear())) {
                                media = task.getResult().get(i).getStatistics().getAverage(); //mese corrente con media
                            } else {
                                media = new MenigaDecimal(0f); //tutti gli altri mesi, media a 0
                            }
                            valoriMeseArrayList.add(new ValoriMese(mese, media, entrate, uscite));
                        }
                        Collections.sort(valoriMeseArrayList, new MesiComparator());
                        ValoriCategorie valoriCategorie = new ValoriCategorie(PfmData.getInstance().getAllParentCategoryId(CategoryType.INCOME).get(i), totale, mediaTotale, valoriMeseArrayList);
                        addValoriCategorie(valoriCategorie);

                    }

                    if (task.getResult().get(0).getValues().size() == 0) {
                        chiamataCallback.onResponse(false);
                    } else {
                        chiamataCallback.onResponse(true);
                    }
                }
                return null;
            }
        });

    }

    //------------------START metodi per categorie escluse------------------

    /**
     * chiamata composta da una sola series. Richiede i valori con categoryType INCOME e li salva in una Map solo per le escluse
     */
    public void chiamataEscluse(final DateTime.Property mese, final MenigaBECallbacks chiamataCallback) {
        List<SeriesSelector> series = new ArrayList<>();
        Options options = new Options(TimeResolution.MONTH, true, false, false);

        final TransactionsFilter filterExcluded = new TransactionsFilter.Builder()
                .periodFrom(PfmUtils.getMonthStartDate(mese.getDateTime()))
                .periodTo(PfmUtils.getMonthEndDate(mese.getDateTime()))
                .categoryType(CategoryType.EXCLUDED)
                .build();
        long idCategoria = PfmUtils.PFM_ID_SPESEESCLUSECATEGORY;
        final List<Long> listaCategorie = PfmUtils.getSubcategoriesByParentId(idCategoria);
        listaCategorie.add(idCategoria);

        for (int i = 0; i < listaCategorie.size(); i++) {
            TransactionsFilter filter = new TransactionsFilter.Builder()
                    .category(listaCategorie.get(i))
                    .periodFrom(PfmUtils.getMonthStartDate(mese.getDateTime()))
                    .periodTo(PfmUtils.getMonthEndDate(mese.getDateTime()))
                    .build();
            series.add(new SeriesSelector(filter));
        }

        MenigaTransactionSeries.fetch(filterExcluded, options, series).getTask().continueWith(new Continuation<List<MenigaTransactionSeries>, Object>() {
            @Override
            public Object then(Task<List<MenigaTransactionSeries>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "getError " + task.getError().getLocalizedMessage());
                    chiamataCallback.onResponse(false);
                    return null;
                }
                if (task.isCompleted()) {
                    ArrayList<MenigaTransactionSeriesForSubCat> escluseSeries = new ArrayList<>();

                    for (int i = 0; i < listaCategorie.size(); i++) {
                        if (task.getResult().get(i).getValues().get(0).getTransactionIds().size() != 0) {
                            escluseSeries.add(new MenigaTransactionSeriesForSubCat(listaCategorie.get(i), task.getResult().get(i).getValues().get(0).getNettoAmount().floatValue()));
                        }
                    }
                    if (escluseMap == null)
                        escluseMap = new HashMap<>();
                    escluseMap.put(PfmUtils.getMonthStartDate(mese.getDateTime()), escluseSeries);

                    if (task.getResult().get(0).getValues().size() == 0) {
                        chiamataCallback.onResponse(false);
                    } else {
                        chiamataCallback.onResponse(true);
                    }
                }
                return null;
            }
        });

    }

    @Nullable
    public ArrayList<MenigaTransactionSeriesForSubCat> getEscluseSeriesPerMese(@NonNull DateTime.Property mese) {
        if (escluseMap != null) {
            if (escluseMap.containsKey(PfmUtils.getMonthStartDate(mese.getDateTime())))
                return escluseMap.get(PfmUtils.getMonthStartDate(mese.getDateTime()));
        }
        return null;
    }

    public float getTotalEsclusePerMese(@NonNull DateTime.Property mese) {
        if (escluseMap != null) {
            if (escluseMap.containsKey(PfmUtils.getMonthStartDate(mese.getDateTime()))) {
                ArrayList<MenigaTransactionSeriesForSubCat> serie = escluseMap.get(PfmUtils.getMonthStartDate(mese.getDateTime()));
                float totale = 0;
                for (int i = 0; i < serie.size(); i++) {
                    totale += serie.get(i).getValore();
                }
                return totale;
            }
        }
        return 0;
    }
    //------------------END metodi categorie escluse------------------

    public void chiamataGeneraleFeedNew(Application app, String jwtToken, final MenigaBECallbacks chiamataCallback) {
        if (getPfmStartDate() == null) {
            PfmData.getInstance().getUserData(new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    if (success) {
                        chiamataGeneraleFeedNew(DateTime.now().monthOfYear(), CategoryType.EXPENSES, chiamataCallback);
                    } else {
                        chiamataCallback.onResponse(false);
                        resultGenerale.postValue(0);
                    }
                }
            }, app, jwtToken);
        } else {
            chiamataGeneraleFeedNew(DateTime.now().monthOfYear(), CategoryType.EXPENSES, chiamataCallback);
        }
    }

    /**
     * @param mesePrec null solo se si viene dal feed mese corrente (e serve la media uscite per card_@uscite_vuoto).
     */
    /*public void chiamataGeneraleFeedNew(@Nullable final DateTime.Property mesePrec, Application app, String jwtToken, final MenigaBECallbacks chiamataCallback) {
        if (PfmData.getInstance().getAccessToken() == null) {
            PfmData.getInstance().auth(app, jwtToken, new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    chiamataGeneralePostAuth(mesePrec, chiamataCallback, app, jwtToken);
                }
            });
        } else {
            chiamataGeneralePostAuth(mesePrec, chiamataCallback, app, jwtToken);
        }
    }*/


    /*private void chiamataGeneralePostAuth(@Nullable final DateTime.Property mesePrec, final MenigaBECallbacks chiamataCallback, Application app, String jwtToken) {
        if (getPfmStartDate() == null) {
            PfmData.getInstance().getUserData(new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    if (success) {
                        chiamataGeneraleFeedNew(mesePrec, CategoryType.EXPENSES, chiamataCallback);
                    } else {
                        chiamataCallback.onResponse(false);
                    }
                }
            }, app, jwtToken);
        } else {
            chiamataGeneraleFeedNew(mesePrec, CategoryType.EXPENSES, chiamataCallback);
        }
    }*/


    /**
     * Chiamata effettuata ogni volta che si entra nel feed, si cambia mese o si refresha il feed
     * <p>
     * Carica per tutte le categorie:
     * - per tutti i mesi le uscite, le entrate
     * - per mese corrente le medie
     * <p>
     * Carica per tutti i mesi:
     * - totale delle uscite
     * - totale delle entrate
     * - totale dei valori netti
     * <p>
     * Valore medio dei netti
     *
     * @param mesePrec         null se mese corrente.
     * @param category         CategoryType
     * @param chiamataCallback MenigaBECallbacks
     */
    public void chiamataGeneraleFeedNew(@Nullable final DateTime.Property mesePrec, final CategoryType category, final MenigaBECallbacks chiamataCallback) {


        List<SeriesSelector> series = new ArrayList<>();
        Options options = new Options(TimeResolution.MONTH, true, false, false);
        DateTime.Property endMonth;
        if (mesePrec == null)
            endMonth = DateTime.now().monthOfYear();
        else
            endMonth = mesePrec;

        setMeseSelezionato(endMonth);


        final TransactionsFilter filterGeneric = new TransactionsFilter.Builder()
                .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                .periodTo(PfmUtils.getMonthEndDate(endMonth.getDateTime()))
                .build();

        List<CategoryType> categoryTypes = new ArrayList<>();
        categoryTypes.add(CategoryType.INCOME);
        categoryTypes.add(CategoryType.EXPENSES);
        final TransactionsFilter filterNet = new TransactionsFilter.Builder()
                .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryTypes(categoryTypes)
                .build();

        final TransactionsFilter filterUscite = new TransactionsFilter.Builder()
                .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryType(CategoryType.EXPENSES)
                .build();


        for (int i = 0; i < PfmData.getInstance().getAllParentCategoryId(category).size(); i++) {
            long parentId = PfmData.getInstance().getAllParentCategoryId(category).get(i);
            List<Long> listaCategorie = PfmUtils.getSubcategoriesByParentId(parentId);
            listaCategorie.add(parentId);
            TransactionsFilter filter = new TransactionsFilter.Builder()
                    .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                    .categories(listaCategorie)
                    .periodTo(PfmUtils.getMonthEndDate(endMonth.getDateTime()))
                    .build();
            series.add(new SeriesSelector(filter));
        }
        if (mesePrec == null) { //solo se mese corrente, aggiungi series per la media uscite (necessaria per card uscite_vuoto)
            series.add(new SeriesSelector(filterUscite));
        }

        series.add(new SeriesSelector(filterNet));
        if (chiamataGeneraleReal != null) {
            chiamataGeneraleReal.cancel();
            Log.v(TAG, "chiamataGenerale cancellata");
        }

        chiamataGeneraleReal = MenigaTransactionSeries.fetch(filterGeneric, options, series);

        final DateTime.Property finalEndMonth = endMonth;
        chiamataGeneraleReal.getTask().continueWith(new Continuation<List<MenigaTransactionSeries>, Object>() {
            @Override
            public Object then(Task<List<MenigaTransactionSeries>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "getError " + task.getError().getLocalizedMessage());
                    chiamataCallback.onResponse(false);
                    resultGenerale.postValue(0);
                    return null;
                }
                if (task.isCompleted()) {
                    PfmData.getInstance().setMesiTot(task.getResult().get(task.getResult().size() - 1));
                    totaleNetto = task.getResult().get(task.getResult().size() - 1).getStatistics().getTotal().floatValue();

                    if (mesePrec == null) {//solo se mese corrente media uscite per uscite_vuoto
                        medieTotali.setMediaUscite(task.getResult().get(task.getResult().size() - 2).getStatistics().getAverage().floatValue());
                        totaleUscite = task.getResult().get(task.getResult().size() - 2).getStatistics().getTotal().floatValue();
                    }

                    for (int i = 0; i < PfmData.getInstance().getAllParentCategoryId(category).size(); i++) {
                        MenigaDecimal totale = task.getResult().get(i).getStatistics().getTotal();
                        MenigaDecimal mediaTotale = task.getResult().get(i).getStatistics().getAverage();
                        ArrayList<ValoriMese> valoriMeseArrayList = new ArrayList<>();
                        //mese corrente
                        for (int ii = 0; ii < task.getResult().get(i).getValues().size(); ii++) {
                            DateTime.Property mese = task.getResult().get(i).getValues().get(ii).getDate().monthOfYear();
                            MenigaDecimal entrate = task.getResult().get(i).getValues().get(ii).getTotalPositive();
                            MenigaDecimal uscite = task.getResult().get(i).getValues().get(ii).getTotalNegative();
                            MenigaDecimal media;
                            if (PfmUtils.checkMonthAndYear(task.getResult().get(i).getValues().get(ii).getDate().monthOfYear(), finalEndMonth)) {
                                media = task.getResult().get(i).getStatistics().getAverage(); //mese corrente con media
                            } else {
                                media = new MenigaDecimal(0f); //tutti gli altri mesi
                            }
                            valoriMeseArrayList.add(new ValoriMese(mese, media, entrate, uscite));
                        }
                        Collections.sort(valoriMeseArrayList, new MesiComparator());
                        ValoriCategorie valoriCategorie = new ValoriCategorie(PfmData.getInstance().getAllParentCategoryId(category).get(i), totale, mediaTotale, valoriMeseArrayList);
                        addValoriCategorie(valoriCategorie);

                    }

                    if (task.getResult().get(0).getValues().size() == 0) {
                        chiamataCallback.onResponse(false);
                        resultGenerale.postValue(0);
                    } else {
                        chiamataCallback.onResponse(true);
                        resultGenerale.postValue(1);
                    }
                }
                return null;
            }
        });
    }


    /**
     * chiamata per avere medie e spese per categorie del mese passato rispetto al mese corrent
     * necessaria solo per card suggestion soglie nel mese corrente
     *
     * @param chiamataCallback
     */
    public void chiamataMesePassato(final MenigaBECallbacks chiamataCallback) {
        List<SeriesSelector> series = new ArrayList<>();
        Options options = new Options(TimeResolution.MONTH, true, false, false);
        final DateTime.Property endMonth = DateTime.now().minusMonths(1).monthOfYear();

        final TransactionsFilter filterGeneric = new TransactionsFilter.Builder()
                .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                .periodTo(PfmUtils.getMonthEndDate(endMonth.getDateTime()))
                .build();


        for (int i = 0; i < PfmData.getInstance().getAllParentCategoryId(CategoryType.EXPENSES).size(); i++) {
            long parentId = PfmData.getInstance().getAllParentCategoryId(CategoryType.EXPENSES).get(i);
            List<Long> listaCategorie = PfmUtils.getSubcategoriesByParentId(parentId);
            listaCategorie.add(parentId);
            TransactionsFilter filter = new TransactionsFilter.Builder()
                    .periodFrom(PfmUtils.getMonthStartDate(getPfmStartDate()))
                    .categories(listaCategorie)
                    .periodTo(PfmUtils.getMonthEndDate(endMonth.getDateTime()))
                    .build();
            series.add(new SeriesSelector(filter));
        }


        if (chiamataMesePrecedente != null) {
            chiamataMesePrecedente.cancel();
            Log.v(TAG, "chiamata Mese Precedente cancellata");
        }
        if (PfmUtils.getMonthEndDate(endMonth.getDateTime()).isBefore(PfmUtils.getMonthStartDate(getPfmStartDate()))) {
            chiamataCallback.onResponse(true);
            return;
        }

        chiamataMesePrecedente = MenigaTransactionSeries.fetch(filterGeneric, options, series);

        chiamataMesePrecedente.getTask().continueWith(new Continuation<List<MenigaTransactionSeries>, Object>() {
            @Override
            public Object then(Task<List<MenigaTransactionSeries>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "task.isFaulted()");
                    chiamataCallback.onResponse(false);
                    return null;
                }
                if (task.isCompleted() && task.getError() == null) {
                    if (task.getResult() != null && task.getResult().size() > 0) {
                        for (int i = 0; i < PfmData.getInstance().getAllParentCategoryId(CategoryType.EXPENSES).size(); i++) {
                            for (int ii = 0; ii < task.getResult().get(i).getValues().size(); ii++) {
                                if (PfmUtils.checkMonthAndYear(task.getResult().get(i).getValues().get(ii).getDate().monthOfYear(), endMonth)) {
                                    float uscite = task.getResult().get(i).getValues().get(ii).getTotalNegative().floatValue();
                                    float media = task.getResult().get(i).getStatistics().getAverage().floatValue(); //mese corrente con media
                                    if (media != 0 && uscite != 0) {
                                        float differenza = Math.abs(media) - Math.abs(uscite);
                                        if (Math.abs(differenza) > Math.abs(getSogliaSuggestedValore()) && differenza < 0) {
                                            setSogliaSuggested(PfmData.getInstance().getAllParentCategoryId(CategoryType.EXPENSES).get(i),
                                                    endMonth.getAsText(Locale.ITALIAN), differenza);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    chiamataCallback.onResponse(true);
                } else {
                    chiamataCallback.onResponse(false);
                }
                return null;
            }
        });


    }

    /**
     * Carica per tutti i mesi:
     * - totale delle uscite
     * - totale delle entrate
     * - totale dei valori netti
     * <p>
     * Valore medio di uscite, entrate e nette
     *
     * @param chiamataCallback MenigaBECallbacks
     */
    public void chiamataMedieExInNet(final MenigaBECallbacks chiamataCallback) {
        List<SeriesSelector> series = new ArrayList<>();
        Options options = new Options(TimeResolution.MONTH, true, false, false);
        DateTime startDate = PfmData.getInstance().getPfmStartDate();
        final TransactionsFilter filterGeneric = new TransactionsFilter.Builder()
                .periodFrom(startDate)
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .build();

        List<CategoryType> categoryTypes = new ArrayList<>();
        categoryTypes.add(CategoryType.INCOME);
        categoryTypes.add(CategoryType.EXPENSES);
        final TransactionsFilter filterNet = new TransactionsFilter.Builder()
                .periodFrom(startDate)
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryTypes(categoryTypes)
                .build();
        final TransactionsFilter filterExpenses = new TransactionsFilter.Builder()
                .periodFrom(startDate)
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryType(CategoryType.EXPENSES)
                .build();
        final TransactionsFilter filterIncome = new TransactionsFilter.Builder()
                .periodFrom(startDate)
                .periodTo(PfmUtils.getMonthEndDate(DateTime.now()))
                .categoryType(CategoryType.INCOME)
                .build();


        series.add(new SeriesSelector(filterNet));
        series.add(new SeriesSelector(filterExpenses)); //AGGIUNGO UNA SERIE GENERICA PER AVERE I VALORI TOTALI
        series.add(new SeriesSelector(filterIncome)); //AGGIUNGO UNA SERIE GENERICA PER AVERE I VALORI TOTALI

        MenigaTransactionSeries.fetch(filterGeneric, options, series).getTask().continueWith(new Continuation<List<MenigaTransactionSeries>, Object>() {
            @Override
            public Object then(Task<List<MenigaTransactionSeries>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "getError " + task.getError().getLocalizedMessage());
                    chiamataCallback.onResponse(false);
                    return null;
                }
                if (task.isCompleted()) {
                    PfmData.getInstance().setMesiTot(task.getResult().get(0));

                    totaleNetto = task.getResult().get(0).getStatistics().getTotal().floatValue();
                    totaleUscite = task.getResult().get(1).getStatistics().getTotal().floatValue();
                    totaleEntrate = task.getResult().get(2).getStatistics().getTotal().floatValue();

                    medieTotali.setMediaNetta(task.getResult().get(0).getStatistics().getAverage().floatValue());
                    medieTotali.setMediaUscite(task.getResult().get(1).getStatistics().getAverage().floatValue());
                    medieTotali.setMediaEntrate(task.getResult().get(2).getStatistics().getAverage().floatValue());
                    if (task.getResult().get(0).getValues().size() == 0) {
                        chiamataCallback.onResponse(false);
                    } else {
                        chiamataCallback.onResponse(true);
                    }
                }
                return null;
            }
        });
    }

    public void chiamataChallenges(final String url, final boolean isMesePrecedente, final DateTime.Property mese, final boolean checkRicorrentiMeseCorrente, final MenigaBECallbacks menigaBECallbacks, Application app, String jwtToken) {
        if (MenigaSDK.getMenigaSettings() == null) {
            initMenigaSDK(new MenigaInitCallbacks() {
                @Override
                public void onResponse() {
                    fetchCategoryTree(new MenigaBECallbacks() {
                        @Override
                        public void onResponse(boolean success) {
                            chiamataChallengesPostCheck(url, isMesePrecedente, mese, checkRicorrentiMeseCorrente, menigaBECallbacks);
                        }
                    });
                }
            }, app, jwtToken);
        } else {
            chiamataChallengesPostCheck(url, isMesePrecedente, mese, checkRicorrentiMeseCorrente, menigaBECallbacks);
        }
    }

    public void chiamataChallenges(final String url, final boolean isMesePrecedente, final DateTime.Property mese, final MenigaBECallbacks menigaBECallbacks, Application app, String jwtToken) {
        if (MenigaSDK.getMenigaSettings() == null) {
            initMenigaSDK(new MenigaInitCallbacks() {
                @Override
                public void onResponse() {
                    fetchCategoryTree(new MenigaBECallbacks() {
                        @Override
                        public void onResponse(boolean success) {
                            chiamataChallengesPostCheck(url, isMesePrecedente, mese, false, menigaBECallbacks);
                        }
                    });
                }
            }, app, jwtToken);
        } else {
            chiamataChallengesPostCheck(url, isMesePrecedente, mese, false, menigaBECallbacks);
        }
    }

    //CHIAMATA PER RECUPERARE LE SOGLIE
    public void chiamataChallengesPostCheck(final String url, final boolean isMesePrecedente, final DateTime.Property mese, final boolean checkRicorrentiMeseCorrente,
                                            final MenigaBECallbacks menigaBECallbacks) {
        final ArrayList<MyMenigaChallenge> listaHistory = new ArrayList<>();
        String stringaStart = "startDateOfMonth=" + mese.getDateTime().getYear() + "-" + mese.get() + "-" + mese.getDateTime().dayOfMonth().withMinimumValue().dayOfMonth().get();
        String stringaEnd = "&endDateOfMonth=" + mese.getDateTime().getYear() + "-" + mese.get() + "-" + mese.getDateTime().dayOfMonth().withMaximumValue().dayOfMonth().get();
        APIRequest.genericRequest(HttpMethod.GET, url + "/poste/challenges/history?" + stringaStart + stringaEnd).getTask().continueWith(new Continuation<Object, Object>() {
            @Override
            public Task then(Task<Object> task) {
                if (task.isFaulted() || task.getError() != null) {
                    Log.d(TAG, "task error: " + (task.getError() != null ? task.getError().getMessage() : "faulted"));
                    menigaBECallbacks.onResponse(false);
                    return null;
                }
                if (task.isCompleted()) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(task.getResult());
                        Type dataType = new TypeToken<ChallengeResponse>() {
                        }.getType();
                        ChallengeResponse data = gson.fromJson(json, dataType);

                        ArrayList<PosteChallenge> listina = (ArrayList<PosteChallenge>) data.getData();

                        for (PosteChallenge p : listina) {
                            //     if (DateTime.parse(p.getStartDate()).isBefore(mese.getDateTime()) && (DateTime.parse(p.getEndDate()).isAfter(mese.getDateTime()))) {
                            listaHistory.add(PfmUtils.generaMyChallengeDaHistoryChallenge(p));
                            //    }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "getError " + e);
                        menigaBECallbacks.onResponse(false);
                    }

                    FetchChallengeFilter params = new FetchChallengeFilter(isMesePrecedente, false, false, false);

                    MenigaChallenge.fetch(params).getTask().continueWith(new Continuation<List<MenigaChallenge>, Object>() {
                        @Override
                        public Object then(Task<List<MenigaChallenge>> task) {
                            if (task.isFaulted() || task.getError() != null) {
                                Log.d(TAG, "task error: " + (task.getError() != null ? task.getError().getMessage() : "faulted"));
                                menigaBECallbacks.onResponse(false);
                                return null;
                            }
                            if (task.isCompleted()) {

                                if (task.getResult().size() == 0) { //challenge non ricorrenti non presenti, non c'è match da fare, si salvano così come sono
                                    saveChallenge(PfmUtils.filtraListaChallenge(listaHistory, mese.getDateTime()), mese);
                                    menigaBECallbacks.onResponse(true);
                                    return null;
                                }

                                if (checkRicorrentiMeseCorrente) {
                                    ricorrentiMeseCorrente = new ArrayList<>();
                                    for (int i = 0; i < task.getResult().size(); i++) {
                                        if (task.getResult().get(i).getStartDate().isEqual(PfmUtils.getMonthStartDate(DateTime.now())) && task.getResult().get(i).getRecurringInterval() != null) {
                                            ricorrentiMeseCorrente.add(PfmUtils.generaMyChallengeDaChallenge(task.getResult().get(i)));
                                        }
                                    }
                                }

                                if (task.getResult().size() > 0) {
                                    final ArrayList<MyMenigaChallenge> listaFinal = new ArrayList<>();
                                    listaFinal.addAll(PfmUtils.generaListaChallenges((ArrayList<MenigaChallenge>) task.getResult(), true, mese.getDateTime()));
                                    listaFinal.addAll(PfmUtils.filtraListaChallenge(listaHistory, mese.getDateTime()));

                                    if (isMesePrecedente || listaHistory.size() == 0) {//se è meseprecedente, salvachallenge e chiudi
                                        saveChallenge(listaFinal, mese);
                                        menigaBECallbacks.onResponse(true);
                                        return null;
                                    }


                                    saveChallenge(listaFinal, mese);
                                    menigaBECallbacks.onResponse(true);

                                }
                            }
                            return null;
                        }
                    });

                }
                return null;
            }

        });

    }

    public ArrayList<MyMenigaChallenge> getRicorrentiMeseCorrente() {
        if (ricorrentiMeseCorrente == null)
            ricorrentiMeseCorrente = new ArrayList<>();
        return ricorrentiMeseCorrente;
    }


    private boolean budgetNextMonth = false;

    //CHIAMATA PER RECUPERARE LE SOGLIE
    public void chiamataBudgetEntries(final DateTime.Property mese, final MenigaBECallbacks menigaBECallbacks) {
        if (mMenigaBudget == null) {
            chiamataMenigaBudget(new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    if (success && mMenigaBudget != null) {
                        if (chiamataBudgetEntryReal != null) {
                            chiamataBudgetEntryReal.cancel();
                            Log.v(TAG, "chiamataBudgetEntry cancellata");
                        }
                        chiamataBudgetEntryReal = MenigaBudgetEntry.fetch(mMenigaBudget.getId(), PfmUtils.getMonthStartDate(mese.getDateTime()), PfmUtils.getMonthEndDate(mese.getDateTime()));
                        chiamataBudgetEntryReal.getTask().continueWith(new Continuation<List<MenigaBudgetEntry>, Object>() {
                            @Override
                            public Object then(Task<List<MenigaBudgetEntry>> task) throws Exception {
                                if (task.isFaulted()) {
                                    Log.e(TAG, "getError " + task.getError().getLocalizedMessage());
                                    resultBudget.postValue(0);
                                    menigaBECallbacks.onResponse(false);
                                    return false;
                                }
                                if (task.isCompleted()) {
                                    List<MenigaBudgetEntry> lista = task.getResult();
                                    if (lista.size() > 0) {
                                        saveBudgets(PfmUtils.generaListaBudgets((ArrayList<MenigaBudgetEntry>) lista, true, mese.getDateTime()), mese);
                                        budgetNextMonth = (PfmUtils.generaListaBudgets((ArrayList<MenigaBudgetEntry>) lista, true, mese.getDateTime().plusMonths(1)).size() > 0);
                                    }
                                    resultBudget.postValue(1);
                                    menigaBECallbacks.onResponse(true);
                                    return false;
                                }
                                return null;
                            }
                        });
                    } else {
                        resultBudget.postValue(0);
                        menigaBECallbacks.onResponse(false);
                    }
                }
            });
        } else {
            if (chiamataBudgetEntryReal != null) {
                chiamataBudgetEntryReal.cancel();
                Log.v(TAG, "chiamataBudgetEntry cancellata");
            }
            chiamataBudgetEntryReal = MenigaBudgetEntry.fetch(mMenigaBudget.getId(), PfmUtils.getMonthStartDate(mese.getDateTime()), PfmUtils.getMonthEndDate(mese.getDateTime()));
            chiamataBudgetEntryReal.getTask().continueWith(new Continuation<List<MenigaBudgetEntry>, Object>() {
                @Override
                public Object then(Task<List<MenigaBudgetEntry>> task) throws Exception {
                    if (task.isFaulted()) {
                        Log.e(TAG, "getError " + task.getError().getLocalizedMessage());
                        resultBudget.postValue(0);
                        menigaBECallbacks.onResponse(false);
                        return false;
                    }
                    if (task.isCompleted()) {
                        List<MenigaBudgetEntry> lista = task.getResult();
                        if (lista != null && lista.size() > 0) {
                            saveBudgets(PfmUtils.generaListaBudgets((ArrayList<MenigaBudgetEntry>) lista, true, mese.getDateTime()), mese);
                            budgetNextMonth = (PfmUtils.generaListaBudgets((ArrayList<MenigaBudgetEntry>) lista, true, mese.getDateTime().plusMonths(1)).size() > 0);
                        }
                        resultBudget.postValue(1);
                        menigaBECallbacks.onResponse(true);
                        return false;
                    }
                    return null;
                }
            });
        }
    }

    private MenigaBudget mMenigaBudget;

    public MenigaBudget getmMenigaBudget() {
        return mMenigaBudget;
    }

    private void setmMenigaBudget(MenigaBudget menigaBudget) {
        mMenigaBudget = menigaBudget;
    }

    public void chiamataMenigaBudget(final MenigaBECallbacks menigaBECallbacks) {
        FetchBudgetsFilter parameters = new FetchBudgetsFilter();
        parameters.setType(BudgetType.BUDGET);

        MenigaBudget.fetch(parameters).getTask().continueWith(new Continuation<List<MenigaBudget>, Object>() {
            @Override
            public Object then(Task<List<MenigaBudget>> task) throws Exception {
                if (task.isFaulted()) {
                    Log.d(TAG, "Fetch Budget Faulted");
                    menigaBECallbacks.onResponse(false);
                    return null;
                }
                if (task.isCompleted()) {
                    Log.d(TAG, "Fetch Budget Completed");
                    if (task.getResult().size() > 0 && task.getResult().get(0) != null) {
                        //update budget container
                        if (task.getResult().get(0).getAccountIds() == null) { //se accountids è null, comprende già tutti gli account.
                            mMenigaBudget = task.getResult().get(0);
                            menigaBECallbacks.onResponse(true);
                        } else {
                            MenigaBudget budget = task.getResult().get(0);
                            budget.update(new BudgetUpdate(budget.getName(), budget.getDescription(), null)).getTask().continueWith(new Continuation<MenigaBudget, Object>() {
                                @Override
                                public Object then(Task<MenigaBudget> task) throws Exception {
                                    if (task.isFaulted() || task.getError() != null) {//error
                                        Log.v(TAG, "Errore aggiornando il budget con i nuovi account");
                                        menigaBECallbacks.onResponse(false);
                                    }
                                    if (task.isCompleted()) {
                                        Log.d(TAG, "Budget aggiornato");
                                        mMenigaBudget = task.getResult();
                                        menigaBECallbacks.onResponse(true);
                                    }
                                    return null;
                                }
                            });
                        }
                    } else {
                        MenigaBudget.create(new NewBudget("Il mio budget", "il mio budget", null)).getTask().continueWith(new Continuation<MenigaBudget, Object>() {
                            @Override
                            public Object then(Task<MenigaBudget> task) throws Exception {
                                if (task.isFaulted()) {
                                    Log.d(TAG, "Budget non creato");
                                    menigaBECallbacks.onResponse(false);
                                    return null;
                                }
                                if (task.isCompleted()) {
                                    Log.d(TAG, "Budget creato");
                                    setmMenigaBudget(task.getResult());
                                    menigaBECallbacks.onResponse(true);
                                    return null;
                                }
                                return null;
                            }
                        });

                    }
                }
                return null;
            }
        });
    }


    public void chiamataSpesaPiuAlta(final DateTime.Property mese, final MenigaBECallbacks menigaBECallbacks) {
        TransactionsFilter filter = new TransactionsFilter.Builder()
                .page(1, 0)
                .orderBy(SeriesOrderBy.BY_AMOUNT)
                .categoryType(CategoryType.EXPENSES)
                .ascendingOrder(true)
                .includeAccounts(false)
                .includeMerchants(false)
                .periodFrom(PfmUtils.getMonthStartDate(mese.getDateTime()))
                .periodTo(PfmUtils.getMonthEndDate(mese.getDateTime()))
                .build();
        if (chiamataSpesaPiuAltaReal != null) {
            chiamataSpesaPiuAltaReal.cancel();
            Log.v(TAG, "chiamataSpesaPiuAlta cancellata");
        }
        chiamataSpesaPiuAltaReal = MenigaTransaction.fetch(filter);
        chiamataSpesaPiuAltaReal.getTask().continueWith(new Continuation<MenigaTransactionPage, Object>() {
            @Override
            public Object then(Task<MenigaTransactionPage> task) throws Exception {
                if (task.isFaulted()) {
                    menigaBECallbacks.onResponse(false);
                    Log.e(TAG, "getError " + task.getError().getLocalizedMessage());
                    return false;
                }
                if (task.getResult().size() > 0) {
                    setSpesaPiuAltaMese(PfmUtils.getParentId(task.getResult().get(0).getCategoryId()),
                            task.getResult().get(0).getIsSplitChild() ? "denaro contante" : task.getResult().get(0).getText(),
                            task.getResult().get(0).getAmount().floatValue(),
                            task.getResult().get(0).getOriginalDate(), task.getResult().get(0).getId());

                }
                menigaBECallbacks.onResponse(true);
                return false;
            }

            ;
        });
    }

    //CHIAMATA PER I DATI UTENTE
    private void getUserData(final MenigaBECallbacks menigaBECallbacks, Application app, String jwtToken) {
        if (!isAttivo()) {
            menigaBECallbacks.onResponse(false);
            return;
        }

        if (MenigaSDK.getMenigaSettings() == null) {
            initMenigaSDK(new MenigaInitCallbacks() {
                @Override
                public void onResponse() {
                    getDTPFM(menigaBECallbacks, app);
                }
            }, app, jwtToken);
        } else {
            getDTPFM(menigaBECallbacks, app);
        }
    }

    private void getDTPFM(final MenigaBECallbacks menigaBECallbacks, Application app) {
        if (isPfmStartDatePresent()) {
            menigaBECallbacks.onResponse(true);
            return;
        }
        final ArrayList<String> metaData = new ArrayList<>();
        metaData.add("DTPFM");
        MenigaUser.fetchMetaData(metaData).getTask().continueWith(new Continuation<List<MenigaUserMetaData>, Object>() {
            @Override
            public Object then(Task<List<MenigaUserMetaData>> task) throws Exception {
                if (task.isCompleted()) {
                    if (task.getError() == null) {
                        try {
                            for (int i = 0; i < task.getResult().size(); i++) {
                                if (task.getResult().get(i).getKey().contentEquals("DTPFM")) {
                                    setPfmStartDate(DateTime.parse(task.getResult().get(i).getValue(), dtf));
                                }
                            }//                            SecureSharedPreferencesAdapter.with(context).setProfonditaMeniga(SharedPreferenceAdapter.with(context).getLoggedUsername(), task.getResult().get(0).getValue());
                            menigaBECallbacks.onResponse(true);
                        } catch (Exception e) {
                            setPfmEnabled(false, app, null);
                            menigaBECallbacks.onResponse(false);
                        }
                        return null;
                    } else {
//  TODO ONEAPP ?                      if (BancoPostaApplication.isFunzionalitaAttiva(BancoPostaApplication.TEST_FIX_ERRORE_MENIGA)) {
//                            setPfmEnabled(false, app);
//                        }
                        menigaBECallbacks.onResponse(false);
                        return null;
                    }
                }
                if (task.isFaulted()) {
//ONEAPP ?                    if (BancoPostaApplication.isFunzionalitaAttiva(BancoPostaApplication.TEST_FIX_ERRORE_MENIGA)) {
//                        setPfmEnabled(false, app);
//                    }
                    menigaBECallbacks.onResponse(false);
                    return null;
                }
                return null;
            }
        });
    }

    public void getPfmUserDataAndCategories(final Application context, final MenigaBECallbacks menigaBECallbacks, String jwtToken) {
        pfmIsLoading = true;
        auth(context, jwtToken, new MenigaBECallbacks() {
            @Override
            public void onResponse(boolean success) {
                if (success) {
                    getUserData(new MenigaBECallbacks() {    //IN CASO DI ASSENZA DELLA DATA DI INIZIO PFM L'UTENTE VERR° TRATTATO COME UN UTENTE NO PFM (all'interno della chiamata su PFMData)
                        @Override
                        public void onResponse(boolean success) {
                            if (success) {
                                getValidAccounts(context, new MenigaBECallbacks() {
                                    @Override
                                    public void onResponse(boolean success) {
                                        if (success) {
                                            if (getMenigaCategoryList() == null || getMenigaCategoryList().size() == 0) {
                                                getCategories(context, new MenigaBECallbacks() {
                                                    @Override
                                                    public void onResponse(boolean success) {
                                                        setPfmInfoLoaded(success);
                                                        setErroreComunicazioneMeniga(!success);
                                                        menigaBECallbacks.onResponse(success);
                                                        for (PfmInfoLoadedCallback callback : pfmInfoLoadedCallbacks) {
                                                            callback.onInfoLoaded(true);
                                                        }
                                                        pfmInfoLoadedCallbacks = new LinkedList<>();
                                                    }
                                                }, context, jwtToken);
                                            } else {
                                                setErroreComunicazioneMeniga(false);
                                                menigaBECallbacks.onResponse(true);
                                                for (PfmInfoLoadedCallback callback : pfmInfoLoadedCallbacks) {
                                                    callback.onInfoLoaded(false);
                                                }
                                            }
                                        } else {
                                            setErroreComunicazioneMeniga(true);
                                            menigaBECallbacks.onResponse(false);
                                            for (PfmInfoLoadedCallback callback : pfmInfoLoadedCallbacks) {
                                                callback.onInfoLoaded(false);
                                            }
                                        }
                                    }
                                });
                            } else {
                                setErroreComunicazioneMeniga(true);
                                menigaBECallbacks.onResponse(false);
                                for (PfmInfoLoadedCallback callback : pfmInfoLoadedCallbacks) {
                                    callback.onInfoLoaded(false);
                                }
                            }
                        }
                    }, context, jwtToken);
                } else {
                    setErroreComunicazioneMeniga(true);
                    menigaBECallbacks.onResponse(false);
                    if (pfmInfoLoadedCallbacks != null && !pfmInfoLoadedCallbacks.isEmpty()) {
                        List<PfmInfoLoadedCallback> clonedList = new ArrayList(pfmInfoLoadedCallbacks);
                        for (PfmInfoLoadedCallback callback : clonedList) {
                            callback.onInfoLoaded(false);
                        }
                    }
                }
                pfmIsLoading = false;
            }
        });
    }

    public void getMenigaMovimento(final long id, Application app, String jwtToken, final MenigaBECallbackWithData menigaBECallbackWithId) {
        if (PfmData.getInstance().isPfmInfoLoaded()) {
            getMenigaMovimentoPostCheck(id, menigaBECallbackWithId);
        } else {
            getPfmUserDataAndCategories(app, new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
//ONEAPP ?                    DialogUtils.hideLoadingDialog();
                    if (success) {
                        getMenigaMovimentoPostCheck(id, menigaBECallbackWithId);
                    }
                }
            }, jwtToken);
        }

    }

    public void getMenigaMovimentoPostCheck(long id, final MenigaBECallbackWithData menigaBECallbackWithId) {
        //DialogUtils.showLoadingDialog(context);
        // TRANSACTION
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id);
        TransactionsFilter filter = new TransactionsFilter.Builder()
                .page(1, 0)
                .transactions(ids)
                .includeAccounts(true)
                .includeMerchants(true)
                .build();

        MenigaTransaction.fetch(filter).getTask().continueWith(new Continuation<MenigaTransactionPage, Object>() {
            @Override
            public Object then(Task<MenigaTransactionPage> task) throws Exception {

                if (task.isFaulted() || task.getResult() == null || task.getResult().size() == 0 || task.getResult().get(0) == null) {
                    menigaBECallbackWithId.onResponse(false, null, null);
                    return null;
                }

                final MenigaTransaction parent = task.getResult().get(0);
                if (parent.getIsSplitParent()) {
                    parent.fetchSplitChildren().getTask().continueWith(new Continuation<List<MenigaTransaction>, Object>() {
                        @Override
                        public Object then(Task<List<MenigaTransaction>> task) throws Exception {

                            if (task.isFaulted() || task.getResult() == null) {
                                menigaBECallbackWithId.onResponse(false, null, null);
                                return null;
                            } else {
                                ArrayList<MenigaTransaction> childs = new ArrayList<>(task.getResult());
                                menigaBECallbackWithId.onResponse(true, parent, childs);
                                return null;
                            }
                        }
                    });
                    return null;
                } else {
                    menigaBECallbackWithId.onResponse(true, parent, null);
                    return null;
                }
            }
        });
    }


    public void getMenigaMovimentoPerBankId(@NonNull final Movimenti movimento, final MenigaBECallbackWithDataCarosello menigaBECallbackWithData, Application app, String jwtToken) {
        if (PfmData.getInstance().isPfmInfoLoaded()) {
            getMenigaMovimentoPerBankIdPostCheck(movimento, menigaBECallbackWithData);
        } else {
            getPfmUserDataAndCategories(app, new MenigaBECallbacks() {
                @Override
                public void onResponse(boolean success) {
                    //ONEAPP ? livedata per dialog? DialogUtils.hideLoadingDialog();
                    if (success) {
                        getMenigaMovimentoPerBankIdPostCheck(movimento, menigaBECallbackWithData);
                    }
                }
            }, jwtToken);
        }
    }

    public void getMenigaMovimentoPerBankIdPostCheck(@NonNull final Movimenti movimento, final MenigaBECallbackWithDataCarosello menigaBECallbackWithData) {

        ArrayList<String> bankId = new ArrayList<>();
        bankId.add(movimento.getTransactionID().replaceFirst("\\s++$", "")); //ELIMINO EVENTUALE SPAZIO BIANCO

        TransactionsFilter filter = new TransactionsFilter.Builder()
                .bankIds(bankId)
                .includeAccounts(true)
                .includeMerchants(true)
                .build();

        MenigaTransaction.fetch(filter).getTask().continueWith(new Continuation<MenigaTransactionPage, Object>() {
            @Override
            public Object then(Task<MenigaTransactionPage> task) throws Exception {

                if (task.isFaulted() || task.getResult() == null || task.getResult().size() == 0 || task.getResult().get(0) == null) {
                    // Transaction non presente su Meniga (vecchio movimento) o errore BE
                    menigaBECallbackWithData.onResponse(true, null, movimento, null);
                    return null;
                }

                final MenigaTransaction parent = task.getResult().get(0);

                if (parent.getIsSplitParent()) {
                    parent.fetchSplitChildren().getTask().continueWith(new Continuation<List<MenigaTransaction>, Object>() {
                        @Override
                        public Object then(Task<List<MenigaTransaction>> task) throws Exception {
                            if (task.isFaulted() || task.getResult() == null) {
                                menigaBECallbackWithData.onResponse(false, null, null, null);
                                return null;
                            } else {
                                ArrayList<MenigaTransaction> childs = new ArrayList<>(task.getResult());
                                menigaBECallbackWithData.onResponse(true, parent, movimento, childs);
                                return null;
                            }
                        }
                    });
                    return null;
                } else {
                    menigaBECallbackWithData.onResponse(true, parent, movimento, null);
                    return null;
                }
            }
        });
    }

    public void getMenigaMovimentoUiModelPerBankId(@NonNull final MovimentiUiModel.ItemMovimentiUiModel movimento, final MenigaBECallbackWithDataCaroselloNew menigaBECallbackWithData) {
        if (PfmData.getInstance().isPfmInfoLoaded()) {
            getMenigaMovimentoUiModelPerBankIdPostCheck(movimento, menigaBECallbackWithData);
        } else {
            menigaBECallbackWithData.onResponse(true, null, movimento, null);
        }
    }

    public void getMenigaMovimentoUiModelPerBankIdPostCheck(@NonNull final MovimentiUiModel.ItemMovimentiUiModel movimento, final MenigaBECallbackWithDataCaroselloNew menigaBECallbackWithData) {
        if (movimento.getTransactionID().isEmpty()) {
            ArrayList<String> bankId = new ArrayList<>();
            bankId.add(movimento.getKey().replaceFirst("\\s++$", "")); //ELIMINO EVENTUALE SPAZIO BIANCO
            getMenigaMovimentoUiModelPerBankIdPost(bankId, movimento, menigaBECallbackWithData);

        } else {
            ArrayList<String> bankId = new ArrayList<>();
            bankId.add(movimento.getTransactionID().replaceFirst("\\s++$", "")); //ELIMINO EVENTUALE SPAZIO BIANCO
            getMenigaMovimentoUiModelPerBankIdPost(bankId, movimento, menigaBECallbackWithData);
        }
    }

    public void getMenigaMovimentoUiModelPerBankIdPost(ArrayList<String> bankId, @NonNull final MovimentiUiModel.ItemMovimentiUiModel movimento, final MenigaBECallbackWithDataCaroselloNew menigaBECallbackWithData) {
        TransactionsFilter filter = new TransactionsFilter.Builder()
                .bankIds(bankId)
                .includeAccounts(true)
                .includeMerchants(true)
                .build();
        MenigaTransaction.fetch(filter).getTask().continueWith(new Continuation<MenigaTransactionPage, Object>() {
            @Override
            public Object then(Task<MenigaTransactionPage> task) throws Exception {

                if (task.isFaulted() || task.getResult() == null || task.getResult().size() == 0 || task.getResult().get(0) == null) {
                    // Transaction non presente su Meniga (vecchio movimento) o errore BE
                    menigaBECallbackWithData.onResponse(true, null, movimento, null);
                    return null;
                }

                final MenigaTransaction parent = task.getResult().get(0);

                if (parent.getIsSplitParent()) {
                    parent.fetchSplitChildren().getTask().continueWith(new Continuation<List<MenigaTransaction>, Object>() {
                        @Override
                        public Object then(Task<List<MenigaTransaction>> task) throws Exception {
                            if (task.isFaulted() || task.getResult() == null) {
                                menigaBECallbackWithData.onResponse(false, null, null, null);
                            } else {
                                ArrayList<MenigaTransaction> childs = new ArrayList<>(task.getResult());
                                menigaBECallbackWithData.onResponse(true, parent, movimento, childs);
                            }
                            return null;
                        }
                    });
                } else {
                    menigaBECallbackWithData.onResponse(true, parent, movimento, null);
                }
                return null;
            }
        });
    }

    public void getMenigaMovimentoPerBankId(@NonNull final String bankId, final MenigaBECallbackWithData menigaBECallbackWithData) {

        ArrayList<String> bankIds = new ArrayList<>();
        bankIds.add(bankId);

        TransactionsFilter filter = new TransactionsFilter.Builder()
                .bankIds(bankIds)
                .includeAccounts(true)
                .includeMerchants(true)
                .build();

        MenigaTransaction.fetch(filter).getTask().continueWith(new Continuation<MenigaTransactionPage, Object>() {
            @Override
            public Object then(Task<MenigaTransactionPage> task) throws Exception {

                if (task.isFaulted() || task.getResult() == null || task.getResult().size() == 0 || task.getResult().get(0) == null) {
                    // Transaction non presente su Meniga (vecchio movimento) o errore BE
                    menigaBECallbackWithData.onResponse(true, null, null);
                    return null;
                }

                final MenigaTransaction parent = task.getResult().get(0);

                if (parent.getIsSplitParent()) {
                    parent.fetchSplitChildren().getTask().continueWith(new Continuation<List<MenigaTransaction>, Object>() {
                        @Override
                        public Object then(Task<List<MenigaTransaction>> task) throws Exception {
                            if (task.isFaulted() || task.getResult() == null) {
                                menigaBECallbackWithData.onResponse(false, null, null);
                                return null;
                            } else {
                                ArrayList<MenigaTransaction> childs = new ArrayList<>(task.getResult());
                                menigaBECallbackWithData.onResponse(true, parent, childs);
                                return null;
                            }
                        }
                    });
                    return null;
                } else {
                    menigaBECallbackWithData.onResponse(true, parent, null);
                    return null;
                }
            }
        });
    }


    //INIZIO METODI NOTIFICHE
    List<MenigaUserEvent> eventi;

    public void getEventTypeNotifiche(final MenigaBECallbacks menigaBECallbacks, Application app, String jwtToken) {
        try {
            if (PfmData.getInstance().getAccessToken() == null) {
                PfmData.getInstance().auth(app, jwtToken, success -> getEventTypeNotifichePostAuth(menigaBECallbacks));
            } else {
                getEventTypeNotifichePostAuth(menigaBECallbacks);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage() + ", getEventTypeNotifiche() called with: menigaBECallbacks = [" + menigaBECallbacks + "], app = [" + app + "], jwtToken = [" + jwtToken + "]");
        }

    }

    public void getEventTypeNotifichePostAuth(final MenigaBECallbacks menigaBECallbacks) {
        MenigaUserEvent.fetch().getTask().continueWith(task -> {
            if (task.getResult() != null && task.getResult().size() > 0) {
                eventi = task.getResult();
                menigaBECallbacks.onResponse(true);
            } else {
                menigaBECallbacks.onResponse(false);
            }
            return null;
        });
    }

    public void sottoscriviPostAuth(boolean subscribe, int id, final PfmData.MenigaBECallbacks menigaBECallbacks) {
        List<MenigaUserEvent> userEvent = PfmData.getInstance().findEvento(id);
        for (MenigaUserEvent userevent : userEvent) {
            Log.d(TAG, "sottoscriviPostAuth: " + userevent.getUserEventTypeIdentifier().name());
        }
        if (userEvent != null && userEvent.size() > 0) {
            MenigaUserEvent.subscribe(userEvent, subscribe, CHANNEL_PUSH, "").getTask().continueWith(task -> {
                menigaBECallbacks.onResponse(!task.isFaulted() && task.isCompleted());
                return null;
            });
        } else {
            Log.w(TAG, "sottoscriviPostAuth: QUALCOSA");
        }
    }


    public List<MenigaUserEvent> getEventi() {
        return eventi;
    }


    public List<UserEventType> getUserEventType(int tipo) {
        ArrayList<UserEventType> list = new ArrayList<>();
        switch (tipo) {
            case Opzione.ENTRATA:
                list.add(UserEventType.TRANSACTIONS_THRESHOLD_DEPOSIT);
                break;
            case Opzione.USCITA:
                list.add(UserEventType.TRANSACTIONS_THRESHOLD_EXPENSES);
                break;
            case Opzione.BUDGET:
                list.add(UserEventType.TRANSACTIONS_CATEGORY_BUDGET_WATCH);
                break;
            case Opzione.DACATEGORIZZARE:
                break;
            case Opzione.RESOCONTO:
                list.add(UserEventType.SCHEDULED_MONTHLY_TRANSACTION_REPORT);
                break;
            case Opzione.SOGLIE:
                list.add(UserEventType.CHALLENGE_PROGRESS);
                list.add(UserEventType.CHALLENGE_COMPLETED);
                break;
            default:
                break;
        }
        return list;
    }

    ArrayList<Opzione> mOpzioni;


    public void setmOpzioni() {
        mOpzioni = new ArrayList<>();
        mOpzioni.add(new Opzione("Entrata", false, Opzione.ENTRATA));
        mOpzioni.add(new Opzione("Uscite", false, Opzione.USCITA));
//        mOpzioni.add(new Opzione("Budget di spesa", false, Opzione.BUDGET));
        //mOpzioni.add(new Opzione("Movimenti da categorizzare", false, Opzione.DACATEGORIZZARE)); //(non in scope release 1)
        mOpzioni.add(new Opzione("Resoconto", false, Opzione.RESOCONTO));
        mOpzioni.add(new Opzione("Categorie", false, Opzione.SOGLIE));
        //Spese fuori dalla norma: See APPBP-974 (non in scope release 1)
    }


    public ArrayList<Opzione> matchOpzioni() {
        if (mOpzioni == null || mOpzioni.isEmpty()) setmOpzioni();
        for (Opzione opzione : mOpzioni) {
            Log.d(TAG, "matchOpzioni() called " + opzione);
        }
        for (Opzione opzione : mOpzioni) {
            Log.d(TAG, "matchOpzioni() called " + opzione.toString());
            List<MenigaUserEvent> eventi = PfmData.getInstance().findEvento(opzione.getTipo());
            Log.d(TAG, "matchOpzioni() evento " + eventi.size());

            if (eventi.size() > 0) {
                for (MenigaUserEvent evento : eventi) {
                    Log.d(TAG, "matchOpzioni() evento " + evento.getUserEventTypeIdentifier());
                    opzione.setChecked(isSubscribedPush(evento));
                    if (opzione.getTipo() == Opzione.ENTRATA || opzione.getTipo() == Opzione.USCITA) {
                        for (UserEventSetting setting : evento.getSettings()) {
                            if (setting.getIdentifier().equals(opzione.getTipo() == Opzione.ENTRATA ? THRESHOLD_ENTRATA : THRESHOLD_USCITA)) {
                                opzione.setSoglia(setting.getValue());
                            }
                        }
                    }
                }
            }
        }
        for (Opzione opzione : mOpzioni) {
            Log.d(TAG, "matchOpzioni() called " + opzione);
        }

        return mOpzioni;
    }

    public void updatePfmSettings(HashMap<String, String> settings, final MenigaBECallbacks menigaBECallbacks) {
        try {
            MenigaUserEvent.updateSettings(settings).getTask().continueWith(task -> {
                menigaBECallbacks.onResponse(!task.isFaulted());
                return null;
            });
        }catch (Exception e){
            //DO NOTHING
        }
    }

    @Nullable
    public List<MenigaUserEvent> findEvento(int type) {
        return findEvento(getUserEventType(type));
    }

    public boolean isSubscribedPush(MenigaUserEvent evento) {
        for (UserEventSubscription subscription : evento.getSubscriptions()) {
            if (subscription.getChannelName().equals(CHANNEL_PUSH)) {
                return subscription.getSubscribed();
            }
        }
        return false;
    }

    @Nullable
    public List<MenigaUserEvent> findEvento(List<UserEventType> type) {
        ArrayList<MenigaUserEvent> list = new ArrayList<>();
        if (type != null && eventi != null) {
            for (UserEventType t : type) {
                for (MenigaUserEvent evento : eventi) {
                    if (evento.getUserEventTypeIdentifier() != t) {
                        for (MenigaUserEvent evento1 : evento.getChildren()) {
                            if (evento1.getUserEventTypeIdentifier() == t)
                                list.add(evento1);
                        }
                    } else {
                        list.add(evento);
                    }
                }
            }
        }
        return list;
    }

//FINE METODI NOTIFICHE

//INIZIO OGGETTI DI UTILITA' PER STORARE INFO

    public static class MenigaTransactionSeriesForSubCat {
        private final long categoryId;
        private float valore;

        public MenigaTransactionSeriesForSubCat(long categoryId, float valore) {
            this.categoryId = categoryId;
            this.valore = valore;
        }

        public long getCategoryId() {
            return categoryId;
        }

        public float getValore() {
            return valore;
        }
    }

    public static class ValoriConfrontoPfm {
        private long categoryId;
        private DateTime.Property mese;
        private final float valore;

        public ValoriConfrontoPfm(long categoryId, DateTime.Property mese, float valore) {
            this.categoryId = categoryId;
            this.mese = mese;
            this.valore = valore;
        }

        public float getValue() {
            return valore;
        }

        public DateTime.Property getMese() {
            return mese;
        }

        public long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(long categoryId) {
            this.categoryId = categoryId;
        }
    }

    public static class RisultatiAndamento {
        private float totalPositive;
        private float totalNegative;
        private float totalNetto;
        private DateTime.Property mese;

        RisultatiAndamento(float totalPositive, float totalNegative, float totalNetto, DateTime.Property mese) {
            this.totalPositive = totalPositive;
            this.totalNegative = totalNegative;
            this.totalNetto = totalNetto;
            this.mese = mese;
        }


        public float getAmount(PfmUtils.ANDAMENTOTYPE andamentotype) {
            switch (andamentotype) {
                case DIFFERENZA:
                    return totalNetto;
                case USCITE:
                    return totalNegative;
                case ENTRATE:
                    return totalPositive;
                default:
                    return 0;

            }
        }

        public DateTime.Property getMese() {
            return mese;
        }

        public float getTotalNegative() {
            return totalNegative;
        }

        public float getTotalPositive() {
            return totalPositive;
        }


        public float getTotalNetto() {
            return totalNetto;
        }
    }

    private class ParentCategory {

        private final long categoryId;
        private final CategoryType categoryType;

        ParentCategory(long categoryId, CategoryType categoryType) {
            this.categoryId = categoryId;
            this.categoryType = categoryType;
        }

        CategoryType categoryType() {
            return categoryType;
        }
    }

    public class SpesaPiuAlta {
        private final long parentId;
        private final String originalText;
        private final float value;
        private final DateTime date;
        private final long transactionId;

        public SpesaPiuAlta(long parentId, String originalText, float value, DateTime date, long transactionId) {

            this.parentId = parentId;
            this.originalText = originalText;
            this.value = value;
            this.date = date;
            this.transactionId = transactionId;
        }

        public long getParentId() {
            return parentId;
        }

        public String getOriginalText() {
            return originalText;
        }

        public float getValue() {
            return value;
        }

        public DateTime getDate() {
            return date;
        }

        public long getTransactionId() {
            return transactionId;
        }
    }
    //FINE OGGETTI DI UTILITA' PER STORARE INFO

    private static ArrayList<ValoriCategorie> valoriCategorie;

    public ValoriMese findValoriCategorie(long id) {
        //per tutti i casi in cui serve la media per categoria totale, quindi non c'è bisogno di specificare il mese, sarà now.
        return findValoriCategorie(id, DateTime.now().monthOfYear());
    }

    @Nullable
    private ValoriMese findValoriCategorie(long id, DateTime.Property mese) {
        if (valoriCategorie != null)
            for (int i = 0; i < valoriCategorie.size(); i++) {
                if (valoriCategorie.get(i).getId() == id) {
                    return valoriCategorie.get(i).findValoriMese(mese);
                }
            }
        return null;
    }


    public class ValoriCategorie {
        private long id;
        private ArrayList<ValoriMese> valoriMese;
        CategoryType categoryType;
        private final MenigaDecimal totale;
        private final MenigaDecimal mediaTotale;

        ValoriCategorie(long id, MenigaDecimal totale, MenigaDecimal mediaTotale, ArrayList<ValoriMese> valoriMese) {
            this.id = id;
            this.valoriMese = valoriMese;
            categoryType = getCategoryTypeFromId(id);
            this.totale = totale;
            this.mediaTotale = mediaTotale;
        }

        public CategoryType getCategoryType() {
            return categoryType;
        }

        @Nullable
        private ValoriMese findValoriMese(DateTime.Property mese) {
            for (int i = 0; i < valoriMese.size(); i++) {
                if (PfmUtils.checkMonthAndYear(valoriMese.get(i).getMese(), mese)) {
                    return valoriMese.get(i);
                }
            }
            return null;
        }

        public void setValoriMese(ArrayList<ValoriMese> valoriMese) {
            this.valoriMese = valoriMese;
        }

        public ArrayList<ValoriMese> getValoriMese() {
            return valoriMese;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }


        public MenigaDecimal getTotale() {
            return totale;
        }

        public MenigaDecimal getMediaTotale() {
            return mediaTotale;
        }
    }


    public class ValoriMese {
        private DateTime.Property mese;
        private MenigaDecimal media;
        private MenigaDecimal entrate;
        private MenigaDecimal uscite;

        ValoriMese(DateTime.Property mese, MenigaDecimal media, MenigaDecimal entrate, MenigaDecimal uscite) {
            this.mese = mese;
            this.media = media;
            this.entrate = entrate;
            this.uscite = uscite;
        }

        public MenigaDecimal getUscite() {
            return uscite;
        }

        public void setUscite(MenigaDecimal uscite) {
            this.uscite = uscite;
        }

        public MenigaDecimal getEntrate() {
            return entrate;
        }

        public void setEntrate(MenigaDecimal entrate) {
            this.entrate = entrate;
        }

        public MenigaDecimal getMedia() {
            return media;
        }

        public void setMedia(MenigaDecimal media) {
            this.media = media;
        }

        public DateTime.Property getMese() {
            return mese;
        }

        public void setMese(DateTime.Property mese) {
            this.mese = mese;
        }

        public float getNetto() {
            return entrate.floatValue() - uscite.floatValue();
        }
    }

    private List<PfmInfoLoadedCallback> pfmInfoLoadedCallbacks = new LinkedList<>();

    public interface PfmInfoLoadedCallback {
        void onInfoLoaded(boolean success);

    }

    public void addPfmInfoLoadedCallback(PfmInfoLoadedCallback callback) {
        pfmInfoLoadedCallbacks.add(callback);
    }

}

