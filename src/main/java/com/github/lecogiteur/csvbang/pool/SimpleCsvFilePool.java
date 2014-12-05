/**
 *  com.github.lecogiteur.csvbang.file.SimpleCsvFilePool
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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
package com.github.lecogiteur.csvbang.pool;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.CsvFileWrapper;
import com.github.lecogiteur.csvbang.file.FileActionType;
import com.github.lecogiteur.csvbang.file.FileName;

/**
 * It's a pool of file. This implementation manage a single file
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleCsvFilePool implements CsvFilePool {
	
	/**
	 * a single file
	 * @since 0.1.0
	 */
	private volatile CsvFileContext file;
	
	/**
	 * The custom header
	 * @since 0.1.0
	 */
	private volatile Object customHeader;
	
	/**
	 * The custom footer
	 * @since 0.1.0
	 */
	private volatile Object customFooter;
	
	/**
	 * The file
	 * @since 0.1.0
	 */
	private CsvFileWrapper fileWrapper;
	
	/**
	 * The configuration
	 * @since 0.1.0
	 */
	private CsvBangConfiguration conf;

	/**
	 * Constructor
	 * @param conf the configuration
	 * @param filename The file name generator
	 * @param customHeader The custom header
	 * @param customFooter The custom footer
	 * @param action action on file
	 * @since 1.0.0
	 */
	public SimpleCsvFilePool(final CsvBangConfiguration conf, final FileName filename, 
			final Object customHeader, final Object customFooter, final FileActionType action) {
		super();
		final CsvFileWrapper fileWrapper = new CsvFileWrapper(filename.getNewFileName(false), action);
		this.file = new CsvFileContext(conf, fileWrapper, customHeader, customFooter);
		this.customHeader = customHeader;
		this.customFooter = customFooter;
		this.conf = conf;
	}
	
	
	/**
	 * Constructor
	 * @param conf configuration
	 * @param file file of pool
	 * @param action action on file
	 * @since 1.0.0
	 */
	public SimpleCsvFilePool(final CsvBangConfiguration conf, final File file, final FileActionType action) {
		super();
		final CsvFileWrapper fileWrapper = new CsvFileWrapper(file, action);
		this.file = new CsvFileContext(conf, fileWrapper, null, null);
		this.conf = conf;
	}
	
	

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getAllFiles()
	 * @since 0.1.0
	 */
	@Override
	public Collection<CsvFileContext> getAllFiles() {
		return Collections.singleton(file);
	}

	/**
	 * This pool does not take into account different criteria
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getFile(int, int)
	 * @since 0.1.0
	 */
	@Override
	public CsvFileContext getFile(int nbRecord, int nbByte)
			throws CsvBangException {
		return file;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#setCustomHeader(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setCustomHeader(Object customHeader) throws CsvBangException {
		this.customHeader = customHeader;
		this.file = new CsvFileContext(conf, fileWrapper, customHeader, customFooter);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#setCustomFooter(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setCustomFooter(Object customFooter) throws CsvBangException {
		this.customFooter = customFooter;
		this.file = new CsvFileContext(conf, fileWrapper, customHeader, customFooter);
	}
}
