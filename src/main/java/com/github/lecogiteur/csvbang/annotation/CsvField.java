/**
 *  com.github.lecogiteur.csvbang.annotation.CsvField
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

import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * Annotation in order to define a CSV field
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvField {

	/**
	 * Name of field in CSV file. If no name is defined, the name of member (field or method) is selected.
	 * By default no name is defined.
	 * @return name
	 * @since 0.0.1
	 */
	String name() default IConstantsCsvBang.DEFAULT_FIELD_NAME;
	
	/**
	 * <P>
	 * Position of field in file. The first position of the first field is 1. 
	 * It is not required that position numbers follow. The default position is -1.
	 * </p>
	 * <p>
	 * If no position is required you can set a negative position. If you want to override a {@link CsvField} annotation in sub class, you
	 * must set a negative value other than -1
	 * </p>
	 * @return the position of field
	 * @since 0.0.1
	 * 
	 */
	int position() default IConstantsCsvBang.DEFAULT_FIELD_POSITION;
	
	/**
	 * Default value if the value is null. By default, it's an empty string
	 * @return default value.
	 * @since 0.0.1
	 * 
	 */
	String defaultIfNull() default IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE;
	
	/**
	 * Delete field if the value is null. Delete field only for the current record. By default false.
	 * @return True if we must delete field.
	 * @since 0.0.1
	 * 
	 */
	boolean deleteIfNull() default IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL; 
}
