/**
 *  com.github.lecogiteur.csvbang.test.bean.csvparser.NoFooterCsvParserBean
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
import com.github.lecogiteur.csvbang.annotation.CsvFooter;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@CsvType
@CsvFooter(noEndRecordOnLastRecord=false)
public class NoFooterCsvParserBean {
	
	@CsvField(position=1, name="myField1")
	public String field1;
	
	@CsvField(position=2, name="myField2")
	public String field2;
	
	@CsvField(position=3)
	private String field3;
	
	@CsvField(position=4, name="the field")
	private String field5;

	/**
	 * Get the field3
	 * @return the field3
	 * @since 1.0.0
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * Set the field3
	 * @param field3 the field3 to set
	 * @since 1.0.0
	 */
	public void setField3(String field3) {
		this.field3 = field3;
	}

	/**
	 * Get the field5
	 * @return the field5
	 * @since 1.0.0
	 */
	public String getField5() {
		return field5;
	}

	/**
	 * Set the field5
	 * @param field5 the field5 to set
	 * @since 1.0.0
	 */
	public void setField5(String field5) {
		this.field5 = field5;
	}

}
