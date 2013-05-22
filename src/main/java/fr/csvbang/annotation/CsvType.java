/**
 *  fr.csvbang.annotation.CsvType
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

import fr.csvbang.util.IConstantsCsvBang;

/**
 * A type which map a CSV file
 * 
 * @author Tony EMMA
 * @version 0.0.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvType {
	
	/**
	 * Delimiter between fields. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_DELIMITER}
	 * @return delimiter
	 * @since 0.0.1
	 */
	String delimiter() default IConstantsCsvBang.DEFAULT_DELIMITER;
	
	/**
	 * Charset of file. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_CHARSET_NAME}
	 * @return Charset
	 * @since 0.0.1
	 */
	String charsetName() default IConstantsCsvBang.DEFAULT_CHARSET_NAME;

	/**
	 * String to put at the start of record. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_START_RECORD}
	 * @return the start of record
	 * @since 0.0.1
	 */
	String startRecord() default IConstantsCsvBang.DEFAULT_START_RECORD;
	
	/**
	 * String to put at the end of record. By default the character {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_END_RECORD}
	 * @return end of record
	 * @since 0.0.1
	 */
	String endRecord() default IConstantsCsvBang.DEFAULT_END_RECORD;
	
	/**
	 * Size of buffer in number of record. Negative value means no buffer. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_BLOCKING_SIZE}
	 * @return the size in number of record
	 * @since 0.0.1
	 */
	int blocksize() default IConstantsCsvBang.DEFAULT_BLOCKING_SIZE;
	
	/**
	 * True if you want to write file asynchronously. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_ASYNCHRONOUS_WRITE}.
	 * <p>You can define the number of thread dedicate to write file in {@link fr.csvbang.factory.FactoryCsvWriter}. 
	 * By default the number of thread is the number of processor divide by 3.
	 * If you create several files in the same time, the thread will be share for each files.</p>
	 * @return True if you want to write asynchronously 
	 * @since 0.0.1
	 */
	boolean asynchronousWriter() default IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE;
	
	/**
	 * Display the header on the first line. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_HEADER}.
	 * The header is generated with the name of field. If no name is defined for a field, 
	 * we take the property name or method name
	 * @return True or false
	 * @since 0.0.1
	 */
	boolean header() default IConstantsCsvBang.DEFAULT_HEADER;
	
	/**
	 * Character in order to quote value. By default, no quote defined.
	 * @return character
	 * @since 0.0.1
	 */
	String quoteCharacter() default IConstantsCsvBang.DEFAULT_QUOTE_CHARACTER;
	
	/**
	 * Character in order to escape the quote character. By default {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_QUOTE_ESCAPE_CHARACTER}
	 * @return character
	 * @since 0.0.1
	 */
	char quoteEscapeCharacter() default IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER;
	
	/**
	 * <p>You can define a static file name. </p>
	 * <p>It is not required. You could define dynamically the filename in Factory.</p>
	 * <p>It is possible to mix configuration. For example you can define dynamically 
	 * the directory with the factory and a static file name with this annotation.</p>
	 * <p>If a file is defined by factory, it overrides the file created by the annotation</p>
	 * <p>By default no static filename defined</p>
	 * @return the file name
	 * @see fr.csvbang.factory.FactoryCsvWriter
	 * @since 0.0.1
	 */
	String fileName() default IConstantsCsvBang.DEFAULT_FILE_NAME;
	
	/**
	 * <p>True if you want to append csv data to a file (if it exist). If the file doesn't exist, it create a new file.</p>
	 * <p>False, if you already want to create a new file. By default, it's {@value fr.csvbang.util.IConstantsCsvBang#DEFAULT_APPEND_FILE}.</p>
	 * @return True if you want to append csv data to a file.
	 * @since 0.0.1
	 */
	boolean append() default IConstantsCsvBang.DEFAULT_APPEND_FILE;
}
