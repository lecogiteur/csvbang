/**
 *  com.github.lecogiteur.csvbang.parser.RecordGrammarAction
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
package com.github.lecogiteur.csvbang.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.lecogiteur.csvbang.configuration.CsvBangConfiguration;
import com.github.lecogiteur.csvbang.configuration.CsvFieldConfiguration;
import com.github.lecogiteur.csvbang.exception.CsvBangException;
import com.github.lecogiteur.csvbang.util.CsvbangUti;
import com.github.lecogiteur.csvbang.util.ReflectionUti;

/**
 * Generate a CSV record
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class RecordGrammarAction<T> implements GrammarAction<T> {
	
	/**
	 * List of fields
	 * @since 1.0.0
	 */
	private final List<GrammarAction<?>> fields;
	
	/**
	 * CSV bean class
	 * @since 1.0.0
	 */
	private final Class<T> beanClass;
	
	/**
	 * CsvBang configuration for the CSV bean
	 * @since 1.0.0
	 */
	private final CsvBangConfiguration conf;
	
	/**
	 * Last record of file. Warning: if file have a footer or comment after last record, this variable is at false.  
	 * @since 1.0.0
	 */
	private boolean isTerminatedRecord = false;

	/**
	 * Constructor
	 * @param beanClass
	 * @param conf
	 * @since 1.0.0
	 */
	public RecordGrammarAction(final Class<T> beanClass, final CsvBangConfiguration conf) {
		super();
		this.beanClass = beanClass;
		this.conf = conf;
		this.fields = new ArrayList<GrammarAction<?>>();
		this.fields.add(new FieldGrammarAction(100));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.RECORD;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#add(byte)
	 * @since 1.0.0
	 */
	@Override
	public void add(byte b) throws CsvBangException {
		if (fields.size() == 1){
			fields.get(0).add(b);
			return;
		}
		throw new CsvBangException(String.format("You can't add byte [%s] to a record.", b));
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#add(com.github.lecogiteur.csvbang.parser.GrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(final GrammarAction<?> word) throws CsvBangException {
		if (word != null && !CsvGrammarActionType.NOTHING_TO_DO.equals(word.getType())){
			if (CsvGrammarActionType.FIELD.equals(word.getType())){
				//add a field
				fields.add(word);
			}else if (CsvGrammarActionType.END.equals(word.getType())){
				isTerminatedRecord = true;
				return false;
			}else if (CsvGrammarActionType.RECORD.equals(word.getType())){
				return false;
			}else{
				throw new CsvBangException(String.format("You can't add a word of type [%s] to a record.", word.getType()));
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return isTerminatedRecord || conf.fields.size() == fields.size() || CsvGrammarActionType.RECORD.equals(next);
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.GrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	//TODO vérifier que les beans CSV ont un constructeur par défaut
	public T execute() throws CsvBangException {
		T bean = null;
		boolean isNull = true;
		if (CsvbangUti.isCollectionNotEmpty(fields)){
			if (fields.size() == 1 && fields.get(0).execute() == null){
				return null;
			}
			if (conf.fields.size() != fields.size()){
				throw new CsvBangException(String.format("The number of fields configurated [%s] is different than the number of field find in file [%s] for type [%s].", 
						conf.fields.size(), fields.size(), beanClass));
			}
			
			//create new CSV bean
			try {
				bean = beanClass.newInstance();
			} catch (InstantiationException e) {
				throw new CsvBangException(String.format("A problem has occurred when we instantiate the CSV type [%s].", beanClass));
			} catch (IllegalAccessException e) {
				throw new CsvBangException(String.format("A problem has occurred when we instantiate the CSV type [%s].", beanClass));
			}
			
			//set all field to CSV bean
			for (int i=0; i<conf.fields.size(); i++){
				final CsvFieldConfiguration confField = conf.fields.get(i);
				final GrammarAction<?> field = fields.get(i);
				final Object content = field.execute();
				isNull = isNull && content == null;
				ReflectionUti.setValue(confField.setter, confField.generator, bean, field.execute());
			}
		}
		return isNull?null:bean;
	}

}
