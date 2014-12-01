/**
 *  com.github.lecogiteur.csvbang.file.FileToOpenForWritingCsvFileState
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
package com.github.lecogiteur.csvbang.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;

/**
 * Describe the state of CSV file when it is ready for opening
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileToOpenForWritingCsvFileState implements CsvFileState {
	
	/**
	 * The configuration
	 * @since 0.1.0
	 */
	private CsvBangConfiguration conf;
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private CsvFileWrapper csvFile;
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	private CsvFileContext context;
	
	/**
	 * True if the file is opened
	 * @since 0.1.0
	 */
	private volatile boolean isOpen = false;
	

	/**
	 * Constructor
	 * @param conf the configuration
	 * @param csvFile the CSV file
	 * @param context the file context
	 * @since 0.1.0
	 */
	public FileToOpenForWritingCsvFileState(final CsvBangConfiguration conf,
			final CsvFileWrapper csvFile, final CsvFileContext context) {
		super();
		this.conf = conf;
		this.csvFile = csvFile;
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void open(final Object customHeader) throws CsvBangException {
		if (isOpen){
			//already open
			return;
		}
		
		synchronized (this) {

			if (isOpen){
				//already open
				return;
			}
			
			//the "physical" file
			File file = csvFile.getFile();
			
			if (file == null){
				throw new CsvBangException("No file defined for CSV writer");
			}
			
			if (!conf.isAppendToFile && file.exists()){
				//if we must not append to the end file
				file.delete();
			}
			
			if (!file.exists()){
				//create file if not exist
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new CsvBangException("Could not create file: " + file.getAbsolutePath(), e);
				}
			}else if (!file.isFile()){
				//must be a file
				throw new CsvBangException(String.format("%s is not a file. ", file.getAbsolutePath()));
			}else if (!file.canWrite()){
				//must write in file
				throw new CsvBangException(String.format("Could not write in file: %s ", file.getAbsolutePath()));	
			}
			
			//create the file stream
			FileOutputStream out = null;
			try {
				out  = new FileOutputStream(file, true);
				csvFile.setOutputStream(out);
			} catch (FileNotFoundException e) {
				throw new CsvBangException("Could not create file: " + file.getAbsolutePath(), e);
			}
			
			//custom header define by CsvWriter#setHeader
			if (customHeader != null){
				try {
					String sHeader = customHeader.toString();
					if (sHeader != null){
						if (!sHeader.endsWith(conf.defaultEndLineCharacter.toString())){
							sHeader += conf.defaultEndLineCharacter.toString();
						}
						out.write(sHeader.getBytes(conf.charset));
					}
				} catch (Exception e) {
					throw new CsvBangException(String.format("Cannot write header (%s) on file %s", conf.header, file.getAbsolutePath()), e);
				}
			}
			
			//generated header
			if (conf.header != null && conf.header.length() > 0){
				try {
					if (!conf.header.endsWith(conf.defaultEndLineCharacter.toString())){
						out.write(conf.defaultEndLineCharacter.toBytes(conf.charset));
					}
					out.write(conf.header.getBytes(conf.charset));
				} catch (Exception e) {
					throw new CsvBangException(String.format("Cannot write header (%s) on file %s", conf.header, file.getAbsolutePath()), e);
				}
			}
			
			//the file is open now we can write
			context.setCsvFileState(new FileReadyForWritingCsvFileState(csvFile, conf, context));
			
			//the file is opened and ready for updates
			isOpen = true;
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 0.1.0
	 */
	@Override
	public void write(final Object customHeader, final String content) throws CsvBangException, CsvBangCloseException {
		open(customHeader);
		context.write(content);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void close(final Object customFooter) throws CsvBangException, CsvBangIOException {
		synchronized (this) {
			if (isOpen){
				//only if file is open
				context.close();
			}
			//do nothing - The file is already close because not open.
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#isOpen()
	 * @since 0.1.0
	 */
	@Override
	public boolean isOpen() {
		return isOpen;
	}

}
