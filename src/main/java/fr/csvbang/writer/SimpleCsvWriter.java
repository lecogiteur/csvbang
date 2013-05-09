/**
 *  fr.csvbang.writer.SimpleCsvWriter
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
import java.util.Collection;

import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;

/**
 * @author Tony EMMA
 *
 */
public class SimpleCsvWriter<T> extends AbstractWriter<T> {

	/**
	 * Constructor
	 * @param file CSV file
	 */
	public SimpleCsvWriter(final File file, final CsvBangConfiguration conf) {
		super(file, conf);
	}
	
	/**
	 * Constructor
	 * @param file path of CSV file
	 */
	public SimpleCsvWriter(final String file, final CsvBangConfiguration conf) {
		super(file, conf);
	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#write(java.util.Collection)
	 */
	public void write(final Collection<T> lines) throws CsvBangException {
		if (lines == null || lines.size() == 0){
			return;
		}
		if (out == null){
			//if not open
			open();
		}
		final StringBuilder sLines = new StringBuilder(defaultLineSize * lines.size());
		
		for (final Object line:lines){
			final StringBuilder sLine = writeLine(line);
			if (sLine != null){
				sLines.append(sLine);
			}
		}
		
		if (sLines.length() > 0){
			byte[] bTab = null;
			try {
				bTab = sLines.toString().getBytes(conf.charset);
			} catch (UnsupportedEncodingException e) {
				throw new CsvBangException(String.format("The charset for [%s] is not supported: %s", file.getAbsolutePath(), conf.charset), e);
			}
			final ByteBuffer bb = ByteBuffer.allocateDirect(bTab.length);
			bb.put(bTab);
			bb.flip();
			try {
				out.getChannel().write(bb);
			} catch (IOException e) {
				throw new CsvBangException(String.format("An error has occured [%s]: %s", file.getAbsolutePath(), sLines), e);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 * @see fr.csvbang.writer.CsvWriter#close()
	 */
	public void close() throws CsvBangException {
		try {
			if (out != null){
				out.close();
			}
		} catch (IOException e) {
			throw new CsvBangException("An error has occured when closed file", e);
		}
	}

}
