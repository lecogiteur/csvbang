/**
 *  com.github.lecogiteur.csvbang.test.bean.objectgeneration.OGFactoryNoDefaultConstNoStaticMethod
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
public class OGFactoryNoDefaultConstNoStaticMethod {
	
	protected static Integer valueOf(Boolean b){
		return b?1:0;
	}
	
	private Integer valueOf(Long l){
		return l.intValue();
	}
}
