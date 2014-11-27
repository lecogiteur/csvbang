/**
 *  com.github.lecogiteur.csvbang.file.FileName
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.CsvBangDateFormat;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Manage filename
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileName implements Cloneable{
	
	/**
	 * The logger
	 * @since 0.1.0
	 */
	private static final Logger LOGGER = Logger.getLogger(FileName.class.getName());
	
	
	/**
	 * Type of format in file name
	 * @author Tony EMMA
	 * @version 0.1.0
	 * @since 0.1.0
	 */
	private enum TYPE_FORMAT{
		/**
		 * File number pattern
		 * @since 0.1.0
		 */
		NUMBER,
		
		/**
		 * Date pattern
		 * @since 0.1.0
		 */
		DATE
	}
	
	
	
	/**
	 * file name
	 * @since 0.1.0
	 */
	private final StringBuilder pattern;

	/**
	 * The type of format by offset in file name
	 * @since 0.1.0
	 */
	private final Map<Integer, TYPE_FORMAT> formatByOffset;
	
	/**
	 * offset where we must insert pattern
	 * @since 0.1.0
	 */
	private final int[] offsetOrdered;
	
	/**
	 * Date format to insert in file name
	 * @since 0.1.0
	 */
	private final CsvBangDateFormat dateFormat;
	
	/**
	 * The file number
	 * @since 0.1.0
	 */
	private AtomicInteger fileNumber = new AtomicInteger(1);
	
	/**
	 * Base directory if it exists. Can be null
	 * @since 0.1.0
	 */
	private String basedir;
	
	
	
	/**
	 * <p>Manage file name. Can insert the file number or date.</p>
	 * <p>
	 * List of pattern
	 * <ul>
	 * 	<li>%n : the number file</li>
	 * 	<li>%d : date format</li>
	 * </ul>
	 * </p>
	 * @param filename pattern of file name
	 * @param datePattern date pattern (required if you want the date in file name). Use a {@link SimpleDateFormat}
	 * @throws CsvBangException if no date pattern is defined or if no File name
	 * @see SimpleDateFormat
	 * @since 0.1.0
	 */
	public FileName(final String filename, final String datePattern) throws CsvBangException{
		final List<Integer> order = new ArrayList<Integer>();
		pattern = new StringBuilder();
		formatByOffset = new HashMap<Integer, FileName.TYPE_FORMAT>();
		boolean containsDate = false;
		
		if (CsvbangUti.isStringNotBlank(filename)){
			//retrieve all pattern
			final int length = filename.length();
			boolean b = false;
			for (int i=0; i<length; i++){
				char c = filename.charAt(i);
				if (c == '%'){
					b = true;
					continue;
				}
				if (b){
					b = false;
					switch (c) {
					case 'n':
						formatByOffset.put(pattern.length(), TYPE_FORMAT.NUMBER);
						order.add(pattern.length());
						break;
					case 'd':
						formatByOffset.put(pattern.length(), TYPE_FORMAT.DATE);
						order.add(pattern.length());
						containsDate = true;
						break;
					default:
						pattern.append("%").append(c);
						continue;
					}
					continue;
				}
				pattern.append(c);
			}
		}else{
			throw new CsvBangException("No file name is defined.");
		}
		
		Collections.reverse(order);
		offsetOrdered = new int[order.size()];
		for(int i=0; i<order.size(); i++){
			offsetOrdered[i] = order.get(i);
		}
		
		if (CsvbangUti.isStringNotBlank(datePattern)){
			dateFormat = new CsvBangDateFormat(datePattern);
		}else{
			if (containsDate){
				throw new CsvBangException("You want to add the date to the file name. But no date pattern is defined. Define a date pattern.");
			}
			dateFormat = null;
		}
	}
	
	/**
	 * Set the base directory which contains CSV file
	 * @param file base directory
	 * @since 0.1.0
	 */
	public void setBaseDirectory(final File file){
		if (file != null){
			setBaseDirectory(file.getAbsolutePath());
		}
	}
	
	/**
	 * Set the base directory which contains CSV file
	 * @param file base directory
	 * @since 0.1.0
	 */
	public void setBaseDirectory(final String file){
		if (basedir != null){
			//delete last basedir
			pattern.delete(0, basedir.length());
		}
		
		if (CsvbangUti.isStringBlank(file)){
			//if basedir is null
			basedir = null;
			return;
		}
		
		//we add base dir
		basedir = file;
		if (!(basedir.endsWith(File.separator) || pattern.charAt(0) == File.separatorChar)){
			basedir += File.separator;
		}
		pattern.insert(0, basedir);
	}

	/**
	 * <p>Get the new file name</p>
	 * <p>
	 * List of pattern
	 * <ul>
	 * 	<li>%n : the number file</li>
	 * 	<li>%d : date format</li>
	 * </ul>
	 * </p>
	 * @param mustAck True if the filename must acknowledge. An ack ensure that the next filename is not delivered before you have acknowledge the current filename.
	 * @return a file name
	 * @see #ackNewFileName() in order to acknowledge the file name
	 * @since 0.1.0
	 */
	public String getNewFileName(final  boolean mustAck){
		final StringBuilder name = new StringBuilder(pattern);
		for (final int offset:offsetOrdered){
			final TYPE_FORMAT type = formatByOffset.get(offset);
			Object toInsert = null;
			switch (type) {
			case DATE:
				toInsert = dateFormat.get().format(new Date());
				break;
			case NUMBER:
				if (mustAck){
					//don't increment the file number here. If multiple threads try to create a new filewe
					toInsert = fileNumber.get();
				}else{
					toInsert = fileNumber.getAndIncrement();					
				}
				break;
			default:
				toInsert = "";
				break;
			}
			name.insert(offset, toInsert);
		}
		
		return name.toString();
	}
	
	/**
	 * If you want that the filename require an ack.
	 * @since 0.1.0
	 */
	public void ackNewFileName(){
		fileNumber.incrementAndGet();
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#clone()
	 * @since 0.1.0
	 */
	@Override
	public FileName clone() {
		FileName fileName;
		try {
			fileName = (FileName) super.clone();
			fileName.fileNumber = new AtomicInteger(1);
			return fileName;
		} catch (CloneNotSupportedException e) {
			LOGGER.log(Level.WARNING, String.format("The file name [%] can't be clone.", pattern), e);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 * @since 0.1.0
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateFormat == null) ? 0 : dateFormat.hashCode());
		result = prime * result
				+ ((fileNumber == null) ? 0 : fileNumber.hashCode());
		result = prime * result
				+ ((formatByOffset == null) ? 0 : formatByOffset.hashCode());
		result = prime * result + Arrays.hashCode(offsetOrdered);
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileName other = (FileName) obj;
		if (dateFormat == null) {
			if (other.dateFormat != null)
				return false;
		} else if (!dateFormat.equals(other.dateFormat))
			return false;
		if (fileNumber == null) {
			if (other.fileNumber != null)
				return false;
		} else if (other.fileNumber == null) {
			if (fileNumber != null)
				return false;
		} else if (fileNumber.get() != other.fileNumber.get())
			return false;
		if (formatByOffset == null) {
			if (other.formatByOffset != null)
				return false;
		} else if (!formatByOffset.equals(other.formatByOffset))
			return false;
		if (!Arrays.equals(offsetOrdered, other.offsetOrdered))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (other.pattern == null || !pattern.toString().equals(other.pattern.toString()))
			return false;
		return true;
	}
	
	
	
}
