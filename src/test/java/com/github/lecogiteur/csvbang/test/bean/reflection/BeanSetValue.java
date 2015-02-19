/**
 *  com.github.lecogiteur.csvbang.test.bean.reflection.BeanSetValue
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
package com.github.lecogiteur.csvbang.test.bean.reflection;

/**
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class BeanSetValue {
	
	private Integer value1;
	
	public Long value2;
	
	private String value3;

	/**
	 * Get the value1
	 * @return the value1
	 * @since 1.0.0
	 */
	public Integer getValue1() {
		return value1;
	}

	/**
	 * Set the value1
	 * @param value1 the value1 to set
	 * @since 1.0.0
	 */
	public void setValue1(Integer value1) {
		this.value1 = value1;
	}

	/**
	 * Get the value3
	 * @return the value3
	 * @since 1.0.0
	 */
	public String getValue3() {
		return value3;
	}

	/**
	 * Set the value3
	 * @param value3 the value3 to set
	 * @since 1.0.0
	 */
	public void setValue3(String value3) {
		this.value3 = value3;
	}

	/**
	 * Get the value2
	 * @return the value2
	 * @since 1.0.0
	 */
	public Long getValue2() {
		return value2;
	}
	
	
	

}
