/**
 *  com.github.lecogiteur.csvbang.util.ObjectGenerator
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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
package com.github.lecogiteur.csvbang.util;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Generate a new instance of a class using a constructor, or static method. Used in order to generates value of CSV field with complex type.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ObjectGenerator<T> {
	
	/**
	 * Generate value of a field. We create a new instance of type of field. 
	 * The generator manage constructor, method using interface of subclass.
	 * @param value value from CSV file.
	 * @return a value converted to type of field.
	 * @throws CsvBangException if a problem has occurred when we generated the new instance. 
	 * @since 1.0.0
	 */
	public T generate(final Object value) throws CsvBangException;

}
