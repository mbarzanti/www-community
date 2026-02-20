Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_PCL433_MM',Current_date);
Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_PCL433_WW',Current_date);
Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_PCL434_MM',Current_date);
Insert into MON_CHK.VEI_DETTAGLIO_OGGETTI_INIZIATIVA values ('124456','MON_DATAMART','Procedura','INSERT_PCL434_WW',Current_date);

create procedure MON_DATAMART.INSERT_PCL433_MM (
       month_extract varchar(6),
       tipoEsecuzione varchar(2)
) language sqlscript sql security invoker default schema MON_DATAMART as
begin

declare var_month_extract    varchar(6);
declare var_nextMese_extract varchar(6);
declare var_minWeek          varchar(6);
declare var_maxWeek          varchar(6);
declare var_last_day_pcl     date;
declare var_data_agg		 timestamp   := current_timestamp;
declare var_codice_flusso    varchar(10) := 'PCL_433_MM';

var_month_extract = :month_extract;

select min(CALWEEK), max(CALWEEK), max(DATE_SQL)
  into var_minWeek,  var_maxWeek,  var_last_day_pcl
  from MON_DATAMART.ANAG_CALENDAR
 where CALMONTH_PCL = :var_month_extract;
	
delete from MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND where kpi = :var_codice_flusso and MESE_ISO <= :var_month_extract;

var_frazionario =
select a.OFFICEID,
       a.FRAZIONARIO_PADRE,
       a.TIPO_FRAZIONARIO,
       a.COMPETENZA,
       a.ALT || ' ' || a.RAM as RAM,
       a.ALT,
       a.MAL
  from MON_DATAMART.ANAG_FRAZIONARIO a
 where to_date(var_last_day_pcl) between a.DATA_INIZIO_VALIDITA and a.DATA_FINE_VALIDITA;


var_dati =
select a.WEEK_ISO,
       fraz.OFFICEID,
       fraz.FRAZIONARIO_PADRE,
       fraz.TIPO_FRAZIONARIO,
       fraz.COMPETENZA,
       fraz.RAM,
       fraz.ALT,
       fraz.MAL,
       m.MACROPRODOTTO,
       sum (a.VOLINTERCETTATI) as QTA_DENOMINATORE,
       sum (a.VOLC + a.VOLINESITATI) as QTA_NUMERATORE
  from MON_DATAMART.FT_IND_MONTH_ACCETTAZIONE a
 inner join (select distinct MACROPRODOTTO, CODLINEA, CODPRODOTTO, CODSERVIZIO
               from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO m
              where CODICE_FLUSSO = substr(:var_codice_flusso,0,7)
                and CODLINEA    is not null
                and CODPRODOTTO is not null
                and CODSERVIZIO is not null) m
    on (( m.CODLINEA =    '') or (a.CODLINEA    = m.CODLINEA))
   and (( m.CODSERVIZIO = '') or (a.CODSERVIZIO = m.CODSERVIZIO))
   and (( m.CODPRODOTTO = '') or (a.CODPRODOTTO = m.CODPRODOTTO))
 left outer join :var_frazionario as fraz
    on fraz.OFFICEID = a.CD_BOF
 where a.WEEK_ISO between :var_minWeek and :var_maxWeek
	and a.AMBITO   = 'TGC'
 group by a.WEEK_ISO,
          fraz.OFFICEID,
          fraz.FRAZIONARIO_PADRE,
          fraz.TIPO_FRAZIONARIO,
          fraz.COMPETENZA,
          fraz.RAM,
          fraz.ALT,
          fraz.MAL,
          m.MACROPRODOTTO;

---------------- Dati relativi ai CD ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       substr(:var_month_extract,5,6) AS MESE_ISO,
       MACROPRODOTTO,
	   OFFICEID AS LOGISTICA,
	   :var_codice_flusso AS KPI,
       QTA_NUMERATORE   AS NUM,
	   QTA_DENOMINATORE AS DEN
  from :var_dati
 where COMPETENZA = 'SERVIZI POSTALI - CD' and 
	   OFFICEID is not null;

