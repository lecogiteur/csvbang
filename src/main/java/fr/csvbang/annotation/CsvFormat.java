/**
 *  fr.csvbang.annotation.CsvFormat
 * 
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
package fr.csvbang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

import fr.csvbang.formatter.CsvFormatter;
import fr.csvbang.formatter.Default;

/**
 * Annotation in order to format value of a field
 * @author Tony EMMA
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvFormat {

	/**
	 * Type of format
	 * @author Tony EMMA
	 *
	 */
	public enum TYPE_FORMAT {
		/**
		 * Format a date. The pattern is based on {@link java.text.SimpleDateFormat} pattern.
		 * 
		 * The type of field can be {@link java.util.Date}, {@link java.sql.Timestamp} or {@link java.util.Calendar}.
		 * 
		 * @see {@link java.text.SimpleDateFormat} used in order to format a date
		 */
		DATE, 
		
		/**
		 * Format a number. The pattern is based on {@link java.text.DecimalFormat} pattern.
		 * 
		 * The type of field can be {@link java.lang.Integer}, {@link java.lang.Long}, {@link java.lang.Float} or {@link java.lang.Double}.
		 * 
		 * @see {@link java.text.DecimalFormat} used in order to format number
		 */
		NUMBER, 
		
		/**
		 * 
		 * Format a price. The pattern is based on {@link java.text.NumberFormat} pattern.
		 * 
		 * The type of field can be {@link java.lang.Integer}, {@link java.lang.Long}, {@link java.lang.Float} or {@link java.lang.Double}.
		 * 
		 * @see {@link java.text.NumberFormat} used in order to format price
		 */
		CURRENCY, 
		
		/**
		 * No format
		 */
		NONE, 
		
		/**
		 * Format boolean value.
		 * 
		 * <BR /><BR />
		 * List of pattern:
		 * <ul>
		 * 	<li><b>boolean</b>: true or false</li>
		 * 	<li><b>Boolean</b>: True or False</li>
		 * 	<li><b>BOOLEAN</b>: TRUE or FALSE</li>
		 * 	<li><b>B</b>: T or F</li>
		 * 	<li><b>b</b>: t or f</li>
		 * 	<li><b>integer</b>: 1 or 0</li>
		 * 	<li><b>letterYN</b>: y or n</li>
		 * 	<li><b>LetterYN</b>: Y or N</li>
		 * 	<li><b>letterON</b>: o or n</li>
		 * 	<li><b>LetterON</b>: O or N</li>
		 * 	<li><b>on/off</b>: on or off</li>
		 * 	<li><b>On/Off</b>: On or Off</li>
		 * 	<li><b>ON/OFF</b: ON or OFF</li>
		 * 	<li><b>litteral</b>: yes or no in function locale</li>
		 * 	<li><b>Litteral</b>: Yes or No in function locale</li>
		 * 	<li><b>LITTERAL</b>: YES or NO in function locale</li>
		 * </ul>
		 */
		BOOLEAN, 
		
		
		/**
		 * Custom format. The class must implement {@link CsvFormatter}
		 */
		CUSTOM
		}
	
	TYPE_FORMAT type() default TYPE_FORMAT.NONE;
	
	/**
	 * Pattern in function type.
	 * @return the pattern
	 * 
	 * @author Tony EMMA
	 */
	String pattern() default "";
	
	/**
	 * Custom format class
	 * 
	 * @return custom formatter
	 * 
	 * @see {@link CsvFormatter} custom formatter must implement {@link CsvFormatter}
	 * 
	 * @author Tony EMMA
	 */
	Class<? extends CsvFormatter> customFormatter() default Default.class;
	
	/**
	 * locale. By Default Locale.French
	 * 
	 * 
	 * @return the result of {@link Locale#toString()}
	 * 
	 * @author Tony EMMA
	 */
	String locale() default "fr__";
}
