/* INSTALL PROCEDURE */
 
INSERT INTO "MON_CHK"."VEI_DETTAGLIO_OGGETTI_INIZIATIVA"  VALUES ( '124456', 'MON_DATAMART', 'Procedura', 'INSERT_PCL200_MM'	, CURRENT_DATE );
INSERT INTO "MON_CHK"."VEI_DETTAGLIO_OGGETTI_INIZIATIVA"  VALUES ( '124456', 'MON_DATAMART', 'Procedura', 'INSERT_PCL200_MM_NXV', CURRENT_DATE );
INSERT INTO "MON_CHK"."VEI_DETTAGLIO_OGGETTI_INIZIATIVA"  VALUES ( '124456', 'MON_DATAMART', 'Procedura', 'INSERT_PCL200_WW'	, CURRENT_DATE );
INSERT INTO "MON_CHK"."VEI_DETTAGLIO_OGGETTI_INIZIATIVA"  VALUES ( '124456', 'MON_DATAMART', 'Procedura', 'INSERT_PCL200_WW_NXV', CURRENT_DATE );

DROP   PROCEDURE "MON_DATAMART"."INSERT_PCL200_MM";
CREATE PROCEDURE "MON_DATAMART"."INSERT_PCL200_MM"  (
	IN month_extract  VARCHAR(6), 
	IN tipoEsecuzione VARCHAR(2)
 ) 
 LANGUAGE sqlscript 
 SQL security invoker 
 DEFAULT SCHEMA "MON_DATAMART" 
AS
BEGIN

-- v1.0.124456 - Cruscotto Posta e Pacchi - Evolutive 2022 - Creazione logica da Universo PE2E per Flusso PCL200_MM 

	DECLARE var_month_extract 	 VARCHAR(6);
	DECLARE var_nextMese_extract VARCHAR(6);
	DECLARE var_minWeek          VARCHAR(6);
    DECLARE var_maxWeek          VARCHAR(6);
    DECLARE var_last_day_pcl     DATE;
	DECLARE var_codice_flusso  	 VARCHAR(10) = 'PCL_200';
	
	var_month_extract = :month_extract ;
	
	-- Svuota la tabella PCL200_MM
	TRUNCATE TABLE "MON_DATAMART"."PCL200_MM";

	SELECT MIN( "CALWEEK" ), MAX( "CALWEEK" )
       INTO var_minWeek, var_maxWeek
    FROM "MON_DATAMART"."ANAG_CALENDAR"
    WHERE "CALMONTH_PCL" = :var_month_extract ;
      
    SELECT MAX( "DATE_SQL" )
        INTO var_last_day_pcl
    FROM "MON_DATAMART"."ANAG_CALENDAR"
	WHERE "CALWEEK" = :var_maxWeek ;
	
------INIZIO CALCOLO Scheda Flusso PCL200_MM-------
	
	-- Recupera tutti i frazionari validi relativo all'ultimo giorno della settimana maggiore relativa al mese solare estratto
	var_frazionario =  
		SELECT  "OFFICEID",
				"FRAZIONARIO_PADRE",
				"TIPO_FRAZIONARIO",
				"COMPETENZA", 
				"MAL",
				"ALT",
				( "ALT" || ' ' || "RAM" ) 	AS "RAM",
				"DATA_INIZIO_VALIDITA",
				"DATA_FINE_VALIDITA"
		FROM "MON_DATAMART"."VIEW_ANAG_FRAZ_TABLEAU"
		WHERE to_date( :var_last_day_pcl ) BETWEEN "DATA_INIZIO_VALIDITA" AND "DATA_FINE_VALIDITA"
	;		

