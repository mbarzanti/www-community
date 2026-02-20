#nullable enable
using PagoPaSendCommon.Library.Cpx;
using PagoPaSendCommon.DAL.Models;
using PagoPaSendCommon.WS.Send.GeneratedSingle;
using System;
using System.Collections.Generic;
using System.Collections.Immutable;
using System.Linq;
using PagoPaSendCommon.Library.FP;
using PagoPaSendCommon.Library.Util;
using static PagoPaSendCommon.Library.FP.TryCatchUtils;
using static PagoPaSendCommon.Library.FP.ExceptionUtils;

namespace EPP_PagoPaSendArchiveSignPreprocessor.Mappers
{
    public static class CpxIndexesToNotificheMassive
    {
        public static Either<List<Exception>, CaricamentoMassivoViewModel> ToCaricamentoMassivo(CpxIndexes cpxIndexes, CodiceEnte codiceEnte, ImmutableDictionary<string, (string MainContentFileName, string PaymentContentFileName)> oldPdfNameToNewPdfName)
        {
            List<Exception> exceptions = new();
            string codiceFiscaleEnte = string.Empty;
            string codiceServizio = string.Empty;
            var headFieldsTry = GetHeadFields(cpxIndexes);
            headFieldsTry.Match(
                left => exceptions.AddRange(left),
                right =>
                {
                    codiceFiscaleEnte = right.CodiceFiscaleEnte;
                    codiceServizio = right.CodiceServizio;
                }
            );

            List<NotificaViewModel> notifiche = new();
            int i = 0;
            foreach (CpxIndexes.CpxIndexesRow cpxIndexesRow in cpxIndexes)
            {
                i++;
                var notificaTry = ToNotifica(cpxIndexesRow, oldPdfNameToNewPdfName);
                notificaTry.Match(
                    left => exceptions.Add(ToAggregateException($"Errore/i riga {i}: ", left)),
                    right => notifiche.Add(right)
                );
            }

            CaricamentoMassivoViewModel okResult = new()
            {
                CodiceEnte = (string)codiceEnte,
                CodiceFiscaleEnte = codiceFiscaleEnte,
                CodiceServizio = codiceServizio,
                Notifiche = notifiche.ToArray()
            };

            return ReturnExceptionsIfAny(okResult, exceptions);
        }

        private static Either<List<Exception>, (string CodiceFiscaleEnte, string CodiceServizio)> GetHeadFields(CpxIndexes cpxIndexes)
        {
            List<Exception> exceptions = new();
            int i = 0;
            List<(string CodiceFiscaleEnte, string CodiceServizio)> okCodesList = new();
            foreach (var indexesRow in cpxIndexes)
            {
                i++;
                var rowResult = ExtractHead(indexesRow);
                rowResult.Match(
                    left => exceptions.Add(ToAggregateException($"Errore/i su codiceFiscaleEnte e/o codiceServizio per la riga {i}: ", left)),
                    right => okCodesList.Add(right)
                );
            }

            if (exceptions.Any())
                return exceptions;

            var okResult = TryCatchCollectExceptionAndReturnValue(() =>
                {
                    (string CodiceFiscaleEnte, string CodiceServizio)[] distinctHeads = okCodesList.Distinct().ToArray();
                    if (distinctHeads.Count() > 1)
                        throw new InvalidOperationException("Gli indici codiceEnte, codiceFiscaleEnte e codiceServizio sono diversi tra le varie righe.");
                    return distinctHeads[0];
                },
                exceptions, (string.Empty, string.Empty)
            );

            return ReturnExceptionsIfAny(okResult, exceptions);
        }

        private static Either<List<Exception>, (string CodiceFiscaleEnte, string CodiceServizio)> ExtractHead(CpxIndexes.CpxIndexesRow row)
        {
            List<Exception> exceptions = new();
            string codiceFiscaleEnte = GetRequired(row, "codiceFiscaleEnte", exceptions);
            string codiceServizio = GetRequired(row, "codiceServizio", exceptions);
            return ReturnExceptionsIfAny((codiceFiscaleEnte, codiceServizio), exceptions);
        }

