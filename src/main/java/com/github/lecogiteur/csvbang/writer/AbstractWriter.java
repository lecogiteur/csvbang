/**
 *  com.github.lecogiteur.csvbang.writer.AbstractWriter
 * 
 *  Copyright (C) 2013  Tony EMMA
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
package com.github.lecogiteur.csvbang.writer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.Comment;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ReflectionUti;


/**
 * Abstract writer
 * 
 * @author Tony EMMA
 * @version 0.1.0
 */
public abstract class AbstractWriter<T> implements CsvWriter<T>{

	/**
	 * a carriage return
	 */
	private static final Pattern PATTERN_CARRIAGE_RETURN = Pattern.compile("\n");
	
	
	/**
	 * Character
	 * @since 0.0.1
	 */
	private String sQuote;
	
	/**
	 * True if we must add quote character
	 * @since 0.0.1
	 */
	private boolean addQuote;
	
	/**
	 * Length of delimiter
	 * @since 0.0.1
	 */
	private int delimiterLength = 0;
	
	/**
	 * Custom header
	 * @since 0.1.0
	 */
	private Object header;
	
	/**
	 * File Writer
	 * @since 0.0.1
	 */
	protected FileOutputStream out;
	
	/**
	 * Csv File
	 * @since 0.0.1
	 */
	protected File file;
	
	/**
	 * Configuration
	 * @since 0.0.1
	 */
	protected final CsvBangConfiguration conf;

	/**
	 * Line size
	 * @since 0.0.1
	 */
	protected int defaultLineSize = 100;
	
	/**
	 * Custom footer
	 */
	protected Object footer;
	
	
	/**
	 * Constructor
	 * @param conf configuration
	 * @since 0.0.1
	 */
	public AbstractWriter(final CsvBangConfiguration conf) {
		super();
		this.conf = conf;
		this.addQuote = this.conf.quote != null;
		if (addQuote){
			this.sQuote = this.conf.quote.toString();
		}
		
		delimiterLength = this.conf.delimiter.length();
		defaultLineSize = 20 * this.conf.fields.size();
	}
	
	/**
	 * Constructor
	 * @param file CSV file
	 * @param conf configuration
	 * @since 0.0.1
	 */
	public AbstractWriter(final File file, final CsvBangConfiguration conf) {
		this(conf);
		this.file = file;
	}
	