---------------Logiche da Universo PE2E------------------						 
	var_ud =	
		SELECT CAL."WEEK_ISO_STOP"         AS "WEEK_STOP1",
			   PROD."MACROPRODOTTO",
			   FRAZ."OFFICEID",			   
			   FRAZ."MAL",
			   FRAZ."ALT",
			   FRAZ."RAM" ,
			   FRAZ."COMPETENZA",			   
			   SUM( FACT."COUNT_J_E2E1" )  AS "QTA_DENOMINATORE",
			   SUM( CASE 
						WHEN FACT."ID_ESITO_STOP1" = 2 AND FACT."ID_KPI_UN_DOPO_INESITO" != 1 THEN FACT."COUNT_J_E2E1" 
						ELSE 0 
					END ) 				   AS "QTA_NUMERATORE" 				-- ID_ESITO_STOP1 = 2 --> INESITO; ID_KPI_UN_DOPO_INESITO = 1 --> CONSEGNATO DOPO INESITO A PARITA' DI UFFICIO
		
		FROM	   	"MON_PE2E"."PE2E_L3_BARCODE_AGGR_SETTIMANA"		AS FACT
		INNER JOIN  (
			SELECT DISTINCT "CALWEEK" AS "WEEK_ISO_STOP" 
			FROM "MON_PE2E"."PE2E_CONFIG_CALENDAR"
		)											  				AS CAL	 	ON CAL."WEEK_ISO_STOP" = FACT."SETTIMANA_ISO_STOP1"		
		INNER JOIN  "MON_DATAMART"."ANAG_FRAZIONARIO"  				AS FRAZ     ON FRAZ."PROGRESSIVO"  = FACT."PROGR_OFFICE_STOP1"									   
		INNER JOIN  "MON_DATAMART"."PE2E_PCL_ANAG_MACROPRODOTTO" 	AS PROD   	ON PROD."ID_CAUSALE" 		   = FACT."CAUSALE_START"
																				AND PROD."MACROPRODOTTO_PE2E"  = FACT."MACROPRODOTTO"
																				AND ( PROD."REPORT_REGOLATORE" = FACT."REGOLATO" OR 
																				  	  PROD."REPORT_REGOLATORE" = 'ALL' )																					
																				AND PROD."FLUSSO"	 		   = :var_codice_flusso
																				AND CURRENT_DATE BETWEEN PROD."DATA_INIZIO_VALIDITA" AND PROD."DATA_FINE_VALIDITA"				   
		WHERE CAL."WEEK_ISO_STOP" BETWEEN :var_minWeek AND :var_maxWeek
		  AND FACT."COUNT_J_E2E1" > 0		
		GROUP BY 
			CAL."WEEK_ISO_STOP",
			PROD."MACROPRODOTTO",			
			FRAZ."OFFICEID",
			FRAZ."MAL",
			FRAZ."ALT",
			FRAZ."RAM",
			FRAZ."COMPETENZA" ;
			
---------------- Dati relativi ai CD ----------------------	
----------------------------
-- Scrivo sulla PCL200_MM --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_MM" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 					    AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            "OFFICEID" 		 					AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            "QTA_DENOMINATORE",
            "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE COMPETENZA = 'SERVIZI POSTALI - CD'
    ;
		
---------------- Dati aggregati per RAM -------------------	
----------------------------
-- Scrivo sulla PCL200_MM --
----------------------------			
	INSERT INTO "MON_DATAMART"."PCL200_MM" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 						AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            "RAM" 		 						AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	   					AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" ) 			AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )  			AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE "RAM" IS NOT NULL 
		   AND "COMPETENZA" = 'SERVIZI POSTALI - CD'
		 GROUP BY
            "WEEK_STOP1",
            "RAM",
            "MACROPRODOTTO"
    ;		
	
---------------- Dati aggregati per AL --------------------	
----------------------------
-- Scrivo sulla PCL200_MM --
----------------------------		
	INSERT INTO "MON_DATAMART"."PCL200_MM" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 						AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            "ALT" 		 						AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" ) 			AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )  			AS "QTA_NUMERATORE"
         FROM  :var_ud
		 WHERE "ALT" IS NOT NULL 
		 GROUP BY
            "WEEK_STOP1",
            "ALT",
            "MACROPRODOTTO"
    ;		
		
---------------- Dati aggregati per MAL --------------------	
----------------------------
-- Scrivo sulla PCL200_MM --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_MM" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 						AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            "MAL" 		 						AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" ) 			AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )  			AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE "MAL" IS NOT NULL 
		 GROUP BY
            "WEEK_STOP1",
            "MAL",
            "MACROPRODOTTO"
    ;		
		
---------------- Dati aggregati per NAZ -------------------	
----------------------------
-- Scrivo sulla PCL200_MM --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_MM" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 						AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            'NAZIONALE' 						AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" ) 			AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )  			AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 GROUP BY
            "WEEK_STOP1",
            "MACROPRODOTTO"
    ;	
		
---------------------------------------
-- Scrivo sulla PCL200_MM_ST STORICA --
---------------------------------------
	DELETE FROM "MON_DATAMART"."PCL200_MM_ST" WHERE SUBSTR( "WEEK_ISO", 1, 4 ) = SUBSTR( :var_month_extract, 1, 4 ) AND "MESE_ISO" = SUBSTR( :var_month_extract, 5, 6 ) ;
    
    INSERT INTO "MON_DATAMART"."PCL200_MM_ST" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"TIPO_FRAZIONARIO",
		"FRAZIONARIO_PADRE",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE",
		"DATA_AGG"     )
		SELECT
			A."WEEK_ISO",
			A."MESE_ISO",
			A."DIZIONE_ORG_TERR",
			B."TIPO_FRAZIONARIO",
			B."FRAZIONARIO_PADRE",
			A."MACRO_PRODOTTO",
			SUM( A."QTA_DENOMINATORE" ),
			SUM( A."QTA_NUMERATORE" ),
			CURRENT_TIMESTAMP			
		FROM 			"MON_DATAMART"."PCL200_MM"  AS A
		LEFT OUTER JOIN :var_frazionario  			AS B  ON A."DIZIONE_ORG_TERR" = B."OFFICEID"
		GROUP BY 
		  A."WEEK_ISO",
		  A."MESE_ISO",
		  A."DIZIONE_ORG_TERR",
		  B."TIPO_FRAZIONARIO",
		  B."FRAZIONARIO_PADRE",
		  A."MACRO_PRODOTTO"
	;