---------------- Dati aggregati per RAM ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       substr(:var_month_extract,5,6) as MESE_ISO,
       MACROPRODOTTO,
	   RAM as LOGISTICA,
	   :var_codice_flusso AS KPI,
	   sum(QTA_NUMERATORE)   as NUM,
       sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where RAM is not null
   and COMPETENZA = 'SERVIZI POSTALI - CD'
 group by WEEK_ISO,
          RAM,
          MACROPRODOTTO;

---------------- Dati aggregati per ALT ----------------   

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       substr(:var_month_extract,5,6) as MESE_ISO,
       MACROPRODOTTO,
	   ALT as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
	   sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where ALT is not null
 group by WEEK_ISO,
          ALT,
          MACROPRODOTTO;

---------------- Dati aggregati per MAL ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       substr(:var_month_extract,5,6) as MESE_ISO,
       MACROPRODOTTO,
	   MAL as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
	   sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where MAL is not null
 group by WEEK_ISO,
          MAL,
          MACROPRODOTTO;

---------------- Dati aggregati per NAZ ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       substr(:var_month_extract,5,6) as MESE_ISO,
       MACROPRODOTTO,
	   'NAZIONALE' as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
       sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 group by WEEK_ISO,
          MACROPRODOTTO;

---------------- Fine Calcoli Scheda Flusso ----------------

select min(CALMONTH_PCL)
  into var_nextMese_extract
  from MON_DATAMART.ANAG_CALENDAR
 where CALMONTH_PCL > :var_month_extract;

if (:tipoEsecuzione = 'S') then
   insert into MON_DATAMART.TABLEAU_ESECUZIONE_FLUSSO
   select substr(:var_codice_flusso,0,7)    as FLUSSO,
          '1900-01-01'          as DATA_ESECUZIONE,
          ::CURRENT_OBJECT_NAME as NOME_PROCEDURA,
          :var_nextMese_extract as PARA_INPUT,
          0                     as ESITO,
          current_date          as DATA_AGG,
          'MENSILE'             as FREQUENZA,
          :tipoEsecuzione       as TIPO_ESECUZIONE
     from dummy;
	
end if;

	update MON_DATAMART.PCL_CONF_SEMAFORO
    set LAST_DATA_WRITE = :var_data_agg
    where NOME_FLUSSO = :var_codice_flusso;

call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);

end;

create procedure MON_DATAMART.INSERT_PCL433_WW (
       week_extract varchar(6),
       tipoEsecuzione varchar(2)
) language sqlscript sql security invoker default schema MON_DATAMART as
begin

declare var_week_extract     varchar(6);
declare var_nextWeek_extract varchar(6);
declare var_mese_iso         varchar(2);
declare var_data_agg		 timestamp   := current_timestamp;
declare var_codice_flusso    varchar(10) := 'PCL_433_WW';

var_week_extract = :week_extract;

select distinct MONTH_OF_WEEK_PCL
  into var_mese_iso 
  from MON_DATAMART.ANAG_CALENDAR
    where CALWEEK IN 
    (SELECT CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
    WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK= :var_week_extract)
    AND CALWEEK<= :var_week_extract);
	
delete from MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND where kpi = :var_codice_flusso and MESE_ISO <= :var_mese_iso;

var_frazionario =
select a.OFFICEID,
       a.FRAZIONARIO_PADRE,
       a.TIPO_FRAZIONARIO,
       a.COMPETENZA,
       a.ALT || ' ' || a.RAM as RAM,
       a.ALT,
       a.MAL
  from MON_DATAMART.VIEW_ANAG_FRAZ_TABLEAU a;


