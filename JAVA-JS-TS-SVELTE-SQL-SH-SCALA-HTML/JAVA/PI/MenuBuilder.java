/*
 * Created on 30-dic-2003
 *
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.infogroup.poste.common;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.eng.poste.ebaas.exceptions.EBAASApplicationException;

/**
 * @author bugnoli
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MenuBuilder {
	private static Logger logger = LogManager.getLogger();
	//private static Properties menuFile;
	@SuppressWarnings("unused")
	private int currentProfile;
	private ArrayList<MenuElement> menu = new ArrayList<MenuElement>();
	
	private static HashMap<String, MenuDBElement> mapMenu;
	
//	private List menuDB;
	
	private static InputStream inputBea;
	
	public MenuBuilder() {}
	
	static {
		
		Context ctx=null;
		DataSource ds=null;
		Connection con=null;
		Statement stmt=null;
		ResultSet rs=null;
		
		// Al primo accesso da parte di un utente xarico gli elementi del menu dal DB e li inserisco in una HashMap una sola volta.
		try {
			
			// 03/03/2017 Modifiche BONUC per rendere statica l'esecuzione di questa parte di codice.
			ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(EBAASConf.getDataSourcePV());
			con = ds.getConnection();			
			stmt = con.createStatement();
			rs=stmt.executeQuery("select CODICE, LABEL, LINK, STRUTTURA from MENU_PVV");
			mapMenu = new HashMap<String, MenuDBElement>();
			while (rs.next()){
				MenuDBElement menuDBElement = new MenuDBElement();
				
				menuDBElement.setLabel(rs.getString("LABEL"));
				menuDBElement.setLink(rs.getString("LINK"));
				menuDBElement.setOperation(rs.getString("CODICE"));
				
				mapMenu.put(rs.getString("STRUTTURA").trim(), menuDBElement);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (con!=null) con.close();				
				logger.info("Connessione al DB chiusa correttamente");
			} catch(Exception x) {
				logger.error(x.getMessage());
				throw new EBAASApplicationException("Errore nella chiusura della connessione al DB");
			}
		}
		
	}
	
	



	public void setProfile(int profile) {
		// costruisco il menu' secondo il profile specificato
		currentProfile = profile;
		buildMenuElements();
	}


	
	
	private void fillMenuDB(ArrayList<MenuElement> elements, String key, MenuElement father) {
		int i = 1;
		
		String label = null;
		
		while (mapMenu.get(key+i) != null) {
			
				label = mapMenu.get(key+i).getLabel();
				String link = mapMenu.get(key+i).getLink();
				String operation = mapMenu.get(key+i).getOperation();
				MenuElement me = new MenuElement(operation, label, link, father);
				
				if (father == null) {
					elements.add(me);
				}
				
				fillMenuDB(elements,key+i+".", me);
			i++;	
		} 
	}
	
	
	
	private void buildMenuElements() {
		fillMenuDB(menu, "",null);
	}

	public String toString() {
		String result = "";
		Iterator<MenuElement> iter = menu.iterator();
		while (iter.hasNext()) {
			MenuElement me =  iter.next();
			result += me.toString() + "\n";
		}
		return result;
	}

	/**
	 * @return Returns the menu.
	 */
	public ArrayList<MenuElement> getMenu() {
		return menu;
	}

	public static void main(String[] args) {
		MenuBuilder mb = new MenuBuilder();
		//MenuBuilder mb = new MenuBuilder(Menu_PostVendita.getPostvendita(6));
		mb.setProfile(0);

	}

	public static MenuElement findMenuElement(ArrayList<MenuElement> menu, String voce) {
		if (menu != null) {
			Iterator<MenuElement> iter = menu.iterator();
			while (iter.hasNext()) {
				MenuElement me = iter.next();
				if (me.getOperation().equals(voce))
					return me;
				else {
					MenuElement meChild = findMenuElement(me.getChilds(), voce);
					if (meChild != null)
						return meChild;
				}
			}
		}
		return null;
	}

	public static MenuElement isPresent(ArrayList<MenuElement> menu, String codice) {
		if (menu != null) {
			Iterator<MenuElement> iter = menu.iterator();
			while (iter.hasNext()) {
				MenuElement me = iter.next();
				if (me.getOperation().equals(codice))
					return me;
			}
		}
		return null;
	}

	public static void resetAll(ArrayList<MenuElement> menu) {
		if (menu != null) {
			Iterator<MenuElement> iter = menu.iterator();
			while (iter.hasNext()) {
				MenuElement me = iter.next();
				me.setEnabled(false);
			}
		}
	}


	public static InputStream getInputBea() {
		return inputBea;
	}


	public static void setInputBea(InputStream inputBea) {
		MenuBuilder.inputBea = inputBea;
	}


}