------FINE CALCOLO PCL 200 MM -------
	
	-- Recupera il CALMONTH_PCL successiva a quello estratto
	SELECT MIN("CALMONTH_PCL")  
	   INTO var_nextMese_extract 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALMONTH_PCL" > :var_month_extract ;
	
	IF ( :tipoEsecuzione='S' ) THEN
	
		INSERT INTO "MON_DATAMART"."TABLEAU_ESECUZIONE_FLUSSO"			
			SELECT 
				'PCL_200' 			  AS "FLUSSO",
				'1900-01-01'		  AS "DATA_ESECUZIONE",
				'INSERT_PCL200_MM'    AS "NOME_PROCEDURA",
				:var_nextMese_extract AS "PARA_INPUT",
				0                     AS "ESITO",
				CURRENT_DATE 		  AS "DATA_AGG",
				'MENSILE' 			  AS "FREQUENZA",
				:tipoEsecuzione 	  AS "TIPO_ESECUZIONE"
			FROM dummy;	
			
	END IF;
	
END;


DROP   PROCEDURE "MON_DATAMART"."INSERT_PCL200_MM_NXV";
CREATE PROCEDURE "MON_DATAMART"."INSERT_PCL200_MM_NXV"  (
	IN month_extract  VARCHAR(6), 
	IN tipoEsecuzione VARCHAR(2)
 ) 
 LANGUAGE sqlscript 
 SQL security invoker 
 DEFAULT SCHEMA "MON_DATAMART" 
AS
BEGIN

-- v1.0.124456 - Cruscotto Posta e Pacchi - Evolutive 2022 - Creazione logica da Universo PE2E per Flusso PCL200_MM_NXV 

	DECLARE var_month_extract 	 VARCHAR(6);
	DECLARE var_nextMese_extract VARCHAR(6);
	DECLARE var_mese_iso		 VARCHAR(2);
	DECLARE var_minWeek          VARCHAR(6);
    DECLARE var_maxWeek          VARCHAR(6);
    DECLARE var_last_day_pcl     DATE;
	DECLARE var_codice_flusso  	 VARCHAR(12) = 'PCL_200_NXV';
	
	var_month_extract = :month_extract ;
	
	-- Svuota la tabella PCL200_MM_NXV
	TRUNCATE TABLE "MON_DATAMART"."PCL200_MM_NXV";

	SELECT MIN( "CALWEEK" ), MAX( "CALWEEK" )
       INTO var_minWeek, var_maxWeek
    FROM "MON_DATAMART"."ANAG_CALENDAR"
    WHERE "CALMONTH_PCL" = :var_month_extract ;
      
    SELECT MAX( "DATE_SQL" )
        INTO var_last_day_pcl
    FROM "MON_DATAMART"."ANAG_CALENDAR"
	WHERE "CALWEEK" = :var_maxWeek ;
	
------INIZIO CALCOLO Scheda Flusso PCL200_MM-------
						
	-- Recupera tutti i frazionari validi relativo all'ultimo giorno della settimana maggiore relativa al mese solare estratto
	var_frazionario =  
		SELECT  "OFFICEID",
				"FRAZIONARIO_PADRE",
				"TIPO_FRAZIONARIO",
				"COMPETENZA", 
				"MAL",
				"ALT",
				( "ALT" || ' ' || "RAM" ) 	AS "RAM",
				"PROGRESSIVO",
				"DATA_INIZIO_VALIDITA",
				"DATA_FINE_VALIDITA"
		FROM "MON_DATAMART"."ANAG_FRAZIONARIO_ALL"
		WHERE IFNULL( COMPETENZA, '' ) = 'NEXIVE'
	;		

