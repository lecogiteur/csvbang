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

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangCloseException;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.CsvFileContext;
import com.github.lecogiteur.csvbang.pool.CsvFilePool;
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
	 * Pool of file
	 * @since 0.1.0
	 */
	protected final CsvFilePool filePool;
	
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
	 * True if writer is closed
	 * @since 0.1.0
	 */
	protected volatile boolean isClose = false;
	
	/**
	 * Constructor
	 * @param file CSV file
	 * @param conf configuration
	 * @throws CsvBangException if an error occurred during the initialization of file pool
	 * @since 0.0.1
	 */
	public AbstractWriter(final CsvFilePool pool, final CsvBangConfiguration conf) throws CsvBangException {
		this.filePool = pool;
		this.conf = conf;
		this.addQuote = this.conf.quote != null;
		if (addQuote){
			this.sQuote = this.conf.quote.toString();
		}
		
		delimiterLength = this.conf.delimiter.length();
		defaultLineSize = 20 * this.conf.fields.size();
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#open()
	 * @since 0.0.1
	 */
	public void open() throws CsvBangException, CsvBangCloseException {				
		final Collection<CsvFileContext> files = filePool.getAllFiles();
		
		if (CsvbangUti.isCollectionNotEmpty(files)){
			for (final CsvFileContext file:files){
				file.open();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#isOpen()
	 * @since 0.0.1
	 */
	public boolean isOpen() {				
		final Collection<CsvFileContext> files = filePool.getAllFiles();
		
		if (CsvbangUti.isCollectionNotEmpty(files)){
			for (final CsvFileContext file:files){
				if (!file.isOpen()){
					return false;
				}
			}
		}
		return !isClose;
	}
	

	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setHeader(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setHeader(Object header) throws CsvBangException {
		if (isOpen()){
			throw new CsvBangException("We can't set the custom header. Some CSV files are already open.");
		}
		filePool.setCustomHeader(header);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#setFooter(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void setFooter(Object footer) throws CsvBangException {
		if (isOpen()){
			throw new CsvBangException("We can't set the custom footer. Some CSV files are already open.");
		}
		filePool.setCustomFooter(footer);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#write(java.lang.Object)
	 * @since 0.0.1
	 */
	public void write(final T line) throws CsvBangException, CsvBangCloseException {
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
	public void write(final T[] lines) throws CsvBangException, CsvBangCloseException {
		if (lines == null || lines.length == 0){
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
	public void write(final Collection<T> lines) throws CsvBangException, CsvBangCloseException {
		internalWrite(lines, false);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(com.github.lecogiteur.csvbang.util.Comment)
	 * @since 0.1.0
	 */
	@Override
	public void comment(final Comment comment) throws CsvBangException, CsvBangCloseException {
		internalWrite(Collections.singleton(comment), true);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#comment(java.lang.Object)
	 * @since 0.1.0
	 */
	@Override
	public void comment(final T line) throws CsvBangException, CsvBangCloseException {
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
	public void comment(final T[] lines) throws CsvBangException, CsvBangCloseException {
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
	public void comment(final Collection<T> lines) throws CsvBangException, CsvBangCloseException {
		internalWrite(lines, true);
	}
	
	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#close()
	 * @since 0.1.0
	 */
	@Override
	public void close() throws IOException {
		isClose = true;
		final Collection<CsvFileContext> files = filePool.getAllFiles();
		if (CsvbangUti.isCollectionNotEmpty(files)){
			for (final CsvFileContext file:files){
				file.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.writer.CsvWriter#isClose()
	 * @since 0.1.0
	 */
	@Override
	public boolean isClose() {
		return isClose;
	}

	/**
	 * manage writing lines
	 * @param lines lines to write
	 * @param isComment True if lines must be commented
	 * @throws CsvBangException if a problem occurs when line
	 * @throws CsvBangCloseException if the writer is closed
	 * @since 0.1.0
	 */
	protected abstract void internalWrite(final Collection<?> lines, final boolean isComment) throws CsvBangException, CsvBangCloseException;
	
	
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
		
		final String[] lines = conf.defaultEndLineCharacter.getPattern().split(comment);
		
		for (final String line:lines){
			if (!conf.patternCommentCharacter.matcher(line).matches()){
				c.append(conf.commentCharacter);
			}
			c.append(line).append(conf.defaultEndLineCharacter);
		}
		return c;
	}

}
