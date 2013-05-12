/**
 *  fr.csvbang.formatter.DateCsvFormatter
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.csvbang.util.CsvbangUti;

/**
 * @author Tony EMMA
 *
 */
public class DateCsvFormatter implements CsvFormatter {
	
	private SimpleDateFormat format;
	
	private String pattern;
	
	private Locale locale;

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#init()
	 */
	public void init() {
		if (locale == null){
			locale = Locale.US;
		}
		if (CsvbangUti.isStringBlank(pattern)){
			pattern = "MM/dd/yyyy";
		}
		format = new SimpleDateFormat(pattern, locale);
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
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 */
	public String format(Object o, final String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		if (o instanceof Calendar){
			return format.format(((Calendar)o).getTime());
		}
		return format.format(o);
	}

}
