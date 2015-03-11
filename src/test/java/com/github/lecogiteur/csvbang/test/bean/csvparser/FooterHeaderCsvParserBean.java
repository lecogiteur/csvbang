/**
 *  com.github.lecogiteur.csvbang.test.bean.csvparser.FooterHeaderCsvParserBean
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
import com.github.lecogiteur.csvbang.annotation.CsvHeader;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
@CsvHeader(header=true)
public class FooterHeaderCsvParserBean extends CustomFooterCsvParserBean {

	@CsvField(position=5)
	private Integer field6;

	/**
	 * Get the field6
	 * @return the field6
	 * @since 1.0.0
	 */
	public Integer getField6() {
		return field6;
	}

	/**
	 * Set the field6
	 * @param field6 the field6 to set
	 * @since 1.0.0
	 */
	public void setField6(Integer field6) {
		this.field6 = field6;
	}
	
	
}