        private static Either<List<Exception>, NotificaViewModel> ToNotifica(CpxIndexes.CpxIndexesRow row, ImmutableDictionary<string, (string MainContentFileName, string PaymentContentFileName)> oldPdfNameToNewPdfName)
        {
            List<Exception> exceptions = new();
            (string mainContentFileName, string paymentContentFileName) = TryCatchCollectExceptionAndReturnValue(() =>
                {
                    if (!oldPdfNameToNewPdfName.ContainsKey(row.FileName))
                        throw new ArgumentException($"Non trovato il file '{row.FileName}' nella mappatura tra vecchi e nuovi pdf.");
                    return oldPdfNameToNewPdfName[row.FileName];
                }
                , exceptions, new(string.Empty, string.Empty));
            return ToNotifica(row, mainContentFileName, paymentContentFileName, exceptions);
        }

        private static Either<List<Exception>, NotificaViewModel> ToNotifica(CpxIndexes.CpxIndexesRow row, string mainContentFileName, string paymentContentFileName, List<Exception> exceptions)
        {
            string? tipoEmail = GetOptional(row, "tipoEmail", exceptions);
            string? email = GetOptional(row, "email", exceptions);

            TryCatchCollectExceptionAndReturnIfCollected(() =>
                {
                    if (string.IsNullOrWhiteSpace(tipoEmail) != string.IsNullOrWhiteSpace(email))
                        throw new ArgumentException("In merito a tipoEmail e email, devono essere valorizzate tutte e due o nessuna delle due.");
                }, exceptions);

            NotificaViewModel okResult = new()
            {
                Titolo = GetRequired(row, "titolo", exceptions),
                NumeroProtocollo = GetRequired(row, "numeroProtocollo", exceptions),
                TipoComunicazioneFisicaNotifica = EnumUtils.ToEnumUpperInvariant<TipoComunicazioneFisicaNotifica>(GetRequired(row, "tipoComunicazioneFisicaNotifica", exceptions), exceptions),
                CodiceTassonomico = GetOptional(row, "codiceTassonomico", exceptions),
                Allegati = ToAllegati(mainContentFileName),
                Destinatari = ToDestinatari(new DestinatarioViewModel
                {
                    CodiceFiscalePIva = GetRequired(row, "codiceFiscalePIva", exceptions),
                    Denominazione = GetRequired(row, "denominazione", exceptions),
                    Email = email,
                    TipoEmail = tipoEmail == null ? null : EnumUtils.ToEnumUpperInvariant<TipoEmail>(tipoEmail, exceptions),
                    TipoPersona = EnumUtils.ToEnumUpperInvariant<TipoPersona>(GetRequired(row, "tipoPersona", exceptions), exceptions),
                    Indirizzo = GetRequired(row, "indirizzo", exceptions),
                    DettaglioIndirizzo = GetOptional(row, "dettaglioIndirizzo", exceptions),
                    Cap = GetRequired(row, "cap", exceptions),
                    PressoIndirizzo = GetOptional(row, "pressoIndirizzo", exceptions),
                    Comune = GetRequired(row, "comune", exceptions),
                    Localita = GetOptional(row, "localita", exceptions),
                    Provincia = GetRequired(row, "provincia", exceptions),
                    StatoEstero = GetOptional(row, "statoEstero", exceptions),
                    Pagamento = ToPagamento(row, paymentContentFileName, GetOptional(row, "codiceAccertamentoSpedizione", exceptions), exceptions)
                })
            };

            return ReturnExceptionsIfAny(okResult, exceptions);
        }

