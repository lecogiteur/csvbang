/**
 *  com.github.lecogiteur.csvbang.formatter.CurrencyCsvFormatter
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
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.lecogiteur.csvbang.exception.CsvBangRuntimeException;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * Format and parse currency value
 * @author Tony EMMA
 * @version 0.0.1
 */
public class CurrencyCsvFormatter implements CsvFormatter {
	
	/**
	 * Currency format.
	 * 
	 * @since 0.0.1
	 * 
	 */
	private DecimalFormat format;
	
	/**
	 * Pattern of currency
	 * 
	 * @since 0.0.1
	 */
	private String pattern;
	
	/**
	 * Locale of currency
	 * 
	 * @since 0.0.1
	 */
	private Locale locale;
	
	/**
	 * The amount has a maximum number of decimal defined
	 * @since 1.0.0
	 */
	private boolean hasDecimal;

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#init()
	 * 
	 * @since 0.0.1
	 */
	public void init() {
		if (locale == null){
			locale = Locale.US;
		}
		
		format = (DecimalFormat)DecimalFormat.getCurrencyInstance(locale);
		format.setCurrency(Currency.getInstance(locale));
		format.setParseBigDecimal(true);
		hasDecimal = true;
		if (CsvbangUti.isStringNotBlank(pattern)){
			String[] nb = ".".equals(pattern)?new String[]{"",""}:pattern.split("\\.");
			
			if (nb[0].contains("0") && nb[0].length() > 1){
				format.setMinimumIntegerDigits(nb[0].length());
				format.setMaximumIntegerDigits(nb[0].length());
			}else{
				if (nb[0].length() != 0){
					format.setMaximumIntegerDigits(nb[0].length());
				}
				format.setMinimumIntegerDigits(0);
			}
			
			if (nb.length == 1){
				format.setParseIntegerOnly(true);
			}else{
				if (!nb[1].equals("")){
					format.setMaximumFractionDigits(nb[1].length());
				}else{
					hasDecimal = false;
					// Upper limit on integer and fraction digits for BigDecimal and BigInteger
					format.setMaximumFractionDigits(Integer.MAX_VALUE);
				}
				if (nb[1].contains("0")){
					format.setMinimumFractionDigits(nb[1].length());
				}else{
					format.setMinimumFractionDigits(0);
				}
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The pattern indicates the number of digits for integer part and fraction part.</p>
	 * 
	 * Example.
	 * <ul>
	 * 	<li>pattern (0.00) : 12.356 ==> 2.36 $</li>
	 * 	<li>pattern (.00) : 12.356 ==> 12.36 $</li>
	 * 	<li>pattern (.##) : 12.356 ==> 12.36 $</li>
	 * 	<li>pattern (##.) : 12.356 ==> 12.356 $</li>
	 * 	<li>pattern (###.##) : 12.356 ==> 12.36 $</li>
	 * 	<li>pattern (000.##) : 12.356 ==> 012.36 $</li>
	 * 	<li>no pattern : 12.356 ==> 12.36 $</li>
	 * </ul>
	 * 
	 * <p>The pattern is not required.</p>
	 * 
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @see java.text.NumberFormat
	 * @since 0.0.1
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 * 
	 * @since 0.0.1
	 */
	public void setLocal(Locale locale) {
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * 
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
			if (hasDecimal){
				number = number.setScale(format.getMaximumFractionDigits(), RoundingMode.HALF_UP);
			}
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
