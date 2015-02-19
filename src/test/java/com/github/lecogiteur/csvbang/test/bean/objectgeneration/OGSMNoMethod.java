/**
 *  com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSMNoMethod
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
package com.github.lecogiteur.csvbang.test.bean.objectgeneration;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class OGSMNoMethod {

	private final Integer in;

	/**
	 * Constructor
	 * @param in
	 * @since 1.0.0
	 */
	public OGSMNoMethod(Integer in) {
		super();
		this.in = in;
	}
	
	public OGSMNoMethod(String in) {
		super();
		this.in = Integer.valueOf(in);
	}
	
	public OGSMNoMethod(Long in) {
		super();
		this.in = in.intValue();
	}
	
	public OGSMNoMethod(Boolean b) {
		if (b){
			this.in = 1;
		}else{
			this.in = 0;
		}
	}

	private static OGSMNoMethod valueOf(Boolean s){
		return new OGSMNoMethod(s);
	}

	protected static OGSMNoMethod valueOf(Long s){
		return new OGSMNoMethod(s);
	}

	/**
	 * Get the in
	 * @return the in
	 * @since 1.0.0
	 */
	public Integer getIn() {
		return in;
	}
	
}
