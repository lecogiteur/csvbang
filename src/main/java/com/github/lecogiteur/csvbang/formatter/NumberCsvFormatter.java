/**
 *  com.github.lecogiteur.csvbang.formatter.NumberCsvFormatter
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.lecogiteur.csvbang.exception.CsvBangRuntimeException;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Format and parse number. This class is based on {@link DecimalFormat}.
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
public class NumberCsvFormatter implements CsvFormatter {
	
	/**
	 * Number format
	 * @since 0.0.1
	 */
	private DecimalFormat format;
	
	/**
	 * the pattern of number
	 * @since 0.0.1
	 */
	private String pattern;

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * @since 0.0.1
	 */
	public void init() {
		format = new DecimalFormat(pattern);
		format.setParseBigDecimal(true);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @see java.text.DecimalFormat
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
		//do nothing
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * @since 0.0.1
	 */
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		return format.format(o);
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
		
		if (String.class.equals(typeOfReturn)){
			return value;
		}
		
		try {
			BigDecimal number = (BigDecimal) format.parse(value.trim());
			number = number.setScale(format.getMaximumFractionDigits(), RoundingMode.HALF_UP);
			
			if (BigDecimal.class.equals(typeOfReturn)){
				return number;
			}
			if (Double.class.equals(typeOfReturn)){
				return number.doubleValue();
			}
			if (Float.class.equals(typeOfReturn)){
				return number.floatValue();
			}
			if (Integer.class.equals(typeOfReturn)){
				return number.setScale(0, RoundingMode.HALF_UP).intValue();
			}
			if (BigInteger.class.equals(typeOfReturn)){
				return number.setScale(0, RoundingMode.HALF_UP).toBigInteger();
			}
			if (AtomicInteger.class.equals(typeOfReturn)){
				return new AtomicInteger(number.setScale(0, RoundingMode.HALF_UP).intValue());
			}
			if (Long.class.equals(typeOfReturn)){
				return number.setScale(0, RoundingMode.HALF_UP).longValue();
			}
			if (AtomicLong.class.equals(typeOfReturn)){
				return new AtomicLong(number.setScale(0, RoundingMode.HALF_UP).longValue());
			}
			if (Byte.class.equals(typeOfReturn)){
				return number.setScale(0, RoundingMode.HALF_UP).byteValue();
			}
			if (Short.class.equals(typeOfReturn)){
				return number.setScale(0, RoundingMode.HALF_UP).shortValue();
			}
		} catch (ParseException e) {
			throw new CsvBangRuntimeException(String.format("We can't parse value [%s] to type [%s].", value, typeOfReturn), e);
		}
		return null;
	}

}