---------------Logiche da Universo PE2E------------------						 
	var_ud =	
		SELECT CAL."WEEK_ISO_STOP"         AS "WEEK_STOP1",
			   PROD."MACROPRODOTTO",
			   FRAZ."OFFICEID",		   
			   SUM( FACT."COUNT_J_E2E1" )  AS "QTA_DENOMINATORE",
			   SUM( CASE 
						WHEN FACT."ID_ESITO_STOP1" = 2 AND FACT."ID_KPI_UN_DOPO_INESITO" != 1 THEN FACT."COUNT_J_E2E1" 
						ELSE 0 
					END ) 				   AS "QTA_NUMERATORE" 				-- ID_ESITO_STOP1 = 2 --> INESITO; ID_KPI_UN_DOPO_INESITO = 1 --> CONSEGNATO DOPO INESITO A PARITA' DI UFFICIO
		
		FROM	   	"MON_PE2E"."PE2E_L3_BARCODE_AGGR_SETTIMANA"		AS FACT
		INNER JOIN  (
			SELECT DISTINCT "CALWEEK" AS "WEEK_ISO_STOP" 
			FROM "MON_PE2E"."PE2E_CONFIG_CALENDAR"
		)											  				AS CAL	 	ON CAL."WEEK_ISO_STOP" = FACT."SETTIMANA_ISO_STOP1"		
		INNER JOIN  :var_frazionario			  					AS FRAZ     ON FRAZ."PROGRESSIVO"  = FACT."PROGR_OFFICE_STOP1"									   
		INNER JOIN  "MON_DATAMART"."PE2E_PCL_ANAG_MACROPRODOTTO" 	AS PROD   	ON PROD."ID_CAUSALE" 		   = FACT."CAUSALE_START"
																				AND PROD."MACROPRODOTTO_PE2E"  = FACT."MACROPRODOTTO"
																				AND ( PROD."REPORT_REGOLATORE" = FACT."REGOLATO" OR 
																				  	  PROD."REPORT_REGOLATORE" = 'ALL' )																					
																				AND PROD."FLUSSO"	 		   = :var_codice_flusso
																				AND CURRENT_DATE BETWEEN PROD."DATA_INIZIO_VALIDITA" AND PROD."DATA_FINE_VALIDITA"				   
		WHERE CAL."WEEK_ISO_STOP" BETWEEN :var_minWeek AND :var_maxWeek
		  AND FACT."COUNT_J_E2E1" > 0		
		GROUP BY 
			CAL."WEEK_ISO_STOP",
			PROD."MACROPRODOTTO",			
			FRAZ."OFFICEID" ;
			
---------------- Dati relativi ai CD ----------------------	
--------------------------------
-- Scrivo sulla PCL200_MM_NXV --
--------------------------------
	INSERT INTO "MON_DATAMART"."PCL200_MM_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 					    AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            "OFFICEID" 		 					AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            "QTA_DENOMINATORE",
            "QTA_NUMERATORE" 
         FROM  :var_ud
    ;
		
---------------- Dati aggregati per NAZ -------------------	
--------------------------------
-- Scrivo sulla PCL200_MM_NXV --
--------------------------------
	INSERT INTO "MON_DATAMART"."PCL200_MM_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 						AS "WEEK_ISO",
            SUBSTR( :var_month_extract, 5, 6 )  AS "MESE_ISO",
            'NAZIONALE' 						AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    				AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" ) 			AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )  			AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 GROUP BY
            "WEEK_STOP1",
            "MACROPRODOTTO"
    ;	
		
-------------------------------------------
-- Scrivo sulla PCL200_MM_ST_NXV STORICA --
-------------------------------------------
	DELETE FROM "MON_DATAMART"."PCL200_MM_ST_NXV" WHERE SUBSTR( "WEEK_ISO", 1, 4 ) = SUBSTR( :var_month_extract, 1, 4 ) AND "MESE_ISO" = SUBSTR( :var_month_extract, 5, 6 ) ;
    
    INSERT INTO "MON_DATAMART"."PCL200_MM_ST_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"TIPO_FRAZIONARIO",
		"FRAZIONARIO_PADRE",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE",
		"DATA_AGG"     )
		SELECT
			A."WEEK_ISO",
			A."MESE_ISO",
			A."DIZIONE_ORG_TERR",
			B."TIPO_FRAZIONARIO",
			B."FRAZIONARIO_PADRE",
			A."MACRO_PRODOTTO",
			SUM( A."QTA_DENOMINATORE" ),
			SUM( A."QTA_NUMERATORE" ),
			CURRENT_TIMESTAMP			
		FROM 			"MON_DATAMART"."PCL200_MM_NXV"  AS A
		LEFT OUTER JOIN :var_frazionario				AS B   ON A."DIZIONE_ORG_TERR" = B."OFFICEID"
															  AND to_date( :var_last_day_pcl ) BETWEEN B."DATA_INIZIO_VALIDITA" AND B."DATA_FINE_VALIDITA"
		GROUP BY 
		  A."WEEK_ISO",
		  A."MESE_ISO",
		  A."DIZIONE_ORG_TERR",
		  B."TIPO_FRAZIONARIO",
		  B."FRAZIONARIO_PADRE",
		  A."MACRO_PRODOTTO"
	;

------FINE CALCOLO PCL 200 MM NXV -------
	
	-- Recupera il CALMONTH_PCL successiva a quello estratto
	SELECT MIN("CALMONTH_PCL")  
	   INTO var_nextMese_extract 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALMONTH_PCL" > :var_month_extract ;
	
	IF ( :tipoEsecuzione='S' ) THEN
	
		INSERT INTO "MON_DATAMART"."TABLEAU_ESECUZIONE_FLUSSO"			
			SELECT 
				'PCL_200_NXV' 			AS "FLUSSO",
				'1900-01-01'		  	AS "DATA_ESECUZIONE",
				'INSERT_PCL200_MM_NXV'  AS "NOME_PROCEDURA",
				:var_nextMese_extract   AS "PARA_INPUT",
				0                       AS "ESITO",
				CURRENT_DATE 		    AS "DATA_AGG",
				'MENSILE' 			    AS "FREQUENZA",
				:tipoEsecuzione 	    AS "TIPO_ESECUZIONE"
			FROM dummy;	
			
		UPDATE "MON_DATAMART"."PCL_CONF_SEMAFORO"
			SET "LAST_DATA_WRITE" = CURRENT_TIMESTAMP
			WHERE "NOME_FLUSSO" = :var_codice_flusso ;
		
	END IF;
	
