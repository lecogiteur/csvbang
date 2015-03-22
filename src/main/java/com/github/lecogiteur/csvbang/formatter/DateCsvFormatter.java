/**
 *  com.github.lecogiteur.csvbang.formatter.DateCsvFormatter
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
package com.github.lecogiteur.csvbang.formatter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.github.lecogiteur.csvbang.exception.CsvBangRuntimeException;
import com.github.lecogiteur.csvbang.util.CsvBangDateFormat;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * Format and parse date value. This class and pattern are based on {@link SimpleDateFormat}
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
public class DateCsvFormatter implements CsvFormatter {
	
	/**
	 * Date format
	 * @since 0.1.0
	 */
	private CsvBangDateFormat format;
	
	/**
	 * Pattern of date
	 * @since 0.0.1
	 */
	private String pattern;
	
	/**
	 * the locale of date
	 * @since 0.0.1
	 */
	private Locale locale;

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	public void init() {
		if (locale == null){
			locale = Locale.US;
		}
		if (CsvbangUti.isStringBlank(pattern)){
			pattern = "MM/dd/yyyy";
		}
		format = new CsvBangDateFormat(pattern, locale);
	}

	/**
	 * {@inheritDoc}
	 * The pattern is required
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * 
	 * @see java.text.SimpleDateFormat#applyPattern(java.lang.String)
	 * 
	 * @since 0.0.1
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 * @since 0.0.1
	 */
	public void setLocal(Locale locale) {
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * @since 0.0.1
	 */
	public String format(Object o, final String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		if (o instanceof Calendar){
			return format.get().format(((Calendar)o).getTime());
		}
		return format.get().format(o);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#parse(java.lang.String, java.lang.Class)
	 * @since 1.0.0
	 */
	@Override
	public Object parse(final String value, final Class<?> typeOfReturn) {
		if (value == null || CsvbangUti.isStringBlank(value) || typeOfReturn == null){
			return null;
		}
		
		try {
			final Date date = format.get().parse(value.trim());
			if (Date.class.equals(typeOfReturn)){
				return date;
			}
			if (Calendar.class.equals(typeOfReturn)){
				final Calendar c = Calendar.getInstance(locale);
				c.setTime(date);
				return c;
			}
			if (Timestamp.class.equals(typeOfReturn)){
				return new Timestamp(date.getTime());
			}
			if (Long.class.equals(typeOfReturn)){
				return date.getTime();
			}
		} catch (ParseException e) {
			throw new CsvBangRuntimeException(String.format("We can't parse CSV value [%s] to the type [%s]. The formatter uses the date pattern: [%s] (See SimpleDateFormat).", 
					value, typeOfReturn, pattern), e);
		}
		return null;
	}

}
