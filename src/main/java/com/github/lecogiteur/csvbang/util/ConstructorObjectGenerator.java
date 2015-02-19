/**
 *  com.github.lecogiteur.csvbang.util.ConstructorObjectGenerator
 * 
 *  Copyright (C) 2013-2015  Tony EMMA
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Generate a new instance of value for field with the type of field in CSV bean. We use constructor in order to generate new instance.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConstructorObjectGenerator<T> implements ObjectGenerator<T> {
	
	/**
	 * The logger
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = Logger.getLogger(ConstructorObjectGenerator.class.getName());
	
	/**
	 * List of constructor by parameter type
	 * @since 1.0.0
	 */
	private final Map<Class<?>, Constructor<T>> constructors;
	
	
	/**
	 * Constructor
	 * @param constructors List of constructor by parameter type
	 * @since 1.0.0
	 */
	public ConstructorObjectGenerator(final Map<Class<?>, Constructor<T>> constructors) {
		super();
		this.constructors = constructors;
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.util.ObjectGenerator#generate(java.lang.Object)
	 * @since 1.0.0
	 */
	@Override
	public T generate(final Object value) throws CsvBangException {
		if (value == null){
			return null;
		}
		T result = null;
		
		//retrieve the constructor
		Constructor<T> constructor = constructors.get(value.getClass());
		if (constructor == null){
			for (final Class<?> c:constructors.keySet()){
				if (c.isAssignableFrom(value.getClass())){
					constructor = constructors.get(c);
				}
			}
			if (constructor == null){
				throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. No constructor found", value));
			}
		}
		
		try {
			result = constructor.newInstance(value);
		} catch (IllegalArgumentException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] with a constructor of a field of type.", value), e);
		} catch (InstantiationException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] with a constructor of a field of type.", value), e);
		} catch (IllegalAccessException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] with a constructor of a field of type.", value), e);
		} catch (InvocationTargetException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] with a constructor of a field of type.", value), e);
		}
		return result;
	}
	
	/**
	 * Generate a new instance of generator for a type of CSV field
	 * @param clazz type of CSV field
	 * @return a new instance of generator for this type
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <U> ConstructorObjectGenerator<U> newInstance(final Class<U> clazz){
		if (clazz != null){
			//verify if class is interface
			if (clazz.isInterface()){
				LOGGER.warning(String.format("The type [%s] is an interface. No class implementation. Perhaps, you must define a factory (See CsvField annotation).", clazz));
				return null;
			}
			//retrieve public constructors
			final Constructor<?>[] cs = clazz.getConstructors();
			if (cs != null){
				//select only constructor with 1 parameter (the value)
				final Map<Class<?>, Constructor<U>> map = new HashMap<Class<?>, Constructor<U>>(cs.length);
				for (final Constructor<?> c:cs){
					final Class<?>[] parameters = c.getParameterTypes();
					if (parameters.length == 1){
						map.put(parameters[0], ((Constructor<U>) c));
					}
				}
				if (map.size() > 0){
					//only if map contains some constructors
					return new ConstructorObjectGenerator<U>(map);
				}
			}
		}
		return null;
	}

}