	/**
	 * Constructor
	 * @param file CSV file
	 * @param conf configuration
	 * @since 0.0.1
	 */
	public AbstractWriter(final String file, final CsvBangConfiguration conf) {
		this(conf);
		if (CsvbangUti.isStringNotBlank(file)){
			this.file = new File(file);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#open()
	 * @since 0.0.1
	 */
	public synchronized void open() throws CsvBangException {
		if (out != null){
			//already open
			return;
		}
		
		
		//complete file name with annotation configuration
		if (CsvbangUti.isStringNotBlank(conf.filename)){
			if (file != null && file.exists() && file.isDirectory()){
				//if file defined by factory is a directory
				file = new File(file, conf.filename);
			}else if (file == null){
				//if no file is defined is
				file = new File(conf.filename);
			}
		}
		
		
		
		if (file == null){
			throw new CsvBangException("No file defined for CSV writer");
		}
		
		if (!conf.isAppendToFile && file.exists()){
			file.delete();
		}
		
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new CsvBangException("Could not create file: " + file.getAbsolutePath(), e);
			}
		}else if (!file.isFile()){
			throw new CsvBangException(String.format("%s is not a file. ", file.getAbsolutePath()));
		}else if (!file.canWrite()){
			throw new CsvBangException(String.format("Could not write in file: %s ", file.getAbsolutePath()));	
		}
		
		try {
			out = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			throw new CsvBangException("Could not create file: " + file.getAbsolutePath(), e);
		}
		
		//custom header define by CsvWriter#setHeader
		if (header != null){
			try {
				String sHeader = header.toString();
				if (sHeader != null){
					//TODO set the type of return
					sHeader += "\n";
					out.write(sHeader.getBytes(conf.charset));
				}
			} catch (Exception e) {
				throw new CsvBangException(String.format("Cannot write header (%s) on file %s", conf.header, file.getAbsolutePath()), e);
			}
		}
		
		//generated header
		if (conf.header != null && conf.header.length() > 0){
			try {
				out.write(conf.header.getBytes(conf.charset));
			} catch (Exception e) {
				throw new CsvBangException(String.format("Cannot write header (%s) on file %s", conf.header, file.getAbsolutePath()), e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#isOpen()
	 * @since 0.0.1
	 */
	public boolean isOpen() {
		return out != null;
	}
	

	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setHeader(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setHeader(Object header) {
		this.header = header;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setFooter(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setFooter(Object footer) {
		this.footer = footer;
		
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.lang.Object)
	 * @since 0.0.1
	 */
	public void write(final T line) throws CsvBangException {
		if (line == null){
			return;
		}
		write(Collections.singleton(line));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.lang.Object[])
	 * @since 0.0.1
	 */
	public void write(final T[] lines) throws CsvBangException {
		if (lines == null || lines.length > 0){
			return;
		}
		write(Arrays.asList(lines));
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.util.Collection)
	 * @since 0.1.0
	 */
	@Override
	public void write(final Collection<T> lines) throws CsvBangException {
		internalWrite(lines, false);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(com.github.lecogiteur.csvbang.util.Comment)
	 * @since 0.1.0
	 */
	@Override
	public void comment(final Comment comment) throws CsvBangException {
		internalWrite(Collections.singleton(comment), true);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void comment(final T line) throws CsvBangException {
		if (line == null){
			return;
		}
		comment(Collections.singleton(line));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(T[])
	 * @since 0.1.0
	 */
	@Override
	public void comment(final T[] lines) throws CsvBangException {
		if (lines == null || lines.length > 0){
			return;
		}
		comment(Arrays.asList(lines));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.util.Collection)
	 * @since 0.1.0
	 */
	@Override
	public void comment(final Collection<T> lines) throws CsvBangException {
		internalWrite(lines, true);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 0.1.0
	 */
	@Override
	public void close() throws CsvBangException {
		try {
			if (out != null){
				//custom footer define by CsvWriter#setFooter
				if (footer != null){
					try {
						String sFooter = footer.toString();
						if (sFooter != null){
							out.write(sFooter.getBytes(conf.charset));
						}
					} catch (Exception e) {
						throw new CsvBangException(String.format("Cannot write footer (%s) on file %s", conf.header, file.getAbsolutePath()), e);
					}
				}
				
				out.close();
			}
		} catch (IOException e) {
			throw new CsvBangException("An error has occured when closed file", e);
		}
	}

	/**
	 * manage writing lines
	 * @param lines lines to write
	 * @param isComment True if lines must be commented
	 * @throws CsvBangException if a problem occurs when line
	 * @since 0.1.0
	 */
	protected abstract void internalWrite(final Collection<?> lines, final boolean isComment) throws CsvBangException;
	
	
	/**
	 * Generate a line or a comment
	 * @param line line or comment
	 * @param isComment True if it's a comment
	 * @return the generated data 
	 * @throws CsvBangException if a problem has occurred when we generate the line or comment 
	 * @since 0.1.0
	 */
	protected StringBuilder generateLine(final Object line, final boolean isComment) throws CsvBangException{
		return isComment?writeComment(line):writeLine(line);
	}
	
	/**
	 * Write a comment
	 * @param line a line
	 * @return a comment
	 * @throws CsvBangException if a problem when retrieve a value
	 * @since 0.1.0
	 * 
	 */
	private StringBuilder writeComment(final Object line) throws CsvBangException{
		if (line == null){
			return null;
		}
		
		//if it's a comment
		if (line instanceof Comment){
			return generateComment((Comment) line);
		}
		
		return generateComment(writeLine(line));
	}
	
	/**
	 * Write a line
	 * @param line a line
	 * @return a serialized line
	 * @throws CsvBangException if a problem when retrieve a value
	 * @since 0.1.0
	 */
	private StringBuilder writeLine(final Object line) throws CsvBangException{
		if (line == null){
			return null;
		}
		
		
		//start line
		final StringBuilder sLine = new StringBuilder(defaultLineSize);
		boolean isNullLine = true;
		
		
		//for each field
		for (final CsvFieldConfiguration f:conf.fields){
			// get value
			final Object v = ReflectionUti.getValue(f.memberBean, line);
			
			//add value
			if (v != null){
				isNullLine = false;
				if (v instanceof Object[]){
					saveCollection(f, sLine, Arrays.asList(v));
				}else if (v instanceof Collection<?>){
					saveCollection(f, sLine, (Collection <?>)v);
				}else{
					addField(f, sLine, v);					
				}
			}else {
				addField(f, sLine, v);		
			}
		}
		
		if (isNullLine || sLine.length() == 0){
			return null;
		}
		sLine.delete(0, delimiterLength);
		sLine.insert(0, conf.startRecord).append(conf.endRecord);
		
		//add comment before
		if (conf.commentsBefore != null){
			for (final AnnotatedElement member:conf.commentsBefore){
				sLine.insert(0, generateComment(new Comment(ReflectionUti.getValue(member, line))));				
			}
		}
		
		//add comment after
		if (conf.commentsAfter != null){
			for (final AnnotatedElement member:conf.commentsAfter){
				sLine.append(generateComment(new Comment(ReflectionUti.getValue(member, line))));				
			}
		}
		
		return sLine;
	}
	
	/**
	 * Quote a value
	 * @param s value
	 * @since 0.0.1
	 */
	private void quote(final StringBuilder s){
		int index = s.indexOf(sQuote);
	    while (index != -1)
	    {
	        s.insert(index, conf.escapeQuoteCharacter);
	        index += 2; // Move to the end of the replacement
	        index = s.indexOf(sQuote, index);
	    }
		s.insert(0, sQuote).append(sQuote);
	}
	
	/**
	 * Add a value of field
	 * @param f configuration of field
	 * @param s line
	 * @param v value of field
	 * @since 0.0.1
	 */
	private void addField(final CsvFieldConfiguration f, final StringBuilder s, final Object v){
		
		//format value
		final String value = f.format.format(v, f.nullReplaceString);
		
		if (f.isDeleteFieldIfNull && (v == null || value == null)){
			return;
		}
		
		//add delimeter
		s.append(conf.delimiter);
		
		if (addQuote){
			//quote value
			final StringBuilder b = new StringBuilder(value);
			quote(b);
			s.append(b);
		}else{
			s.append(value);
		}
	}
	
	/**
	 * Add a collection to a line
	 * @param f configuration of field
	 * @param s line
	 * @param c the collection
	 * @since 0.0.1
	 */
	private void saveCollection(final CsvFieldConfiguration f, final StringBuilder s, final Collection<?> c){
		for (final Object o:c){
			addField(f, s, o);
		}
	}
	
	/**
	 * Generate the comment
	 * @param comment the comment
	 * @return the comment
	 * @since 0.1.0
	 */
	private StringBuilder generateComment(final Comment comment){
		final String cmt = comment.getComment();
		return generateComment(cmt);
	}
	
	/**
	 * Generate the comment
	 * @param comment the comment
	 * @return the comment
	 * @since 0.1.0
	 */
	private StringBuilder generateComment(final CharSequence comment){
		if (comment == null){
			return null;
		}
		
		final StringBuilder c = new StringBuilder(comment.length() + 10).append(conf.startComment);
		
		final String[] lines = PATTERN_CARRIAGE_RETURN.split(comment);
		
		for (final String line:lines){
			c.append(conf.commentCharacter).append(line).append("\n");
		}
		return c;
	}

}
