/**
 *  com.github.lecogiteur.csvbang.test.bean.SimpleConfigurationBean
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
package com.github.lecogiteur.csvbang.test.bean.configuration;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;


@CsvType
public class SimpleConfigurationBean {
	
	private String name;
	
	@CsvField(defaultIfNull="No Date", position=3)
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern="yyyyMMdd")
	private Calendar date;
	
	@CsvField(position=-2, name="TheYear")
	public int year;
	
	@CsvField(position=8)
	protected boolean old;
	
	public String notUsed;

	/**
	 * @return the name
	 */
	@CsvField(position=1, name="name")
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
	 * @return the date
	 */
	public Calendar getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}
	
	@CsvField(position=10)
	public double customMethod(){
		return 12.99;
	}
}
