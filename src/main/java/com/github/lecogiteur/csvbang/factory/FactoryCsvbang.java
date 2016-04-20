/**
 *  com.github.lecogiteur.csvbang.factory.FactoryCsvbang
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
package com.github.lecogiteur.csvbang.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.file.FileName;
import com.github.lecogiteur.csvbang.pool.CsvbangExecutorService;
import com.github.lecogiteur.csvbang.pool.CsvbangThreadPoolExecutor;
import com.github.lecogiteur.csvbang.reader.AsynchronousCsvReader;
import com.github.lecogiteur.csvbang.reader.CsvReader;
import com.github.lecogiteur.csvbang.reader.SimpleCsvReader;
import com.github.lecogiteur.csvbang.util.ConfigurationUti;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ReflectionUti;
import com.github.lecogiteur.csvbang.writer.AsynchronousBlockCsvWriter;
import com.github.lecogiteur.csvbang.writer.AsynchronousCsvWriter;
import com.github.lecogiteur.csvbang.writer.BlockCsvWriter;
import com.github.lecogiteur.csvbang.writer.CsvWriter;
import com.github.lecogiteur.csvbang.writer.DelegatedWriterWithRegisterThreadCsvWriter;
import com.github.lecogiteur.csvbang.writer.SimpleCsvWriter;


/**
 * <p>Factory which generates CSV writer or reader. This factory manages a list of class (CSV bean) and their configuration.</p>
 * <p>
 * The factory creates a writer/reader in function of configuration. Writers/Readers are thread-safe and can manage different strategies of multi-threading.
 * In fact, it exists two strategies of multi-threading:
 * <ul>
 * 	<li>parallelism</li>
 *  <li>distributed</li>
 * </ul> 
 * The factory selects the best writer/reader in function your strategy. 
 * </p>
 * @author Tony EMMA
 * @version 1.0.0
 */
public class FactoryCsvbang {

	/**
	 * Pattern in order to split a list of package
	 * @since 1.0.0
	 */
	private static final Pattern PACKAGE_SEPARATOR = Pattern.compile("\\s*,\\s*");
	
	/**
	 * Number of thread used for all asynchronous writer
	 * @since 1.0.0
	 */
	private int numberOfWriterThread = Math.max(1, Math.round(Runtime.getRuntime().availableProcessors() / 3));
	
	/**
	 * Service which manages Thread. 
	 * @since 1.0.0
	 */
	private CsvbangExecutorService executorService;


	/**
	 * List of CSV bean with their configuration
	 * @since 1.0.0
	 */
	private final Map<Class<?>, CsvBangConfiguration> configurations = new HashMap<Class<?>, CsvBangConfiguration>();
	
	/**
	 * Default constructor. In this case the configuration of CSV class is loaded in lazy mode.
	 * @since 1.0.0
	 */
	public FactoryCsvbang(){
		executorService = new CsvbangThreadPoolExecutor(numberOfWriterThread);
	}


	/**
	 * Constructor
	 * @param clazzs list of class to parse. This class must be annotated with {@link CsvType}
	 * @throws CsvBangException if we cannot load a CSV bean configuration
	 * @since 1.0.0
	 */
	public FactoryCsvbang (final Collection<Class<?>> clazzs) throws CsvBangException{
		loadConfigurations(clazzs);
		executorService = new CsvbangThreadPoolExecutor(numberOfWriterThread);
	}

	/**
	 * Constructor
	 * @param sPkg list of package separated by comma which contains annotated classes with {@link CsvType}
	 * @throws CsvBangException if we cannot scan package or load a CSV bean configuration
	 * @since 1.0.0
	 */
	public FactoryCsvbang (final String sPkg) throws CsvBangException{
		if (CsvbangUti.isStringNotBlank(sPkg)){
			final String[] pkgs = PACKAGE_SEPARATOR.split(sPkg);
			if (pkgs != null){
				for (final String pkg:pkgs){
					final Collection<Class<?>> clazzs = ReflectionUti.scanPackageClass(pkg);
					loadConfigurations(clazzs);
				}
			}
		}
		executorService = new CsvbangThreadPoolExecutor(numberOfWriterThread);
	}
	
