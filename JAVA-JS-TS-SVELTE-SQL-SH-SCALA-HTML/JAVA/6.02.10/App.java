package it.almaviva.trasparenza.dbalign;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import it.almaviva.trasparenza.dbalign.job.IscAlignJob;
import it.almaviva.trasparenza.dbalign.utils.PropertiesManager;

/**
 * Classe main per l'avvio da schedulazione esterna della fase di riallineamento
 * delle tabelle ISC e ISC_DETAIL e ISC_PROFILO_NOME, da Oracle a DB2
 *
 */
public class App 
{
	
	private static Log log = LogFactory.getLog(App.class);
	
	
    public static void main( String[] args ) throws IscAlignException
    {
    	
    	log.info("-----------------------------------------------");
    	log.info("-----------------ISC-DB-ALIGN------------------");
    	log.info("-----------------------------------------------");
    	
    	log.info("Orario di start:"+new Date());
    	log.info("Parametri: "+Arrays.toString(args));
    	CommandLineParser parser = new DefaultParser();
    	
    	// creazione delle opzioni di innesco del batch
    	Options options = new Options();
    	
    	Option optX = new Option( "x", "cancella-tabelle", true, 
    			"Questa opzione permette di cancellare preventivamente "+
    			"tutti i valori pre-esistenti delle tabelle cancellabili. Es. -x C6TBFISC C6TBDISC");
    	optX.setArgs(3);
    	optX.setOptionalArg(true);
    	optX.setValueSeparator(' ');
    	options.addOption(optX);
    	options.addOption( "f", "ftp-edwh", false, "Se presente tale indicazione, l'app. crea ed invia il file di allineamento incrementale "
    			+"ad EDWH, via FTP, secondo i parametri impostati nel file application.properties");
    	//options.addOption( "e", "escludi-tabella", true, "Esclude dal batch la tabella indicata");
    	options.addOption( "l", "log-level", true, "Impostazione del livello di log: TRACE, DEBUG, INFO, WARN, ERROR");
    	options.addOption( "s", "stop-on-error", false, 
    			"Stop dell'esecuzione al verificarsi di un errore, con ROLLBACK di tutte le operazioni effettuate su DB2. "
    			+"Se non presente l'app. continua fino alla fine dei record, restituendo la lista degli errori eventualmente lanciati.");
    	//options.addOption( "x", "exec-fase", true, "Impostazione della specifica fase di esecuzione: "
    	//		+"0=ALLINEMENTO-DB, 1=CREAZIONE-FILE-EDWH, 2=INVIO-FILE-EDWH");
    	options.addOption( "h", "help", false, "Indicazioni di avvio.");
    	
    	CommandLine line = null;
    	try {
    	    line = parser.parse( options, args );
    	    PropertiesManager pm = PropertiesManager.getInstance();
    	    
    	    if( line.hasOption( "h" ) ) {
    	    	HelpFormatter formatter = new HelpFormatter();
    	    	formatter.printHelp("dbalign", "modalità di innesco dell'applicazione dbalign", options, "", true);
    	    	System.exit(0);
    	    	return;
    	    }

    	    // validazione delle options
    	    if( line.hasOption( "x" ) ) {
    	    	String[] tabs = line.getOptionValues( "cancella-tabelle" );
    	    	if (tabs == null)
    	    		throw new ParseException("E' stato impostato il parametro -k (cancella-tabelle) non impostando nessun nome di tabella");
    	        log.info("cancella-tabelle: " + Arrays.toString(tabs));
    	        for (int i=0; i<tabs.length; i++) {
    	        	if (!tabs[i].matches(pm.getProperty("db2.tabelle", ".*"))) {
    	        		throw new ParseException("La tabella "+tabs[i]+" non rientra tra quelle cancellabili, che sono: "+pm.getProperty("db2.tabelle"));
    	        	}
    	        }
    	    }
    	    
    	    if( line.hasOption( "l" ) ) {
    	    	String[] levels = {"DEBUG","ERROR","INFO","TRACE","WARN"};
    	    	String level = line.getOptionValue( "log-level" );
    	    	if (level == null)
    	    		throw new ParseException("I valori ammessi per il parametro -l (--log-level) sono: "+Arrays.toString(levels));
    	        log.info("log-level: " + level);
    	        Logger.getLogger("it.almaviva.trasparenza").setLevel(Level.toLevel(level));
    	        if (Arrays.binarySearch(levels, level.toUpperCase()) < 0) {
   	        		throw new ParseException("Il livello del log può essere uno tra: "+Arrays.toString(levels));
    	        }
    	    }
    	    
    	    /*if( line.hasOption( "exec-fase" ) ) {
    	    	
    	    	String fase = line.getOptionValue( "exec-fase" );
    	        log.info("exec-fase: " + fase);
    	        if (fase == null || !fase.matches("^\n{0-2}$")) {
    	        	throw new ParseException("I valori ammessi per il parametro -x (--exec-fase) sono: 0 (ALLINEMENTO-DB), 1 (CREAZIONE-FILE-EDWH), 2 (INVIO-FILE-EDWH)");
    	        }
    	    }*/
    	    
    	}
    	catch( ParseException exp ) {
    	    log.error( "Unexpected exception:" + exp.getMessage() );
    	    System.exit(9);
    	    throw new IscAlignException(9, exp.getMessage());
    	}

    	//if (true) return;
    	
    	
    	try {
    		
    		IscAlignJob job = new IscAlignJob();
    		job.run(line);
    		log.info("FINE procedura di allineamento!");
    		System.exit(0);
    		
    	} catch (SQLException e) {
    		log.error( "SQLException:" + e.getMessage(), e);
    		System.exit(99);
    		throw new IscAlignException(99, "SQLException", e);
    	} catch (IscAlignException e) {
    		log.error( "IscAlignException:" + e.getMessage(), e);
    		System.exit(e.getCodiceErrore());
    		throw e;
    	} catch (Exception e) {
    		log.error( "Unexpected Exception:" + e.getMessage(), e);
    		System.exit(999);
    		throw new IscAlignException(999, "Eccezione imprevista.", e);
    	}
    	
    		
    }
}