END;


DROP   PROCEDURE "MON_DATAMART"."INSERT_PCL200_WW";
CREATE PROCEDURE "MON_DATAMART"."INSERT_PCL200_WW"  (
	IN week_extract  VARCHAR(6), 
	IN tipoEsecuzione VARCHAR(2)
 ) 
 LANGUAGE sqlscript 
 SQL security invoker 
 DEFAULT SCHEMA "MON_DATAMART" 
AS
BEGIN

-- v1.0.124456 - Cruscotto Posta e Pacchi - Evolutive 2022 - Creazione logica da Universo PE2E per Flusso PCL200_WW 

	DECLARE var_week_extract 	 VARCHAR(6);
	DECLARE var_nextWeek_extract VARCHAR(6);
	DECLARE var_mese_iso		 VARCHAR(2);
	DECLARE var_codice_flusso  	 VARCHAR(10) = 'PCL_200';
	
	var_week_extract = :week_extract ;
	
	-- Svuota la tabella PCL200_WW
	TRUNCATE TABLE "MON_DATAMART"."PCL200_WW";

	-- Recupera il MONTH_OF_WEEK_PCL relativo alla settimana estratta
	SELECT DISTINCT "MONTH_OF_WEEK_PCL"
		INTO var_mese_iso 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALWEEK" = :var_week_extract ;
	
------INIZIO CALCOLO Scheda Flusso PCL200_WW-------
	
	-- Recupera tutti i frazionari validi relativo all'ultimo giorno della settimana maggiore relativa al mese solare estratto
	var_frazionario =  
		SELECT  "OFFICEID",
				"FRAZIONARIO_PADRE",
				"TIPO_FRAZIONARIO",
				"COMPETENZA", 
				"MAL",
				"ALT",
				( "ALT" || ' ' || "RAM" ) 	AS "RAM",
				"DATA_INIZIO_VALIDITA",
				"DATA_FINE_VALIDITA"
		FROM "MON_DATAMART"."VIEW_ANAG_FRAZ_TABLEAU"
	;		

---------------Logiche da Universo PE2E------------------						 
	var_ud =	
		SELECT CAL."WEEK_ISO_STOP"         AS "WEEK_STOP1",
			   PROD."MACROPRODOTTO",
			   FRAZ."OFFICEID",			   
			   FRAZ."MAL",
			   FRAZ."ALT",
			   FRAZ."RAM" ,
			   FRAZ."COMPETENZA",			   
			   SUM( FACT."COUNT_J_E2E1" )  AS "QTA_DENOMINATORE",
			   SUM( CASE 
						WHEN FACT."ID_ESITO_STOP1" = 2 AND FACT."ID_KPI_UN_DOPO_INESITO" != 1 THEN FACT."COUNT_J_E2E1" 
						ELSE 0 
					END ) 				   AS "QTA_NUMERATORE" 				-- ID_ESITO_STOP1 = 2 --> INESITO; ID_KPI_UN_DOPO_INESITO = 1 --> CONSEGNATO DOPO INESITO A PARITA' DI UFFICIO
		
		FROM	   	"MON_PE2E"."PE2E_L3_BARCODE_AGGR_SETTIMANA"		AS FACT
		INNER JOIN  (
			SELECT DISTINCT "CALWEEK" AS "WEEK_ISO_STOP" 
			FROM "MON_PE2E"."PE2E_CONFIG_CALENDAR"
		)											  				AS CAL	 	ON CAL."WEEK_ISO_STOP" = FACT."SETTIMANA_ISO_STOP1"		
		INNER JOIN  "MON_DATAMART"."ANAG_FRAZIONARIO"  				AS FRAZ     ON FRAZ."PROGRESSIVO"  = FACT."PROGR_OFFICE_STOP1"									   
		INNER JOIN  "MON_DATAMART"."PE2E_PCL_ANAG_MACROPRODOTTO" 	AS PROD   	ON PROD."ID_CAUSALE" 		   = FACT."CAUSALE_START"
																				AND PROD."MACROPRODOTTO_PE2E"  = FACT."MACROPRODOTTO"
																				AND ( PROD."REPORT_REGOLATORE" = FACT."REGOLATO" OR 
																				  	  PROD."REPORT_REGOLATORE" = 'ALL' )																					
																				AND PROD."FLUSSO"	 		   = :var_codice_flusso
																				AND CURRENT_DATE BETWEEN PROD."DATA_INIZIO_VALIDITA" AND PROD."DATA_FINE_VALIDITA"				   
		WHERE CAL."WEEK_ISO_STOP" = :var_week_extract
		  AND FACT."COUNT_J_E2E1" > 0		
		GROUP BY 
			CAL."WEEK_ISO_STOP",
			PROD."MACROPRODOTTO",			
			FRAZ."OFFICEID",
			FRAZ."MAL",
			FRAZ."ALT",
			FRAZ."RAM",
			FRAZ."COMPETENZA" ;
			
