/**
 *  com.github.lecogiteur.csvbang.parser.AbstractStringGrammarAction
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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
package com.github.lecogiteur.csvbang.parser;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.ByteStreamBuffer;

/**
 * Abstract action which generate string
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractStringGrammarAction extends AbstractGrammarAction<String> {
	
	/**
	 * CsvBang configuration
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * Buffer of bytes. Before we convert to a character sequence each bytes with a charset decoder
	 * @since 1.0.0
	 */
	protected final ByteStreamBuffer buffer;
	
	/**
	 * String result of byte array
	 * @since 1.0.0
	 */
	protected final StringBuilder result;
	
	

	/**
	 * Constructor
	 * @param conf the CsvBang configuration
	 * @param capacity the initial capacity of content
	 * @since 1.0.0
	 */
	public AbstractStringGrammarAction(final CsvBangConfiguration conf, final int capacity) {
		super();
		this.buffer = new ByteStreamBuffer(capacity);
		this.result = new StringBuilder(capacity);
		this.conf = conf;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) {
		buffer.add(b);
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	public String execute() throws CsvBangException {
		flushByteBuffer();
		return result.length() == 0?null:result.toString();
	}
	
	/**
	 * flush the byte buffer. Convert bytes to character sequence
	 * @throws CsvBangException if a problem has occurred when we convert byte array
	 * @since 1.0.0
	 */
	protected void flushByteBuffer() throws CsvBangException{
		if (buffer.length() > 0){
			result.append(buffer.toString(conf.charset));
			buffer.clear();
		}
	}
	
	/**
	 * A object to result of content of this action. This method include to clear the byte buffer
	 * @throws CsvBangException if a problem has occurred when we convert byte array
	 * @since 1.0.0
	 */
	protected void addToResult(final Object o) throws CsvBangException{
		flushByteBuffer();
		result.append(o);
	}
}
