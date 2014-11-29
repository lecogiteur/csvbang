/**
 *  com.github.lecogiteur.csvbang.writer.AsynchronousCsvWriter
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
import java.util.concurrent.Callable;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.pool.CsvbangExecutorService;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * asynchronous writer
 * @author Tony EMMA
 * @version 0.1.0
 *
 */
public class AsynchronousCsvWriter<T> extends AbstractWriter<T> {
	
	/**
	 * Service which manages threads
	 * @since 0.1.0
	 */
	private CsvbangExecutorService executor;

	
	/**
	 * Constructor
	 * @param pool pool of files
	 * @param conf configuration
	 * @param serviceExecutor service which manages threads
	 * @throws CsvBangException if a problem occurred during initialization
	 * @since 0.1.0
	 */
	public AsynchronousCsvWriter(final CsvFilePool pool, final CsvBangConfiguration conf, final CsvbangExecutorService serviceExecutor) throws CsvBangException {
		super(pool, conf);
		executor = serviceExecutor;
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

		//Submit the writing to the executor
		executor.submit(this.hashCode(), new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				//generate content to add to file
				final StringBuilder result = new StringBuilder(lines.size() * defaultLineSize);
				for (final Object line:lines){
					final StringBuilder sLine = generateLine(line, isComment);
					if (sLine != null){
						result.append(sLine);
					}
				}
				
				//get a file in order to write
				final CsvFileContext file = filePool.getFile(isComment?0:lines.size(), result.length());

				//write data
				file.write(result.toString());

				return null;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 0.1.0
	 */
	public void close() throws CsvBangIOException {
		try {
			if (executor.awaitGroupTermination(this.hashCode())){
				//close file
				super.close();
			}
		} catch (CsvBangException e) {
			throw new CsvBangIOException(String.format("Error has occurred on closing file."), e);
		}

	}
}
