/**
 *  com.github.lecogiteur.csvbang.test.bean.writer.SimpleWriterBean
 * 
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
package com.github.lecogiteur.csvbang.test.bean.writer;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvComment;
import com.github.lecogiteur.csvbang.annotation.CsvComment.DIRECTION;
import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;


@CsvType
public class SimpleWriterBean {
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
	@CsvField(position=1, deleteIfNull=true)
	private Integer id;
	
	private String name;
	

	@CsvField(position=4, defaultIfNull="no date")
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern=DATE_PATTERN)
	private Calendar birthday;
	
	@CsvField(position=5)
	private Double price;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@CsvField(position=2)
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
	 * @return the birthday
	 */
	public Calendar getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@CsvField(position=3)
	public String getPublicName(){
		return "public Name: " + name;
	}

	public Double getMyCommentDouble(){
		return 145.15d;
	}
}
