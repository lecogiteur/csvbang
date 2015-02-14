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

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.CsvFileWrapper;
import com.github.lecogiteur.csvbang.file.FileActionType;
import com.github.lecogiteur.csvbang.file.FileName;

/**
 * Implementation of pool. Manage file by file
 * @author Tony EMMA
 * @version 1.0.0
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
	 * Action on each file of pool
	 * @since 1.0.0
	 * @see com.github.lecogiteur.csvbang.file.FileActionType
	 */
	private final FileActionType action;
	
	/**
	 * List of files to use for pool. This list initialize the pool file with a specific list of files.
	 * @since 1.0.0
	 */
	private final ConcurrentLinkedQueue<File> filesToUse;
	
	/**
	 * Maximum of record by file
	 * @since 1.0.0
	 */
	private final long maxRecords;
	
	/**
	 * Maximum of file in pool
	 * @since 1.0.0
	 */
	private final long maxFiles;
	
	/**
	 * Constructor (for writing)
	 * @param conf the configuration
	 * @param fileName the file name
	 * @param customHeader the custom header
	 * @param customFooter the custom footer
	 * @param action Action on each file of pool
	 * @since 0.1.0
	 */
	public OneByOneCsvFilePool(final CsvBangConfiguration conf, final FileName fileName, 
			final Object customHeader, final Object customFooter, final FileActionType action) {
		this.customHeader = customHeader;
		this.customFooter = customFooter;
		this.conf = conf;
		this.fileName = fileName;
		this.action = action;
		this.filesToUse = null;
		this.maxFiles = conf.maxFile;
		this.maxRecords = conf.maxRecordByFile;
	}
	

	/**
	 * Constructor (for reading)
	 * @param conf the configuration
	 * @param filesToUse list of files to use for pool
	 * @param action action on each file
	 * @since 1.0.0
	 */
	public OneByOneCsvFilePool(final CsvBangConfiguration conf, final Collection<File> filesToUse, final FileActionType action) {
		this.customHeader = customHeader;
		this.customFooter = customFooter;
		this.conf = conf;
		this.fileName = null;
		this.action = action;
		this.filesToUse = new ConcurrentLinkedQueue<File>(filesToUse);
		this.maxFiles = filesToUse.size();
		this.maxRecords = -1;
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
			newData.maxByte = data.maxByte;
			newData.nbByte = data.nbByte + nbByte;
			newData.nbRecord = data.nbRecord + nbRecord;

			if (newData.file != null && isAllowedToModification(data, newData, newData.nbRecord, newData.nbByte)){
				//if we can modify it, we return it
				if (reference.compareAndSet(data, newData)){
					return newData.file;
				}
				//hoops, not lucky another thread already changes data [length, nbRecord] about file, we must retry
				continue;
			}else if (filesToUse != null && filesToUse.size() > 0){
				//use the initialization list in order to give a new file
				final File file = filesToUse.peek();
				if (file != null){
					newData.nbByte = nbByte;
					newData.nbRecord = nbRecord;
					newData.file = generateNewFile(file);
					newData.maxByte = file.length();
					if (reference.compareAndSet(data, newData)){
						filesToUse.poll(); 
						list.add(newData.file);
						return newData.file;
					}
				}
				continue;
			}else if ((maxFiles < 0 || list.size() < maxFiles) && isAllowedToModification(null, null, nbRecord, nbByte)){
				//can't modify this file. This file is full. We generate a new file
				newData.nbByte = nbByte;
				newData.nbRecord = nbRecord;
				newData.file = generateNewFile();	
				newData.maxByte = conf.maxFileSize;			
				if (reference.compareAndSet(data, newData)){
					//one thread can add a file
					fileName.ackNewFileName();
					list.add(newData.file);
					return newData.file;
				}
				//hoops, not lucky another thread already creates new file, we must retry
				continue;
			} 
			
			throw new CsvBangException(String.format("No file available in pool for update. The maximum number files [%s] has been already created and are full.", conf.maxFile));
		}
	}
	
	
	/**
	 * Verify if we can modify the file
	 * @param previousData processed file
	 * @param newData processed file
	 * @param nbRecord the number of record to add to the file
	 * @param nbByte the number of byte to append to the file
	 * @return True, if we can update the file.
	 * @since 0.1.0
	 */
	protected boolean isAllowedToModification(final WrapperCsvFileContext previousData, final WrapperCsvFileContext newData,
			long nbRecord, long nbByte){
		return (maxRecords < 0 || maxRecords >=  nbRecord) 
		&& ((newData != null && (newData.maxByte < 0 
							|| newData.maxByte >= nbByte 
							|| (FileActionType.READ_ONLY.equals(this.action) && previousData.maxByte > previousData.nbByte))
			)|| (newData == null && (conf.maxFileSize < 0 || conf.maxFileSize >= nbByte)));
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
		final CsvFileWrapper file = new CsvFileWrapper(fileName.getNewFileName(true), action);
		return new CsvFileContext(conf, file, customHeader, customFooter);
	}
	
	/**
	 * Generate new file
	 * @param file file to use
	 * @return the new file
	 * @since 1.0.0
	 */
	private CsvFileContext generateNewFile(final File file){
		final CsvFileWrapper f = new CsvFileWrapper(file, action);
		return new CsvFileContext(conf, f, customHeader, customFooter);
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
