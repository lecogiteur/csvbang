/**
 *  com.github.lecogiteur.csvbang.writer.SimpleCsvWriter
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
package com.github.lecogiteur.csvbang.writer;

import java.util.Collection;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * Simple writer
 * @author Tony EMMA
 * @version 0.0.1
 */
public class SimpleCsvWriter<T> extends AbstractWriter<T> {

	/**
	 * Constructor
	 * @param file CSV file
	 * @throws CsvBangException 
	 * @since 0.0.1
	 */
	public SimpleCsvWriter(final CsvFilePool pool, final CsvBangConfiguration conf) throws CsvBangException {
		super(pool, conf);
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.AbstractWriter#internalWrite(java.util.Collection, boolean)
	 * @since 0.1.0
	 */
	@Override
	protected void internalWrite(final Collection<?> lines, final boolean isComment) throws CsvBangException {
		if (CsvbangUti.isCollectionEmpty(lines)){
			return;
		}
		
		final StringBuilder sLines = new StringBuilder(defaultLineSize * lines.size());
		
		for (final Object line:lines){
			final StringBuilder sLine = generateLine(line, isComment);
			if (sLine != null){
				sLines.append(sLine);
			}
		}
		
		filePool.getFile(isComment?0:lines.size(), sLines.length()).write(sLines.toString());
	}

}