var_dati =
select a.WEEK_ISO,
       fraz.OFFICEID,
       fraz.FRAZIONARIO_PADRE,
       fraz.TIPO_FRAZIONARIO,
       fraz.COMPETENZA,
       fraz.RAM,
       fraz.ALT,
       fraz.MAL,
       m.MACROPRODOTTO,
       sum (a.VOLINTERCETTATI) as QTA_DENOMINATORE,
       sum (a.VOLC + a.VOLINESITATI) as QTA_NUMERATORE
  from MON_DATAMART.FT_IND_WEEK_ACCETTAZIONE a
 inner join (select distinct MACROPRODOTTO, CODLINEA, CODPRODOTTO, CODSERVIZIO
               from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO m
              where CODICE_FLUSSO = substr(:var_codice_flusso,0,7)
                and CODLINEA    is not null
                and CODPRODOTTO is not null
                and CODSERVIZIO is not null) m
    on (( m.CODLINEA =    '') or (a.CODLINEA    = m.CODLINEA))
   and (( m.CODSERVIZIO = '') or (a.CODSERVIZIO = m.CODSERVIZIO))
   and (( m.CODPRODOTTO = '') or (a.CODPRODOTTO = m.CODPRODOTTO))
 left outer join :var_frazionario as fraz
    on fraz.OFFICEID = a.CD_BOF
 where a.WEEK_ISO  IN 
    (SELECT CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
    WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK= :var_week_extract)
    AND CALWEEK<= :var_week_extract)
	and a.AMBITO   = 'TGC'
 group by a.WEEK_ISO,
          fraz.OFFICEID,
          fraz.FRAZIONARIO_PADRE,
          fraz.TIPO_FRAZIONARIO,
          fraz.COMPETENZA,
          fraz.RAM,
          fraz.ALT,
          fraz.MAL,
          m.MACROPRODOTTO;

---------------- Dati relativi ai CD ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       :var_mese_iso AS MESE_ISO,
       MACROPRODOTTO,
	   OFFICEID AS LOGISTICA,
	   :var_codice_flusso AS KPI,
       QTA_NUMERATORE   AS NUM,
	   QTA_DENOMINATORE AS DEN
  from :var_dati
 where COMPETENZA = 'SERVIZI POSTALI - CD' and 
	   OFFICEID is not null;

---------------- Dati aggregati per RAM ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       :var_MESE_ISO as MESE_ISO,
       MACROPRODOTTO,
	   RAM as LOGISTICA,
	   :var_codice_flusso AS KPI,
	   sum(QTA_NUMERATORE)   as NUM,
       sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where RAM is not null
   and COMPETENZA = 'SERVIZI POSTALI - CD'
 group by WEEK_ISO,
          RAM,
          MACROPRODOTTO;

---------------- Dati aggregati per ALT ----------------   

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       :var_MESE_ISO as MESE_ISO,
       MACROPRODOTTO,
	   ALT as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
	   sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where ALT is not null
 group by WEEK_ISO,
          ALT,
          MACROPRODOTTO;

---------------- Dati aggregati per MAL ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       :var_MESE_ISO as MESE_ISO,
       MACROPRODOTTO,
	   MAL as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
	   sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 where MAL is not null
 group by WEEK_ISO,
          MAL,
          MACROPRODOTTO;

---------------- Dati aggregati per NAZ ----------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
       SETTIMANA_ISO,
       MESE_ISO,
       MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
select :var_data_agg AS DATA_AGG,
	   WEEK_ISO AS SETTIMANA_ISO,
       :var_MESE_ISO as MESE_ISO,
       MACROPRODOTTO,
	   'NAZIONALE' as LOGISTICA,
	   :var_codice_flusso AS KPI,
       sum(QTA_NUMERATORE)   as NUM,
       sum(QTA_DENOMINATORE) as DEN
  from :var_dati
 group by WEEK_ISO,
          MACROPRODOTTO;

select min(CALWEEK)
  into var_nextWeek_extract
  from MON_DATAMART.ANAG_CALENDAR
 where CALWEEK > :var_week_extract;

