/**
 *  com.github.lecogiteur.csvbang.file.FileToCloseForWritingCsvFileState
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
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Describe the state of CSV file when it is ready for closing
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileToCloseForWritingCsvFileState implements CsvFileState {
	
	/**
	 * The CSV file
	 * @since 0.1.0
	 */
	private CsvFileWrapper csvFile;
	
	/**
	 * The configuration
	 * @since 0.1.0
	 */
	private CsvBangConfiguration conf;
	
	/**
	 * True if the file is closed
	 * @since 0.1.0
	 */
	private boolean isClosed = false;
	
	

	/**
	 * constructor
	 * @param csvFile the csv file
	 * @param conf the configuration
	 * @since 0.1.0
	 */
	public FileToCloseForWritingCsvFileState(CsvFileWrapper csvFile,
			CsvBangConfiguration conf) {
		super();
		this.csvFile = csvFile;
		this.conf = conf;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#open(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void open(Object customHeader) throws CsvBangCloseException {
		final File file = csvFile.getFile();
		String s = "no file defined";
		if (file != null){
			s = file.getAbsolutePath();
		}
		throw new CsvBangCloseException(String.format("The file %s is closed. We can't open it.", s));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#write(java.lang.Object, java.lang.String)
	 * @since 0.1.0
	 */
	@Override
	public void write(final Object customHeader, String content) throws CsvBangCloseException {
		final File file = csvFile.getFile();
		String s = "no file defined";
		if (file != null){
			s = file.getAbsolutePath();
		}
		throw new CsvBangCloseException(String.format("The file is closed. We can't write in %s the content: %s .", s, content));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#close(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void close(Object customFooter) throws CsvBangException {
		if (isClosed){
			//already closed
			return;
		}
		isClosed = true;

		final File file = csvFile.getFile();
		final FileOutputStream out = csvFile.getOutPutStream();
		
		try{
			if (conf.noEndRecordOnLastRecord){
				//if we must delete the end record characters
				try {
					out.flush();
					final long nbBytes = file.length();
					final int sizeEndRecord = conf.endRecord.length();
					if (nbBytes > sizeEndRecord){
						//create an access random file in order to access to the end of file
						final RandomAccessFile raf = new RandomAccessFile(file, "rw");

						//verify if it's the end of file is equals to the end record
						byte[] endBytes = new byte[sizeEndRecord];
						raf.seek(nbBytes - sizeEndRecord);
						raf.read(endBytes);
						final String endString = new String(endBytes);
						if (conf.endRecord.equals(endString)){
							//delete the last end record
							raf.setLength(nbBytes - sizeEndRecord);
						}
						raf.close();
					}
				} catch (Exception e) {
					throw new CsvBangException(String.format("Cannot delete the last end record characters on file %s", file.getAbsolutePath()), e);
				}
			}

			//write footer
			String sFooter = conf.footer;
			if (customFooter != null){
				//custom footer define by CsvWriter#setFooter
				sFooter = sFooter == null?customFooter.toString(): new StringBuilder(customFooter.toString()).append(sFooter).toString();
			}
			if (sFooter != null){
				try {
					out.write(sFooter.getBytes(conf.charset));
				} catch (Exception e) {
					throw new CsvBangException(String.format("Cannot write footer (%s) on file %s", sFooter, file.getAbsolutePath()), e);
				}
			}

			out.flush();
			out.close();
		}catch (Exception e) {
			throw new CsvBangException("An error has occured when closed file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#isOpen()
	 * @since 0.1.0
	 */
	@Override
	public boolean isOpen() {
		return !isClosed;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.file.CsvFileState#read()
	 * @since 1.0.0
	 */
	@Override
	public CsvDatagram read() throws CsvBangException, CsvBangCloseException {
		throw new CsvBangException("You cannot read a file with a writer !");
	}

}
