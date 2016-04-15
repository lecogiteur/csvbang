/**
 *  com.github.lecogiteur.csvbang.util.CsvbangUti
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
package com.github.lecogiteur.csvbang.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * General Utility class
 * @author Tony EMMA
 * @version 1.0.0
 * @since 0.0.1
 *
 */
public class CsvbangUti {
	
	/**
	 * File filter in order to retrieve sub-directories
	 * @since 1.0.0
	 */
	private static final FileFilter DIRECTORY_FILTER = new FileFilter() {
		
		/**
		 * {@inheritDoc}
		 * @see java.io.FileFilter#accept(java.io.File)
		 * @since 1.0.0
		 */
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	/**
	 * Verify if a String is blank (null, empty or white space)
	 * @param s a String
	 * @return true if the String is blank
	 * @since 0.0.1
	 */
	public static final boolean isStringBlank(final String s){
		return !isStringNotBlank(s);
	}

	/**
	 * Verify if a String is not blank (null, empty or white space)
	 * @param s a String
	 * @return true if the String is not blank
	 * @since 0.0.1
	 */
	public static final boolean isStringNotBlank(final String s){
		if (s == null || "".equals(s)){
			return false;
		}
		
		final byte[] bytes = s.getBytes();
		for (final byte c:bytes){
			if (((char) c) != ' '){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verify if a collection is empty
	 * @param c a collection
	 * @return True if collection is empty
	 * @since 0.0.1
	 */
	public static final boolean isCollectionEmpty(final Collection<?> c){
		return c == null || c.isEmpty();
	}
	
	/**
	 * Verify if a collection is not empty
	 * @param c a collection
	 * @return True if collection is not empty
	 * @since 0.0.1
	 */
	public static final boolean isCollectionNotEmpty(final Collection<?> c){
		return c != null && !c.isEmpty();
	}
	
	/**
	 * Verify if a map is empty
	 * @param map a map
	 * @return True if map is empty
	 * @since 1.0.0
	 */
	public static final boolean isCollectionEmpty(final Map<?, ?> map){
		return map == null || map.isEmpty();
	}
	
	/**
	 * Verify if a map is not empty
	 * @param map a map
	 * @return True if map is not empty
	 * @since 1.0.0
	 */
	public static final boolean isCollectionNotEmpty(final Map<?, ?> map){
		return map != null && !map.isEmpty();
	}
	
	
	/**
	 * Get all files of a base directory and its sub-directories
	 * @param baseDir base directory
	 * @param filter filter on file name
	 * @return all files
	 * @since 1.0.0
	 */
	public static final Collection<File> getAllFiles(final File baseDir, final FilenameFilter filter){
		if (baseDir != null && baseDir.exists()){ 
			if (baseDir.isDirectory()){
				final Collection<File> result = new HashSet<File>();
				final File[] subDirectories = baseDir.listFiles(DIRECTORY_FILTER);
				if (subDirectories != null){
					for (final File dir:subDirectories){
						final Collection<File> c = getAllFiles(dir, filter);
						if (c != null) result.addAll(c);
					}
				}
				
				final File[] files = baseDir.listFiles(filter);
				if (files != null){
					for (final File file:files){
						if (!file.isDirectory()){
							result.add(file);
						}
					}
				}
				return result;
			}else{
				return Collections.singleton(baseDir);
			}
		}
		return null;
	}
	
	/**
	 * Delete the last element of the collection
	 * @param col a collection
	 * @since 1.0.0
	 */
	public static final void deleteLastElement(final Collection<?> col){
		if (col == null || col.isEmpty()){
			return;
		}
		int size = col.size();
		final Iterator<?> it = col.iterator();
		it.next();
		while (size > 1){
			it.next();
			size--;
		}
		it.remove();
	}
}
