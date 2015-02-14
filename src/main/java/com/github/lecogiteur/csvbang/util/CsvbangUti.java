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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
		return c == null || c.size() == 0;
	}
	
	/**
	 * Verify if a collection is not empty
	 * @param c a collection
	 * @return True if collection is not empty
	 * @since 0.0.1
	 */
	public static final boolean isCollectionNotEmpty(final Collection<?> c){
		return c != null && c.size() > 0;
	}
	
	
	/**
	 * Get all files of a base directory and its sub-directories
	 * @param baseDir base directory
	 * @return all files
	 * @since 1.0.0
	 */
	public static final Collection<File> getAllFiles(final File baseDir, final FilenameFilter filter){
		if (baseDir != null && baseDir.exists()){ 
			if (baseDir.isDirectory()){
				final Collection<File> result = new ArrayList<File>();
				final File[] files = baseDir.listFiles(filter);
				if (files != null){
					for (final File file:files){
						if (file.isDirectory()){
							final Collection<File> c = getAllFiles(file, filter);
							if (c != null) result.addAll(c);
						}else{
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
}