---------------- Dati relativi ai CD ----------------------	
----------------------------
-- Scrivo sulla PCL200_WW --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_WW" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 		AS "WEEK_ISO",
            :var_mese_iso 		AS "MESE_ISO",
            "OFFICEID" 		 	AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    AS "MACRO_PRODOTTO",
            "QTA_DENOMINATORE",
            "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE "COMPETENZA" = 'SERVIZI POSTALI - CD'
    ;
		
---------------- Dati aggregati per RAM -------------------	
----------------------------
-- Scrivo sulla PCL200_WW --
----------------------------			
	INSERT INTO "MON_DATAMART"."PCL200_WW" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 				AS "WEEK_ISO",
            :var_mese_iso 				AS "MESE_ISO",
            "RAM" 		 				AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    		AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" )   AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )     AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE "RAM" IS NOT NULL 
		   AND "COMPETENZA" = 'SERVIZI POSTALI - CD'
		 GROUP BY
            "WEEK_STOP1",
            "RAM",
            "MACROPRODOTTO"
    ;		
	
---------------- Dati aggregati per AL --------------------	
----------------------------
-- Scrivo sulla PCL200_WW --
----------------------------		
	INSERT INTO "MON_DATAMART"."PCL200_WW" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 				AS "WEEK_ISO",
            :var_mese_iso 				AS "MESE_ISO",
            "ALT" 		 				AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    		AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" )   AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )     AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 WHERE "ALT" IS NOT NULL 
		 GROUP BY
            "WEEK_STOP1",
            "ALT",
            "MACROPRODOTTO"
    ;		
		
---------------- Dati aggregati per MAL --------------------	
----------------------------
-- Scrivo sulla PCL200_WW --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_WW" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 				AS "WEEK_ISO",
            :var_mese_iso 				AS "MESE_ISO",
            "MAL" 		 				AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    		AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" )   AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )     AS "QTA_NUMERATORE"
         FROM  :var_ud
		 WHERE "MAL" IS NOT NULL 
		 GROUP BY
            "WEEK_STOP1",
            "MAL",
            "MACROPRODOTTO"
    ;		
		
---------------- Dati aggregati per NAZ -------------------	
----------------------------
-- Scrivo sulla PCL200_WW --
----------------------------
	INSERT INTO "MON_DATAMART"."PCL200_WW" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 				AS "WEEK_ISO",
            :var_mese_iso 				AS "MESE_ISO",
            'NAZIONALE' 				AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    		AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" )   AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )     AS "QTA_NUMERATORE" 
         FROM  :var_ud
		 GROUP BY
            "WEEK_STOP1",
            "MACROPRODOTTO"
    ;	
		
---------------------------------------
-- Scrivo sulla PCL200_WW_ST STORICA --
---------------------------------------
	DELETE FROM "MON_DATAMART"."PCL200_WW_ST" WHERE "WEEK_ISO" = :var_week_extract ;
    
    INSERT INTO "MON_DATAMART"."PCL200_WW_ST" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"TIPO_FRAZIONARIO",
		"FRAZIONARIO_PADRE",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE",
		"DATA_AGG"     )
		SELECT
			A."WEEK_ISO",
			A."MESE_ISO",
			A."DIZIONE_ORG_TERR",
			B."TIPO_FRAZIONARIO",
			B."FRAZIONARIO_PADRE",
			A."MACRO_PRODOTTO",
			SUM( A."QTA_DENOMINATORE" ),
			SUM( A."QTA_NUMERATORE" ),
			CURRENT_TIMESTAMP			
		FROM 			"MON_DATAMART"."PCL200_WW"  AS A
		LEFT OUTER JOIN :var_frazionario  			AS B  ON A."DIZIONE_ORG_TERR" = B."OFFICEID"
		GROUP BY 
		  A."WEEK_ISO",
		  A."MESE_ISO",
		  A."DIZIONE_ORG_TERR",
		  B."TIPO_FRAZIONARIO",
		  B."FRAZIONARIO_PADRE",
		  A."MACRO_PRODOTTO"
	;

------FINE CALCOLO PCL 200 WW -------
	
	-- Recupera il WEEK_ISO successiva a quella estratta
	SELECT MIN("CALWEEK")  
	   INTO var_nextweek_extract 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALWEEK" > :var_week_extract ;
	
	IF ( :tipoEsecuzione='S' ) THEN
	
		INSERT INTO "MON_DATAMART"."TABLEAU_ESECUZIONE_FLUSSO"			
			SELECT 
				'PCL_200' 			  AS "FLUSSO",
				'1900-01-01'		  AS "DATA_ESECUZIONE",
				'INSERT_PCL200_WW'    AS "NOME_PROCEDURA",
				:var_nextWeek_extract AS "PARA_INPUT",
				0                     AS "ESITO",
				CURRENT_DATE 		  AS "DATA_AGG",
				'SETTIMANALE' 		  AS "FREQUENZA",
				:tipoEsecuzione 	  AS "TIPO_ESECUZIONE"
			FROM dummy;	
			
	END IF;
	
