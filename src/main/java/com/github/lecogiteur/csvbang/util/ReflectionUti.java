/**
 *  com.github.lecogiteur.csvbang.util.ReflectionUti
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
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lecogiteur.csvbang.annotation.CsvComment;
import com.github.lecogiteur.csvbang.annotation.CsvField;
import com.github.lecogiteur.csvbang.annotation.CsvFile;
import com.github.lecogiteur.csvbang.annotation.CsvFooter;
import com.github.lecogiteur.csvbang.annotation.CsvFormat;
import com.github.lecogiteur.csvbang.annotation.CsvHeader;
import com.github.lecogiteur.csvbang.annotation.CsvType;
import com.github.lecogiteur.csvbang.exception.CsvBangException;


/**
 * Utility class on reflection
 * @author Tony EMMA
 * @version 1.0.0
 * @since 0.0.1
 *
 */
public class ReflectionUti {
	
	/**
	 * The logger
	 */
	private static final Logger LOGGER = Logger.getLogger(ReflectionUti.class.getName());

	/**
	 * Extension file for class java
	 * @since 0.0.1
	 */
	private static final String CLASS_EXTENSION = ".class";

	/**
	 * Pattern in order to verify if string is a Java file
	 * @since 0.0.1
	 */
	private static final Pattern CLASS_FILE = Pattern.compile("^.*" + Pattern.quote(CLASS_EXTENSION) + "$");

	/**
	 * keyword for JAR protocol
	 * @since 0.0.1
	 */
	private static final String JAR_PROTOCOL = "jar";


