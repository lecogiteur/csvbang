/**
 *  fr.csvbang.factory.FactoryCsvWriter
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
package fr.csvbang.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import fr.csvbang.annotation.CsvField;
import fr.csvbang.annotation.CsvFormat;
import fr.csvbang.annotation.CsvFormat.TYPE_FORMAT;
import fr.csvbang.annotation.CsvType;
import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.configuration.CsvFieldConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.formatter.BooleanCsvFormatter;
import fr.csvbang.formatter.CsvFormatter;
import fr.csvbang.formatter.CurrencyCsvFormatter;
import fr.csvbang.formatter.DateCsvFormatter;
import fr.csvbang.formatter.Default;
import fr.csvbang.formatter.NumberCsvFormatter;
import fr.csvbang.util.ReflectionUti;
import fr.csvbang.writer.BlockingCsvWriter;
import fr.csvbang.writer.CsvWriter;
import fr.csvbang.writer.SimpleCsvWriter;

/**
 * @author Tony EMMA
 *
 */
public class FactoryCsvWriter {

	private static final Pattern PACKAGE_SEPARATOR = Pattern.compile("\\s*,\\s*"); 
	private static final Pattern LOCALE_SEPARATOR = Pattern.compile("_"); 
	private static final CsvFormatter DEFAULT_FORMAT = new Default(); 


	private final Map<Class<?>, CsvBangConfiguration> configurations = new HashMap<Class<?>, CsvBangConfiguration>();


	public FactoryCsvWriter (final Collection<Class<?>> clazzs) throws IntrospectionException, IllegalAccessException, InstantiationException{
		loadConfigurations(clazzs);
	}

	public FactoryCsvWriter (final String sPkg) throws ClassNotFoundException, IOException, IntrospectionException, IllegalAccessException, InstantiationException{
		final String[] pkgs = PACKAGE_SEPARATOR.split(sPkg);
		if (pkgs != null){
			for (final String pkg:pkgs){
				final Collection<Class<?>> clazzs = ReflectionUti.scanPackageClass(pkg);
				loadConfigurations(clazzs);
			}
		}
	}

	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final String destination) throws CsvBangException{
		final CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			throw new CsvBangException("Pas de conf");
		}
		if (0 < conf.blockingSize){
			return new BlockingCsvWriter<T>(destination, conf);
		}

		return new SimpleCsvWriter<T>(destination, conf);
	}

	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final File destination) throws CsvBangException{
		final CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			throw new CsvBangException("Pas de conf");
		}

		if (0 < conf.blockingSize){
			return new BlockingCsvWriter<T>(destination, conf);
		}

		return new SimpleCsvWriter<T>(destination, conf);
	}

	private void loadConfigurations(final Collection<Class<?>> clazzs) throws IntrospectionException, IllegalAccessException, InstantiationException{
		if (clazzs != null){
			for (final Class<?> clazz:clazzs){
				final Annotation[] annotations = clazz.getAnnotations();
				if (annotations == null || annotations.length == 0){
					continue;
				}
				//TODO manage from superclass
				CsvBangConfiguration conf = null;
				for (final Annotation annontation:annotations){
					if (annontation instanceof CsvType){
						final CsvType csvType = (CsvType) annontation;
						conf = new CsvBangConfiguration();
						conf.blockingSize = csvType.blocksize();
						conf.charset = csvType.charsetName();
						conf.delimiter = csvType.delimiter();
						conf.endRecord = csvType.endRecord();
						conf.startRecord = csvType.startRecord();
						conf.isDisplayHeader = csvType.header();
						conf.escapeQuoteCharacter = csvType.quoteEscapeCharacter();
						if (csvType.quoteCharacter() != null && csvType.quoteCharacter().length() > 0){
							conf.quote = csvType.quoteCharacter().charAt(0);
						}
						break;
					}
				}
				if (conf == null){
					continue;
				}


				final List<AnnotatedElement> members = new ArrayList<AnnotatedElement>();
				Field[] fields = null;
				Method[] methods = null;
				Class<?> parent = clazz;
				while (parent != null){
					fields = parent.getDeclaredFields();
					methods = parent.getMethods();
					if (fields != null && fields.length > 0){
						members.addAll(Arrays.asList(fields));
					}
					if (methods != null && methods.length > 0){
						members.addAll(Arrays.asList(methods));
					}
					parent = parent.getSuperclass();
				}


				final TreeMap<Integer, CsvFieldConfiguration> confFileds = loadCsvField(clazz, members);
				if (confFileds.size() == 0){
					continue;
				}
				conf.fields = confFileds.values();
				generateHeader(conf, confFileds);
				configurations.put(clazz, conf);
			}
		}
	}

	private final TreeMap<Integer, CsvFieldConfiguration> loadCsvField(final Class<?> c, final Collection<AnnotatedElement> members) 
	throws IntrospectionException, IllegalAccessException, InstantiationException{
		final TreeMap<Integer, CsvFieldConfiguration> confFileds = new TreeMap<Integer, CsvFieldConfiguration>();
		int count = 1000;
		for (final AnnotatedElement member:members){
			final Annotation[] annotations = member.getAnnotations();
			if (annotations == null || annotations.length == 0){
				continue;
			}


			final CsvFieldConfiguration conf = new CsvFieldConfiguration();
			for (final Annotation annontation:annotations){
				if (annontation instanceof CsvField){
					final CsvField csvField = (CsvField) annontation;
					int pos = csvField.position();
					if (pos <= 0){
						pos = members.size() + count;
						++count;
					}

					conf.name = csvField.name();



					AnnotatedElement temp = member;
					if (member instanceof Field){
						Field f = (Field)member;
						if (!f.isAccessible()){
							final Method m = ReflectionUti.getGetterMethod(c, f.getName());
							if (m != null){
								temp = m;
							}else{
								//TODO log
								continue;
							}
						}
					}

					conf.memberBean = temp;

					conf.nullReplaceString = csvField.defaultIfNull();
					if (conf.format == null){
						conf.format = DEFAULT_FORMAT;
					}
					//TODO vérifier si déjà renseigné à cette position
					confFileds.put(pos, conf);

				}else if (annontation instanceof CsvFormat){
					final CsvFormat csvFormat = (CsvFormat) annontation;
					conf.format = getFormat(csvFormat);
				}
			}
		}

		return confFileds;
	}

	private void generateHeader(final CsvBangConfiguration conf, final Map<Integer, CsvFieldConfiguration> confFileds){
		if (conf.isDisplayHeader){
			final StringBuilder header = new StringBuilder(1000).append(conf.startRecord);
			for (final Entry<Integer, CsvFieldConfiguration> entry:confFileds.entrySet()){

				final CsvFieldConfiguration field = entry.getValue();
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

	private CsvFormatter getFormat(final CsvFormat csvFormat) throws IllegalAccessException, InstantiationException{
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
			case CUSTOM:
				if (csvFormat.customFormatter() != null){
					format = csvFormat.customFormatter().newInstance();
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

}
