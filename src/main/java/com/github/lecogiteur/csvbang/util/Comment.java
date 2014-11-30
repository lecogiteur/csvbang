/**
 *  com.github.lecogiteur.csvbang.util.Comment
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
package com.github.lecogiteur.csvbang.util;

import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * Define a comment. 
 * A comment is define on one line. If value has carriage return, a comment character will be added after the carriage return. 
 * @author Tony EMMA
 * @version 0.1.0
 * @see CsvType
 *
 */
public class Comment {
	
	/**
	 * the comment
	 * @since 0.1.0
	 */
	private String comment;
	
	
	/**
	 * the default constructor
	 * @since 0.1.0
	 */
	public Comment(){
		comment = null;
	}
	
	/**
	 * Constructor
	 * @param value a comment (we used the method {@link Object#toString()}
	 * @since 0.1.0
	 */
	public Comment(final Object value){
		if (value != null){
			comment = value.toString();
		}
	}
	
	/**
	 * Constructor
	 * @param value a comment
	 * @since 0.1.0
	 */
	public Comment(final String value){
		comment = value;
	}

	/**
	 * the comment
	 * @return the comment
	 * @since 0.1.0
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * the comment
	 * @param comment the comment to set
	 * @since 0.1.0
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
