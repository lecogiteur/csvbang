/**
 *  com.github.lecogiteur.csvbang.annotation.CsvComment
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

/**
 * Define a field or a method like a CSV comment.
 * This annotation doesn't override CsvField annotation.
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvComment {
	
	/**
	 * Define where insert the comment
	 * @since 0.1.0
	 */
	public enum DIRECTION {
		
		/**
		 * The comment is inserted before the record
		 * @since 0.1.0
		 */
		BEFORE_RECORD,
		
		
		/**
		 * The comment is inserted after the record
		 * @since 0.1.0
		 */
		AFTER_RECORD
	}
	
	/**
	 * Define where insert the comment. By default DIRECTION.BEFORE_RECORD;
	 * @return the direction
	 * @since 0.1.0
	 * @see DIRECTION
	 */
	DIRECTION direction() default DIRECTION.BEFORE_RECORD;

}
