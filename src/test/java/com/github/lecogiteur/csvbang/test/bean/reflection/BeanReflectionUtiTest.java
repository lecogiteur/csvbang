/**
 *  com.github.lecogiteur.csvbang.test.bean.BeanReflectionUtiTest
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
package com.github.lecogiteur.csvbang.test.bean.reflection;

import com.github.lecogiteur.csvbang.annotation.CsvComment;
import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 *
 */
@CsvType
public class BeanReflectionUtiTest {

	@CsvField
	@CsvFormat
	public String publicField = "publicField";

	public int intField = 20;

	public Double doubleField = 30.5d;

	private String privateField = "privateField";
	
	private String otherPrivateField = "otherPrivateField";

	protected String protectedField = "protectedField";

	protected String othrProtectedField = "othrProtectedField";
	
	@CsvComment
	public Integer comment;
	
	public String getPrivateField(){
		return privateField;
	}
	
	public String getProtectedField(){
		return protectedField;
	}
	
	public String getPublicField(){
		return "publicMethod";
	}
	
	public String simpleMethod(){
		return "simpleMethod";
	}
	
	protected String protectedMethod(){
		return "protectedMethod";
	}
	
	@SuppressWarnings("unused")
	private String privateMethod(){
		return "privateMethod";
	}
	
	public int intMethod(){
		return -20;
	}
	
	public Double doubleMethod(){
		return -30.5d;
	}
	
	public String getOther(){
		return otherPrivateField;
	}
	
	
}
