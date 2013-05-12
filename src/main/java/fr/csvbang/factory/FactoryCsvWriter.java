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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import fr.csvbang.annotation.CsvType;
import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.util.ConfigurationUti;
import fr.csvbang.util.CsvbangUti;
import fr.csvbang.util.ReflectionUti;
import fr.csvbang.writer.AsynchronousBlockingCsvWriter;
import fr.csvbang.writer.AsynchronousCsvWriter;
import fr.csvbang.writer.BlockingCsvWriter;
import fr.csvbang.writer.CsvWriter;
import fr.csvbang.writer.SimpleCsvWriter;

/**
 * Factory which generate CSV write
 * @author Tony EMMA
 *
 */
public class FactoryCsvWriter {

	/**
	 * Pattern in order to split a list of package
	 */
	private static final Pattern PACKAGE_SEPARATOR = Pattern.compile("\\s*,\\s*");
	
	/**
	 * Number of thread used for all asynchronous writer
	 */
	private int numberOfWriterThread = Math.max(1, Math.round(Runtime.getRuntime().availableProcessors() / 3));
	
	/**
	 * Service which manages writing Thread. 
	 */
	private ExecutorService executorWriterService;


	/**
	 * List of CSV bean with their configuration
	 */
	private final Map<Class<?>, CsvBangConfiguration> configurations = new HashMap<Class<?>, CsvBangConfiguration>();


	/**
	 * Constructor
	 * @param clazzs list of class to parse. This class must be annotated with {@link CsvType}
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	//TODO revoir les exception
	public FactoryCsvWriter (final Collection<Class<?>> clazzs) throws IntrospectionException, IllegalAccessException, InstantiationException{
		loadConfigurations(clazzs);
		executorWriterService = Executors.newFixedThreadPool(numberOfWriterThread);
	}

	/**
	 * Constructor
	 * @param sPkg list of package separated by comma and contained class annotated with {@link CsvType}
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	//TODO revoir les exceptions
	public FactoryCsvWriter (final String sPkg) throws ClassNotFoundException, IOException, IntrospectionException, IllegalAccessException, InstantiationException{
		final String[] pkgs = PACKAGE_SEPARATOR.split(sPkg);
		if (pkgs != null){
			for (final String pkg:pkgs){
				final Collection<Class<?>> clazzs = ReflectionUti.scanPackageClass(pkg);
				loadConfigurations(clazzs);
			}
		}
		executorWriterService = Executors.newFixedThreadPool(numberOfWriterThread);
	}
	
	/**
	 * Set the number of thread in order to write files. Used only if you want to write asynchronous. By default the number of processor divide by 3.
	 * @param number number of thread
	 * 
	 * @author Tony EMMA
	 */
	public void setNumberOfWriterThread(int number){
		this.numberOfWriterThread = number;
		executorWriterService = Executors.newFixedThreadPool(numberOfWriterThread);
	}

	/**
	 * Create a writer
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param destination path of file  for destination
	 * @return the CSV writer
	 * @throws CsvBangException if an error occurred
	 * 
	 * @author Tony EMMA
	 */
	//TODO revoir les exceptions
	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final String destination) throws CsvBangException{
		final CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			throw new CsvBangException("Pas de conf");
		}
		if (0 < conf.blockingSize){
			if (conf.isAsynchronousWrite){
				return new AsynchronousBlockingCsvWriter<T>(destination, conf, executorWriterService);
			}
			return new BlockingCsvWriter<T>(destination, conf);
		}
		
		if (conf.isAsynchronousWrite){
			return new AsynchronousCsvWriter<T>(destination, conf, executorWriterService);
		}

		return new SimpleCsvWriter<T>(destination, conf);
	}

	/**
	 * Create a writer
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param destination path of file  for destination
	 * @return the CSV writer
	 * @throws CsvBangException if an error occurred
	 * 
	 * @author Tony EMMA
	 */
	//TODO revoir les exceptions
	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final File destination) throws CsvBangException{
		final CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			throw new CsvBangException("Pas de conf");
		}

		if (0 < conf.blockingSize){
			if (conf.isAsynchronousWrite){
				return new AsynchronousBlockingCsvWriter<T>(destination, conf, executorWriterService);
			}
			return new BlockingCsvWriter<T>(destination, conf);
		}
		
		if (conf.isAsynchronousWrite){
			return new AsynchronousCsvWriter<T>(destination, conf, executorWriterService);
		}

		return new SimpleCsvWriter<T>(destination, conf);
	}

	//TODO revoir les exceptions
	public void add(final Class<?> clazz) throws IntrospectionException, IllegalAccessException, InstantiationException{
		if (clazz != null){
			Collection<Class<?>> c =new ArrayList<Class<?>>(1);
			c.add(clazz);
			loadConfigurations(c);
		}
	}

	//TODO revoir les exceptions
	public void add(final Collection<Class<?>> clazzs) throws IntrospectionException, IllegalAccessException, InstantiationException{
		loadConfigurations(clazzs);
	}

	//TODO revoir les exceptions
	public void addPackage(final String packages) throws IOException, IntrospectionException, IllegalAccessException, InstantiationException{
		if(CsvbangUti.isStringNotBlank(packages)){
			final String[] pkgs = PACKAGE_SEPARATOR.split(packages);
			if (pkgs != null){
				for (final String pkg:pkgs){
					final Collection<Class<?>> clazzs = ReflectionUti.scanPackageClass(pkg);
					loadConfigurations(clazzs);
				}
			}
		}
	}

	/**
	 * Parse class annotated with {@link CsvType} and load the configuration
	 * @param clazzs a list of class
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 * @author Tony EMMA
	 */
	//TODO revoir les exceptions
	private void loadConfigurations(final Collection<Class<?>> clazzs) throws IntrospectionException, IllegalAccessException, InstantiationException{
		if (clazzs != null){
			for (final Class<?> clazz:clazzs){
				final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(clazz);
				if (conf != null){
					configurations.put(clazz, conf);
				}
			}
		}
	}
}
