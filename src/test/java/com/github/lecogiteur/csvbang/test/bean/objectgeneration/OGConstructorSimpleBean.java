/**
 *  com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGSimpleBean
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
public class OGConstructorSimpleBean {
	
	private Integer i;
	
	private String s;

	/**
	 * Constructor
	 * @param i
	 * @since 1.0.0
	 */
	public OGConstructorSimpleBean(Integer i) {
		super();
		this.i = i;
	}
	
	public OGConstructorSimpleBean(String i) {
		super();
		this.i = Integer.valueOf(i);
	}

	/**
	 * Constructor
	 * @param i
	 * @param s
	 * @since 1.0.0
	 */
	public OGConstructorSimpleBean(String s, Integer i) {
		super();
		this.i = i;
		this.s = s;
	}

	/**
	 * Get the i
	 * @return the i
	 * @since 1.0.0
	 */
	public Integer getI() {
		return i;
	}

	/**
	 * Set the i
	 * @param i the i to set
	 * @since 1.0.0
	 */
	public void setI(Integer i) {
		this.i = i;
	}

	/**
	 * Get the s
	 * @return the s
	 * @since 1.0.0
	 */
	public String getS() {
		return s;
	}

	/**
	 * Set the s
	 * @param s the s to set
	 * @since 1.0.0
	 */
	public void setS(String s) {
		this.s = s;
	}
}
