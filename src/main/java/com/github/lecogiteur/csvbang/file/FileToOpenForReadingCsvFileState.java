/**
 *  com.github.lecogiteur.csvbang.file.FileToOpenForReadingCsvFileState
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.exception.CsvBangIOException;
import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;

/**
 * State of file: we open file in order to read it.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileToOpenForReadingCsvFileState implements CsvFileState {
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private final CsvFileWrapper csvFile;
	
	/**
	 * The file context
	 * @since 0.1.0
	 */
	private final CsvFileContext context;
	
	/**
	 * True if the file is opened
	 * @since 0.1.0
	 */
	private volatile boolean isOpen = false;
	

	/**
	 * Constructor
	 * @param csvFile CSV file
	 * @param context context of file
	 * @since 1.0.0
	 */
	public FileToOpenForReadingCsvFileState(CsvFileWrapper csvFile, CsvFileContext context) {
		super();
		this.csvFile = csvFile;
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void open(Object customHeader) throws CsvBangException,
			CsvBangCloseException {
		if (isOpen){
			//already open
			return;
		}
		
		final File file = csvFile.getFile();
		
		if (!file.exists()){
			throw new CsvBangException(String.format("The file [%s] doesn't exist. We can't read it.", file.getAbsolutePath()));
		}
		
		if (!file.isFile()){
			throw new CsvBangException(String.format("[%s] is not a file. We can't read it.", file.getAbsolutePath()));
		}
		
		if (!file.canRead()){
			throw new CsvBangException(String.format("The file [%s] can't be read. Verify authorizations.", file.getAbsolutePath()));
		}

		//get the size of file
		final long totalByte = file.length();
		
		//size of byte block 
		int sizeBlock = IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE;
		
		if (totalByte < (long) IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE){
			//it's a little file
			sizeBlock = ((int)totalByte) / 4;
		}

		if (!isOpen){

			synchronized (this) {

				if (isOpen){
					//already open
					return;
				}
				
				
				try {
					final FileInputStream in = new FileInputStream(file);
					csvFile.setInputStream(in);
				} catch (FileNotFoundException e) {
					throw new CsvBangException(String.format("The file [%s] doesn't exist. We can't open it.", file.getAbsolutePath()), e);
				}

				if (file.length() == 0){
					//no data we can close it
					context.setCsvFileState(new FileToCloseForReadingCsvFileState(csvFile));
					try {
						context.close();
					} catch (CsvBangIOException e) {
						throw new CsvBangCloseException(String.format("A problem has occurred when we close the file [%s]", file.getAbsolutePath()), e);
					}
				}else{
					//the file is open now we can change state
					context.setCsvFileState(new FileReadyForReadingCsvFileState(csvFile, context, sizeBlock, 
							csvFile.getFile().hashCode(), totalByte));
				}
				
				//the file is opened and ready for reading
				isOpen = true;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 1.0.0
	 */
	@Override
	public void write(Object customHeader, String content)
			throws CsvBangException, CsvBangCloseException {
		throw new CsvBangException("You cannot write into file with a reader !");
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#read()
	 * @since 1.0.0
	 */
	@Override
	public CsvDatagram read() throws CsvBangException, CsvBangCloseException {
		open(null);
		return context.read();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public void close(Object customFooter) throws CsvBangException,
			CsvBangIOException {
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
	 * @since 1.0.0
	 */
	@Override
	public boolean isOpen() {
		return isOpen;
	}

}
