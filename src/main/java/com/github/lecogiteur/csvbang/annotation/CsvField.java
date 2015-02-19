/**
 *  com.github.lecogiteur.csvbang.annotation.CsvField
 * 
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
package com.github.lecogiteur.csvbang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.lecogiteur.csvbang.util.IConstantsCsvBang;


/**
 * Annotation in order to define a CSV field
 * @author Tony EMMA
 * @since 0.0.1
 * @version 1.0.0
 *
 */
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CsvField {

	/**
	 * Name of field in CSV file. If no name is defined, the name of member (field or method) is selected.
	 * By default no name is defined.
	 * @return name
	 * @since 0.0.1
	 */
	String name() default IConstantsCsvBang.DEFAULT_FIELD_NAME;
	
	/**
	 * <P>
	 * Position of field in file. The first position of the first field is 1. 
	 * It is not required that position numbers follow. The default position is -1.
	 * </p>
	 * <p>
	 * If no position is required you can set a negative position. If you want to override a {@link CsvField} annotation in sub class, you
	 * must set a negative value other than -1
	 * </p>
	 * @return the position of field
	 * @since 0.0.1
	 * 
	 */
	int position() default IConstantsCsvBang.DEFAULT_FIELD_POSITION;
	
	/**
	 * Default value if the value is null. By default, it's an empty string
	 * @return default value.
	 * @since 0.0.1
	 * 
	 */
	String defaultIfNull() default IConstantsCsvBang.DEFAULT_FIELD_NULL_VALUE;
	
	/**
	 * Delete field if the value is null. Delete field only for the current record. By default {@value com.github.lecogiteur.csvbang.util.IConstantsCsvBang#DEFAULT_FIELD_DELETE_IF_NULL}.
	 * @return True if we must delete field.
	 * @since 0.0.1
	 * 
	 */
	boolean deleteIfNull() default IConstantsCsvBang.DEFAULT_FIELD_DELETE_IF_NULL; 
	
	/**
	 * The name of custom method in order to set CSV bean when we read CSV file. 
	 * By default, CsvBang verify if the field is public modifier or standard getter and setter method.
	 * In case of the field is a method, we can define a custom method in order to set CSV bean. 
	 * @return name of custom method in order to set CSV bean.
	 * @since 1.0.0
	 */
	String customMethodNameSetter() default IConstantsCsvBang.DEFAULT_FIELD_SETTER;
	
	/**
	 * <p>The factory is used in order to set CSV bean. When CsvBang read a CSV file, it generate field with String type. 
	 * But, each field can define its type in CSV bean.</p> 
	 * <p>By default, in order to generate a new instance of field type, CsvBang searches by priority:
	 * <ul>
	 * 	<li>A custom factory</li>
	 * 	<li>Static method in class of CSV field type named "valueOf"</li>
	 * 	<li>Static method in class of CSV field type named "newInstance"</li>
	 * 	<li>constructor in class of CSV field type</li>
	 * 	<li>Static methods in class of CSV field type with custom method name</li>
	 * </ul>
	 * </p>
	 * <p>
	 * CsvBang stops to a step when it finds some static methods or constructor with one parameter. 
	 * If the type of CSV is String and there is no factory defined, CsvBang sets directly the field in CSV bean.
	 * </p>
	 * </BR>
	 * <p>
	 * "factory" permit you to define a custom factory in order to generate a new instance of type of CSV field with a String parameter. 
	 * The factory must contains some static methods or non-static methods with String parameter. In case of non-static method you must define a default constructor for your factory. 
	 * </p>
	 * @return the factory of value for field Type
	 * @since 1.0.0
	 */
	Class<?> factory();
	
	/**
	 * The name of method to use in factory. If the name is not defined, CsvBang searches automatically all methods with one parameter.
	 * @return the name method. By default, no method is defined
	 * @since 1.0.0
	 * @see {@link com.github.lecogiteur.csvbang.annotation.CsvField#factory()}
	 */
	String factoryMethodName();
	
	
}
