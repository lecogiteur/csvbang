/**
 *  com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration
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
package com.github.lecogiteur.csvbang.configuration;

import java.lang.reflect.AnnotatedElement;

import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * Configuration of field
 * @author Tony EMMA
 * @version 0.0.1
 */
public class CsvFieldConfiguration {
	
	/**
	 * Name of field. Used in order to generate header.
	 * @since 0.0.1
	 */
	public String name = IConstantsCsvBang.DEFAULT_FIELD_NAME;
	
	/**
	 * Position of field. Negative value means that field have no position
	 * @since 0.0.1
	 */
	public int position = IConstantsCsvBang.DEFAULT_FIELD_POSITION;
	
	/**
	 * property or method of bean
	 * @since 0.0.1
	 */
	public AnnotatedElement memberBean;
	
	/**
	 * String which replace null value
	 * @since 0.0.1
	 */
	public String nullReplaceString = IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE;
	
	/**
	 * Delete field if the value is null. Delete field only for the current record. By default false.
	 * @since 0.0.1
	 */
	public boolean isDeleteFieldIfNull = IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL;
	
	/**
	 * Format value
	 * @since 0.0.1
	 */
	public CsvFormatter format;
}
