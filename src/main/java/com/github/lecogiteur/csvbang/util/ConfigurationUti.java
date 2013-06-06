/**
 *  com.github.lecogiteur.csvbang.util.ConfigurationUti
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.formatter.BooleanCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.CsvFormatter;
import com.github.lecogiteur.csvbang.formatter.CurrencyCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.DateCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.Default;
import com.github.lecogiteur.csvbang.formatter.NoCarriageReturnCsvFormatter;
import com.github.lecogiteur.csvbang.formatter.NumberCsvFormatter;


/**
 * Utility class in order to load and parse configuration of a CSV bean
 * @author Tony EMMA
 * @version 0.0.1
 *
 */
public class ConfigurationUti {
	
	/**
	 * The logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ConfigurationUti.class.getName());
	
	/**
	 * Pattern in order to split locale
	 * @since 0.0.1
	 */
	private static final Pattern LOCALE_SEPARATOR = Pattern.compile("_"); 
	
	/**
	 * Default Format
	 * @since 0.0.1
	 */
	private static final CsvFormatter DEFAULT_FORMAT = new Default(); 
	
	
	
	/**
	 * Get value, if the value is equals to defaultValue, we return the old value, else we return the value
	 * @param oldValue the old value
	 * @param value the actual value
	 * @param defaultValue the default value
	 * @return the value
	 * @since 0.0.1
	 */
	private static String getParameterValue(final String oldValue, final String value, final String defaultValue){
		if (defaultValue.equals(value)){
			return oldValue;
		}
		return value;
	}
	
	/**
	 * Get value, if the value is equals to defaultValue, we return the old value, else we return the value
	 * @param oldValue the old value
	 * @param value the actual value
	 * @param defaultValue the default value
	 * @return the value
	 * @since 0.0.1
	 */
	private static int getParameterValue(final int oldValue, final int value, final int defaultValue){
		if (defaultValue == value){
			return oldValue;
		}
		return value;
	}
	
	/**
	 * Get value, if the value is equals to defaultValue, we return the old value, else we return the value
	 * @param oldValue the old value
	 * @param value the actual value
	 * @param defaultValue the default value
	 * @return the value
	 * @since 0.0.1
	 */
	private static char getParameterValue(final char oldValue, final char value, final char defaultValue){
		if (defaultValue == value){
			return oldValue;
		}
		return value;
	}
	
	/**
	 * Get value, if the value is equals to defaultValue, we return the old value, else we return the value
	 * @param oldValue the old value
	 * @param value the actual value
	 * @param defaultValue the default value
	 * @return the value
	 * @since 0.0.1
	 */
	private static boolean getParameterValue(final boolean oldValue, final boolean value, final boolean defaultValue){
		if (defaultValue == value){
			return oldValue;
		}
		return value;
	}
	
	
	/**
	 * Load configuration for all CSV field of class (not of its parent)
	 * @param finalClass the final class
	 * @param clazz class (can be a parent of final class
	 * @param mapConf all configuration of each field (with configuration of class parent). KEY: name of member || VALUE: its configuration
	 * 

	 * @throws CsvBangException <p>if we can't retrieve getter method of field</p>
	 * 							<p>if we can't create an instance of custom formatter</p>
	 * @since 0.0.1
	 */
	private static void loadCsvFieldConfiguration(final Class<?> finalClass, final Class<?> clazz, 
			final Map<String, CsvFieldConfiguration> mapConf) throws CsvBangException {
		final List<AnnotatedElement> members = ReflectionUti.getMembers(clazz);
		for (final AnnotatedElement member:members){
			final CsvField csvField = ReflectionUti.getCsvFieldAnnotation(member.getDeclaredAnnotations());
			if (csvField == null){
				continue;
			}
			
			//Retrieve getter of value
			AnnotatedElement getter = member;
			if (member instanceof Field){
				final Field f = (Field)member;
				if (!Modifier.isPublic(f.getModifiers())){
					final Method m = ReflectionUti.getGetterMethod(finalClass, f.getName());
					if (m != null){
						getter = m;
					}else{
						LOGGER.warning(String.format("No way in order to access to %s in class %s. You must define a getter method or change the modifier of field.", f.getName(), finalClass));
						continue;
					}
				}
			}
			
			//retrieve conf
			String internName = getter instanceof Field?"F: ":"M: ";
			internName += ((Member)getter).getName();
			CsvFieldConfiguration conf = mapConf.get(internName);
			if (conf == null){
				conf = new CsvFieldConfiguration();
				mapConf.put(internName, conf);
			}
			
			
			conf.position = getParameterValue(conf.position, csvField.position(), IConstantsCsvBang.DEFAULT_FIELD_POSITION);
			conf.isDeleteFieldIfNull =  getParameterValue(conf.isDeleteFieldIfNull, csvField.deleteIfNull(), IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL);
			conf.memberBean = getter;
			conf.nullReplaceString = getParameterValue(conf.nullReplaceString, csvField.defaultIfNull(), IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE);
			
			String realName = csvField.name();
			if (CsvbangUti.isStringBlank(realName)){
				realName = ((Member)member).getName();
				
				final Member m = ReflectionUti.getGetterMethod(finalClass, conf.name);
				if (m != null && m.getName().equals(realName)){
					//case where we override annotation of field with the getter method
					realName = IConstantsCsvBang.DEFAULT_FIELD_NAME;
				}
			}
			conf.name = getParameterValue(conf.name, realName, IConstantsCsvBang.DEFAULT_FIELD_NAME);
			
			final CsvFormatter format = loadCsvFormat(member);
			if (DEFAULT_FORMAT.equals(format) && conf.format != null){
				continue;
			}
			conf.format = format;
		}
	}
	
