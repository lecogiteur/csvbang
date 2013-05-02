/**
 *  fr.csvbang.formatter.NumberCsvFormatter
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
package fr.csvbang.formatter;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * @author Tony EMMA
 *
 */
public class NumberCsvFormatter implements CsvFormatter {
	
	private DecimalFormat format;
	
	private String pattern;

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#init()
	 */
	public void init() {
		format = new DecimalFormat(pattern);
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 */
	public void setLocal(Locale locale) {
		//do nothing
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 */
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		return format.format(o);
	}

}