if (:tipoEsecuzione='S') then

   insert into MON_DATAMART.TABLEAU_ESECUZIONE_FLUSSO
   select substr(:var_codice_flusso,0,7)    as FLUSSO,
          '1900-01-01'          as DATA_ESECUZIONE,
          ::CURRENT_OBJECT_NAME as NOME_PROCEDURA,
          :var_nextWeek_extract as PARA_INPUT,
          0                     as ESITO,
          current_date          as DATA_AGG,
          'SETTIMANALE'         as FREQUENZA,
          :tipoEsecuzione       as TIPO_ESECUZIONE
     from dummy;
	
end if;

	update MON_DATAMART.PCL_CONF_SEMAFORO
    set LAST_DATA_WRITE = :var_data_agg
    where NOME_FLUSSO = :var_codice_flusso;

call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);

end;

create procedure MON_DATAMART.INSERT_PCL434_MM (
       month_extract varchar(6),
       tipoEsecuzione varchar(2)
) language sqlscript sql security invoker default schema MON_DATAMART as
begin

declare var_month_extract    varchar(6);
declare var_nextMese_extract varchar(6);
declare var_minWeek          varchar(6);
declare var_maxWeek          varchar(6);
declare var_last_day_pcl     date;
declare var_data_agg		 timestamp   := current_timestamp;
declare var_codice_flusso    varchar(10) = 'PCL_434_MM';

var_month_extract = :month_extract;

select min(CALWEEK), max(CALWEEK), max(DATE_SQL)
  into var_minWeek,  var_maxWeek,  var_last_day_pcl
  from MON_DATAMART.ANAG_CALENDAR
 where CALMONTH_PCL = :var_month_extract;
	
delete from MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND where kpi = :var_codice_flusso and MESE_ISO <= :var_month_extract;

var_frazionario =
select a.OFFICEID,
       a.FRAZIONARIO_PADRE,
       a.TIPO_FRAZIONARIO,
       a.COMPETENZA,
       a.ALT || ' ' || a.RAM as RAM,
       a.ALT,
       a.MAL
  from MON_DATAMART.ANAG_FRAZIONARIO a
 where to_date(var_last_day_pcl) between a.DATA_INIZIO_VALIDITA and a.DATA_FINE_VALIDITA;

var_dati =	select a.WEEK_ISO
				   ,fraz.OFFICEID
				   ,fraz.FRAZIONARIO_PADRE
				   ,fraz.TIPO_FRAZIONARIO
				   ,fraz.COMPETENZA
				   ,fraz.RAM
				   ,fraz.ALT
				   ,fraz.MAL
				   ,m.MACROPRODOTTO
                   ,sum(a.CONSEGNATI_PMR) as QTA_DENOMINATORE
                   ,sum(a.CONSEGNATI_PMR_IN_SLA) as QTA_NUMERATORE
			from MON_DATAMART.FT_IND_MONTH_PMR as a 
			inner join (select distinct MACROPRODOTTO, CODLINEA, CODPRODOTTO, CODSERVIZIO
									from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO m
									where CODICE_FLUSSO = SUBSTR(:var_codice_flusso,0,7)
										and CODLINEA    is not null
										and CODPRODOTTO is not null
										and CODSERVIZIO is not null) m
				on (( m.CODLINEA =    '') or (a.CODLINEA    = m.CODLINEA))
					and (( m.CODSERVIZIO = '') or (a.CODSERVIZIO = m.CODSERVIZIO))
					and (( m.CODPRODOTTO = '') or (a.CODPRODOTTO = m.CODPRODOTTO))
			inner join :var_frazionario as fraz
				on fraz.OFFICEID = a.CD_RITORNO
			where a.WEEK_ISO between :var_minWeek and :var_maxWeek
			group by a.WEEK_ISO
                     ,fraz.OFFICEID
                     ,fraz.FRAZIONARIO_PADRE
                     ,fraz.TIPO_FRAZIONARIO
                     ,fraz.COMPETENZA
                     ,fraz.RAM
                     ,fraz.ALT
                     ,fraz.MAL
                     ,m.MACROPRODOTTO;

