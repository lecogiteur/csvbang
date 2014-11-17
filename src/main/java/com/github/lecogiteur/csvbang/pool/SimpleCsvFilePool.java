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

import java.util.Collection;
import java.util.Collections;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.CsvFileWrapper;
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
	
	private volatile Object customHeader;
	
	private volatile Object customFooter;
	
	private FileName filename;
	
	private CsvBangConfiguration conf;

	public SimpleCsvFilePool(final CsvBangConfiguration conf, final FileName filename, 
			final Object customHeader, final Object customFooter) {
		super();
		final CsvFileWrapper w = new CsvFileWrapper(filename.getNewFileName(false));
		this.file = new CsvFileContext(conf, w, customHeader, customFooter);
		this.customHeader = customHeader;
		this.customFooter = customFooter;
		this.filename = filename;
		this.conf = conf;
	}

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

	@Override
	public void setCustomHeader(Object customHeader) throws CsvBangException {
		this.customHeader = customHeader;
		final CsvFileWrapper w = new CsvFileWrapper(filename.getNewFileName(false));
		this.file = new CsvFileContext(conf, w, customHeader, customFooter);
	}

	@Override
	public void setCustomFooter(Object customFooter) throws CsvBangException {
		this.customFooter = customFooter;
		final CsvFileWrapper w = new CsvFileWrapper(filename.getNewFileName(false));
		this.file = new CsvFileContext(conf, w, customHeader, customFooter);
	}
}
