Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_FT_IND_MONTH_PMR',Current_date);
Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_FT_IND_WEEK_PMR',Current_date);

CREATE OR REPLACE PROCEDURE MON_DATAMART.INSERT_FT_IND_MONTH_PMR()
       LANGUAGE sqlscript SQL security invoker DEFAULT SCHEMA MON_DATAMART as
begin

declare var_esito_proc     char(2);
declare var_ricalcolo_proc char(2);
declare var_ParInput       varchar(100);
declare var_nextParInput   varchar(100);
declare var_DataMin        date;
declare var_DataMax        date;
declare var_WeekMin        char(8);
declare var_WeekMax        char(8);

-- FILTRI LOG_CARICAMENTO
declare var_SERVIZIO   varchar(50) = 'IND';
declare var_PROGR_PROC varchar(3)  = '034';
declare var_CODICE_DM  varchar(3)  = '107';


select ESITO_PROC,     RICALCOLO_PROC,     case when RICALCOLO_PROC = 'NO'
                                                then PAR_INPUT
                                                when RICALCOLO_PROC = 'SI'
                                                 and PAR_INPUT_RIC < PAR_INPUT
                                                then PAR_INPUT_RIC
                                           end
  into var_esito_proc, var_ricalcolo_proc, var_ParInput
  from MON_DATAMART.LOG_CARICAMENTO
 where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM;


-- Aggiornamento dati in tabella LOG
update MON_DATAMART.LOG_CARICAMENTO
   set ESITO_PROC = 'KO',
       DATA_AGG = current_timestamp
 where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'OK';


if (:var_esito_proc = 'OK' and             --> esecuzione precedente OK
    (:var_ricalcolo_proc = 'SI' OR :var_ricalcolo_proc = 'NO') and --> valore ricalcolo SI/NO
    :var_ParInput is not null              --> data di partenza OK
) then
	
	-- selezione range di date di cui calcolare i volumi
   select min(DATE_SQL), max(DATE_SQL)
     into var_DataMin,   var_DataMax
     from MON_DATAMART.ANAG_CALENDAR
    where CALMONTH_PCL = :var_ParInput;
			 
   var_tgc = 
      select t.CODUFFICIOC_RITORNO	    						as CD_RITORNO
          ,t.WEEK_ISO                               			as WEEK_ISO
          ,t.CODLINEA                               			as CODLINEA
          ,t.CODPRODOTTO                            			as CODPRODOTTO
          ,t.CODSERVIZIO                            			as CODSERVIZIO
		  ,sum(case when T.CODESITOCIVICO_RITORNO = 'IC' then 1 else 0 end) as CONSEGNATI_PMR
		  ,sum(case when T.CODESITOCIVICO_RITORNO = 'IC' and T.VOLCLAVORATI1EC_J_R <= 4 then 1 else 0 end) as CONSEGNATI_PMR_IN_SLA		  
    from (
          select distinct
                 cal.CALWEEK                                   as WEEK_ISO,
                 ifNull(p.CODLINEAPRODOTTORENDICONTAZIONE, '') as CODLINEA,
                 ifNull(p.CODPRODOTTO, '')                     as CODPRODOTTO,
                 ifNull(p.SERVIZIACCESSORI, '')                as CODSERVIZIO,
                 i.* --> per schiacciare sulla chiave...
            from MON_DATAMART.IND_MONSPED_P p
           inner join MON_DATAMART.IND_MONSPED_PINV_DETTAGLIO i
              on p.idp = i.idp
           inner join (select distinct CODLINEA, CODPRODOTTO, CODSERVIZIO
                         from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO
                        where DM = 'Ritono PMR Indescritta'
                          and CODLINEA    is not null
                          and CODPRODOTTO is not null
                          and CODSERVIZIO is not null) m
              on ((m.CODLINEA = '')    or (m.CODLINEA    = ifNull(p.CODLINEAPRODOTTORENDICONTAZIONE ,'')))
             and ((m.CODPRODOTTO = '') or (m.CODPRODOTTO = ifNull(p.CODPRODOTTO,'')))
             and ((m.CODSERVIZIO = '') or (m.CODSERVIZIO = ifNull(p.SERVIZIACCESSORI,'')))
           inner join MON_DATAMART.ANAG_CALENDAR cal
              on i.DTSTART_RITORNO = cal.DATE_SQL
           where i.DTSTART_RITORNO between :var_DataMin and :var_DataMax
		         and i.CODESITOCIVICO = 'IN'
				 and nullif(i.CODUFFICIOC_RITORNO,'') is not null
				 and p.STATOACCETTAZIONE = 'S'
     ) t
    group by t.CODUFFICIOC_RITORNO,
             t.WEEK_ISO,
             t.CODLINEA,
             t.CODPRODOTTO,
             t.CODSERVIZIO
			;
	
	if (:var_ricalcolo_proc = 'SI') then

       select min(CALWEEK), max(CALWEEK)
        into var_WeekMin, var_WeekMax
        from MON_DATAMART.ANAG_CALENDAR
       where CALMONTH_PCL = :var_ParInput;
	   
	   delete from MON_DATAMART.FT_IND_MONTH_PMR where WEEK_ISO between :var_WeekMin and :var_WeekMax;
	   
	end if;
	  
   -- TGC
   insert into MON_DATAMART.FT_IND_MONTH_PMR (
   CD_RITORNO					
   ,WEEK_ISO 				
   ,CODLINEA 				
   ,CODPRODOTTO 			  
   ,CODSERVIZIO 			
   ,CONSEGNATI_PMR			
   ,CONSEGNATI_PMR_IN_SLA	
   )
   select CD_RITORNO					
		  ,WEEK_ISO 				
		  ,CODLINEA 				
		  ,CODPRODOTTO 			  
		  ,CODSERVIZIO 			
		  ,CONSEGNATI_PMR			
		  ,CONSEGNATI_PMR_IN_SLA
   from :var_tgc;

   if (:var_ricalcolo_proc = 'NO') then

      select min(CALMONTH)
        into var_nextParInput
        from MON_DATAMART.ANAG_CALENDAR
       where CALMONTH > :var_ParInput;

      update MON_DATAMART.LOG_CARICAMENTO
         set PAR_INPUT = :var_nextParInput,
             ESITO_PROC = 'OK',
             DATA_AGG = current_timestamp
         where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';

   else
      update MON_DATAMART.LOG_CARICAMENTO
         set ESITO_PROC = 'OK',
             RICALCOLO_PROC = 'NO',
             NOTE_RICALCOLO = concat('Ultima elaborazione mese: ', :var_ParInput),
             PAR_INPUT_RIC = null,
             DATA_AGG = current_timestamp
       where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';
   end if;

