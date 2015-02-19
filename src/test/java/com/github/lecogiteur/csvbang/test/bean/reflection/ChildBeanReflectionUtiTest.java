/**
 *  com.github.lecogiteur.csvbang.test.bean.ChildBeanReflectionUtiTest
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

import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFooter;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;


/**
 * @author Tony EMMA
 *
 */
@CsvFile
@CsvHeader(header=true)
@CsvFooter
public class ChildBeanReflectionUtiTest extends BeanReflectionUtiTest {
	
	private String subclass = "subclass";

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.test.bean.reflection.BeanReflectionUtiTest#getProtectedField()
	 */
	@Override
	public String getProtectedField() {
		return protectedField + "subclass";
	}
	
	public void setProtectedField(String protectedField){
		this.protectedField = protectedField;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.test.bean.reflection.BeanReflectionUtiTest#simpleMethod()
	 */
	@Override
	public String simpleMethod() {
		return "simpleMethod2";
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.test.bean.reflection.BeanReflectionUtiTest#protectedMethod()
	 */
	@Override
	protected String protectedMethod() {
		return super.protectedMethod();
	}

	public String getSubclass(){
		return subclass;
	}
	
	public String getOtherPF(){
		return othrProtectedField;
	}
}
