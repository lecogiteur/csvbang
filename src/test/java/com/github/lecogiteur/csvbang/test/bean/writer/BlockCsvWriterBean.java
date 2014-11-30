/**
 *  com.github.lecogiteur.csvbang.test.bean.writer.BlockCsvWriterBean
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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
package com.github.lecogiteur.csvbang.test.bean.writer;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFooter;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;

/**
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
@CsvType(delimiter=";", quoteCharacter="\"")
@CsvHeader(header=true, customHeader="test header\n")
@CsvFooter(noEndRecordOnLastRecord=true, customFooter="\ntest footer\nretest footer")
@CsvFile(append=true, asynchronousWriter=false, blocksize=1000, maxFileNumber=3, fileByFile=false, maxRecordByFile=18000, fileName="block-%n.csv")
public class BlockCsvWriterBean {

	@CsvField(position=1)
	private String name;
	
	public BlockCsvWriterBean(String name) {
		super();
		this.name = name;
	}

	/**
	 * Get the name
	 * @return the name
	 * @since 0.1.0
	 */
	public String getName() {
		return name;
	}



	/**
	 * Set the name
	 * @param name the name to set
	 * @since 0.1.0
	 */
	public void setName(String name) {
		this.name = name;
	}



	@CsvField(name="value", position=2)
	public int getValue(){
		return name.length();
	}
	
	
}
