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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.CsvbangUti;


/**
 * Writer by block
 * @author Tony EMMA
 * @version 0.0.1
 */
public class BlockingCsvWriter<T> extends AbstractWriter<T> {
	
	/**
	 * the buffer
	 * @since 0.0.1
	 * 
	 */
	private BlockingQueue<CharSequence> buffer;

	/**
	 * Constructor
	 * @param file CSV file
	 * @since 0.0.1
	 */
	public BlockingCsvWriter(final File file, final CsvBangConfiguration conf) {
		super(file, conf);
		this.file = file;
		buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
	}
	
	/**
	 * Constructor
	 * @param file path of CSV file
	 * @since 0.0.1
	 */
	public BlockingCsvWriter(final String file, final CsvBangConfiguration conf) {
		super(file, conf);
		if (file != null){
			buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
		}
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
			final StringBuilder sLine = isComment?writeComment(line):writeLine(line);
			if (sLine != null){
				while (!buffer.offer(sLine)){
					emptyQueue();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 0.0.1
	 */
	public void close() throws CsvBangException {
		emptyQueue();
		try {
			if (out != null){
				out.close();
			}
		} catch (IOException e) {
			throw new CsvBangException("An error has occured when closed file", e);
		}
	}
	
	/**
	 * Empty the buffer
	 * 
	 * @throws CsvBangException if a problem has occurred when write in file 
	 * @since 0.0.1
	 */
	private void emptyQueue() throws CsvBangException{
		if (out == null){
			open();
		}
		final Collection<CharSequence> list = new ArrayList<CharSequence>(); 
		buffer.drainTo(list);
		final StringBuilder lines = new StringBuilder(buffer.size() * defaultLineSize);
		for (final CharSequence s:list){
			lines.append(s);
		}
		
		if (lines.length() > 0){
			byte[] bTab = null;
			try {
				bTab = lines.toString().getBytes(conf.charset);
			} catch (UnsupportedEncodingException e) {
				throw new CsvBangException(String.format("The charset for [%s] is not supported: %s", 
						file.getAbsolutePath(), conf.charset), e);
			}
			final ByteBuffer bb = ByteBuffer.allocateDirect(bTab.length);
			bb.put(bTab);
			bb.flip();
			try {
				out.getChannel().write(bb);
			} catch (IOException e) {
				throw new CsvBangException(String.format("An error has occured [%s]: %s", file.getAbsolutePath(), lines), e);
			}
		}
	}

}
