/**
 *  fr.csvbang.util.ReflectionUti
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
package fr.csvbang.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.csvbang.exception.CsvBangException;

/**
 * Utility class on reflection
 * @author Tony EMMA
 *
 */
public class ReflectionUti {
	
	private static final String CLASS_EXTENSION = ".class";
	
	private static final Pattern CLASS_FILE = Pattern.compile("^.*" + Pattern.quote(CLASS_EXTENSION) + "$");
	
	private static final String JAR_PROTOCOL = "jar";
	
	
	/**
	 * Generate a class from the type name
	 * @param className the name of the non-base type class to find
	 * @return the class
	 * 
	 * @author Tony EMMA
	 */
	private static Class<?> generateClass(final String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			//do nothing
		} catch (UnsatisfiedLinkError e){
			//do nothing
		} catch (Exception e){
			//do nothing
		} catch (NoClassDefFoundError e){
			//do nothing
		}
		return null;
	}
	
	
	/**
	 * Scan a package in order to get all classes of a package
	 * @param directory directory of package
	 * @param packageName name of package to find
	 * @return list of class of package
	 * 
	 * @author Tony EMMA
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
						final Collection<Class<?>> list = scanSimplePackage(subDirectory, className);
						if (list != null && list.size() > 0){
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
	 * @throws IOException if there i probleme with Jar
	 * 
	 * @author Tony EMMA
	 */
	private static Collection<Class<?>> scanJar(final URL resource, final String pathOfPackage) 
	throws IOException {

		Collection<Class<?>> clazzs = null;
		
		//Get the jar
		final JarURLConnection connection = (JarURLConnection) resource.openConnection();
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
		
		return clazzs;
	}
	
	
	

    /**
     * Get all classes of the package
     * @param packageUrls list of package from classloaders
     * @param pathOfPackage the package to find
     * @param packageName name of package to find
     * @return Get all classes of package
     * @throws IOException if there is problem with a jar
     * 
     * @author Tony EMMA
     */
    private static Collection<Class<?>> getClasses(final Enumeration<URL> packageUrls, final String pathOfPackage, 
    		String packageName) throws IOException{
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

            if (c != null && c.size() > 0){
                clazzs.addAll(c);
            }
        }

        return clazzs;
    }

    /**
     * Scan all classloader in order to find all classes of the package
     * @param packageName the package to find
     * @return all classes of the package
     * @throws IOException if there is problem with a jar
     * 
     * @author Tony EMMA
     */
    public static Collection<Class<?>> scanPackageClass(String packageName) throws IOException {
    	if (packageName == null || "".equals(packageName)){
    		return null;
    	}
    	
    	final Collection<Class<?>> clazzs = new HashSet<Class<?>>();
    	
    	String pn = packageName;
    	if (packageName.endsWith(".*")){
    		pn = packageName.substring(0, packageName.length() - 2);
    	}
    	
    	final String pathOfPackage = pn.replace('.', File.separatorChar);
    	
    	//get package the the system classloader
    	Enumeration<URL> packageUrls = ClassLoader.getSystemClassLoader().getResources(pathOfPackage);
    	Collection<Class<?>> c = getClasses(packageUrls, pathOfPackage, pn);
    	
    	if (c != null && c.size() > 0){
    		clazzs.addAll(c);
    	}
    	
    	//get package from the classloader of application
    	final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    	if (contextLoader != null){
    		//No need to get classloader parent, "getResources" do it.
    		packageUrls = contextLoader.getResources(pathOfPackage);
    		c = getClasses(packageUrls, pathOfPackage, pn);
    		if (c != null && c.size() > 0){
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
     * 
     * @author Tony EMMA
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
    
	 public static void main(String[] args) throws ClassNotFoundException, IOException{
		 scanPackageClass("java.lang");
		 String[] kiki = new String[]{};
		 if (kiki instanceof Object[]){
			 System.out.println("year !!");
		 }
	 }

}
