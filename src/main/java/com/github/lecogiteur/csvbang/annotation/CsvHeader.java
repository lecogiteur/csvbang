/**
 *  com.github.lecogiteur.csvbang.annotation.CsvHeader
 * 
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
 * Annotation in order to define header of CSV file
 * @author Tony EMMA
 * @version 0.1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvHeader {
	
	/**
	 * Display the header on the first line. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_HEADER}.
	 * The header is generated with the name of field and keeps the defined order of field. If no name is defined for a field, 
	 * we take the property name or method name
	 * @return True or false
	 * @since 0.1.0
	 */
	boolean header() default IConstantsCsvBang.DEFAULT_HEADER;
	
	
	/**
	 * You can define your custom static header. By default no custom header is defined. If {@link CsvHeader#header()} is set to true, the custom header is written before the generated header.
	 * @return the custom header
	 * @since 0.1.0
	 */
	String customHeader() default IConstantsCsvBang.DEFAULT_CUSTOM_HEADER;
}
