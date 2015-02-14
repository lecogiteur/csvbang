/**
 *  com.github.lecogiteur.csvbang.file.MultiCsvFilePool
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.file.CsvFileWrapper;
import com.github.lecogiteur.csvbang.file.FileActionType;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Pool which manages many file
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class MultiCsvFilePool implements CsvFilePool {
	
	/**
	 * The configuration
	 * @since 0.1.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * The custom header for each file
	 * @since 0.1.0
	 */
	private volatile Object customHeader;
	
	/**
	 * The custom footer for each file
	 * @since 0.1.0
	 */
	private volatile Object customFooter;
	
	/**
	 * Number of files in pool
	 * @since 0.1.0
	 */
	private final AtomicLong nbFiles = new AtomicLong(0);
	
	/**
	 * Number of files which are full
	 * @since 0.1.0
	 */
	private final AtomicLong nbFilesFull = new AtomicLong(0);
	
	/**
	 * The generator of file name
	 * @since 0.1.0
	 */
	private final FileName fileName; 
	
	/**
	 * File which can be processed yet 
	 * @since 0.1.0
	 */
	private final ConcurrentLinkedQueue<WrapperCsvFileContext> files = new ConcurrentLinkedQueue<WrapperCsvFileContext>();
	
	/**
	 * All files in pool
	 * @since 0.1.0
	 */
	private final ConcurrentLinkedQueue<CsvFileContext> allFiles = new ConcurrentLinkedQueue<CsvFileContext>();
	
	/**
	 * Action on each file of pool
	 * @since 1.0.0
	 * @see com.github.lecogiteur.csvbang.file.FileActionType
	 */
	private final FileActionType action;
	
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
	 * Constructor (for writing). With this constructor, the pool generates new file from the given pattern (see fileName parameter).
	 * @param conf the configuration
	 * @param fileName the file name generator
	 * @param customHeader the custom header for each file
	 * @param customFooter the custom footer for each file
	 * @param action Action on each file of pool
	 * @since 0.1.0
	 */
	public MultiCsvFilePool(final CsvBangConfiguration conf, final FileName fileName, 
			final Object customHeader, final Object customFooter, final FileActionType action){
		this.customFooter = customFooter;
		this.customHeader = customHeader;
		this.conf = conf;
		this.fileName = fileName;
		this.action = action;
		this.maxRecords = conf.maxRecordByFile;
		this.maxFiles = conf.maxFile;
	}
	

	/**
	 * Constructor (for reading). With this constructor, the pool doesn't generate new file. It uses a given list of file (see filesToUse parameter).
	 * @param conf the configuration
	 * @param filesToUse List of file to read
	 * @param customHeader the custom header for each file
	 * @param customFooter the custom footer for each file
	 * @param action Action on each file of pool
	 * @since 1.0.0
	 */
	public MultiCsvFilePool(final CsvBangConfiguration conf, final Collection<File> filesToUse,
			final FileActionType action){
		this.customFooter = null;
		this.customHeader = null;
		this.conf = conf;
		this.action = action;
		this.maxRecords = -1;
		this.fileName = null;
		this.maxFiles = filesToUse.size();
		this.nbFiles.set(this.maxFiles);
		if (CsvbangUti.isCollectionNotEmpty(filesToUse)){
			for (final File file:filesToUse){
				final WrapperCsvFileContext wrapper = generateNewFile(file);
				files.add(wrapper);
				allFiles.add(wrapper.file);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getAllFiles()
	 * @since 0.1.0
	 */
	@Override
	public Collection<CsvFileContext> getAllFiles() {
		final Collection<CsvFileContext> list = new ArrayList<CsvFileContext>();
		list.addAll(allFiles);
		return list;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.pool.CsvFilePool#getFile(int, int)
	 * @since 0.1.0
	 */
	@Override
	public CsvFileContext getFile(int nbRecord, int nbByte)  throws CsvBangException{
		WrapperCsvFileContext file = null;
		while (file == null){
			//get file in pool
			file = files.poll();
			
			if (file == null){
			    //if no file in pool we verify if we can create a new file
				final long index = nbFiles.getAndIncrement();
				
				if (index < maxFiles){
					//new file
					file = generateNewFile();					
					allFiles.add(file.file);
				}else if (nbFilesFull.get() >= maxFiles){
					//all files are full.
					throw new CsvBangException(String.format("No file available in pool for update. The maximum number files [%s] has been already created and are full.", conf.maxFile));
				}else{
					//maximum of files have been created in pool but not available. All are used by another thread so we wait.
					continue;
				}
			
			}else if (!isAllowedToModification(file, nbRecord, nbByte)){
				//this file is full. We can't modify this file so we search another file.
				file = null;
				nbFilesFull.incrementAndGet();
			}
		}
		
		//update date about file
		file.nbByte += nbByte;
		file.nbRecord += nbRecord;
		
		//file can be taken by another thread now.
		files.add(file);
		
		return file.file;
	}
	
	/**
	 * Verify if we can modify the file
	 * @param file the file
	 * @param nbRecord the number of record to add to the file
	 * @param nbByte the number of byte to append to the file
	 * @return True, if we can update the file.
	 * @since 0.1.0
	 */
	private boolean isAllowedToModification(final WrapperCsvFileContext file, int nbRecord, int nbByte){
		return (maxRecords < 0 || maxRecords >= file.nbRecord + nbRecord) 
		&& (file.maxByte < 0 || file.maxByte >= file.nbByte + nbByte || (FileActionType.READ_ONLY.equals(action) && file.maxByte > file.nbByte));
	}
	

	
	/**
	 * Generate a new file
	 * @return a new file
	 * @since 0.1.0
	 */
	private WrapperCsvFileContext generateNewFile(){
		final CsvFileWrapper file = new CsvFileWrapper(fileName.getNewFileName(false), action);
		final WrapperCsvFileContext w = new WrapperCsvFileContext();
		w.file = new CsvFileContext(conf, file, customHeader, customFooter);
		w.maxByte = conf.maxFileSize;
		return w;
	}
	
	/**
	 * Generate a new file
	 * @param f a file
	 * @return a new file
	 * @since 1.0.0
	 */
	private WrapperCsvFileContext generateNewFile(final File f){
		final CsvFileWrapper file = new CsvFileWrapper(f, action);
		final WrapperCsvFileContext w = new WrapperCsvFileContext();
		w.file = new CsvFileContext(conf, file, customHeader, customFooter);
		w.maxByte = f.length();
		return w;
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
