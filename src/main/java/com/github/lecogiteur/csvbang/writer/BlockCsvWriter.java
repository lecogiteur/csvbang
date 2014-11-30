/**
 *  com.github.lecogiteur.csvbang.writer.BlockingCsvWriter
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * Writer by block
 * @author Tony EMMA
 * @version 0.1.0
 */
public class BlockCsvWriter<T> extends AbstractWriter<T> {
	/**
	 * Line wrapper
	 * @author Tony EMMA
	 * @version 0.1.0
	 * @since 0.1.0
	 */
	private final class LinesWrapper{
		/**
		 * A line
		 * @since 0.1.0
		 */
		private Object line;
		/**
		 * True if it's a comment
		 * @since 0.1.0
		 */
		private boolean isComment = false;
	}
	
	/**
	 * the buffer
	 * @since 0.0.1
	 * 
	 */
	private final ConcurrentLinkedQueue<LinesWrapper> buffer;
	
	/**
	 * Number of writer task in action
	 * @since 0.1.0
	 */
	private final AtomicInteger isEnded = new AtomicInteger(0);

	/**
	 * Constructor
	 * @param pool pool of file
	 * @param conf the configuration
	 * @throws CsvBangException if a problem occurred during initialization
	 * @since 0.1.0
	 */
	public BlockCsvWriter(final CsvFilePool pool, final CsvBangConfiguration conf) throws CsvBangException {
		super(pool, conf);	
		buffer = new ConcurrentLinkedQueue<LinesWrapper>();
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
		for (final Object line:lines){
			final LinesWrapper wrapper = new LinesWrapper();
			wrapper.line = line;
			wrapper.isComment = isComment;
			buffer.offer(wrapper);
			if (buffer.size() > conf.blockSize){
				emptyQueue(false);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 0.1.0
	 */
	public void close() throws CsvBangIOException {
		try {
			emptyQueue(true);
			//close file
			int workers = isEnded.get();
			isClose = workers == 0;
			if (isClose){
				super.close();
			}else{
				throw new CsvBangCloseException(workers);
			}
		} catch (CsvBangException e) {
			throw new CsvBangIOException(String.format("Error has occurred on closing file."), e);
		}
	}
	
	/**
	 * Drain the queue in order to write them in file
	 * @param isEnd True if it's the last drain
	 * @return the list of line to write in file
	 * @since 0.1.0
	 */
	private Collection<LinesWrapper> drainQueue(final boolean isEnd){
		if (isEnd || buffer.size()>conf.blockSize){
			int i = 0;
			final Collection<LinesWrapper> c = new ArrayList<LinesWrapper>(conf.blockSize);
			while ((isEnd || i < conf.blockSize) && buffer.size() > 0){
				final LinesWrapper wrapper = buffer.poll();
				if (wrapper != null){
					c.add(wrapper);
					i++;
				}else{
					return c;
				}
			}
			return c;
		}
		return null;
	}
	
	/**
	 * Empty the buffer
	 * @param isEnd True if it's the last 
	 * @throws CsvBangException if a problem occurred during writing
	 * @since 0.1.0
	 */
	private final void emptyQueue(boolean isEnd) throws CsvBangException{
		isEnded.incrementAndGet();
		final Collection<LinesWrapper> wrappers = drainQueue(isEnd);
		if (CsvbangUti.isCollectionNotEmpty(wrappers)){
			//Submit the writing to the executor
			final StringBuilder result = new StringBuilder(wrappers.size() * defaultLineSize);
			int nbRealLine = 0;
			for (final LinesWrapper wrapper:wrappers){
				//generate content to add to file
				final StringBuilder sLine = generateLine(wrapper.line, wrapper.isComment);
				if (sLine != null){
					result.append(sLine);
					if (!wrapper.isComment){
						++nbRealLine;
					}
				}
			}

			//get a file in order to write
			final CsvFileContext file = filePool.getFile(nbRealLine, result.length());

			//write data
			file.write(result.toString());

		}
		isEnded.decrementAndGet();
	}

}
