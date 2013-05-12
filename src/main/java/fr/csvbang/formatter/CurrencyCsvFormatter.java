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
 * @author Tony EMMA
 *
 */
public class CurrencyCsvFormatter implements CsvFormatter {
	
	private NumberFormat format;
	
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
	 * @see fr.csvbang.formatter.CsvFormatter#setPattern(java.lang.String)
	 * @see {@link NumberFormat} format class 
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
	public String format(Object o, String defaultIfNull) {
		if (o == null){
			return defaultIfNull;
		}
		return format.format(o);
	}

}
