package com.github.lecogiteur.csvbang.test.bean.reader;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;

@CsvType(delimiter=";")
@CsvHeader(header=true)
@CsvFile(registerThread=false)
public class Read1 {

	@CsvField(name="ID", position=1)
	private int id;
	
	@CsvField(name="Nom", position=2)
	private String text;
	
	@CsvField(name="Length", position=3)
	private Integer nbChar;
	
	@CsvField(name="Date", position=4)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd/MM/yyyy")
	private Calendar date;

	/**
	 * Get the id
	 * @return the id
	 * @since 1.0.0
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id
	 * @param id the id to set
	 * @since 1.0.0
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the text
	 * @return the text
	 * @since 1.0.0
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text
	 * @param text the text to set
	 * @since 1.0.0
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Get the nbChar
	 * @return the nbChar
	 * @since 1.0.0
	 */
	public Integer getNbChar() {
		return nbChar;
	}

	/**
	 * Set the nbChar
	 * @param nbChar the nbChar to set
	 * @since 1.0.0
	 */
	public void setNbChar(Integer nbChar) {
		this.nbChar = nbChar;
	}

	/**
	 * Get the date
	 * @return the date
	 * @since 1.0.0
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * Set the date
	 * @param date the date to set
	 * @since 1.0.0
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	
}
