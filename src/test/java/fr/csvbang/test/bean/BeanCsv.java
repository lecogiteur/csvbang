/**
 *  fr.csvbang.test.bean.BeanCsv
 * 
 *  Copyright (C) 2013  Tony EMMA
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
package fr.csvbang.test.bean;

import java.util.Date;
import java.util.List;

import fr.csvbang.annotation.CsvField;
import fr.csvbang.annotation.CsvFormat;
import fr.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import fr.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 *
 */
@CsvType(header=true, blocksize=10000, asynchronousWriter=true)
public class BeanCsv {

	@CsvField(name="fieldName", position=2)
	private String name;
	
	@CsvField(position=1)
	private String value;
	
	@CsvField(position=5)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd/yyyy")
	private Date madate;
	
	@CsvField(position=6)
	@CsvFormat(type=TYPE_FORMAT.BOOLEAN, pattern="integer")
	private String yes;
	
	@CsvField
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="dd/MM/yyyy")
	private List<Date> dateYear;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the madate
	 */
	public Date getMadate() {
		return madate;
	}

	/**
	 * @param madate the madate to set
	 */
	public void setMadate(Date madate) {
		this.madate = madate;
	}

	/**
	 * @return the yes
	 */
	public String getYes() {
		return yes;
	}

	/**
	 * @param yes the yes to set
	 */
	public void setYes(String yes) {
		this.yes = yes;
	}

	/**
	 * @return the dateYear
	 */
	public List<Date> getDateYear() {
		return dateYear;
	}

	/**
	 * @param dateYear the dateYear to set
	 */
	public void setDateYear(List<Date> dateYear) {
		this.dateYear = dateYear;
	}
	
	
	
	
}