	/**
	 * Set the number of thread in order to write/read files. Used only if you want to write/read asynchronous. 
	 * By default the number of processor divide by 3. Be careful. if set this value during CSV generation you loose all information.
	 * This method reset all processes. 
	 * @param number number of thread
	 * @since 1.0.0
	 */
	public void setNumberOfWriterThread(int number){
		this.numberOfWriterThread = number;
		executorService = new CsvbangThreadPoolExecutor(numberOfWriterThread);
	}
	
	/**
	 * Create a writer
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @return the CSV writer
	 * @throws CsvBangException if an error occurred
	 * @since 1.0.0
	 */
	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz) throws CsvBangException{
		return createCsvWriter(clazz, (File)null);
	}
	
	/**
	 * Create a writer
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param destination path of file  for destination
	 * @return the CSV writer
	 * @throws CsvBangException if an error occurred
	 * @since 1.0.0
	 */
	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final String destination) throws CsvBangException{
		if (CsvbangUti.isStringNotBlank(destination)){
			return createCsvWriter(clazz, new File(destination));
		}
		return createCsvWriter(clazz, (File)null);
	}

	/**
	 * Create a writer
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param destination path of file  for destination
	 * @return the CSV writer
	 * @throws CsvBangException if an error occurred
	 * @since 1.0.0
	 */
	public <T> CsvWriter<T> createCsvWriter(final Class<T> clazz, final File destination) throws CsvBangException{
		CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			//lazy mode
			add(clazz);
			conf = configurations.get(clazz);
			if (conf == null){
				throw new CsvBangException(String.format("No configuration available for class [%s]. Verify if this class has an annotation CsvType.", clazz));
			}
		}

		if (0 < conf.blockSize){
			if (conf.isAsynchronousWrite){
				return mustRegisterThread(conf, new AsynchronousBlockCsvWriter<T>(CsvFilePoolFactory.createPoolForWriting(conf, 
						destination, null, null), 
						conf, executorService));
			}
			return mustRegisterThread(conf, new BlockCsvWriter<T>(CsvFilePoolFactory.createPoolForWriting(conf, 
					destination, null, null), conf));
		}
		
		if (conf.isAsynchronousWrite){
			return mustRegisterThread(conf, new AsynchronousCsvWriter<T>(CsvFilePoolFactory.createPoolForWriting(conf, 
					destination, null, null), conf, executorService));
		}

		return mustRegisterThread(conf, new SimpleCsvWriter<T>(CsvFilePoolFactory.createPoolForWriting(conf, 
				destination, null, null), conf));
	}
	
	/**
	 * Verify if we must register thread in order to close CSV writer when all threads are closed.
	 * @param conf the CSV configuration
	 * @param writer the writer to delegate
	 * @return the writer implementation
	 * @since 1.0.0
	 */
	private <T> CsvWriter<T> mustRegisterThread(final CsvBangConfiguration conf, final CsvWriter<T> writer){
		if (conf.isRegisterThread){
			return new DelegatedWriterWithRegisterThreadCsvWriter<T>(writer);
		}
		return writer;
	}

	/**
	 * Create a reader
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param paths list of directories and file to read. No warranty on order when we processed files. can be null.
	 * @param filename the file name to use in order to retrieve file to read. Can be null. If null, we use the {@link com.github.lecogiteur.csvbang.annotation.CsvFile#fileName()} definition. 
	 * @return the CSV reader
	 * @throws CsvBangException if no file to read or if no configuration has been found for CSV bean.
	 * @since 1.0.0
	 */
	public <T> CsvReader<T> createCsvReader(final Class<T> clazz, final Collection<File> paths, final FileName filename) throws CsvBangException{
		CsvBangConfiguration conf = configurations.get(clazz);

		if (conf == null){
			//lazy mode
			add(clazz);
			conf = configurations.get(clazz);
			if (conf == null){
				throw new CsvBangException(String.format("No configuration available for class [%s]. Verify if this class has an annotation CsvType.", clazz));
			}
		}
		if (conf.isAsynchronousWrite){
			return new AsynchronousCsvReader<T>(conf, clazz, 
					CsvFilePoolFactory.createPoolForReading(conf, paths, filename), executorService);
		}

		return new SimpleCsvReader<T>(conf, clazz, CsvFilePoolFactory.createPoolForReading(conf, paths, filename));
	}
	
	/**
	 * Create a reader
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param paths list of directories and file to read. No warranty on order when we processed files. can't be null.
	 * @return the CSV reader
	 * @throws CsvBangException if no file to read or if no configuration has been found for CSV bean.
	 * @since 1.0.0
	 */
	public <T> CsvReader<T> createCsvReader(final Class<T> clazz, final Collection<File> paths) throws CsvBangException{
		return createCsvReader(clazz, paths, null);
	}
	
	/**
	 * Create a reader
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @param filename the file name to use in order to retrieve file to read. Can be null. If null, we use the {@link com.github.lecogiteur.csvbang.annotation.CsvFile#fileName()} definition. 
	 * @return the CSV reader
	 * @throws CsvBangException if no file to read or if no configuration has been found for CSV bean.
	 * @since 1.0.0
	 */
	public <T> CsvReader<T> createCsvReader(final Class<T> clazz, final FileName filename) throws CsvBangException{
		return createCsvReader(clazz, null, filename);
	}
	
	/**
	 * Create a reader. The CSV bean must defined filename attribute in CsvFile annotation (without <b>*</b> pattern).
	 * @param <T> bean CSV annotated with {@link CsvType}
	 * @param clazz bean CSV annotated with {@link CsvType}
	 * @return the CSV reader
	 * @throws CsvBangException if no file to read or if no configuration has been found for CSV bean.
	 * @since 1.0.0
	 */
	public <T> CsvReader<T> createCsvReader(final Class<T> clazz) throws CsvBangException{
		return createCsvReader(clazz, null, null);
	}
	
	
	/**
	 * Add a class to the factory
	 * @param clazz class
	 * @throws CsvBangException if we cannot load a CSV bean configuration
	 * @since 1.0.0
	 */
	public void add(final Class<?> clazz) throws CsvBangException{
		if (clazz != null){
			Collection<Class<?>> c =new ArrayList<Class<?>>(1);
			c.add(clazz);
			loadConfigurations(c);
		}
	}

	/**
	 * Add a list of class to factory
	 * @param clazzs classes
	 * @throws CsvBangException if we cannot load a CSV bean configuration
	 * @since 1.0.0
	 */
	public void add(final Collection<Class<?>> clazzs) throws CsvBangException {
		loadConfigurations(clazzs);
	}

	/**
	 * Add package to the factory. The package must be separated be a comma.
	 * @param packages packages
	 * @throws CsvBangException if we cannot scan package or load a CSV bean configuration
	 * @since 1.0.0
	 */
	public void addPackage(final String packages) throws CsvBangException {
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
	 * 
	 * @throws CsvBangException if we cannot load configuration
	 * @since 1.0.0
	 */
	private void loadConfigurations(final Collection<Class<?>> clazzs) throws CsvBangException {
		if (clazzs != null){
			for (final Class<?> clazz:clazzs){
				try{
					final CsvBangConfiguration conf = ConfigurationUti.loadCsvBangConfiguration(clazz);
					if (conf != null){
						configurations.put(clazz, conf);
					}
				}catch(CsvBangException e){
					throw new CsvBangException(String.format("Cannot load configuration of class %s. Verify annotations.", clazz), e);
				}
			}
		}
	}
}
