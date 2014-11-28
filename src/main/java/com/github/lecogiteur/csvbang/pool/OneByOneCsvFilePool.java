/**
 *  com.github.lecogiteur.csvbang.file.OneByOneCsvFilePool
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.CsvFileWrapper;
import com.github.lecogiteur.csvbang.file.FileName;

/**
 * Implementation of pool. Managecfile by file
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class OneByOneCsvFilePool implements CsvFilePool {
	
	
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
	 * Generatro of filename
	 * @since 0.1.0
	 */
	private final FileName fileName;
	
	/**
	 * Current file
	 * @since 0.1.0
	 */
	private AtomicReference<WrapperCsvFileContext> reference = new AtomicReference<WrapperCsvFileContext>(new WrapperCsvFileContext());
	
	/**
	 * List file in pool
	 * @since 0.1.0
	 */
	private Set<CsvFileContext> list = new HashSet<CsvFileContext>();
	
	/**
	 * The configuration
	 * @since 0.1.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * Constructor
	 * @param conf the configuration
	 * @param fileName the file name
	 * @param customHeader the custom header
	 * @param customFooter the custom footer
	 * @since 0.1.0
	 */
	public OneByOneCsvFilePool(final CsvBangConfiguration conf, final FileName fileName, 
			final Object customHeader, final Object customFooter) {
		this.customHeader = customHeader;
		this.customFooter = customFooter;
		this.conf = conf;
		this.fileName = fileName;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getFile(int, int)
	 * @since 0.1.0
	 */
	@Override
	public CsvFileContext getFile(int nbRecord, int nbByte)
			throws CsvBangException {
		
		while (true){
			//retrieve current file
			final WrapperCsvFileContext data = reference.get();

			//wrapped it in a new object
			WrapperCsvFileContext newData = new WrapperCsvFileContext();
			newData.file = data.file;
			newData.nbByte = data.nbByte + nbByte;
			newData.nbRecord = data.nbRecord + nbRecord;

			if (newData.file != null && isAllowedToModification(newData.nbRecord, newData.nbByte)){
				//if we can modify it, we return it
				if (reference.compareAndSet(data, newData)){
					return newData.file;
				}
				//hoops, not lucky another thread already changes data [lenght, nbRecord] about file, we must retry
				continue;
			}else if ((conf.maxFile < 0 || list.size() < conf.maxFile) && isAllowedToModification(nbRecord, nbByte)){
				//can't modify this file. This file is full
				newData.nbByte = nbByte;
				newData.nbRecord = nbRecord;
				newData.file = generateNewFile();				
				if (reference.compareAndSet(data, newData)){
					//one thread can add a file
					fileName.ackNewFileName();
					list.add(newData.file);
					return newData.file;
				}
				//hoops, not lucky another thread already changes data [lenght, nbRecord] about file, we must retry
				continue;
			}
			
			throw new CsvBangException(String.format("No file available in pool for update. The maximum number files [%s] has been already created and are full.", conf.maxFile));
		}
	}
	
	
	/**
	 * Verify if we can modify the file
	 * @param nbRecord the number of record to add to the file
	 * @param nbByte the number of byte to append to the file
	 * @return True, if we can update the file.
	 * @since 0.1.0
	 */
	protected boolean isAllowedToModification(long nbRecord, long nbByte){
		return (conf.maxRecordByFile < 0 || conf.maxRecordByFile >=  nbRecord) 
		&& (conf.maxFileSize < 0 || conf.maxFileSize >= nbByte);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getAllFiles()
	 * @since 0.1.0
	 */
	@Override
	public Collection<CsvFileContext> getAllFiles() {
		return list;
	}
	
	/**
	 * Generate new file
	 * @return the new file
	 * @since 0.1.0
	 */
	private CsvFileContext generateNewFile(){
		final CsvFileWrapper file = new CsvFileWrapper(fileName.getNewFileName(true));
		return new CsvFileContext(conf, file, customHeader, customFooter);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#setCustomHeader(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setCustomHeader(final Object customHeader) throws CsvBangException {
		this.customHeader = customHeader;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#setCustomFooter(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setCustomFooter(final Object customFooter) throws CsvBangException {
		this.customFooter = customFooter;
	}

}
