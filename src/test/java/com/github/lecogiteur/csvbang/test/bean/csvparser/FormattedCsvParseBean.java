/**
 *  com.github.lecogiteur.csvbang.test.bean.csvparser.FormattedCsvParseBean
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
 *
 *  This file is part of Csvbang.
 *  
 *  Csvbang is a comma-separated values ( CSV ) API, written in JAVA and thread-safe.
 *
 *  Csvbang is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *   
 *  Csvbang is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *   
 *  You should have received a copy of the GNU General Public License
 *  along with Csvbang. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.lecogiteur.csvbang.test.bean.csvparser;

import java.math.BigDecimal;
import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@CsvType(quoteCharacter="\"")
public class FormattedCsvParseBean {
	
	@CsvField(position=1)
	private Integer number;
	
	@CsvField(position=2)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd-MM-yyyy")
	private Calendar date;
	
	@CsvField(position=3)
	@CsvFormat(type=TYPE_FORMAT.CURRENCY, pattern=".00", locale="FR_fr")
	private BigDecimal cost;
	
	@CsvField(position=4, factory=NumberFactory.class, customMethodNameSetter="generate")
	@CsvFormat(type=TYPE_FORMAT.NUMBER, pattern="0.00")
	private Double anotherNumber;
	
	@CsvField(position=5)
	private String name;

	/**
	 * Get the number
	 * @return the number
	 * @since 1.0.0
	 */
	public Integer getNumber() {
		return number;
	}

	/**
	 * Set the number
	 * @param number the number to set
	 * @since 1.0.0
	 */
	public void setNumber(Integer number) {
		this.number = number;
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

	/**
	 * Get the cost
	 * @return the cost
	 * @since 1.0.0
	 */
	public BigDecimal getCost() {
		return cost;
	}

	/**
	 * Set the cost
	 * @param cost the cost to set
	 * @since 1.0.0
	 */
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	/**
	 * Get the name
	 * @return the name
	 * @since 1.0.0
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * @param name the name to set
	 * @since 1.0.0
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the anotherNumber
	 * @return the anotherNumber
	 * @since 1.0.0
	 */
	public Double getAnotherNumber() {
		return anotherNumber;
	}

	/**
	 * Set the anotherNumber
	 * @param anotherNumber the anotherNumber to set
	 * @since 1.0.0
	 */
	public void setAnotherNumber(Double anotherNumber) {
		this.anotherNumber = anotherNumber;
	}
	
	
}
