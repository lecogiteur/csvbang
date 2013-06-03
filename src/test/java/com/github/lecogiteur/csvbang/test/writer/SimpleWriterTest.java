/**
 *  com.github.lecogiteur.csvbang.test.writer.SimpleWriterTest
 * 
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
package com.github.lecogiteur.csvbang.test.writer;

import java.util.Collection;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.writer.AbstractWriter;


public class SimpleWriterTest<T> extends AbstractWriter<T> {

	StringBuilder result = new StringBuilder();
	
	public SimpleWriterTest(CsvBangConfiguration conf) {
		super(conf);
	}

	@Override
	public void write(Collection<T> lines) throws CsvBangException {
		for(T line:lines){
			result.append(writeLine(line));
		}
	}
	
	public String getResult(){
		return result.toString();
	}

	@Override
	public void close() throws CsvBangException {
	}

}
