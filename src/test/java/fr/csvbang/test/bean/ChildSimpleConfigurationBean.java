/**
 *  fr.csvbang.test.bean.ChildSimpleConfigurationBean
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
package fr.csvbang.test.bean;

import java.util.Calendar;

import fr.csvbang.annotation.CsvField;
import fr.csvbang.annotation.CsvFormat;
import fr.csvbang.annotation.CsvFormat.TYPE_FORMAT;

/**
 * @author Tony EMMA
 *
 */
public class ChildSimpleConfigurationBean extends SimpleConfigurationBean{

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.test.bean.SimpleConfigurationBean#getName()
	 */
	@Override
	@CsvField(name="The Name", position=5)
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	
	
	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.test.bean.SimpleConfigurationBean#getDate()
	 */
	@Override
	@CsvField(position=-1, deleteIfNull=true)
	@CsvFormat(type=TYPE_FORMAT.NONE)
	public Calendar getDate() {
		// TODO Auto-generated method stub
		return super.getDate();
	}



	public boolean isOld(){
		return old;
	}



	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.test.bean.SimpleConfigurationBean#customMethod()
	 */
	@Override
	@CsvField(defaultIfNull="0")
	public double customMethod() {
		// TODO Auto-generated method stub
		return super.customMethod();
	}
}