end if;

if (:var_ParInput is null or :var_ricalcolo_proc is null) then

   update MON_DATAMART.LOG_CARICAMENTO
      set NOTE_RICALCOLO = 'Mese ricalcolo errato',
          DATA_AGG = current_timestamp
    where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';

end if;

call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);

end;

CREATE OR REPLACE PROCEDURE MON_DATAMART.INSERT_FT_IND_WEEK_PMR()
       LANGUAGE sqlscript SQL security invoker DEFAULT SCHEMA MON_DATAMART as
begin

declare var_esito_proc     char(2);
declare var_ricalcolo_proc char(2);
declare var_ParInput       varchar(100);
declare var_nextParInput   varchar(100);
declare var_DataMin        date;
declare var_DataMax        date;

-- FILTRI LOG_CARICAMENTO
declare var_SERVIZIO   varchar(50) = 'IND';
declare var_PROGR_PROC varchar(3)  = '033';
declare var_CODICE_DM  varchar(3)  = '107';


select ESITO_PROC,     RICALCOLO_PROC,     case when RICALCOLO_PROC = 'NO'
                                                then PAR_INPUT
                                                when RICALCOLO_PROC = 'SI'
                                                 and PAR_INPUT_RIC < PAR_INPUT
                                                then PAR_INPUT_RIC
                                           end
  into var_esito_proc, var_ricalcolo_proc, var_ParInput
  from MON_DATAMART.LOG_CARICAMENTO
 where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM;


-- Aggiornamento dati in tabella LOG
update MON_DATAMART.LOG_CARICAMENTO
   set ESITO_PROC = 'KO',
       DATA_AGG = current_timestamp
 where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'OK';


