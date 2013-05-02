/**
 *  fr.csvkuai.test.uti.WriterThread
 * 
 * 
 *  Copyright (C) 2011  Tony EMMA
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.csvbang.test.uti;

import java.util.List;

import fr.csvbang.exception.CsvBangException;
import fr.csvbang.writer.CsvWriter;

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
			int i=0;
			while (i<beans.size()){
				final List<T> l = beans.subList(i, Math.min(i + 1000, beans.size()));
				writer.write(l);
				System.out.println(i + "-" + Math.min(i, beans.size()) + " --> " + l.size());
				i +=1000;
			}
		} catch (CsvBangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
