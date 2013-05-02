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

/**
 * A type which map a CSV file
 * 
 * @author Tony EMMA
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvType {
	
	/**
	 * Delimiter between fields. By default {@value ,}
	 * @return delimiter
	 * 
	 * @author Tony EMMA
	 */
	String delimiter() default ",";
	
	/**
	 * Charset of file. By default {@value UTF-8}
	 * @return Charset
	 * 
	 * @author Tony EMMA
	 */
	String charsetName() default "UTF-8";

	/**
	 * String to put at the start of line. By default nothing
	 * @return the start of line
	 * 
	 * @author Tony EMMA
	 */
	String startLine() default "";
	
	/**
	 * String to put at the end of line. By default End of line {@value \n}
	 * @return end of line
	 * 
	 * @author Tony EMMA
	 */
	String endLine() default "\n";
	
	/**
	 * Size of buffer in number of line. Negative value means no buffer.
	 * @return the size in number of line
	 * 
	 * @author Tony EMMA
	 */
	int blocksize() default -1;
	
	/**
	 * Display the header on the first line. By default {@value false}
	 * @return True or false
	 * 
	 * @author Tony EMMA
	 */
	boolean header() default false;
	
	/**
	 * Character in order to quote value. By default, no quote.
	 * @return character
	 * 
	 * @author Tony EMMA
	 */
	String quoteCharacter() default "";
	
	/**
	 * Character in order to escape the quote character. By default {@value \}
	 * @return character
	 * 
	 * @author Tony EMMA
	 */
	char quoteEscapeCharacter() default '\\';
}