if (:var_esito_proc = 'OK' and             --> esecuzione precedente OK
    (:var_ricalcolo_proc = 'SI' OR :var_ricalcolo_proc = 'NO') and --> valore ricalcolo SI/NO
    :var_ParInput is not null              --> data di partenza OK
) then

	-- selezione range di date di cui calcolare i volumi - tutte le settimane del mese
	 select min(DATE_SQL), max(DATE_SQL)
     into var_DataMin,   var_DataMax
     from MON_DATAMART.ANAG_CALENDAR
    where CALWEEK IN 
    (SELECT CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
    WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK= :var_ParInput)
    AND CALWEEK<= :var_ParInput);
			 
   var_tgc = 
      select t.CODUFFICIOC_RITORNO	    						as CD_RITORNO
          ,t.WEEK_ISO                               			as WEEK_ISO
          ,t.CODLINEA                               			as CODLINEA
          ,t.CODPRODOTTO                            			as CODPRODOTTO
          ,t.CODSERVIZIO                            			as CODSERVIZIO
		  ,sum(case when T.CODESITOCIVICO_RITORNO = 'IC' then 1 else 0 end) as CONSEGNATI_PMR
		  ,sum(case when T.CODESITOCIVICO_RITORNO = 'IC' and T.VOLCLAVORATI1EC_J_R <= 4 then 1 else 0 end) as CONSEGNATI_PMR_IN_SLA		  
    from (
          select distinct
                 cal.CALWEEK                                   as WEEK_ISO,
                 ifNull(p.CODLINEAPRODOTTORENDICONTAZIONE, '') as CODLINEA,
                 ifNull(p.CODPRODOTTO, '')                     as CODPRODOTTO,
                 ifNull(p.SERVIZIACCESSORI, '')                as CODSERVIZIO,
                 i.* --> per schiacciare sulla chiave...
            from MON_DATAMART.IND_MONSPED_P p
           inner join MON_DATAMART.IND_MONSPED_PINV_DETTAGLIO i
              on p.idp = i.idp
           inner join (select distinct CODLINEA, CODPRODOTTO, CODSERVIZIO
                         from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO
                        where DM = 'Ritono PMR Indescritta'
                          and CODLINEA    is not null
                          and CODPRODOTTO is not null
                          and CODSERVIZIO is not null) m
              on ((m.CODLINEA = '')    or (m.CODLINEA    = ifNull(p.CODLINEAPRODOTTORENDICONTAZIONE ,'')))
             and ((m.CODPRODOTTO = '') or (m.CODPRODOTTO = ifNull(p.CODPRODOTTO,'')))
             and ((m.CODSERVIZIO = '') or (m.CODSERVIZIO = ifNull(p.SERVIZIACCESSORI,'')))
           inner join MON_DATAMART.ANAG_CALENDAR cal
              on i.DTSTART_RITORNO = cal.DATE_SQL
           where i.DTSTART_RITORNO between :var_DataMin and :var_DataMax
		         and i.CODESITOCIVICO = 'IN'
				 and nullif(i.CODUFFICIOC_RITORNO,'') is not null
				 and p.STATOACCETTAZIONE = 'S'
     ) t
    group by t.CODUFFICIOC_RITORNO,
             t.WEEK_ISO,
             t.CODLINEA,
             t.CODPRODOTTO,
             t.CODSERVIZIO
			;
   delete from MON_DATAMART.FT_IND_WEEK_PMR  where WEEK_ISO in (SELECT distinct CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
			WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK=:var_ParInput)
			AND CALWEEK<=:var_ParInput);
	  
   -- TGC
   insert into MON_DATAMART.FT_IND_WEEK_PMR (
   CD_RITORNO					
   ,WEEK_ISO 				
   ,CODLINEA 				
   ,CODPRODOTTO 			  
   ,CODSERVIZIO 			
   ,CONSEGNATI_PMR			
   ,CONSEGNATI_PMR_IN_SLA	
   )
   select CD_RITORNO					
		  ,WEEK_ISO 				
		  ,CODLINEA 				
		  ,CODPRODOTTO 			  
		  ,CODSERVIZIO 			
		  ,CONSEGNATI_PMR			
		  ,CONSEGNATI_PMR_IN_SLA
   from :var_tgc;

   if (:var_ricalcolo_proc = 'NO') then

      select min(CALWEEK)
        into var_nextParInput
        from MON_DATAMART.ANAG_CALENDAR
       where CALWEEK > :var_ParInput;

      update MON_DATAMART.LOG_CARICAMENTO
         set PAR_INPUT = :var_nextParInput,
             ESITO_PROC = 'OK',
             DATA_AGG = current_timestamp
         where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';

   else
      update MON_DATAMART.LOG_CARICAMENTO
         set ESITO_PROC = 'OK',
             RICALCOLO_PROC = 'NO',
             NOTE_RICALCOLO = concat('Ultima elaborazione settimana: ', :var_ParInput),
             PAR_INPUT_RIC = null,
             DATA_AGG = current_timestamp
       where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';
   end if;

end if;

if (:var_ParInput is null or :var_ricalcolo_proc is null) then

   update MON_DATAMART.LOG_CARICAMENTO
      set NOTE_RICALCOLO = 'Settimana ricalcolo errata',
          DATA_AGG = current_timestamp
    where SERVIZIO = :var_SERVIZIO and PROGR_PROC = :var_PROGR_PROC and CODICE_DM = :var_CODICE_DM and ESITO_PROC = 'KO';

end if;

call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);

end;