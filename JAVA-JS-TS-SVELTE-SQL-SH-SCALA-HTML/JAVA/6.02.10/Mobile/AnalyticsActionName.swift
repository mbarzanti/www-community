//
//  AnalyticsActionName.swift
//  OneApp
//
//  Created by Christian Lucaccioni on 06/02/23.
//

import Foundation

enum AnalyticsActionName: String {
    case inserimentoDatiPreLogin = "Inserimento Dati Pre-Login"
    case inserimentoDatiPostLogin = "Inserimento Dati Post-Login"
    case tapOnCard = "Tap on"
    case tapOnConferma = "Tap on Conferma"
    case tapOnScopriDiPiu = "Tap on Scopri di piu"

    // MARK: Bollettini

    case tapOnCompilazioneManuale = "Tap On Compilazione Manuale"
    case selezionaTipologiaBollettino = "Seleziona Tipologia Bollettino"
    case scansionaQRCode = "Scansiona QR Code"
    case scansioneCodice = "Scansione Codice"
    case tutorial = "Tutorial"
    case inquadraConto = "Inquadra Conto"
    case aperturaTutorial = "Apertura Tutorial"
    case inserimentoCodice = "Inserimento Codice"

    // MARK: Home Operazioni Veloci actions

    case tapOnPrenotaInUfficioPostale = "Tap on Prenota in Ufficio Postale"
    case tapOnTracciaSpedizione = "Tap on Traccia Spedizione"
    case tapOnSpedisciPacco = "Tap on Spedisci Pacco"
    case tapOnBolletteEPagamenti = "Tap on Bollette e Pagamenti"
    case tapOnSpedisciPosta = "Tap on Spedisci Posta"
    case tapOnBolloAutoEMoto = "Tap on Bollo Auto e Moto"
    case tapOnAmministrazioniStatali = "Tap on Amministrazioni Statali"
    case tapOnOperazioniASportello = "Tap on Operazioni a Sportello"
    case tapOnAccediAPuntoPoste = "Tap on Accedi a Punto Poste da Te - HomePage"
    case tapOnRicaricaSim = "Tap on Ricarica Sim"
    case tapOnInviaRichiediDenaro = "Tap on Invia e Richiedi Denaro"
    case tapOnRicercaContenuti = "Tap on Ricerca Contenuti"
    case tapOnPagaConQRCode = "Tap on Paga con QR Code"
    case tapOnRicaricaPostepay = "Tap on Ricarica Postepay"
    case tapOnBonificoSEPA = "Tap on Bonifico SEPA"
    case tapOnPostagiro = "Tap on Postagiro"
    case tapOnDomiciliazioni = "Tap on Domiciliazioni"
    case tapOnPrelevaSenzaCarta = "Tap on Preleva Senza Carta"
    case tapOnPagaCodiceQR = "Tap on Paga Con Codice QR"
    case tapOnOperazioneCustom = "Tap On Operazione Custom"
    case tapOnScopriIProdottiPoste = "Tap on Scopri I Prodotti Poste"

    // MARK: Mondo Poste

    case conti = "Conti"
    case postaPacchi = "Posta e pacchi"
    case cartePostepay = "Carte Postepay"

    // MARK: Poste da te

    case tapOnAccediAPuntoPosteDaTeOperazioni = "Tap on Accedi a Punto Poste da Te - Operazioni"
    case tapOnAccediAPuntoPosteDaTeSpedizioni = "Tap on Accedi a Punto Poste da Te - Spedizioni"
    case tapGestisciPuntoPosteDaTe = "Tap Gestisci Punto Poste da Te"
    case tapOnConfermaDisdettaPrenotazione = "Tap on Conferma Disdetta Prenotazione"

    // MARK: Rubrica

    case clickSuRubrica = "Click su Rubrica"

    // MARK: Pensate per te

    case tapOnPensatePerTePrelogin = "Tap on Card Prelogin"
    case tapOnPensatePerTePostlogin = "Tap on Card Postlogin"

    // MARK: Gestisci bonifici permanenti

    case revocaBonifico = "Revoca Bonifico"
    case revocaPostagiro = "Revoca Postagiro"

    // MARK: Bacheca

    case tapOnApplicaFiltri = "Tap on Applica Filtri"

    // MARK: One Booking

    case cardIngaggioTYPOnebooking = "Card Ingaggio TYP Onebooking"
    case tapOnCardTYPPrenotazione = "Tap on Card"
    case archivioPrenotazioniAnnullaAppuntamentoSportello = "Archivio Prenotazioni - Annulla Appuntamento Sportello"
    case archivioPrenotazioniAnnullaAppuntamentoSalaConsulenza = "Archivio Prenotazioni - Annulla Appuntamento Sala Consulenza"
    case tapOnNuovaPrenotazionePaginaConsulente = "Tap On Nuova Prenotazione - Pagina Consulente"
    case tapOnNuovaPrenotazioneArchivioPrenotazioni = "Tap On Nuova Prenotazione - Archivio Prenotazioni"
    case tapOnScaricaQrCode = "Tap on Scarica QrCode"
    case tapEliminaQRCode = "Tap Elimina QRCode"