---------------- Dati relativi ai CD ----------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
	select :var_data_agg 					as DATA_AGG
			,WEEK_ISO
			,substr(:var_month_extract,5,6) as MESE_ISO
			,MACROPRODOTTO
			,OFFICEID 						as LOGISTICA
			,:var_codice_flusso 			as KPI
			,QTA_NUMERATORE 				as NUM
			,QTA_DENOMINATORE 				as DEM
	from :var_dati
	where COMPETENZA = 'SERVIZI POSTALI - CD';    

---------------- Dati aggregati per RAM -------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg 					as DATA_AGG
            ,WEEK_ISO
            ,substr(:var_month_extract,5,6) as MESE_ISO
            ,MACROPRODOTTO
			,RAM 							as LOGISTICA
			,:var_codice_flusso 			as KPI
            ,sum(QTA_NUMERATORE) 			as NUM
			,sum(QTA_DENOMINATORE) 			as DEN
         from :var_dati
        where RAM is not null and 
			  COMPETENZA = 'SERVIZI POSTALI - CD'
        group by WEEK_ISO
				 ,RAM
				 ,MACROPRODOTTO;

---------------- Dati aggregati per ALT --------------------   

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg 					as DATA_AGG
            ,WEEK_ISO
            ,substr(:var_month_extract,5,6) as MESE_ISO
            ,MACROPRODOTTO
			,ALT 							as LOGISTICA
			,:var_codice_flusso 			as KPI
            ,sum(QTA_NUMERATORE) 			as NUM
			,sum(QTA_DENOMINATORE) 			as DEN
         from :var_dati
        where ALT is not null
        group by WEEK_ISO
				 ,ALT
				 ,MACROPRODOTTO;

---------------- Dati aggregati per MAL --------------------    

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg 					as DATA_AGG
            ,WEEK_ISO
            ,substr(:var_month_extract,5,6) as MESE_ISO
            ,MACROPRODOTTO
			,MAL 							as LOGISTICA
			,:var_codice_flusso 			as KPI
            ,sum(QTA_NUMERATORE) 			as NUM
			,sum(QTA_DENOMINATORE) 			as DEN
         from :var_dati
        where MAL is not null
        group by WEEK_ISO
				 ,MAL
				 ,MACROPRODOTTO;

---------------- Dati aggregati per NAZ --------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg 					as DATA_AGG
            ,WEEK_ISO
            ,substr(:var_month_extract,5,6) as MESE_ISO
            ,MACROPRODOTTO
			,'NAZIONALE' 					as LOGISTICA
			,:var_codice_flusso 			as KPI
            ,sum(QTA_NUMERATORE) 			as NUM
			,sum(QTA_DENOMINATORE) 			as DEN
         from :var_dati
        group by WEEK_ISO
				 ,MACROPRODOTTO;

---------------- Fine Calcoli Scheda Flusso ----------------

select min(CALMONTH_PCL)
  into var_nextMese_extract
  from MON_DATAMART.ANAG_CALENDAR
 where CALMONTH_PCL > :var_month_extract;

if (:tipoEsecuzione = 'S') then

   insert into MON_DATAMART.TABLEAU_ESECUZIONE_FLUSSO
   select substr(:var_codice_flusso,0,7)    as FLUSSO,
          '1900-01-01'          as DATA_ESECUZIONE,
          ::CURRENT_OBJECT_NAME as NOME_PROCEDURA,
          :var_nextMese_extract as PARA_INPUT,
          0                     as ESITO,
          current_date          as DATA_AGG,
          'MENSILE'             as FREQUENZA,
          :tipoEsecuzione       as TIPO_ESECUZIONE
     from dummy;
	
