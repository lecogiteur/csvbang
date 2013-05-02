package fr.csvbang.test.bean;

import fr.csvbang.annotation.CsvField;

public class BeanChildCsv extends BeanCsv {
	
	
	private String date;
	
	private String dudu;

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the dudu
	 */
	@CsvField(name="dudu")
	public String getDudu() {
		return dudu;
	}

	/**
	 * @param dudu the dudu to set
	 */
	public void setDudu(String dudu) {
		this.dudu = dudu;
	}
	
	

}
