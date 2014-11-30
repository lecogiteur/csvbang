/**
 *  com.github.lecogiteur.csvbang.test.util.WriterThread
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
package com.github.lecogiteur.csvbang.test.util;

import java.util.List;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.writer.CsvWriter;


/**
 * @author Tony EMMA
 *
 */
public class WriterThread<T> implements Runnable {
	
	
	private CsvWriter<T> writer;
	
	private List<T> beans;
	
	

	/**
	 * @param writer
	 * @param beans
	 */
	public WriterThread(CsvWriter<T> writer, List<T> beans) {
		super();
		this.writer = writer;
		this.beans = beans;
	}



	/**
	 * {@inheritDoc}
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			for (int j=0; j<15; j++){
				int i=0;
				while (i<beans.size()){
					final List<T> l = beans.subList(i, Math.min(i + 1000, beans.size()));
					writer.write(l);
					//System.out.println(i + "-" + Math.min(i, beans.size()) + " --> " + l.size());
					i +=1000;
				}
			}
		} catch (CsvBangException e) {
			e.printStackTrace();
		}
	}

}