end if;

	update MON_DATAMART.PCL_CONF_SEMAFORO
    set LAST_DATA_WRITE = :var_data_agg
    where NOME_FLUSSO = :var_codice_flusso;
    
call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);
    
end;

create procedure MON_DATAMART.INSERT_PCL434_WW
      (week_extract varchar(6), tipoEsecuzione varchar(2)) 
 language sqlscript sql security invoker default schema MON_DATAMART as
begin

declare var_week_extract 		varchar(6);
declare var_nextWeek_extract 	varchar(6);
declare var_mese_iso 			varchar(2);
declare var_data_agg		 	timestamp 	:= current_timestamp;
declare var_codice_flusso    	varchar(10) := 'PCL_434_WW';

var_week_extract = :week_extract;
	
    select distinct MONTH_OF_WEEK_PCL 
        into var_mese_iso 
        from MON_DATAMART.ANAG_CALENDAR
--where CALWEEK = :var_week_extract;
    where CALWEEK IN 
    (SELECT CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
    WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK= :var_week_extract)
    AND CALWEEK<= :var_week_extract);

delete from MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND where kpi = :var_codice_flusso  and MESE_ISO <= :var_mese_iso;
    
    var_frazionario =   select
                            a.OFFICEID,
                            a.FRAZIONARIO_PADRE,
                            a.TIPO_FRAZIONARIO,
                            a.COMPETENZA,
                            a.ALT || ' ' || a.RAM as RAM,
                            a.ALT,
                            a.MAL                         
                       from MON_DATAMART.VIEW_ANAG_FRAZ_TABLEAU a;

    var_dati =      select 
                            a.WEEK_ISO,
                            fraz.OFFICEID,
                            fraz.FRAZIONARIO_PADRE,
                            fraz.TIPO_FRAZIONARIO,
                            fraz.COMPETENZA,
                            fraz.RAM,
                            fraz.ALT,
                            fraz.MAL,
                            m.MACROPRODOTTO,
                    sum(a.CONSEGNATI_PMR) as QTA_DENOMINATORE,
                    sum(a.CONSEGNATI_PMR_IN_SLA) as QTA_NUMERATORE
                        from MON_DATAMART.FT_IND_WEEK_PMR as a 
                inner join (select distinct MACROPRODOTTO, CODLINEA, CODPRODOTTO, CODSERVIZIO
									from MON_DATAMART.IND_PCL_ANAG_MACROPRODOTTO m
									where CODICE_FLUSSO = substr(:var_codice_flusso,0,7)
										and CODLINEA    is not null
										and CODPRODOTTO is not null
										and CODSERVIZIO is not null) m
                 on (( m.CODLINEA =    '') or (a.CODLINEA    = m.CODLINEA))
                and (( m.CODSERVIZIO = '') or (a.CODSERVIZIO = m.CODSERVIZIO))
                and (( m.CODPRODOTTO = '') or (a.CODPRODOTTO = m.CODPRODOTTO))
              inner join :var_frazionario as fraz
                 on fraz.OFFICEID = a.CD_RITORNO
				 where a.WEEK_ISO  IN 
					(SELECT CALWEEK FROM MON_DATAMART.ANAG_CALENDAR 
					WHERE CALMONTH_PCL=(SELECT MAX(CALMONTH_PCL) FROM MON_DATAMART.ANAG_CALENDAR WHERE CALWEEK= :var_week_extract)
					AND CALWEEK<= :var_week_extract)
              group by a.WEEK_ISO,
                       fraz.OFFICEID,
                       fraz.FRAZIONARIO_PADRE,
                       fraz.TIPO_FRAZIONARIO,
                       fraz.COMPETENZA,
                       fraz.RAM,
                       fraz.ALT,
                       fraz.MAL,
                       m.MACROPRODOTTO;