	/**
	 * Load format of a CSV field
	 * @param member field or method of a class
	 * @return its format
	 * @throws CsvBangException if we can't create an instance of custom formatter
	 * @since 0.0.1
	 */
	private static CsvFormatter loadCsvFormat(final AnnotatedElement member) 
	throws CsvBangException{
		final CsvFormat csvFormat = ReflectionUti.getCsvFormatAnnotation(member.getDeclaredAnnotations());
		
		if (csvFormat == null || csvFormat.type() == null){
			return DEFAULT_FORMAT;
		}
		
		CsvFormatter format = null;
		if (!TYPE_FORMAT.NONE.equals(csvFormat.type())){
			final String[] localeParams = LOCALE_SEPARATOR.split(csvFormat.locale());
			Locale locale = null;
			if (localeParams.length == 3){
				locale =new Locale(localeParams[0], localeParams[1], localeParams[2]);
			}else{
				locale = Locale.FRANCE;
			}

			switch (csvFormat.type()) {
			case DEFAULT:
				format = DEFAULT_FORMAT;
				break;
			case DATE:
				format = new DateCsvFormatter();
				break;
			case BOOLEAN:
				format = new BooleanCsvFormatter();
				break;
			case CURRENCY:
				format = new CurrencyCsvFormatter();
				break;
			case NUMBER:
				format = new NumberCsvFormatter();
				break;
			case NO_CARRIAGE_RETURN:
				format = new NoCarriageReturnCsvFormatter();
				break;
			case CUSTOM:
				if (csvFormat.customFormatter() != null){
					try {
						format = csvFormat.customFormatter().newInstance();
					} catch (Exception e) {
						throw new CsvBangException(String.format("Cannot instanciate custom formatter: %s", csvFormat.customFormatter()), e);
					}
					break;
				}
			default:
				format = DEFAULT_FORMAT;
				break;
			}

			format.setLocal(locale);
			format.setPattern(csvFormat.pattern());
			format.init();
		}
		return format;
	}
	
	
	/**
	 * Generate header of file if necessary
	 * @param conf a general configuration
	 * @since 0.0.1
	 */
	private static void generateHeader(final CsvBangConfiguration conf){
		if (conf.isDisplayHeader){
			final StringBuilder header = new StringBuilder(1000).append(conf.startRecord);
			for (final CsvFieldConfiguration field : conf.fields){
				
				String n = field.name;
				if (!(n != null && n.length() > 0)){
					n = ((Member)field.memberBean).getName();
				}
				header.append(n).append(conf.delimiter);
			}
			header.delete(header.length() - conf.delimiter.length(), header.length());
			header.append(conf.endRecord);
			conf.header = header.toString();
		}
	}
	
