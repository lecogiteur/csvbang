/**
 *  com.github.lecogiteur.csvbang.test.bean.csvparser.BigDataCsvParser
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

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@CsvType
public class BigDataCsvParser {

	@CsvField(position=1)
	private Integer field1;
	
	@CsvField(position=2)
	private String field2;
	
	@CsvField(position=3)
	private Double field3;
	
	@CsvField(position=4)
	private Boolean field4;

	/**
	 * Get the field1
	 * @return the field1
	 * @since 1.0.0
	 */
	public Integer getField1() {
		return field1;
	}

	/**
	 * Set the field1
	 * @param field1 the field1 to set
	 * @since 1.0.0
	 */
	public void setField1(Integer field1) {
		this.field1 = field1;
	}

	/**
	 * Get the field2
	 * @return the field2
	 * @since 1.0.0
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * Set the field2
	 * @param field2 the field2 to set
	 * @since 1.0.0
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}

	/**
	 * Get the field3
	 * @return the field3
	 * @since 1.0.0
	 */
	public Double getField3() {
		return field3;
	}

	/**
	 * Set the field3
	 * @param field3 the field3 to set
	 * @since 1.0.0
	 */
	public void setField3(Double field3) {
		this.field3 = field3;
	}

	/**
	 * Get the field4
	 * @return the field4
	 * @since 1.0.0
	 */
	public Boolean getField4() {
		return field4;
	}

	/**
	 * Set the field4
	 * @param field4 the field4 to set
	 * @since 1.0.0
	 */
	public void setField4(Boolean field4) {
		this.field4 = field4;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 * @since 1.0.0
	 */
	@Override
	public String toString() {
		return "BigDataCsvParser [field2=" + field2 + ", field4=" + field4
				+ "]";
	}
	
	
}