END;


DROP   PROCEDURE "MON_DATAMART"."INSERT_PCL200_WW_NXV";
CREATE PROCEDURE "MON_DATAMART"."INSERT_PCL200_WW_NXV"  (
	IN week_extract   VARCHAR(6), 
	IN tipoEsecuzione VARCHAR(2)
 ) 
 LANGUAGE sqlscript 
 SQL security invoker 
 DEFAULT SCHEMA "MON_DATAMART" 
AS
BEGIN

-- v1.0.124456 - Cruscotto Posta e Pacchi - Evolutive 2022 - Creazione logica da Universo PE2E per Flusso PCL_200_NXV 

	DECLARE var_week_extract 	 VARCHAR(6);
	DECLARE var_nextWeek_extract VARCHAR(6);
	DECLARE var_mese_iso		 VARCHAR(2);
	DECLARE var_codice_flusso  	 VARCHAR(12) = 'PCL_200_NXV';
	
	var_week_extract = :week_extract ;
	
	-- Svuota la tabella PCL200_WW_NXV
	TRUNCATE TABLE "MON_DATAMART"."PCL200_WW_NXV";

	-- Recupera il MONTH_OF_WEEK_PCL relativo alla settimana estratta
	SELECT DISTINCT "MONTH_OF_WEEK_PCL"
		INTO var_mese_iso 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALWEEK" = :var_week_extract ;
	
------INIZIO CALCOLO Scheda Flusso PCL200_WW_NXV-------
	
	-- Recupera tutti i frazionari padre 
	var_frazionario_padre =	
		SELECT 	"OFFICEID",
				"FRAZIONARIO_PADRE",
				"TIPO_FRAZIONARIO",
				"COMPETENZA",
				"MAL",
				"ALT",
				( "ALT" || ' ' || "RAM" ) 	AS "RAM"
		FROM "MON_DATAMART"."VIEW_ANAG_FRAZ_TABLEAU_NXV"
	;
	
	-- Recupera tutti i frazionari validi relativo all'ultimo giorno della settimana maggiore relativa al mese solare estratto
	var_frazionario =  
		SELECT  "OFFICEID",
				"FRAZIONARIO_PADRE",
				"TIPO_FRAZIONARIO",
				"COMPETENZA", 
				"MAL",
				"ALT",
				( "ALT" || ' ' || "RAM" ) 	AS "RAM",
				"PROGRESSIVO",
				"DATA_INIZIO_VALIDITA",
				"DATA_FINE_VALIDITA"
		FROM "MON_DATAMART"."ANAG_FRAZIONARIO_ALL"
		WHERE IFNULL( "COMPETENZA", '' ) = 'NEXIVE'
	;		

---------------Logiche da Universo PE2E------------------						 
	var_ud =	
		SELECT CAL."WEEK_ISO_STOP"         AS "WEEK_STOP1",
			   PROD."MACROPRODOTTO",
			   FRAZ."OFFICEID",			   			   
			   SUM( FACT."COUNT_J_E2E1" )  AS "QTA_DENOMINATORE",
			   SUM( CASE 
						WHEN FACT."ID_ESITO_STOP1" = 2 AND FACT."ID_KPI_UN_DOPO_INESITO" != 1 THEN FACT."COUNT_J_E2E1" 
						ELSE 0 
					END ) 				   AS "QTA_NUMERATORE" 				-- ID_ESITO_STOP1 = 2 --> INESITO; ID_KPI_UN_DOPO_INESITO = 1 --> CONSEGNATO DOPO INESITO A PARITA' DI UFFICIO
		
		FROM	   	"MON_PE2E"."PE2E_L3_BARCODE_AGGR_SETTIMANA"		AS FACT
		INNER JOIN  (
			SELECT DISTINCT "CALWEEK" AS "WEEK_ISO_STOP" 
			FROM "MON_PE2E"."PE2E_CONFIG_CALENDAR"
		)											  				AS CAL	 	ON CAL."WEEK_ISO_STOP" = FACT."SETTIMANA_ISO_STOP1"		
		INNER JOIN  :var_frazionario  								AS FRAZ     ON FRAZ."PROGRESSIVO"  = FACT."PROGR_OFFICE_STOP1"									   
		INNER JOIN  "MON_DATAMART"."PE2E_PCL_ANAG_MACROPRODOTTO" 	AS PROD   	ON PROD."ID_CAUSALE" 		   = FACT."CAUSALE_START"
																				AND PROD."MACROPRODOTTO_PE2E"  = FACT."MACROPRODOTTO"
																				AND ( PROD."REPORT_REGOLATORE" = FACT."REGOLATO" OR 
																				  	  PROD."REPORT_REGOLATORE" = 'ALL' )																					
																				AND PROD."FLUSSO"	 		   = :var_codice_flusso
																				AND CURRENT_DATE BETWEEN PROD."DATA_INIZIO_VALIDITA" AND PROD."DATA_FINE_VALIDITA"				   
		WHERE CAL."WEEK_ISO_STOP" = :var_week_extract
		  AND FACT."COUNT_J_E2E1" > 0		
		GROUP BY 
			CAL."WEEK_ISO_STOP",
			PROD."MACROPRODOTTO",			
			FRAZ."OFFICEID" ;
			
