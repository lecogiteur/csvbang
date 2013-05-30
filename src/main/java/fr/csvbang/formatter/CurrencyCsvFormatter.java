/**
 *  fr.csvbang.formatter.CurrencyCsvFormatter
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

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import fr.csvbang.util.CsvbangUti;

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
	private NumberFormat format;
	
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
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#init()
	 * 
	 * @since 0.0.1
	 */
	public void init() {
		if (locale == null){
			locale = Locale.US;
		}
		format = NumberFormat.getCurrencyInstance(locale);
		format.setCurrency(Currency.getInstance(locale));
		if (CsvbangUti.isStringNotBlank(pattern)){
			String[] nb = pattern.split("\\.");
			
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
				format.setMaximumFractionDigits(nb[1].length());
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
	 * @see fr.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @see java.text.NumberFormat
	 * @since 0.0.1
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#setLocal(java.util.Locale)
	 * 
	 * @since 0.0.1
	 */
	public void setLocal(Locale locale) {
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.formatter.CsvFormatter#format(java.lang.Object, java.lang.String)
	 * 
	 * @since 0.0.1
	 */
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		return format.format(o);
	}

}