	/**
	 * Load configuration of CSV bean (file CSV)
	 * @param clazz a class
	 * @return its configuration (or null if the class or parents is not annotated with CsvType)
	 * @throws CsvBangException <p>if we can't retrieve getter method of field</p>
	 * 							<p>if we can't create an instance of custom formatter</p>
	 * @since 0.0.1
	 */
	public static final CsvBangConfiguration loadCsvBangConfiguration(final Class<?> clazz) throws CsvBangException{
		if (clazz == null){
			return null;
		}
		
		//Verify if class has annotations
		if (ReflectionUti.getCsvTypeAnnotation(clazz.getAnnotations()) == null){
			return null;
		}
		
		//get parents
		final List<Class<?>> parents = new ArrayList<Class<?>>();
		Class<?> parent = clazz;
		while (parent != null){
			parents.add(parent);
			parent = parent.getSuperclass();
		}
		Collections.reverse(parents);
		
		final CsvBangConfiguration conf = new CsvBangConfiguration();
		final Map<String, CsvFieldConfiguration> mapFieldConf = new HashMap<String, CsvFieldConfiguration>();
		for (final Class<?> c:parents){
			final CsvType csvType = ReflectionUti.getCsvTypeAnnotation(c.getDeclaredAnnotations());
			if (csvType != null){
				conf.blockingSize = getParameterValue(conf.blockingSize, csvType.blocksize(), IConstantsCsvBang.DEFAULT_BLOCKING_SIZE);
				conf.isAsynchronousWrite = getParameterValue(conf.isAsynchronousWrite, csvType.asynchronousWriter(), IConstantsCsvBang.DEFAULT_ASYNCHRONOUS_WRITE);
				conf.charset = getParameterValue(conf.charset, csvType.charsetName(), IConstantsCsvBang.DEFAULT_CHARSET_NAME);
				conf.delimiter = getParameterValue(conf.delimiter, csvType.delimiter(), IConstantsCsvBang.DEFAULT_DELIMITER);
				conf.endRecord = getParameterValue(conf.endRecord, csvType.endRecord(), IConstantsCsvBang.DEFAULT_END_RECORD);
				conf.startRecord = getParameterValue(conf.startRecord, csvType.startRecord(), IConstantsCsvBang.DEFAULT_START_RECORD);
				conf.isDisplayHeader = getParameterValue(conf.isDisplayHeader, csvType.header(), IConstantsCsvBang.DEFAULT_HEADER);
				conf.escapeQuoteCharacter = getParameterValue(conf.escapeQuoteCharacter, csvType.quoteEscapeCharacter(), IConstantsCsvBang.DEFAULT_QUOTE_ESCAPE_CHARACTER);
				conf.filename = getParameterValue(conf.filename, csvType.fileName(), IConstantsCsvBang.DEFAULT_FILE_NAME);
				conf.isAppendToFile = getParameterValue(conf.isAppendToFile, csvType.append(), IConstantsCsvBang.DEFAULT_APPEND_FILE);
				if (csvType.quoteCharacter() != null && csvType.quoteCharacter().length() > 0){
					conf.quote = csvType.quoteCharacter().charAt(0);
				}
			}
			loadCsvFieldConfiguration(clazz, c, mapFieldConf);
		}
		
		//manage order of field
		final TreeMap<Integer, CsvFieldConfiguration> orderedField = new TreeMap<Integer, CsvFieldConfiguration>();
		int count = 100000;
		for (final Entry<String, CsvFieldConfiguration> entry:mapFieldConf.entrySet()){
			int pos = entry.getValue().position;
			if (pos <= 0){
				pos = mapFieldConf.size() + count;
				++count;
			}
			orderedField.put(pos, entry.getValue());
		}
		
		if (orderedField.size() == 0){
			//TODO mettre des logs
			return null;
		}
		conf.fields = new ArrayList<CsvFieldConfiguration>(orderedField.values());
		
		//generate header
		generateHeader(conf);
		
		return conf;
	}

}
