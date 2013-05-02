/**
 *  fr.csvbang.annotation.CsvField
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

/**
 * Annotation in order to define a CSV field
 * @author Tony EMMA
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvField {

	/**
	 * Name of field in CSV file
	 * @return name
	 * 
	 * @author Tony EMMA
	 */
	String name() default "";
	
	/**
	 * Position of field in file. The first position of the first field is 1. 
	 * It is not required that position numbers follow.
	 * If no position is required you can set a negative position. 
	 * @return the position of field
	 * 
	 * @author Tony EMMA
	 */
	int position() default -1;
	
	/**
	 * Default value if the value is null. By default, it's an empty string
	 * @return default value.
	 * 
	 * @author Tony EMMA
	 */
	String defaultIfNull() default "";
}
