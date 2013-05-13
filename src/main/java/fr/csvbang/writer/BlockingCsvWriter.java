/**
 *  fr.csvbang.writer.BlockingCsvWriter
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
package fr.csvbang.writer;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.util.CsvbangUti;

/**
 * @author Tony EMMA
 *
 */
public class BlockingCsvWriter<T> extends AbstractWriter<T> {
	
	/**
	 * the buffer
	 */
	private BlockingQueue<CharSequence> buffer;

	/**
	 * Constructor
	 * @param file CSV file
	 */
	public BlockingCsvWriter(final File file, final CsvBangConfiguration conf) {
		super(file, conf);
		this.file = file;
		buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
	}
	
	/**
	 * Constructor
	 * @param file path of CSV file
	 */
	public BlockingCsvWriter(final String file, final CsvBangConfiguration conf) {
		super(file, conf);
		if (file != null){
			buffer = new ArrayBlockingQueue<CharSequence>(conf.blockingSize);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#write(java.util.Collection)
	 */
	public void write(final Collection<T> lines) throws CsvBangException {
		if (CsvbangUti.isCollectionEmpty(lines)){
			return;
		}
		for (final Object line:lines){
			final StringBuilder sLine = writeLine(line);
			if (sLine != null){
				while (!buffer.offer(sLine)){
					emptyQueue();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#close()
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
	 * @throws CsvBangException
	 * 
	 * @author Tony EMMA
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
