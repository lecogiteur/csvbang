/**
 *  com.github.lecogiteur.csvbang.reader.DelegatedReaderWithThreadRegister
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
package com.github.lecogiteur.csvbang.reader;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.Future;

import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.AbstractDelegatedWithRegisterThread;

/**
 * Delegate to another reader operations. It manages all thread which read or open Csv Writer and close the Reader when all thread are terminated
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class DelegatedReaderWithThreadRegister<T> extends AbstractDelegatedWithRegisterThread implements CsvReader<T> {

	/**
	 * CSV reader
	 * @since 1.0.0
	 */
	private CsvReader<T> reader;

	/**
	 * Constructor
	 * @param reader
	 * @since 1.0.0
	 */
	public DelegatedReaderWithThreadRegister(CsvReader<T> reader) {
		super();
		this.reader = reader;
	}

	/**
	 * {@inheritDoc}
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#open()
	 * @since 1.0.0
	 */
	public void open() throws CsvBangException, CsvBangCloseException {
		reader.open();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#isOpen()
	 * @since 1.0.0
	 */
	public boolean isOpen() {
		return reader.isOpen();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @throws CsvBangException
	 * @throws CsvBangCloseException
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#readBlock()
	 * @since 1.0.0
	 */
	public Collection<T> readBlock() throws CsvBangException,
			CsvBangCloseException {
		return reader.readBlock();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#isClose()
	 * @since 1.0.0
	 */
	public boolean isClose() {
		return reader.isClose();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @throws CsvBangException
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getHeader()
	 * @since 1.0.0
	 */
	public Future<String> getHeader() throws CsvBangException {
		return reader.getHeader();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @throws CsvBangException
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getFooter()
	 * @since 1.0.0
	 */
	public Future<String> getFooter() throws CsvBangException {
		return reader.getFooter();
	}

	/**
	 * {@inheritDoc}
	 * @return
	 * @throws CsvBangException
	 * @see com.github.lecogiteur.csvbang.reader.CsvReader#getComment()
	 * @since 1.0.0
	 */
	public Future<String> getComment() throws CsvBangException {
		return reader.getComment();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.util.AbstractDelegatedWithRegisterThread#getActor()
	 * @since 1.0.0
	 */
	@Override
	public Closeable getActor() {
		return reader;
	}
}