---------------- Dati relativi ai CD ----------------------	
--------------------------------
-- Scrivo sulla PCL200_WW_NXV --
--------------------------------
	INSERT INTO "MON_DATAMART"."PCL200_WW_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 		AS "WEEK_ISO",
            :var_mese_iso 		AS "MESE_ISO",
            "OFFICEID" 		 	AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    AS "MACRO_PRODOTTO",
            "QTA_DENOMINATORE",
			"QTA_NUMERATORE" 
         FROM  :var_ud
    ;
		
---------------- Dati aggregati per NAZ -------------------	
--------------------------------
-- Scrivo sulla PCL200_WW_NXV --
--------------------------------
	INSERT INTO "MON_DATAMART"."PCL200_WW_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE" )
         SELECT
            "WEEK_STOP1" 				AS "WEEK_ISO",
            :var_mese_iso 				AS "MESE_ISO",
            'NAZIONALE' 				AS "DIZIONE_ORG_TERR",
            "MACROPRODOTTO"	    		AS "MACRO_PRODOTTO",
            SUM( "QTA_DENOMINATORE" )   AS "QTA_DENOMINATORE",
			SUM( "QTA_NUMERATORE" )     AS "QTA_NUMERATORE"
         FROM  :var_ud
		 GROUP BY
            "WEEK_STOP1",
            "MACROPRODOTTO"
    ;	
		
---------------------------------------
-- Scrivo sulla PCL200_WW_ST_NXV STORICA --
---------------------------------------
	DELETE FROM "MON_DATAMART"."PCL200_WW_ST_NXV" WHERE "WEEK_ISO" = :var_week_extract ;
    
    INSERT INTO "MON_DATAMART"."PCL200_WW_ST_NXV" (
		"WEEK_ISO",
		"MESE_ISO",
		"DIZIONE_ORG_TERR",
		"TIPO_FRAZIONARIO",
		"FRAZIONARIO_PADRE",
		"MACRO_PRODOTTO",
		"QTA_DENOMINATORE",
		"QTA_NUMERATORE",
		"DATA_AGG"     )
		SELECT
			A."WEEK_ISO",
			A."MESE_ISO",
			A."DIZIONE_ORG_TERR",
			B."TIPO_FRAZIONARIO",
			B."FRAZIONARIO_PADRE",
			A."MACRO_PRODOTTO",
			SUM( A."QTA_DENOMINATORE" ),
			SUM( A."QTA_NUMERATORE" ),
			CURRENT_TIMESTAMP			
		FROM 			"MON_DATAMART"."PCL200_WW_NXV"  AS A
		LEFT OUTER JOIN :var_frazionario_padre			AS B  ON A."DIZIONE_ORG_TERR" = B."OFFICEID"
		GROUP BY 
		  A."WEEK_ISO",
		  A."MESE_ISO",
		  A."DIZIONE_ORG_TERR",
		  B."TIPO_FRAZIONARIO",
		  B."FRAZIONARIO_PADRE",
		  A."MACRO_PRODOTTO"
	;

------FINE CALCOLO PCL 200 WW NXV-------
	
	-- Recupera il WEEK_ISO successiva a quella estratta
	SELECT MIN("CALWEEK")  
	   INTO var_nextweek_extract 
	FROM "MON_DATAMART"."ANAG_CALENDAR" 
	WHERE "CALWEEK" > :var_week_extract ;
	
	IF (:tipoEsecuzione='S') THEN
	
		INSERT INTO "MON_DATAMART"."TABLEAU_ESECUZIONE_FLUSSO"			
			SELECT 
				'PCL_200_NXV'		   AS "FLUSSO",
				'1900-01-01'		   AS "DATA_ESECUZIONE",
				'INSERT_PCL200_WW_NXV' AS "NOME_PROCEDURA",
				:var_nextWeek_extract  AS "PARA_INPUT",
				0                      AS "ESITO",
				CURRENT_DATE 		   AS "DATA_AGG",
				'SETTIMANALE' 		   AS "FREQUENZA",
				:tipoEsecuzione 	   AS "TIPO_ESECUZIONE"
			FROM dummy;	
			
		UPDATE "MON_DATAMART"."PCL_CONF_SEMAFORO"
			SET "LAST_DATA_WRITE" = current_timestamp
			WHERE "NOME_FLUSSO" = :var_codice_flusso ;
	
	END IF;
	
END;