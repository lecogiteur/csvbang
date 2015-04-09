/**
 *  com.github.lecogiteur.csvbang.annotation.CsvFormat
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
package com.github.lecogiteur.csvbang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.formatter.Default;


/**
 * Annotation in order to format value of a field
 * @author Tony EMMA
 * @version 0.0.4
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvFormat {

	/**
	 * Type of format
	 * @since 0.0.1
	 *
	 */
	public enum TYPE_FORMAT {
		
		/**
		 * Default formatter. If value is null, it will be replace by an empty string.
		 * @since 0.0.1
		 */
		DEFAULT,
		
		/**
		 * <p>Delete all carriage return in field value and replace them by a pattern (By default a space).</p> 
		 * <p>You can define the end line with its replacement by this syntax: <i>[end line characters]-->[replacement characters]</i>. For example, you can set pattern to: <code>\n-->MYENDLINE</code></p>
		 * <p>If you don't define <i>[end line characters]</i>, the end line pattern used is \r?\n. For example if you set pattern with: <code>MYENDLINE</code>, all end lines which match with \r?\n pattern, will be replaced. 
		 * if you don't define <i>[end line characters]</i>, when we parse CSV file we replace <i>[replacement characters]</i> by \n character.</p>
		 * @since 0.0.4
		 */
		NO_CARRIAGE_RETURN,
		
		/**
		 * <p>Format a date. The pattern is based on {@link java.text.SimpleDateFormat} pattern.</p>
		 * 
		 * <p>The type of field can be {@link java.util.Date java.util.Date}, {@link java.sql.Timestamp} or {@link java.util.Calendar}.</p>
		 * 
		 * <p>The default locale is Locale.FRANCE and the default pattern is MM/dd/yyyy</p>
		 * @see java.text.SimpleDateFormat
		 * @since 0.0.1
		 */
		DATE, 
		
		/**
		 * <p>Format a number. The pattern is based on {@link java.text.DecimalFormat} pattern. The pattern is required.</p>
		 * 
		 * <p>The type of field can be {@link java.lang.Integer}, {@link java.lang.Long}, {@link java.lang.Float} or {@link java.lang.Double}.</p>
		 * 
		 * @see java.text.DecimalFormat
		 * @since 0.0.1
		 */
		NUMBER, 
		
		/**
		 * 
		 * <p>Format a price. The pattern indicates the number of digits for integer part and fraction part.</p>
		 * 
		 * Example.
		 * <ul>
		 * 	<li>pattern (0.00) : 12.356 ==> 2.36 </li>
		 * 	<li>pattern (.00) : 12.356 ==> 12.36 </li>
		 * 	<li>pattern (.##) : 12.356 ==> 12.36 </li>
		 * 	<li>pattern (##.) : 12.356 ==> 12.356 </li>
		 * 	<li>pattern (###.##) : 12.356 ==> 12.36 </li>
		 * 	<li>pattern (000.##) : 12.356 ==> 012.36 </li>
		 * 	<li>no pattern : 12.356 ==> 12.36 </li>
		 * </ul>
		 * 
		 * <p>The locale must to set the country variable. The pattern is not required.</p>
		 * 
		 * <p>The type of field can be {@link java.lang.Integer}, {@link java.lang.Long}, {@link java.lang.Float} or {@link java.lang.Double}.</p>
		 * <p>
		 * The default local is Locale.FRANCE. No pattern defines.
		 * </p>
		 * 
		 * @see java.text.NumberFormat
		 * @since 0.0.1
		 */
		CURRENCY, 
		
		/**
		 * No format
		 * @since 0.0.1
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
		 * 	<li><b>y/n</b>: y or n</li>
		 * 	<li><b>Y/N</b>: Y or N</li>
		 * 	<li><b>o/n</b>: o or n</li>
		 * 	<li><b>O/N</b>: O or N</li>
		 * 	<li><b>on/off</b>: on or off</li>
		 * 	<li><b>On/Off</b>: On or Off</li>
		 * 	<li><b>ON/OFF</b: ON or OFF</li>
		 * 	<li><b>litteral</b>: yes or no in function locale</li>
		 * 	<li><b>Litteral</b>: Yes or No in function locale</li>
		 * 	<li><b>LITTERAL</b>: YES or NO in function locale</li>
		 * </ul>
		 * <p>
		 * The default local is Locale.FRANCE and the default pattern is "boolean"
		 * </p>
		 * @since 0.0.1
		 */
		BOOLEAN, 
		
		
		/**
		 * Custom format. The class must implement {@link CsvFormatter}
		 * @since 0.0.1
		 */
		CUSTOM
		}
	
	/**
	 * Type of format. By default NONE
	 * @return the type of format.
	 */
	TYPE_FORMAT type() default TYPE_FORMAT.NONE;
	
	/**
	 * Pattern in function type.
	 * @return the pattern
	 * 
	 * @since 0.0.1
	 */
	String pattern() default "";
	
	/**
	 * Custom format class
	 * 
	 * @return custom formatter
	 * 
	 * @see CsvFormatter
	 * 
	 * @since 0.0.1
	 */
	Class<? extends CsvFormatter> customFormatter() default Default.class;
	
	/**
	 * <p>locale. By Default Locale.French</P>
	 * 
	 * <p>Concatenation of language, country and variant separated by _</p>
	 * 
	 * @return the result of {@link Locale#toString()}
	 * 
	 * @since 0.0.1
	 */
	String locale() default "fr_FR_";
}