---------------- Dati relativi ai CD ----------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg AS DATA_AGG,
             WEEK_ISO,
            :var_mese_iso as MESE_ISO,
            MACROPRODOTTO,
			OFFICEID as LOGISTICA,
			:var_codice_flusso AS KPI,
            QTA_NUMERATORE as NUM,
			QTA_DENOMINATORE as DEM
         from
            :var_dati
         where COMPETENZA = 'SERVIZI POSTALI - CD';    

---------------- Dati aggregati per RAM -------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg AS DATA_AGG,
             WEEK_ISO,
            :var_mese_iso as MESE_ISO,
            MACROPRODOTTO,
			RAM as LOGISTICA,
			:var_codice_flusso AS KPI,
            sum(QTA_NUMERATORE) as NUM,
			sum(QTA_DENOMINATORE) as DEN
         from
            :var_dati
        where
            RAM is not null
        and COMPETENZA = 'SERVIZI POSTALI - CD'
        group by
            WEEK_ISO,
            RAM,
            MACROPRODOTTO;

---------------- Dati aggregati per ALT --------------------   

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg AS DATA_AGG,
             WEEK_ISO,
            :var_mese_iso as MESE_ISO,
            MACROPRODOTTO,
			ALT as LOGISTICA,
			:var_codice_flusso AS KPI,
            sum(QTA_NUMERATORE) as NUM,
			sum(QTA_DENOMINATORE) as DEN
         from
            :var_dati
        where
            ALT is not null
        group by
            WEEK_ISO,
            ALT,
            MACROPRODOTTO;

---------------- Dati aggregati per MAL --------------------    

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg AS DATA_AGG,
             WEEK_ISO,
            :var_mese_iso as MESE_ISO,
            MACROPRODOTTO,
			MAL as LOGISTICA,
			:var_codice_flusso AS KPI,
            sum(QTA_NUMERATORE) as NUM,
			sum(QTA_DENOMINATORE) as DEN
         from
            :var_dati
        where
            MAL is not null
        group by
            WEEK_ISO,
            MAL,
            MACROPRODOTTO;

---------------- Dati aggregati per NAZ --------------------

insert into MON_DATAMART.PCL_KPI_CRUSCOTTO_PP_IND (
	   DATA_AGG,
	   SETTIMANA_ISO,
	   MESE_ISO,
	   MACROPRODOTTO,
	   LOGISTICA,
	   KPI,
	   NUM,
	   DEN
)
        select
			:var_data_agg AS DATA_AGG,
             WEEK_ISO,
            :var_mese_iso as MESE_ISO,
            MACROPRODOTTO,
			'NAZIONALE' as LOGISTICA,
			:var_codice_flusso AS KPI,
            sum(QTA_NUMERATORE) as NUM,
			sum(QTA_DENOMINATORE) as DEN
         from
            :var_dati
        group by
            WEEK_ISO,
            MACROPRODOTTO;

    select min(CALWEEK)
        into var_nextWeek_extract
        from MON_DATAMART.ANAG_CALENDAR
    where CALWEEK > :var_week_extract;

    if (:tipoEsecuzione='S') then
	
     insert into MON_DATAMART.TABLEAU_ESECUZIONE_FLUSSO
     select substr(:var_codice_flusso,0,7)    as FLUSSO,
                '1900-01-01' as DATA_ESECUZIONE,
                ::CURRENT_OBJECT_NAME as NOME_PROCEDURA,
                :var_nextWeek_extract as PARA_INPUT,
                0 as ESITO,
                current_date as DATA_AGG,
                'SETTIMANALE' as FREQUENZA,
                :tipoEsecuzione as TIPO_ESECUZIONE
            from dummy;

    End if;
	
	update MON_DATAMART.PCL_CONF_SEMAFORO
    set LAST_DATA_WRITE = :var_data_agg
    where NOME_FLUSSO = :var_codice_flusso;
    
call mydebug(::CURRENT_OBJECT_SCHEMA, ::CURRENT_OBJECT_NAME);
    
end;