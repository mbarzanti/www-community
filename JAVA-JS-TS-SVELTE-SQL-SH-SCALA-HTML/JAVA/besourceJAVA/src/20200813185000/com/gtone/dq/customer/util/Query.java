package com.gtone.dq.customer.util;

/***************************************************************************
*                                                                           

*
**************************************************************************/


public class Query 
{
	private String insertQuery = "";	//SQL Query¹®
	private String deleteQuery = "";	//SQL Query¹®
	private String selectQuery = "";	//SQL Query¹®
	
	/*****

	 */
	public void selectQuery(String selectQuery){
		this.insertQuery = selectQuery;
	}

	/*****

	 */
	public String getSelectQuery(){
		return this.selectQuery;
	}

	/*****

	 */
	public void setInsertQuery(String insertQuery){
		this.insertQuery = insertQuery;
	}
	/*****

	 */
	public void setDeleteQuery(String deleteQuery){
		this.deleteQuery = deleteQuery;
	}
	/*****

	 */
	public String getInsertQuery(){
		return insertQuery;
	}
	/*****

	 */
	public String getDeleteQuery(){
		return deleteQuery;
	}
}