        private static string? GetOptional(CpxIndexes.CpxIndexesRow row, string indexNameCaseInsensitive, List<Exception> exceptions)
        {
            return TryCatchCollectExceptionAndReturnValue(() =>
            {
                string result = row.GetValue(indexNameCaseInsensitive);
                if (string.IsNullOrWhiteSpace(result))
                    return null;
                return result;
            }
                , exceptions, string.Empty);
        }

        private static string GetRequired(CpxIndexes.CpxIndexesRow row, string indexNameCaseInsensitive, List<Exception> exceptions)
        {
            return TryCatchCollectExceptionAndReturnValue(() =>
                {
                    string result = row.GetValue(indexNameCaseInsensitive);
                    if (string.IsNullOrWhiteSpace(result))
                        throw new Exception($"E' presente l'indice per il campo '{indexNameCaseInsensitive}', ma non ha valore, quando è obbligatorio.");
                    return result;
                }
                , exceptions, string.Empty);
        }

        private static AllegatoViewModel[] ToAllegati(string filename)
        {
            List<AllegatoViewModel> allegati = new()
            {
                new AllegatoViewModel()
                {
                    NomeFile = filename
                }
            };
            return allegati.ToArray();
        }

        private static AllegatoViewModel ToAllegato(string filename)
        {
            return new AllegatoViewModel()
            {
                NomeFile = filename
            };
        }


        private static DestinatarioViewModel[] ToDestinatari(DestinatarioViewModel destinatario)
        {
            List<DestinatarioViewModel> destinatari = new()
            {
                destinatario
            };
            return destinatari.ToArray();
        }

        private static PagamentoViewModel ToPagamento(CpxIndexes.CpxIndexesRow row, string paymentsDocumenName, string? codiceAccertamentoSpedizione, List<Exception> exceptions)
        {
            var paymentsTry = CpxPaymentsExtractor.ExtractPayments(row);
            bool skipParsing = false;
            string codiceFiscalePIvaPa1 = string.Empty;
            ImmutableArray<(string NumeroAvviso, TipoPagamento TipoPagamento, int ImportoInCentesimi)> pagamenti;
            paymentsTry.Match(
                left =>
                {
                    skipParsing = true;
                    exceptions.AddRange(left);
                },
                right =>
                {
                    codiceFiscalePIvaPa1 = right.CodiceFiscalePIvaPa1;
                    pagamenti = right.Pagamenti;
                }
            );
            if (skipParsing)
                return new PagamentoViewModel();
            return TryCatchCollectExceptionAndReturnValue<PagamentoViewModel>(() =>
                {
                    if (pagamenti.Count() == 0)
                        throw new InvalidOperationException("Atteso almeno un pagamento, nessun numeroAvviso, importoInCentesimi e tipoPagamento valorizzato.");
                    (string primoNumeroAvviso, TipoPagamento primoTipoPagamento, int primoImportoInCentesimi) = pagamenti[0];

                    return new PagamentoViewModel()
                    {
                        NumeroAvviso = primoNumeroAvviso,
                        CodiceFiscalePIvaPa = codiceFiscalePIvaPa1,
                        TipoPagamento = primoTipoPagamento,
                        ImportoInCentesimi = primoImportoInCentesimi,
                        CodiceAccertamentoSpedizione = codiceAccertamentoSpedizione,
                        Allegato = ToAllegato(paymentsDocumenName),
                        PagamentiAlternativi = pagamenti
                                            .Skip(1)
                                            .Select(ToPagamentoAlternativo)
                                            .ToArray()
                    };
                }
            , exceptions, new PagamentoViewModel());
        }

        private static PagamentoAlternativoViewModel ToPagamentoAlternativo((string NumeroAvviso, TipoPagamento TipoPagamento, int ImportoInCentesimi) datiPagamento)
        {
            return new()
            {
                NumeroAvviso = datiPagamento.NumeroAvviso,
                TipoPagamento = datiPagamento.TipoPagamento,
                ImportoInCentesimi = datiPagamento.ImportoInCentesimi
            };
        }
    }
}
