/**
 *  com.github.lecogiteur.csvbang.util.FactoryObjectGenerator
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
 * Generate a new instance of value for field with the type of field in CSV bean. We use factory method in order to generate new instance.
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class FactoryObjectGenerator<T> implements ObjectGenerator<T> {
	
	/**
	 * The logger
	 * @since 1.0.0
	 */
	private static final Logger LOGGER = Logger.getLogger(FactoryObjectGenerator.class.getName());
	
	/**
	 * List of factory method by parameter type
	 * @since 1.0.0
	 */
	private final Map<Class<?>, Method> factoryMethod;
	
	/**
	 * Type of field
	 * @since 1.0.0
	 */
	private final Class<T> classOfType;
	

	/**
	 * Constructor
	 * @param factoryMethod List of factory method by parameter type
	 * @param classOfType Type of field
	 * @since 1.0.0
	 */
	public FactoryObjectGenerator(final Map<Class<?>, Method> factoryMethod,
			final Class<T> classOfType) {
		super();
		this.factoryMethod = factoryMethod;
		this.classOfType = classOfType;
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
		
		//retrieve method
		Method method = factoryMethod.get(value.getClass());
		if (method == null){
			for (final Class<?> c:factoryMethod.keySet()){
				if (c.isAssignableFrom(value.getClass())){
					method = factoryMethod.get(c);
				}
			}
			if (method == null){
				throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
						+ "No factory method found with parameter type [%s].", value, value.getClass()));
			}
		}
		
		Object factory = null;
		if (!Modifier.isStatic(method.getModifiers())){
			//it's a non method for this factory. In order to be thread safe we create a new instance for each call
			try {
				factory = method.getDeclaringClass().newInstance();
			} catch (InstantiationException e) {
				throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
						+ "We can't instantiate the factory [%s] for parameter type [%s].", 
						value, value.getClass(), method.getDeclaringClass(), method.getName()));
			} catch (IllegalAccessException e) {
				throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
						+ "We can't instantiate the factory [%s] for parameter type [%s].", 
						value, value.getClass(), method.getDeclaringClass(), method.getName()));
			}
		}
		
		//generate result
		T result = null;
		try {
			result = classOfType.cast(method.invoke(factory, value));
		} catch (IllegalArgumentException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
					+ "We can't generate value with the factory [%s] for parameter type [%s].", 
					value, value.getClass(), method.getDeclaringClass(), method.getName()));
		} catch (IllegalAccessException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
					+ "We can't generate value with the factory [%s] for parameter type [%s].", 
					value, value.getClass(), method.getDeclaringClass(), method.getName()));
		} catch (InvocationTargetException e) {
			throw new CsvBangException(String.format("A problem has occurred when we generate value [%s] of a field of type. "
					+ "We can't generate value with the factory [%s] for parameter type [%s].", 
					value, value.getClass(), method.getDeclaringClass(), method.getName()));
		}
		return result;
	}

	/**
	 * Generate a new instance of generator for a type of CSV field
	 * @param clazz type of CSV field
	 * @param factory the class factory which generates some. The factory must have a default constructor.
	 * @param factoryMethod the name of factory method. If null, the generator selects the all methods which return the type of CSV field 
	 * @return a new instance of generator for this type
	 * @throws CsvBangException If no factory is defined.
	 * @since 1.0.0
	 */
	public static <U> FactoryObjectGenerator<U> newInstance(final Class<U> clazz, final Class<?> factory, final String factoryMethod) throws CsvBangException{
		if (factory == null){
			throw new CsvBangException(String.format("You want a factory generator for type [%s]. But you don't have defined a factory", clazz));
		}
		if (clazz != null){
			//verify if factory is interface
			if (factory.isInterface()){
				LOGGER.warning(String.format("The factory [%s] for type [%s] is an interface.", clazz, factory));
				return null;
			}
			//retrieve public constructors
			final Method[] array = factory.getMethods();
			if (array != null){
				//select only constructor with 1 parameter (the value)
				final Map<Class<?>, Method> map = new HashMap<Class<?>, Method>(array.length);
				for (final Method m:array){
					if (Modifier.isPublic(m.getModifiers()) && (CsvbangUti.isStringBlank(factoryMethod) || m.getName().equals(factoryMethod))){
						final Class<?>[] parameters = m.getParameterTypes();
						if (parameters.length == 1 && m.getReturnType() != null 
								&& m.getReturnType().isAssignableFrom(clazz)){
							if (map.containsKey(parameters[0])){
								LOGGER.warning(String.format("A generator for the type [%s] exists already. Duplicate method in factory [%s] with parameter [%s].", clazz, factory, parameters[0]));
								continue;
							}
							map.put(parameters[0], m);
						}
					}
				}
				if (map.size() > 0){
					//only if map contains some constructors
					return new FactoryObjectGenerator<U>(map, clazz);
				}
			}
		}
		return null;
	}
	//TODO dans l'annotation csvfield il faut ajouter les factory et factory method
}
