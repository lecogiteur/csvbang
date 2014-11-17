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

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
import com.github.lecogiteur.csvbang.pool.MultiCsvFilePool;
import com.github.lecogiteur.csvbang.pool.OneByOneCsvFilePool;
import com.github.lecogiteur.csvbang.pool.SimpleCsvFilePool;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;

/**
 * File pool factory
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class CsvFilePoolFactory {
	
	
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
	public static final CsvFilePool createPool(final CsvBangConfiguration conf, final File file, 
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
			return new SimpleCsvFilePool(conf, filename, customHeader, customFooter);
		}else if (conf.isFileByFile || conf.maxFile <= 1){
			return new OneByOneCsvFilePool(conf, filename, customHeader, customFooter);
		}
			
		return new MultiCsvFilePool(conf, filename, customHeader, customFooter);
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
	public static final CsvFilePool createPool(final CsvBangConfiguration conf, final String file, 
			final Object customHeader, final Object customFooter) throws CsvBangException{
		if (CsvbangUti.isStringNotBlank(file)){
			return createPool(conf, new File(file), customHeader, customFooter);
		}
		return createPool(conf, (File)null, customHeader, customFooter);
	}

}
