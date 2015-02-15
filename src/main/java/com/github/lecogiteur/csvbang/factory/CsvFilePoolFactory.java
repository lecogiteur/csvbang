/**
 *  com.github.lecogiteur.csvbang.file.CsvFilePoolFactory
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
package com.github.lecogiteur.csvbang.factory;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileActionType;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.pool.MultiCsvFilePool;
import com.github.lecogiteur.csvbang.pool.OneByOneCsvFilePool;
import com.github.lecogiteur.csvbang.pool.SimpleCsvFilePool;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * File pool factory. A file pool is used in order to delivery a CSV file 
 * to CsvBang for processing the writing and reading
 * @author Tony EMMA
 * @version 1.0.0
 * @since 0.1.0
 */
public class CsvFilePoolFactory {
	//TODO verifier si les customHeader et footer sont réellement nécessaire, il semble qu'il soient toujours initialisé à null.
	
	/**
	 * Create a pool of file for a CSV bean
	 * @param conf CsvBang configuration of CSV bean
	 * @param file base directory or override the CSV file
	 * @param customHeader the custom header
	 * @param customFooter the custom footer
	 * @return the pool of CSV file
	 * @throws CsvBangException if a problem occurred during creation of pool
	 * @since 0.1.0
	 */
	public static final CsvFilePool createPoolForWriting(final CsvBangConfiguration conf, final File file, 
			final Object customHeader, final Object customFooter) throws CsvBangException{
		FileName filename = null;
		if (file != null){
			if (file.exists() && file.isDirectory()){
				//it's the base directory
				filename = conf.fileName.clone();
				filename.setBaseDirectory(file);
			}else{
				//it's file. we must override the filename define in configuration
				filename = new FileName(file.getAbsolutePath(), conf.fileDatePattern);
			}
		}else{
			filename = conf.fileName.clone();
		}
		
		if (conf.maxFileSize < 0 && conf.maxRecordByFile < 0){
			//create a simple pool
			return new SimpleCsvFilePool(conf, filename, customHeader, customFooter, FileActionType.WRITE_ONLY);
		}else if (conf.isWriteFileByFile || conf.maxFile <= 1){
			return new OneByOneCsvFilePool(conf, filename, customHeader, customFooter, FileActionType.WRITE_ONLY);
		}
			
		return new MultiCsvFilePool(conf, filename, customHeader, customFooter, FileActionType.WRITE_ONLY);
	}
	
	/**
	 * Create a pool of file for a CSV bean
	 * @param conf CsvBang configuration of CSV bean
	 * @param file base directory or override the CSV file
	 * @param customHeader the custom header
	 * @param customFooter the custom footer
	 * @return the pool of CSV file
	 * @throws CsvBangException if a problem occurred during creation of pool
	 * @since 0.1.0
	 */
	public static final CsvFilePool createPoolForWriting(final CsvBangConfiguration conf, final String file, 
			final Object customHeader, final Object customFooter) throws CsvBangException{
		if (CsvbangUti.isStringNotBlank(file)){
			return createPoolForWriting(conf, new File(file), customHeader, customFooter);
		}
		return createPoolForWriting(conf, (File)null, customHeader, customFooter);
	}
	
	
	/**
	 * Create a pool of file in order to read CSV file
	 * @param conf CsvBang configuration of CSV bean
	 * @param paths list of directories and file to read. No warranty on order when we processed files
	 * @param fileNameFilter the file name to use in order to retrieve file to read. Can be null. If null, we use the {@link com.github.lecogiteur.csvbang.annotation.CsvFile#fileName()} definition. 
	 * @return a pool of CSV file
	 * @since 1.0.0
	 */
	public static final CsvFilePool createPoolForReading(final CsvBangConfiguration conf, final Collection<File> paths, final FileName fileNameFilter){
		if (paths != null){
			Collection<File> files = new HashSet<File>();
			for(final File path:paths){
				if (path.exists()){
					if(path.isDirectory()){
						final Collection<File> c = CsvbangUti.getAllFiles(path, fileNameFilter == null?conf.fileName.generateFilter():fileNameFilter.generateFilter()); 
						if (CsvbangUti.isCollectionNotEmpty(c)){
							files.addAll(c);
						}
					}else{
						files.add(path);
					}
				}

				if (CsvbangUti.isCollectionNotEmpty(files)){
					if (files.size() == 1){
						//create a simple pool
						return new SimpleCsvFilePool(conf, files.iterator().next(), FileActionType.READ_ONLY);
					}else if (conf.isReadFileByFile){
						return new OneByOneCsvFilePool(conf, files, FileActionType.READ_ONLY);
					}

					return new MultiCsvFilePool(conf, files, FileActionType.READ_ONLY);
				}
			}
		}
		return null;
	}

}
