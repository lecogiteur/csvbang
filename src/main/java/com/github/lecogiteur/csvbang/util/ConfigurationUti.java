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
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
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

import com.github.lecogiteur.csvbang.annotation.CsvComment;
import com.github.lecogiteur.csvbang.annotation.CsvComment.DIRECTION;
import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFooter;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileName;
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
 * @version 1.0.0
 * @since 0.0.1
 *
 */
public class ConfigurationUti {
	
	/**
	 * The logger
	 * @since 0.0.1
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
	 * Load configuration for all CSV field of class (not of its parent)
	 * @param finalClass the final class
	 * @param clazz class (can be a parent of final class
	 * @param mapConf all configuration of each field (with configuration of class parent). KEY: name of member || VALUE: its configuration
	 * @param commentFields map which contains comment members. KEY: {@link CsvComment#direction()} || VALUE = [[Map with  KEY: name of member || VALUE: its getter]]

	 * @param generators list of existing generators by type generated
	 * @throws CsvBangException <p>if we can't retrieve getter method of field</p>
	 * 							<p>if we can't create an instance of custom formatter</p>
	 * @since 0.1.0
	 */
	private static void loadCsvFieldConfiguration(final Class<?> finalClass, final Class<?> clazz, 
			final Map<String, CsvFieldConfiguration> mapConf, 
			final Map<DIRECTION, Map<String, AnnotatedElement>> commentFields, final Map<Class<?>, ObjectGenerator<?>> generators) 
	throws CsvBangException {
		final List<AnnotatedElement> members = ReflectionUti.getMembers(clazz);
		for (final AnnotatedElement member:members){
			final CsvField csvField = ReflectionUti.getCsvFieldAnnotation(member.getDeclaredAnnotations());
			final CsvComment csvComment = ReflectionUti.getCsvCommentAnnotation(member.getDeclaredAnnotations());

			if (csvField == null && csvComment == null){
				//No Csv Field
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
			
			if (csvComment != null){
				//a comment field
				final Map<String, AnnotatedElement> commentFieldList =  commentFields.get(csvComment.direction());
				commentFieldList.put(internName, getter);
				DIRECTION direction = null;
				if (DIRECTION.BEFORE_RECORD.equals(csvComment.direction())){
					direction = DIRECTION.AFTER_RECORD;
				}else{
					direction = DIRECTION.BEFORE_RECORD;
				}
				commentFields.get(direction).remove(internName);
			}
			
			if (csvField == null){
				//not a field
				continue;
			}
			
			CsvFieldConfiguration conf = mapConf.get(internName);
			if (conf == null){
				conf = new CsvFieldConfiguration();
				mapConf.put(internName, conf);
			}
			
			
			//configur
			conf.position = getParameterValue(conf.position, csvField.position(), IConstantsCsvBang.DEFAULT_FIELD_POSITION);
			conf.isDeleteFieldIfNull = csvField.deleteIfNull();
			conf.getter = getter;
			conf.nullReplaceString = csvField.defaultIfNull();
			
			if (CsvbangUti.isStringNotBlank(csvField.customMethodNameSetter())){
				conf.setter = ReflectionUti.getMethod(finalClass, csvField.customMethodNameSetter());
			}
			if (conf.setter == null){
				conf.setter = ReflectionUti.getSetterMethod(member, finalClass);
			}
			if (conf.setter == null){
				LOGGER.warning(String.format("No way in order to set %s in class %s. You must define a setter method or change the modifier of field.", conf.name, finalClass));
				//continue;
			}else{
				//defined the type of setter
				conf.typeOfSetter = ReflectionUti.getSetterType(conf.setter);
				if (conf.typeOfSetter.isArray()){
					conf.parameterizedCollectionType = conf.typeOfSetter.getComponentType();
				}else if (ReflectionUti.isCollection(conf.typeOfSetter)){
					conf.parameterizedCollectionType = ReflectionUti.getParameterizedSetterType(conf.setter);
				}
				
				//get the class to generate
				final Class<?> c = conf.parameterizedCollectionType != null?conf.parameterizedCollectionType:conf.typeOfSetter;
				ObjectGenerator<?> generator = generators.get(c);
				if (generator == null || csvField.factory() != null){
					generator = ReflectionUti.createTypeGenerator(c, csvField.factory(), csvField.factoryMethodName());
					if (csvField.factory() == null){
						generators.put(c, generator);
					}
				}
				conf.generator = generator;
				
			}
			
			//retrieve name of field
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
	 * @since 0.0.4
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
	 * Load configuration of CSV bean (file CSV)
	 * @param clazz a class
	 * @return its configuration (or null if the class or parents is not annotated with CsvType)
	 * @throws CsvBangException <p>if we can't retrieve getter method of field</p>
	 * 							<p>if we can't create an instance of custom formatter</p>
	 * @since 0.1.0
	 */
	public static final CsvBangConfiguration loadCsvBangConfiguration(final Class<?> clazz) throws CsvBangException{
		if (clazz == null){
			return null;
		}
		
		//Verify if class has annotations
		if (ReflectionUti.getCsvTypeAnnotation(clazz.getAnnotations()) == null){
			return null;
		}

		//verify if this class has a default constructor
		try {
			clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new CsvBangException(String.format("This class [%s] must have a default constructor.", clazz), e);
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
		final Map<DIRECTION, Map<String, AnnotatedElement>> mapCommentFields = new HashMap<CsvComment.DIRECTION, Map<String,AnnotatedElement>>();
		final Map<Class<?>, ObjectGenerator<?>> generators = new HashMap<Class<?>, ObjectGenerator<?>>();
		
		mapCommentFields.put(DIRECTION.BEFORE_RECORD, new HashMap<String, AnnotatedElement>());
		mapCommentFields.put(DIRECTION.AFTER_RECORD, new HashMap<String, AnnotatedElement>());
		
		//Load the configuration of class
		//We read annotations from the parent to child
		boolean hasCsvTypeDefined = false;
		for (final Class<?> c:parents){
			final CsvType csvType = ReflectionUti.getCsvTypeAnnotation(c.getDeclaredAnnotations());
			if (csvType != null){
				try{
					conf.charset = Charset.forName(csvType.charsetName());
				}catch(IllegalCharsetNameException e1){
					throw new CsvBangException(String.format("The charset [%s] for CSV file is undefined for the class %s. See https://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html", csvType.charsetName(), c));
				}catch(IllegalArgumentException e2){
					throw new CsvBangException(String.format("The charset [%s] for CSV file is undefined for the class %s. See https://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html", csvType.charsetName(), c));
				}
				conf.delimiter = csvType.delimiter();
				conf.endRecord = csvType.endRecord();
				conf.startRecord = csvType.startRecord();
				conf.escapeQuoteCharacter = csvType.quoteEscapeCharacter();
				conf.commentCharacter = csvType.commentCharacter();
				conf.defaultEndLineCharacter = csvType.defaultEndLineCharacter();
				if (csvType.quoteCharacter() != null && csvType.quoteCharacter().length() > 0){
					conf.quote = csvType.quoteCharacter().charAt(0);
				}
				hasCsvTypeDefined = true;
				if (IConstantsCsvBang.DEFAULT_END_RECORD.equals(conf.endRecord)){
					conf.endRecord = conf.defaultEndLineCharacter.toString();
				}
			}

			final CsvHeader csvHeader = ReflectionUti.getCsvHeaderAnnotation(c.getDeclaredAnnotations());
			if (csvHeader != null){
				conf.isDisplayHeader = csvHeader.header();
				conf.header = csvHeader.customHeader();
			}

			final CsvFooter csvFooter = ReflectionUti.getCsvFooterAnnotation(c.getDeclaredAnnotations());
			if (csvFooter != null){
				conf.footer = csvFooter.customFooter();
				conf.noEndRecordOnLastRecord = csvFooter.noEndRecordOnLastRecord();
			}

			final CsvFile csvFile = ReflectionUti.getCsvFileAnnotation(c.getDeclaredAnnotations());
			if (csvFile != null){
				//configuration about file
				conf.fileName = new FileName(csvFile.fileName(), csvFile.datePattern());
				conf.isAppendToFile = csvFile.append();
				conf.blockSize = csvFile.blocksize();
				conf.isAsynchronousWrite = csvFile.asynchronousWriter();
				conf.isAsynchronousRead = csvFile.asynchronousReader();
				conf.maxFile = csvFile.maxFileNumber();
				conf.maxFileSize = csvFile.maxFileSize();
				conf.maxRecordByFile = csvFile.maxRecordByFile();
				conf.isWriteFileByFile = csvFile.writeFileByFile();
				conf.isReadFileByFile = csvFile.readFileByFile();
				conf.isReadingSubFolder = csvFile.readSubFolders();
				conf.isRegisterThread = csvFile.registerThread();
			}
			
			loadCsvFieldConfiguration(clazz, c, mapFieldConf, mapCommentFields, generators);
		}
		
		if (!hasCsvTypeDefined){
			//The CsvType is required
			return null;
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
			LOGGER.warning(String.format("No field defined for class:", clazz));
			return null;
		}
		conf.fields = new ArrayList<CsvFieldConfiguration>(orderedField.values());
		
		//manage comment field
		if (CsvbangUti.isCollectionNotEmpty(mapCommentFields.get(DIRECTION.BEFORE_RECORD).values())){
			conf.commentsBefore = mapCommentFields.get(DIRECTION.BEFORE_RECORD).values();
		}
		
		if (CsvbangUti.isCollectionNotEmpty(mapCommentFields.get(DIRECTION.AFTER_RECORD).values())){
			conf.commentsAfter = mapCommentFields.get(DIRECTION.AFTER_RECORD).values();
		}
		
		conf.init();
		
		return conf;
	}

}
