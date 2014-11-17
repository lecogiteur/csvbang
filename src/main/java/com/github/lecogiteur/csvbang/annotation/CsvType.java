/**
 *  com.github.lecogiteur.csvbang.annotation.CsvType
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

import com.github.lecogiteur.csvbang.util.EndLineType;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * A type which map a CSV file. This annotation is required in order to define a bean CSV.
 * 
 * @author Tony EMMA
 * @version 0.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvType {
	
	static final EndLineType end = IConstantsCsvBang.DEFAULT_END_LINE;
	
	/**
	 * Delimiter between fields. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_DELIMITER}
	 * @return delimiter
	 * @since 0.1.0
	 */
	String delimiter() default IConstantsCsvBang.DEFAULT_DELIMITER;
	
	/**
	 * Charset of file. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_CHARSET_NAME}
	 * @return Charset
	 * @since 0.0.1
	 */
	String charsetName() default IConstantsCsvBang.DEFAULT_CHARSET_NAME;

	/**
	 * String to put at the start of record. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_START_RECORD}
	 * @return the start of record
	 * @since 0.0.1
	 */
	String startRecord() default IConstantsCsvBang.DEFAULT_START_RECORD;
	
	/**
	 * String to put at the end of record. By default the character {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_END_RECORD}
	 * @return end of record
	 * @since 0.0.1
	 */
	String endRecord() default IConstantsCsvBang.DEFAULT_END_RECORD;
	
	/**
	 * Character in order to quote value. By default, no quote defined.
	 * @return character
	 * @since 0.0.1
	 */
	String quoteCharacter() default IConstantsCsvBang.DEFAULT_QUOTE_CHARACTER;
	
	/**
	 * Character in order to escape the quote character. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_QUOTE_ESCAPE_CHARACTER}
	 * @return character
	 * @since 0.0.1
	 */
	char quoteEscapeCharacter() default IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER;
	
	/**
	 * Define the character in order to comment a line or data. Each comments will be write on a new line. 
	 * If a field value has carriage return, a comment character will be added after the carriage return.
	 * By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_COMMENT_CHARACTER}
	 * @return comment character
	 * @since 0.1.0
	 * @see Comment
	 */
	char commentCharacter() default IConstantsCsvBang.DEFAULT_COMMENT_CHARACTER;
	
	/**
	 * Define the end line character used for comment, header, footer or end record (if not defined).
	 * @return the type of end line character. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_END_LINE}
	 * @since 0.1.0
	 * @see com.github.lecogiteur.csvbang.util.EndLineType
	 */
	EndLineType defaultEndLineCharacter() default EndLineType.LINE_FEED;
}
