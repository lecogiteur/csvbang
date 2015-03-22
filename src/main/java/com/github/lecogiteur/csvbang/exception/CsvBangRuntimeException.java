/**
 *  com.github.lecogiteur.csvbang.exception.CsvBangRuntimeException
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
package com.github.lecogiteur.csvbang.exception;

/**
 * CsvBang runtime exception
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsvBangRuntimeException extends RuntimeException {

	/**
	 * @since 1.0.0
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @since 1.0.0
	 */
	public CsvBangRuntimeException() {
		super();
	}

	/**
	 * Constructor
	 * @param message
	 * @param cause
	 * @since 1.0.0
	 */
	public CsvBangRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 * @since 1.0.0
	 */
	public CsvBangRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause
	 * @since 1.0.0
	 */
	public CsvBangRuntimeException(Throwable cause) {
		super(cause);
	}
	
	

}
