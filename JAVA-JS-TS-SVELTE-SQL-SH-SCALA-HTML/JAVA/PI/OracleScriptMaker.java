package com.infogroup.poste.common;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OracleScriptMaker {

	private static Logger logger = LogManager.getLogger();

	Connection conn;
	String lineSep = System.getProperty("line.separator");
	String scriptSQL;

	private void getConnection() {
		if (conn == null) {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				//conn = DriverManager.getConnection("jdbc:oracle:thin:@infodev-vm01.servizi.infogroup.it:1531:infdev02", "ebaasposte", "ebaasposte");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@dbdev01.servizi.infogroup.it:1521:dbdev07", "ebaasposte", "ebaasposte");
			} catch (ClassNotFoundException e) {
	        	logger.error(e.getMessage()); 
			} catch (SQLException e) {
	        	logger.error(e.getMessage()); 
			}
		}
	}

	private String createScript(String tableName) throws SQLException {
		return createScript(tableName, null);
	}

	private String createScript(String tableName, String whereCondition) throws SQLException {
		return createScript(tableName, whereCondition, false);
	}
	
	private String createScript(String tableName, String whereCondition, boolean comment) throws SQLException {
		StringBuilder result = new StringBuilder();
		Statement s = null;
		ResultSet r = null;
		String sql = "select * from " + tableName;
		if (whereCondition != null)
			sql += " where " + whereCondition;
		
		try {
			s = conn.createStatement();
			r = s.executeQuery(sql);
			ResultSetMetaData rsMetaData = r.getMetaData();
			
			StringBuilder colsName = new StringBuilder();
			int colNumber = rsMetaData.getColumnCount();
			for (int i = 1; i <= colNumber; i++) {
				if (i != 1) 
					colsName.append(", ");
				String colName = rsMetaData.getColumnName(i);
				colsName.append(colName);
			}
	
			while (r.next()) {
				// INSERT
				if (comment)
					result.append("--");
				
				result.append("insert into " + tableName + "(" + colsName.toString() +") values (");
				for (int i = 1; i <= colNumber; i++) {
					// se il valore e' NULL
					if (r.getString(i) == null) {
						if (i != 1)
							result.append(", null");
						else
							result.append("null");
	
					} else {
	
						int colType = rsMetaData.getColumnType(i);
						switch (colType) {
						case java.sql.Types.NUMERIC:
						case java.sql.Types.DECIMAL:
						case java.sql.Types.DOUBLE:
							if (i != 1)
								result.append(", ");
							String value = r.getString(i);
							if (value.startsWith("."))
								value = "0" + value;
							result.append(value);
							break;
						case java.sql.Types.VARCHAR:
						case java.sql.Types.CHAR:
							if (i != 1)
								result.append(", '");
							else
								result.append("'");
							String str = r.getString(i);
							str = str.replaceAll("'", "''");
							result.append(str);
							result.append("'");
							break;
						default:
							result.append("---ATTENZIONE---");
						}
					}
	
				}
				result.append(");" + lineSep);
			}
			r.close();
			s.close();
		} 
		catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if (r != null)
				r.close();
			if (s != null)
				s.close();
		}
		return result.toString();
	}
	
	private String createMergeScript(String tableName, String whereCondition, String onCondition, String keyName) throws SQLException {
		StringBuilder result = new StringBuilder();
		Statement s = null;
		ResultSet r = null;
		String sql = "select * from " + tableName;
		if (whereCondition != null)
			sql += " where " + whereCondition;

		try {
			s = conn.createStatement();
			r = s.executeQuery(sql);
			ResultSetMetaData rsMetaData = r.getMetaData();
			
			StringBuilder colsName = new StringBuilder();
			int colNumber = rsMetaData.getColumnCount();
			for (int i = 1; i <= colNumber; i++) {
				if (i != 1) 
					colsName.append(", ");
				String colName = rsMetaData.getColumnName(i);
				colsName.append(colName);
			}
	
			while (r.next()) {
				// MERGE
				StringBuilder select = new StringBuilder();
				StringBuilder insert = new StringBuilder();
				StringBuilder update = new StringBuilder();
				
				for (int i = 1; i <= colNumber; i++) {
					boolean keyColumn = false;
					if (keyName.toUpperCase().indexOf(rsMetaData.getColumnName(i).toUpperCase()) != -1) 
						keyColumn = true;
					
					// se il valore e' NULL
					if (r.getString(i) == null) {
						if (i != 1) {
							if (!keyColumn) {
								if (update.length() > 0)
									update.append(", " + rsMetaData.getColumnName(i) + " = null");
								else
									update.append(rsMetaData.getColumnName(i) + " = null");
							}
							select.append(", null as " + rsMetaData.getColumnName(i));
							insert.append(", null");
						} else {
							if (!keyColumn) 
								update.append(rsMetaData.getColumnName(i) + " = null");
							select.append("null as " + rsMetaData.getColumnName(i));
							insert.append("null");
						}
					} else {
	
						int colType = rsMetaData.getColumnType(i);
						switch (colType) {
						case java.sql.Types.NUMERIC:
						case java.sql.Types.DECIMAL:
						case java.sql.Types.DOUBLE:
							if (i != 1) {
								if (!keyColumn) {
									if (update.length() > 0)
										update.append(", " + rsMetaData.getColumnName(i) + " = ");
									else 
										update.append(rsMetaData.getColumnName(i) + " = ");
								}
								select.append(", ");
								insert.append(", ");
							} else {
								if (!keyColumn) 
									update.append(" " + rsMetaData.getColumnName(i) + " = ");
								select.append(" ");
							}
							String value = r.getString(i);
							if (value.startsWith("."))
								value = "0" + value;
							
							select.append(value + " as " + rsMetaData.getColumnName(i));
							if (!keyColumn) 
								update.append(value);
							insert.append(value);
									
							break;
						case java.sql.Types.VARCHAR:
						case java.sql.Types.CHAR:
							if (i != 1) {
								if (!keyColumn) { 
									if (update.length() > 0) 
										update.append(", " + rsMetaData.getColumnName(i) + " = '");
									else 
										update.append(rsMetaData.getColumnName(i) + " = '");
								}
								select.append(", '");
								insert.append(", '");
							} else {
								if (!keyColumn) 
									update.append(rsMetaData.getColumnName(i) + " = '");
								select.append("'");
								insert.append("'");
							}
							String str = r.getString(i);
							str = str.replaceAll("'", "''");
							if (!keyColumn) 
								update.append(str);
							select.append(str);
							insert.append(str);
							
							if (!keyColumn) 
								update.append("'");
							select.append("' as " + rsMetaData.getColumnName(i));
							insert.append("'");
							break;
						default:
							result.append("---ATTENZIONE---");
						}
					}
				}
				result.append("merge into " + tableName + " target using (select " + 
						select.toString() +" from dual) source on (" + onCondition + ") when matched then update set " +
						update.toString() + " when not matched then insert (" + colsName.toString() + ") values (" + 
						insert.toString());
				result.append(");" + lineSep);
			}
			r.close();
			s.close();
		} 
		catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if (r != null)
				r.close();
			if (s != null)
				s.close();
		}

		return result.toString();
	}
	
	private String createDeleteScript(String tableName, String whereCondition) throws SQLException {
		StringBuilder result = new StringBuilder();
		Statement s = null;
		ResultSet r = null;
		String sql = "select * from " + tableName;
		if (whereCondition != null)
			sql += " where " + whereCondition;

		try {
			s = conn.createStatement();
			r = s.executeQuery(sql);
			ResultSetMetaData rsMetaData = r.getMetaData();
			int colNumber = rsMetaData.getColumnCount();
		
			while (r.next()) {
				// DELETE
				result.append("delete from " + tableName + " where");
				for (int i = 1; i <= colNumber; i++) {
					// se il valore e' NULL
					if (r.getString(i) == null) {
						if (i != 1)
							result.append(" and " + rsMetaData.getColumnName(i) + "=null");
						else
							result.append(" " + rsMetaData.getColumnName(i) + "=null");
	
					} else {
	
						int colType = rsMetaData.getColumnType(i);
						switch (colType) {
						case java.sql.Types.NUMERIC:
						case java.sql.Types.DECIMAL:
						case java.sql.Types.DOUBLE:
							if (i != 1)
								result.append(" and " + rsMetaData.getColumnName(i) + "=");
							else 
								result.append(" " + rsMetaData.getColumnName(i) + "=");
							String value = r.getString(i);
							if (value.startsWith("."))
								value = "0" + value;
							result.append(value);
							break;
						case java.sql.Types.VARCHAR:
						case java.sql.Types.CHAR:
							if (i != 1)
								result.append(" and " + rsMetaData.getColumnName(i) + "='");
							else
								result.append(" " + rsMetaData.getColumnName(i) + "='");
							String str = r.getString(i);
							str = str.replaceAll("'", "''");
							result.append(str);
							result.append("'");
							break;
						default:
							result.append("---ATTENZIONE---");
						}
					}
	
				}
				result.append(";" + lineSep);
			}
			r.close();
			s.close();

		} 
		catch (Exception ex) {
			logger.error(ex.getMessage());
		} finally {
			if (r != null)
				r.close();
			if (s != null)
				s.close();
		}
		return result.toString();
	}

	private void closeConnection() {
		try {
			conn.commit();
			conn.close();
			conn = null;
		} catch (Exception e) {
        	logger.error(e.getMessage(), e);
		}
	}

	public void doWork(String cod_conv, String cod_quest) throws SQLException {
		logger.debug("Creo script per polizza " + cod_conv);
		getConnection();
		
		//String cod_conv = "09PCA";
		//String cod_quest = "007";
		
		//String cod_conv = "01INF";
		//String cod_quest = "008";
		
		//String cod_conv = "01MAL";
		//String cod_quest = "009";
		
		//String cod_conv = "01ISE";
		//String cod_quest = "010";
		
		//String cod_conv = "08IBA";
		//String cod_quest = "006";
		
		StringBuilder result = new StringBuilder();
		result.append("set define off;" + lineSep);
		// pulisco la convenzione
		//result.append("delete from ATTRIB_TENDINE where id_tendina in (select id_tendina from TENDINE where cod_convenzione = '"+ cod_conv +"');" + lineSep);
		result.append("delete from ATTRIB_TENDINE where id_tendina in (select id_tendina from TENDINE where cod_convenzione = '"+ cod_conv +"') and id_tendina not in (select id_tendina from TENDINE where cod_convenzione != '"+ cod_conv +"');" + lineSep);
		//result.append(createDeleteScript("ATTRIB_TENDINE", "id_tendina in (select id_tendina from TENDINE where cod_convenzione = '"+ cod_conv +"')"));
		//result.append(lineSep);	
		result.append("delete from TENDINE where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		
		result.append("delete from QUESTIONARIO_RISP where cod_quest like '" + cod_quest + "%';" + lineSep);
		result.append("delete from QUESTIONARIO_DOM where cod_quest like '" + cod_quest + "%';" + lineSep);
		result.append("delete from QUESTIONARIO where cod_quest like '" + cod_quest + "%';" + lineSep);
		
		//result.append("delete from MAPPA where cod_mappa in (select cod_mappa from percorsi where cod_convenzione = '"+ cod_conv +"');" + lineSep);
		result.append("delete from MAPPA where cod_mappa in (select cod_mappa from percorsi where cod_convenzione = '"+ cod_conv +"') and cod_mappa not in (select cod_mappa from percorsi where cod_convenzione != '"+ cod_conv +"');" + lineSep);
		//result.append(createDeleteScript("MAPPA", "cod_mappa in (select cod_mappa from percorsi where cod_convenzione = '"+ cod_conv +"')"));
		//result.append(lineSep);		
		result.append("delete from PERCORSI where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		
		result.append("delete from TARIFFAZIONE where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from PERIODICITA where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from REGOLE_OGGETTO_INDIVIDUO where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from REGOLE_NUCLEO where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from IMPORTI where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from SOMME_ASSIC where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from GARANZIA where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from PACCHETTO where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append("delete from CONVENZIONE where cod_convenzione = '"+ cod_conv +"';" + lineSep);
		result.append(lineSep);
		
		// inserisco la nuova convenzione
		result.append(createScript("CONVENZIONE", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("PACCHETTO", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("GARANZIA", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("SOMME_ASSIC", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("IMPORTI", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("REGOLE_NUCLEO", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("REGOLE_OGGETTO_INDIVIDUO", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("PERIODICITA", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		result.append(createScript("TARIFFAZIONE", "cod_convenzione = '"+ cod_conv +"'"));
		result.append(lineSep);
		
		// inserisco il percorso di navigazione
		result.append(createMergeScript("MAPPA", "cod_mappa in (select cod_mappa from percorsi where cod_convenzione = '"+ cod_conv +"')", "target.cod_mappa = source.cod_mappa", "cod_mappa"));
		result.append(lineSep);
		result.append(createScript("MAPPA", "cod_mappa in (select cod_mappa from percorsi where cod_convenzione = '"+ cod_conv +"')", true));
		result.append(lineSep);
		result.append(createScript("PERCORSI", "cod_convenzione = '"+ cod_conv +"' and TIPO_PERCORSO = 'EMISSIONE'"));
		result.append(lineSep);
		result.append(createScript("PERCORSI", "cod_convenzione = '"+ cod_conv +"' and TIPO_PERCORSO = 'POSTVENDITA'"));
		result.append(lineSep);
		result.append(createScript("PERCORSI", "cod_convenzione = '"+ cod_conv +"' and TIPO_PERCORSO = 'PREVPROSPECT'"));
		result.append(lineSep);
		
		result.append(createScript("QUESTIONARIO", "cod_quest like '" + cod_quest + "%'"));
		result.append(lineSep);
		result.append(createScript("QUESTIONARIO_DOM", "cod_quest like '" + cod_quest + "%'"));
		result.append(lineSep);
		result.append(createScript("QUESTIONARIO_RISP", "cod_quest like '" + cod_quest + "%'"));
		result.append(lineSep);
		
		result.append(createScript("TENDINE", "cod_convenzione = '"+ cod_conv +"' order by id_tendina"));
		result.append(lineSep);
		result.append(createMergeScript("ATTRIB_TENDINE", "id_tendina in (select id_tendina from TENDINE where cod_convenzione = '"+ cod_conv +"')", "target.id_tendina = source.id_tendina and target.dsc_attributo = source.dsc_attributo", "id_tendina dsc_attributo)"));
		result.append(lineSep);
		result.append(createScript("ATTRIB_TENDINE", "id_tendina in (select id_tendina from TENDINE where cod_convenzione = '"+ cod_conv +"') order by id_tendina", true));
		result.append(lineSep);
		
		result.append("set define on;" + lineSep);
		result.append("commit;" + lineSep);
		closeConnection();

		scriptSQL = result.toString();
		try {
			saveScript("e:/appoggio/3-Polizza "+cod_conv+".sql");
		} catch (Exception x) {
        	logger.error(x.getMessage(), x);
		}
	}

	public void saveScript(String nomeFile) throws IOException {
		FileWriter fw = null;
		try {
			fw = new FileWriter(nomeFile);
			fw.write(scriptSQL);
			fw.close();
		} catch (Exception e) {
			fw.close();
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// MAIN di DEBUG
	static public void main(String[] args) {
		OracleScriptMaker dbscript = new OracleScriptMaker();
		try {
			//POLIZZE DANNI
			//dbscript.doWork("01INF", "008");
			//dbscript.doWork("01PPT", "006");
			//dbscript.doWork("02PPT", "022");
			//dbscript.doWork("01PPI", "015");
			//dbscript.doWork("02MAL", "016");
			//dbscript.doWork("01DEN", "023");
			//dbscript.doWork("01RCV", "030");
			
			
/*			
			dbscript.doWork("01INF", "008");
			dbscript.doWork("01MAL", "009");
			dbscript.doWork("08IBA", "006");
			dbscript.doWork("01ISE", "010");
			dbscript.doWork("02GFA", "011");
			dbscript.doWork("01MSH", "014");
			dbscript.doWork("01PPI", "015");
			dbscript.doWork("02PPI", "015");
			dbscript.doWork("02MAD", "017");
			dbscript.doWork("10PCA", "018");
			dbscript.doWork("03ISE", "010");
*/
//			dbscript.doWork("03RCP", "028");
//			dbscript.doWork("02INF", "032");
			
			
			//dbscript.doWork("07INF", "002");
			//dbscript.doWork("07MAL", "003");
			//dbscript.doWork("07ISE", "005");
			//dbscript.doWork("02ISE", "010");
			//dbscript.doWork("09PCA", "012");
			//dbscript.doWork("11PCA", "017");
			//dbscript.doWork("RI001", "018");
			//dbscript.doWork("C01", "019");
			//dbscript.doWork("C03", "020");
			//dbscript.doWork("C04", "021");
			//dbscript.doWork("I01", "022");
			//dbscript.doWork("LTCC1", "019");
			//dbscript.doWork("LTCD1", "019");

			//POLIZZE VITA
			//dbscript.doWork("PAGD7", "026");
			//dbscript.doWork("PAG07", "026");
			//dbscript.doWork("MRU01", "  ");
			//dbscript.doWork("MRUD1", "  ");
			//dbscript.doWork("V_MRU", "  ");
			//dbscript.doWork("REND2", "  ");
			//dbscript.doWork("REND4", "  ");
			//dbscript.doWork("RIC05", "  ");
			//dbscript.doWork("RIC10", "  ");
			//dbscript.doWork("RR001", "  ");
			//dbscript.doWork("V_INL", "  ");
			//dbscript.doWork("V_LTC", "  ");
			//dbscript.doWork("V_PIP", "  ");
			//dbscript.doWork("V_TCM", "  ");
			//dbscript.doWork("V_TM1", "  ");
			//dbscript.doWork("V_TMG", "  ");
			//dbscript.doWork("V_UUL", "  ");
			//dbscript.doWork("V_SCU", "  ");
			dbscript.doWork("MRU04", "  ");
			//dbscript.doWork("V_TM2", "  ");
			

		} catch (Exception x) {
        	logger.error(x.getMessage(), x);
		}
	}
}