    // MARK: Assistenza

    case tapOnEntraInChat = "Tap on Entra in Chat"
    case tapOnChiamaci = "Tap on Chiamaci"
    case tapOnAvviaChat = "Tap on Avvia Chat"
    case tapOnChiama = "Tap on Chiama"
    case tapOnPrenotaChiamata = "Tap On Prenota una Chiamata"

    // MARK: Buoni

    case portafoglioBuoni = "Portafoglio Buoni"

    // MARK: Libretto Smart

    case tapOnAssociaConto = "Tap On Associa Conto"
    case tapOnEliminaModaleRevoca = "Tap On Elimina - Modale Revoca"
    case tapOnAbilitaLibretto = "Tap On Abilita Libretto"

    // MARK: Libretto Ordinario

    case tapOnPrenotaAppuntamento = "Tap on Prenota Appuntamento"
    case passaggioALibrettoSmartTapOnScopriComeFare = "Passaggio a Libretto Smart - Tap on Scopri Come Fare"
    case gestisciTapOnScopriComeFare = "Gestisci - Tap on Scopri Come Fare"

    // MARK: Libretto Minore

    case tapOnAssociaNuovoConto = "Tap On Associa Nuovo Conto"

    // MARK: Global Search

    case tapCerca = "Tap Cerca"
    case tapAutocomplete = "Tap Autocomplete"

    // MARK: Salvadanaio

    case tapOnVersaSubito = "Tap On Versa Subito"
    case tapOnVersa = "Tap On Versa"
    case tapOnArrotondamentoSpese = "Tap On Arrotondamento Spese"

    // MARK: Questionario

    case compilaQuestionario = "Compila Questionario"

    // MARK: Risparmio Postale - OSS

    case tapOnAttivaSupersmart = "Tap On Attiva Supersmart"
    case tapOnAttivaSupersmartDaOfferteAttive = "Tap On Attiva Supersmart da Offerte Attive"
    case tapOnEffettuaDisattivazioneTotaleAlertImportoMax = "Tap On Effettua Disattivazione Totale - Alert Importo Max"
    case tapOnEffettuaDisattivazioneTotaleAlertImportoAlto = "Tap On Effettua Disattivazione Totale - Alert Importo Alto"

    // MARK: Energy

    case landingEnergiaPreventivi = "Landing Energia Preventivi"

    // MARK: App2App

    case app2appBP = "Tap on Accedi Più Rapidamente BP"
    case app2appPP = "Tap on Accedi Più Rapidamente PP"
    case tapOnAccediBP = "Tap On Accedi BP"
    case tapOnAccediPP = "Tap On Accedi PP"
    case tapOnNomeUtenteEPasswordBP = "Tap on Nome Utente e Password BP"
    case tapOnNomeUtenteEPasswordPP = "Tap on Nome Utente e Password PP"

    // MARK: Wallet

    case tapOnPersonalizza = "Tap On Personalizza"
    case tapOnAggiungiIdentita = "Tap On Aggiungi - Identità"
    case tapOnAggiungiCarteFedelta = "Tap On Aggiungi - Carte Fedeltà"
    case tapOnPortataDiManoSportello = "Operazioni a sportello"
    case tapOnPortataDiManoAppuntamento = "Appuntamento"
    case tapOnPortataDiManoPrenotazione = "Prenotazione"
    case tapOnPortataDiManoNumero = "Numero"
    case tapOnPortataDiManoEnergia = "Riprendi preventivo"
    case tapOnPostePass = "Tap on Poste Pass"
    case tapOnDeleteDoc = "Tap on Elimina - Documento Salvato"
    case tapOnUploadPhotoDoc = "Tap on Carica Foto Documento"

    // MARK: Telefonia

    case tapOnPagaOra = "Tap on Paga Ora"
    case tapOnCambiaMetodoDiPagamento = "Tap On Cambia Metodo di Pagamento"
    case tapOnRiprova = "Tap On Riprova"

    // MARK: Authorize

    case tapOnAutorizzaOperazione = "Tap on autorizza operazione"
    case tapOnNegaAutorizzazione = "Tap on nega autorizzazione"
    case tapOnRicaricaAutorizzazioneKOSaldoInsufficiente = "Tap on Ricarica - Autorizzazione KO Saldo insufficiente"
    case tapOnAnnullaAutorizzazioneKOSaldoInsufficiente = "Tap on Annulla - Autorizzazione KO Saldo insufficiente"

    case none = ""
}
