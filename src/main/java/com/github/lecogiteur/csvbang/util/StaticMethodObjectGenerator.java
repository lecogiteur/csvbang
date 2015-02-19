/**
 *  com.github.lecogiteur.csvbang.util.StaticMethodObjectGenerator
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Generate a new instance of value for field with the type of field in CSV bean. We use static method in order to generate new instance.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class StaticMethodObjectGenerator<T> implements ObjectGenerator<T> {
	
	/**
	 * The logger
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = Logger.getLogger(StaticMethodObjectGenerator.class.getName());
	
	/**
	 * List of method by parameter which can be generated a new instance of field value for CSV bean.
	 * @since 1.0.0
	 */
	private final Map<Class<?>, Method> methods;
	
	/**
	 * Class of field type
	 * @since 1.0.0
	 */
	private final Class<T> clazz;

	/**
	 * Constructor
	 * @param methods List of method by parameter which can be generated a new instance of field value for CSV bean.
	 * @since 1.0.0
	 */
	public StaticMethodObjectGenerator(final Map<Class<?>, Method> methods,  final Class<T> clazz) {
		super();
		this.methods = methods;
		this.clazz = clazz;
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
		
		//retrieve method
		Method method = methods.get(value.getClass());
		if (method == null){
			for (final Class<?> c:methods.keySet()){
				if (c.isAssignableFrom(value.getClass())){
					method = methods.get(c);
				}
			}
			if (method == null){
				throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
					+ "No method found with parameter type [%s].", value, value.getClass()));
			}
		}
		
		try {
			//we generate new value
			result = clazz.cast(method.invoke(null, value));
		} catch (IllegalArgumentException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type [%s]. "
					+ "We use a static method [%s] of this type.", value, value.getClass(), method.getName()), e);
		} catch (IllegalAccessException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type [%s]. "
					+ "We use a static method [%s] of this type.", value, value.getClass(), method.getName()), e);
		} catch (InvocationTargetException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type [%s]. "
					+ "We use a static method [%s] of this type.", value, value.getClass(), method.getName()), e);
		} catch (ClassCastException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type [%s]. "
					+ "We use a static method [%s] of this type.", value, value.getClass(), method.getName()), e);
		}
		
		return result;
	}
	
	/**
	 * Generate a new instance of generator for a type of CSV field
	 * @param clazz type of CSV field
	 * @return a new instance of generator for this type
	 * @since 1.0.0
	 */
	public static <U> StaticMethodObjectGenerator<U> newInstance(final Class<U> clazz){
		if (clazz != null){
			//verify if class is interface
			if (clazz.isInterface()){
				LOGGER.warning(String.format("The type [%s] is an interface. No method implementation. Perhaps, you must define a factory (See CsvField annotation).", clazz));
				return null;
			}
			//retrieve public constructors
			final Method[] array = clazz.getMethods();
			if (array != null){
				//select only constructor with 1 parameter (the value)
				final Map<Class<?>, Method> map = new HashMap<Class<?>, Method>(array.length);
				for (final Method m:array){
					if (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers())){
						final Class<?>[] parameters = m.getParameterTypes();
						if (parameters.length == 1 && m.getReturnType() != null 
								&& m.getReturnType().isAssignableFrom(clazz)){
							if (map.containsKey(parameters[0])){
								LOGGER.warning(String.format("A generator for the type [%s] exists already. Duplicate static method with parameter [%s].", clazz, parameters[0]));
								continue;
							}
							map.put(parameters[0], m);
						}
					}
				}
				if (map.size() > 0){
					//only if map contains some constructors
					return new StaticMethodObjectGenerator<U>(map, clazz);
				}
			}
		}
		return null;
	}

}




