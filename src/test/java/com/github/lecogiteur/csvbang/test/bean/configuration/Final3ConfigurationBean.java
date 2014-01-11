/**
 *  com.github.lecogiteur.csvbang.test.bean.configuration.Final2ConfigurationBean
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
package com.github.lecogiteur.csvbang.test.bean.configuration;

import java.util.Calendar;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 *
 */
@CsvType(startRecord="**")
@CsvFile(fileName="test2.csv")
@CsvHeader(header=false, customHeader="")
public class Final3ConfigurationBean extends Child3SimpleConfigurationBean {

	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.test.bean.configuration.ChildSimpleConfigurationBean#customMethod()
	 */
	@Override
	@CsvField(name="custom")
	public double customMethod() {
		return super.customMethod();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.test.bean.configuration.ChildSimpleConfigurationBean#getDate()
	 */
	@Override
	@CsvField(position=-2)
	public Calendar getDate() {
		return super.getDate();
	}
}