	/**
	 * Generate a class from the type name
	 * @param className the name of the non-base type class to find
	 * @return the class
	 * @since 0.0.1
	 */
	private static Class<?> generateClass(final String className){
		try {
			return Class.forName(className);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, String.format("Cannot scan class %s", className), e);
		} 
		return null;
	}


	/**
	 * Scan a package in order to get all classes of a package
	 * @param directory directory of package
	 * @param packageName name of package to find
	 * @return list of class of package
	 * @since 0.0.1
	 */
	private static Collection<Class<?>> scanSimplePackage(final File directory, final String packageName) {

		Collection<Class<?>> clazzs = null;

		//list of file of directory
		final String[] files = directory.list();

		if (files != null && files.length > 0){
			clazzs = new HashSet<Class<?>>();
			for (final String file:files) {
				//for each file, we get all class
				final StringBuilder fileName = new StringBuilder(file);
				String className = null;
				final Matcher m = CLASS_FILE.matcher(fileName);

				if (m.matches()) {
					//if the file it's a class
					className = fileName.delete(fileName.length() - CLASS_EXTENSION.length(), fileName.length()).insert(0, ".").insert(0, packageName).toString();
					final Class<?> clazz = generateClass(className);
					if (clazz != null){
						clazzs.add(clazz);
					}
				}else {
					//if the file is a directory, we scan this directory in order to get all classes
					final File subDirectory = new File(directory, fileName.toString());
					if (subDirectory.isDirectory()) {
						className = fileName.insert(0, ".").insert(0, packageName).toString();
						final Collection<Class<?>> list = scanSimplePackage(subDirectory, className);
						if (CsvbangUti.isCollectionNotEmpty(list)){
							clazzs.addAll(list);
						}
					}
				}
			}
		}
		return clazzs;
	}

	/**
	 * Scan a Jar in order to get all classes of a package
	 * @param resource the jar
	 * @param pathOfPackage name of package to find
	 * @return list of classes of package
	 * @throws CsvBangException if there is problems with access to Jar
	 * @since 0.0.1
	 */
	private static Collection<Class<?>> scanJar(final URL resource, final String pathOfPackage) 
	throws CsvBangException {

		Collection<Class<?>> clazzs = null;

		//Get the jar
		JarURLConnection connection;
		try {
			connection = (JarURLConnection) resource.openConnection();
			final JarFile jarFile = new JarFile(new File(connection.getJarFileURL().getFile()));   

			//list of entries of Jar
			final Enumeration<JarEntry> entries = jarFile.entries();

			if (entries.hasMoreElements()){
				clazzs = new HashSet<Class<?>>();
				final Pattern p = Pattern.compile("^" + Pattern.quote(pathOfPackage) + ".+" + Pattern.quote(CLASS_EXTENSION) + "$");

				while(entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					final String entryName = entry.getName();
					String className = null;
					final Matcher m = p.matcher(entryName);
					if(m.matches()) {
						//if entry is a class
						className = entryName.replace(File.separatorChar, '.').replace(CLASS_EXTENSION, "");
						final Class<?> clazz = generateClass(className);
						if (clazz != null){
							clazzs.add(clazz);
						}
					}
				}
			}

		} catch (IOException e) {
			new CsvBangException(String.format("Cannot access to Jar %s", resource), e);
		}
		
		return clazzs;
	}




	/**
	 * Get all classes of the package
	 * @param packageUrls list of package from classloaders
	 * @param pathOfPackage the package to find
	 * @param packageName name of package to find
	 * @return Get all classes of package
	 * @throws CsvBangException if we cannot access to Jar file
	 * @since 0.0.1
	 */
	private static Collection<Class<?>> getClasses(final Enumeration<URL> packageUrls, final String pathOfPackage, 
			String packageName) throws CsvBangException{
		final Collection<Class<?>> clazzs = new HashSet<Class<?>>();

		if (packageUrls == null) {
			return null;
		}

		while (packageUrls.hasMoreElements()){
			final URL packageUrl = packageUrls.nextElement();
			Collection<Class<?>> c = null;
			if(JAR_PROTOCOL.equals(packageUrl.getProtocol())) {
				//if it's a jar
				c = scanJar(packageUrl, pathOfPackage);
			} else {
				//if it's a package
				c = scanSimplePackage(new File(packageUrl.getPath()), packageName);
			}

			if (CsvbangUti.isCollectionNotEmpty(c)){
				clazzs.addAll(c);
			}
		}

		return clazzs;
	}

	/**
	 * Scan all classloader in order to find all classes of the package
	 * @param packageName the package to find
	 * @return all classes of the package
	 * @throws CsvBangException if we cannot access to Jar files or resources
	 * @since 0.0.1
	 */
	public static Collection<Class<?>> scanPackageClass(String packageName) throws CsvBangException {
		if (CsvbangUti.isStringBlank(packageName)){
			return null;
		}

		final Collection<Class<?>> clazzs = new HashSet<Class<?>>();

		String pn = packageName;
		if (packageName.endsWith(".*")){
			pn = packageName.substring(0, packageName.length() - 2);
		}

		final String pathOfPackage = pn.replace('.', File.separatorChar);

		//get package the the system classloader
		Enumeration<URL> packageUrls;
		try {
			packageUrls = ClassLoader.getSystemClassLoader().getResources(pathOfPackage);
		} catch (IOException e) {
			throw new CsvBangException(String.format("Cannot access to resource %s in system classloader.", packageName), e);
		}
		Collection<Class<?>> c = getClasses(packageUrls, pathOfPackage, pn);

		if (CsvbangUti.isCollectionNotEmpty(c)){
			clazzs.addAll(c);
		}

		//get package from the classloader of application
		final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		if (contextLoader != null){
			//No need to get classloader parent, "getResources" do it.
			try {
				packageUrls = contextLoader.getResources(pathOfPackage);
			} catch (IOException e) {
				throw new CsvBangException(String.format("Cannot access to resource %s in current thread classloader.", packageName), e);
			}
			c = getClasses(packageUrls, pathOfPackage, pn);
			if (CsvbangUti.isCollectionNotEmpty(c)){
				clazzs.addAll(c);
			}
		}

		return clazzs;
	}

	/**
	 * Get value of property or method of a bean
	 * @param m the property or method
	 * @param bean the bean
	 * @return the value
	 * @throws CsvBangException if can't retrieve the value
	 * @since 0.0.1
	 */
	public static final Object getValue(final AnnotatedElement m, final Object bean) throws CsvBangException{
		Object v = null;
		try{
			if (m instanceof Field){
				v = ((Field) m).get(bean);
			}else if (m instanceof Method){
				v = ((Method) m).invoke(bean);
			}
		}catch(Exception e){
			throw new CsvBangException(String.format("Un problème est survenue dans la récupération d'un champs ou une méthode dans [%s]: %s", bean.getClass(), ((Member)m).getName()), e);
		}
		return v;
	}

	/**
	 * Get the getter method of a property
	 * @param c the class
	 * @param name the name of property
	 * @return the getter
	 * @throws CsvBangException if an error occurred when retrieve the getter
	 * @since 0.0.1
	 */
	public static final Method getGetterMethod(final Class<?> c, final String name) throws CsvBangException{
		BeanInfo info;
		try {
			info = Introspector.getBeanInfo(c);
		} catch (IntrospectionException e) {
			throw new CsvBangException(String.format("Cannot inspect %s", c), e);
		}
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			if (pd.getReadMethod() != null && pd.getName().equals(name)){
				return pd.getReadMethod();
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the setter method for a field
	 * @param field field
	 * @param c class of field
	 * @return the member which can be use in order to set a CSV field 
	 * @throws CsvBangException if the field is final.
	 * @since 1.0.0
	 */
	//TODO pour les méthode annoté CsvField, on doit pouvoir indiquer le nom de la méthode setter et getter (surtout pour les méthodes annotées CsvField). Il faut modifier l'annotation CsvField
	public static final AnnotatedElement getSetterMethod(final AnnotatedElement field, final Class<?> c) 
			throws CsvBangException{
		if (field != null && field instanceof Field){
			final Field f = (Field)field;
			if (Modifier.isFinal(f.getModifiers())){
				throw new CsvBangException(String.format("The field [%s] of class [%s] is final. We can't set it.", f.getName(), c));
			}
			
			if (Modifier.isPublic(f.getModifiers())){
				//public field we can set it
				return f;
			}
			
			//search a setter method
			BeanInfo info;
			try {
				info = Introspector.getBeanInfo(c);
			} catch (IntrospectionException e) {
				throw new CsvBangException(String.format("Cannot inspect %s", c), e);
			}
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				if (pd.getWriteMethod() != null && pd.getName().equals(f.getName())){
					return pd.getWriteMethod();
				}
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the type of field in order to set it.
	 * @param setter a setter (can be a property or method)
	 * @return the type of field
	 * @throws CsvBangException If in case of a method, the number of parameter is not correct 
	 * @since 1.0.0
	 */
	public static final Class<?> getSetterType(final AnnotatedElement setter)
			throws CsvBangException{
		if (setter != null){
			if (setter instanceof Field){
				return ((Field) setter).getType();
			}else if (setter instanceof Method){
				final Class<?>[] types = ((Method)setter).getParameterTypes();
				if (types == null || types.length == 0){
					throw new CsvBangException(String.format("The setter method [%s] has no parameter. So we can't set value to field.", 
							((Method)setter).getName()));
				}else if (types.length > 1){
					throw new CsvBangException(String.format("The setter method [%s] has multiples parameters. So we can't set value to field.", 
							((Method)setter).getName()));
				}
				return types[0];
			}
		}
		return null;
	}
	
	/**
	 * Creates a generator for a type in order to create new object of this type. When we read a CSV file, we must set the CSV bean with each types fo CSV field.
	 * This method select to best generator for a given type.
	 * It is possible to use factory, static methods or constructor. If the type is String (and no factory defined), no generator is necessary so we return null.
	 * @param type type of a CSV field.
	 * @param factory a factory to use for creating object. Can be null.
	 * @param factoryMethodName name of the factory method of factory class to use.
	 * @return a generator of object. If the type is String (and no factory defined), no generator is necessary so we return null.
	 * @throws CsvBangException If a problem occurred during creation.
	 * @since 1.0.0
	 */
	//TODO faire les tests unitaires pour cette méthode. Necessite la création des annotations factory et factory méthode
	public static final <T> ObjectGenerator<T> createTypeGenerator(final Class<T> type, 
			final Class<?> factory, final String factoryMethodName) throws CsvBangException{
		ObjectGenerator<T> generator = null;
		if (type != null){
			if (factory != null){
				//if a factory is defined
				generator = FactoryObjectGenerator.newInstance(type, factory, factoryMethodName);
				if (generator != null){
					return generator;
				}
			}
			
			if (String.class.equals(type)){
				//CsvBang manages natively String, so no need to generate again
				return null;
			}
			
			//if no factory, we search static method
			generator = StaticMethodObjectGenerator.newInstance(type);
			if (generator != null){
				return generator;
			}
			
			//if no static methods are defined, we search some constructors
			generator = ConstructorObjectGenerator.newInstance(type);
		}
		throw new CsvBangException(String.format("No generator is defined for Type [%s]. You must defined a constructor, static method in order to generate a new instance of %s", type, type));
	}
	
	/**
	 * Retrieve the CsvType annotation
	 * @param annotations the list of annotation
	 * @return the CsvType annotation or null if not exists
	 * @since 0.0.1
	 */
	public static CsvType getCsvTypeAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvType){
				return (CsvType)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvHeader annotation
	 * @param annotations the list of annotation
	 * @return the CsvHeader annotation or null if not exists
	 * @since 0.1.0
	 */
	public static CsvHeader getCsvHeaderAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvHeader){
				return (CsvHeader)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvFooter annotation
	 * @param annotations the list of annotation
	 * @return the CsvFooter annotation or null if not exists
	 * @since 0.1.0
	 */
	public static CsvFooter getCsvFooterAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvFooter){
				return (CsvFooter)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvField annotation
	 * @param annotations  the list of annotation
	 * @return the CsvField annotation or null if not exists
	 * @since 0.0.1
	 */
	public static CsvField getCsvFieldAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvField){
				return (CsvField)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvFormat annotation
	 * @param annotations  the list of annotation
	 * @return the CsvFormat annotation or null if not exists
	 * @since 0.0.1
	 */
	public static CsvFormat getCsvFormatAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvFormat){
				return (CsvFormat)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvFile annotation
	 * @param annotations  the list of annotation
	 * @return the CsvFile annotation or null if not exists
	 * @since 0.1.0
	 */
	public static CsvFile getCsvFileAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvFile){
				return (CsvFile)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the CsvComment annotation
	 * @param annotations  the list of annotation
	 * @return the CsvComment annotation or null if not exists
	 * @since 0.1.0
	 */
	public static CsvComment getCsvCommentAnnotation(final Annotation[] annotations){
		if (annotations == null || annotations.length == 0){
			return null;
		}
		for (final Annotation annotation:annotations){
			if (annotation instanceof CsvComment){
				return (CsvComment)annotation;
			}
		}
		return null;
	}
	
	/**
	 * Get Members (fields and methods) of a class. Do not search members of parent class.
	 * 
	 * @param clazz a class
	 * @return methods and all field declared on the class (not on its parent) 
	 * @since 0.0.1
	 */
	public static List<AnnotatedElement> getMembers(final Class<?> clazz){
		final List<AnnotatedElement> members = new ArrayList<AnnotatedElement>();
		final Field[] fields = clazz.getDeclaredFields();
		final Method[] methods = clazz.getDeclaredMethods();
		if (fields != null && fields.length > 0){
			members.addAll(Arrays.asList(fields));
		}
		if (methods != null && methods.length > 0){
			for (final Method m:methods){
				members.add(m);
			}
		}
		return members;
	}
}
