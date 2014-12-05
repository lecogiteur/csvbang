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
import java.io.FilenameFilter;
import java.text.ParseException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.CsvBangDateFormat;
import com.github.lecogiteur.csvbang.util.CsvbangUti;

/**
 * Manage filename
 * @author Tony EMMA
 * @version 1.0.0
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
		DATE,
		
		/**
		 * Multiple characters (only use for reading files. In case of writing it replace jocker by a blank character)
		 * @since 1.0.0
		 */
		JOKER
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
	 * Pattern of file name
	 * @since 1.0.0
	 */
	private Pattern filenamePattern; 
	
	
	/**
	 * <p>Manage file name. Can insert the file number or date. You define an absolute path, relative path or just file name</p>
	 * <p>
	 * List of pattern
	 * <ul>
	 * 	<li>%n : the number file</li>
	 * 	<li>%d : date format</li>
	 * 	<li>* :  Zero or multiple characters (only use for reading files. In case of writing it replaces this joker by an empty string)</li>
	 * </ul>
	 * </p>
	 * @param filename pattern of file name
	 * @param datePattern date pattern (required if you want the date in file name). Use a {@link SimpleDateFormat}
	 * @throws CsvBangException if no date pattern is defined or if no File name
	 * @see SimpleDateFormat
	 * @since 1.0.0
	 */
	public FileName(final String filename, final String datePattern) throws CsvBangException{
		final List<Integer> order = new ArrayList<Integer>();
		pattern = new StringBuilder();
		final StringBuilder filenamePatternS = new StringBuilder();
		formatByOffset = new HashMap<Integer, FileName.TYPE_FORMAT>();
		boolean containsDate = false;
		
		if (CsvbangUti.isStringNotBlank(filename)){
			//retrieve all pattern
			final int length = filename.length();
			boolean b = false; //is %
			boolean noSlashPattern = false;
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
						filenamePatternS.append("([0-9]+)");
						continue;
					case 'd':
						formatByOffset.put(pattern.length(), TYPE_FORMAT.DATE);
						order.add(pattern.length());
						containsDate = true;
						filenamePatternS.append("(.+?)");
						continue;
					default:
						pattern.append("%");
						filenamePatternS.append("\\Q").append("%").append("\\E");
					}
				}
				if (c == '*'){
					formatByOffset.put(pattern.length(), TYPE_FORMAT.JOKER);
					order.add(pattern.length());
					noSlashPattern = filenamePatternS.length()>1?filenamePatternS.charAt(filenamePatternS.length() - 1) == File.separatorChar:false;
					filenamePatternS.append("(.*?)");
					continue;
				}
				pattern.append(c);
				if (noSlashPattern && c == File.separatorChar){
					noSlashPattern = false;
				}else{
					filenamePatternS.append("\\Q").append(c).append("\\E");
				}
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
		
		//compile the file pattern
		filenamePattern = Pattern.compile(filenamePatternS.toString());
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
		final int baseDirLength = basedir == null?0:basedir.length();
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
			name.insert(offset + baseDirLength, toInsert);
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
	
	/**
	 * Generate a filter for file name.
	 * @return the filter
	 * @since 1.0.0
	 */
	public FilenameFilter generateFilter(){
		return new InternalFilenameFilter(filenamePattern, dateFormat, offsetOrdered);
	}
	
	
	private class InternalFilenameFilter implements FilenameFilter {

		/**
		 * Pattern of filename
		 * @since 1.0.0
		 */
		private Pattern pattern;
		
		/**
		 * Format of date
		 * @since 1.0.0
		 */
		private CsvBangDateFormat format;
		
		/** 
		 * list of offset where string (number, date, ...) are insert. (revert sort)
		 * @since 1.0.0
		 */
		private int[] offsetOrdered;
		
		
		
		/**
		 * Constructor
		 * @param pattern pattern of file name
		 * @param format format of date
		 * @param offsetOrdered list of offset where string (number, date, ...) are insert. (revert sort)
		 * @since 1.0.0
		 */
		public InternalFilenameFilter(final Pattern pattern,
				final CsvBangDateFormat format, final int[] offsetOrdered) {
			super();
			this.pattern = pattern;
			this.format = format;
			this.offsetOrdered = offsetOrdered;
		}



		/**
		 * {@inheritDoc}
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 * @since 1.0.0
		 */
		@Override
		public boolean accept(File dir, String name) {
			final File file = new File(dir, name);
			final String path = file.getAbsolutePath();

			final Matcher m = pattern.matcher(path);
			int count = m.groupCount();
			if (m.find()){
				if (count != offsetOrdered.length){
					return false;
				}
				for (final int offset:offsetOrdered){
					final TYPE_FORMAT type = formatByOffset.get(offset);
					switch(type){
					case DATE:
						try {
							final SimpleDateFormat f = format.get();
							final String group = m.group(count--);
							f.setLenient(false);
							f.parse(group);
						} catch (ParseException e) {
							return false;
						}
						default:
							continue;
					}
				}
				return true;
			}
			
			return false;
		}
	}
}
