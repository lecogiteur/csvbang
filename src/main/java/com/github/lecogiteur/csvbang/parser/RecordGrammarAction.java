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
import com.github.lecogiteur.csvbang.util.FactoryObjectGenerator;
import com.github.lecogiteur.csvbang.util.ReflectionUti;

/**
 * Generate a CSV record
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class RecordGrammarAction<T> extends AbstractGrammarAction<T> {
	
	/**
	 * List of fields
	 * @since 1.0.0
	 */
	private final List<FieldGrammarAction> fields;
	
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
	 * Total number of field which can be deleted if there are null
	 * @since 1.0.0
	 */
	private final int totalNbFieldWhichCanBeDeleted;

	/**
	 * Constructor
	 * @param beanClass type of CSV bean
	 * @param conf configuration of CSV bean
	 * @param totalNbFieldWhichCanBeDeleted total number of field which can be deleted
	 * @since 1.0.0
	 */
	public RecordGrammarAction(final Class<T> beanClass, final CsvBangConfiguration conf, final int totalNbFieldWhichCanBeDeleted) {
		super();
		this.beanClass = beanClass;
		this.conf = conf;
		this.fields = new ArrayList<FieldGrammarAction>();
		this.fields.add(new FieldGrammarAction(conf, 100));
		this.totalNbFieldWhichCanBeDeleted = totalNbFieldWhichCanBeDeleted;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#getType()
	 * @since 1.0.0
	 */
	@Override
	public CsvGrammarActionType getType() {
		return CsvGrammarActionType.RECORD;
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(byte)
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
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#add(com.github.lecogiteur.csvbang.parser.CsvGrammarAction)
	 * @since 1.0.0
	 */
	@Override
	public boolean add(final CsvGrammarAction<?> word) throws CsvBangException {
		if (word != null){
			switch (word.getType()) {
			case FIELD:
				//add a field
				fields.add((FieldGrammarAction)word);
				endOffset = word.getEndOffset();
				isTerminated = isTerminated || word.isLastAction();
				return true;
			case QUOTE:
				if (fields.size() == 1){
					fields.get(0).add(word);
					endOffset = word.getEndOffset();
					return true;
				}
				return false;
			case FOOTER:
				manageFooter((FooterGrammarAction) word);
				return false;
			case END:
				isTerminated = isTerminated || word.isLastAction();
				return true;
			default:
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Manage footer
	 * @param foot the footer
	 * @throws CsvBangException if a problem has occurred when parse footer
	 * @since 1.0.0
	 */
	private void manageFooter(final FooterGrammarAction foot) throws CsvBangException{
		if (fields.size() < conf.fields.size() && 
				(totalNbFieldWhichCanBeDeleted == 0 || conf.fields.size() - fields.size() > totalNbFieldWhichCanBeDeleted)){
			//the number of field of last record of file is invalid. So it's a part of footer.
			final StringBuilder s = new StringBuilder();
			for (final FieldGrammarAction field:fields){
				final String c = field.execute();
				if (c!=null){
					s.append(conf.delimiter).append(c);
				}
			}
			s.delete(0, conf.delimiter.length());
			foot.addBefore(s);
			foot.setStartOffset(fields.get(0).getStartOffset());
			endOffset = foot.getEndOffset();
			fields.clear();
		}else if (hasDeletedField()){
			//manage case where there is deleted field
			try{
				execute();
			}catch(Exception e){
				for (int i=fields.size()-1;i >=0; i--){
					final FieldGrammarAction field = fields.get(i);
					final StringBuilder s = new StringBuilder();
					s.append(i==0?"":conf.delimiter).append(field.execute());
					foot.addBefore(s);
					foot.setStartOffset(field.getStartOffset());
					field.setEndOffset(foot.getEndOffset());
					endOffset = foot.getEndOffset();
				}
				fields.clear();
			}
		}else if (fields.size() == conf.fields.size()){
			//we verify if the last field of last record can be set
			final T bean = newInstance();
			boolean b = true;
			int idx = fields.size() - 1;
			int size = 0;
			while (b){
				while(b){
					try{
						setField(idx, idx, bean);
						b = false;
					}catch(Exception e){
						final Character a = fields.get(idx).deleteLastChar();
						if (a == null){
							b = conf.fields.get(idx).isDeleteFieldIfNull;
							if (b){
								fields.remove(idx);
								idx--;
							}
						}else{
							size++;
							foot.addBefore(a);
						}
					}
				}
			}
			foot.setStartOffset(fields.get(idx).getStartOffset());
			fields.get(idx).setEndOffset(foot.getEndOffset());
			endOffset = foot.getEndOffset();
		}
		foot.terminateSetFooterContent();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isActionCompleted(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType)
	 * @since 1.0.0
	 */
	@Override
	public boolean isActionCompleted(final CsvGrammarActionType next) {
		return (isTerminated || conf.fields.size() == fields.size() || CsvGrammarActionType.RECORD.equals(next) 
				|| CsvGrammarActionType.END.equals(next) || CsvGrammarActionType.COMMENT.equals(next)) 
				&& !CsvGrammarActionType.FOOTER.equals(next);
	}
	
	/**
	 * Verify if this record has deleted field
	 * @return true if this field has deleted field
	 * @since 1.0.0
	 */
	private boolean hasDeletedField(){
		return totalNbFieldWhichCanBeDeleted > 0 && conf.fields.size() > fields.size() 
				&& conf.fields.size() - totalNbFieldWhichCanBeDeleted <= fields.size();
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
	//TODO vérifier que les beans CSV ont un constructeur par défaut
	//TODO créer un paramètre pour désactiver la lecture des commentaires
	public T execute() throws CsvBangException {
		T bean = null;
		boolean isNull = true;
		if (CsvbangUti.isCollectionNotEmpty(fields)){
			//init
			final List<Integer> fieldWhichCanBeDeleted = new ArrayList<Integer>();
			final List<Integer> mustSetField = new ArrayList<Integer>();
			
			//effective max number of field deleted on this record
			int maxNbFieldDeleted = 0;
			
			
			//true if this record contains some fields are deleted 
			boolean hasDeletedField = hasDeletedField();
			
			//true: try to set the bean
			boolean mustretry = true;
			
			if (fields.size() == 1 && fields.get(0).execute() == null){
				//CSV bean null. Don't return it
				return null;
			}
			
			if (conf.fields.size() != fields.size()){
				//the number of field retrieved in CSV file is different than the number field defined on configuration.
				//verify if there is deleted field
				if (!hasDeletedField){
					throw new CsvBangException(String.format("A problem has occurred between offset [%s] and [%s] for a record. "
							+ "The number of fields configurated [%s] is different than the number of field find in file [%s] "
							+ "for type [%s].", 
							startOffset, endOffset, conf.fields.size(), fields.size(), beanClass));
				}
				
				//effective max number of field deleted on this record
				maxNbFieldDeleted = conf.fields.size() - fields.size();
			}
			
			//create new CSV bean
			bean = newInstance();
			
			while (mustretry){
				//init
				mustretry = false;
				isNull = true;
				
				//set all field to CSV bean
				for (int indexConf=0,indexField=0; indexConf<conf.fields.size() && indexField<fields.size(); indexConf++,indexField++){
					
					if (hasDeletedField && !mustSetField.contains(indexConf) 
							&& maxNbFieldDeleted >  fieldWhichCanBeDeleted.size() 
							&& conf.fields.get(indexConf).isDeleteFieldIfNull){
						//this field can be deleted
						fieldWhichCanBeDeleted.add(indexConf);
						indexField--;
						continue;
					}

					try{
						//try to set a field
						isNull = setField(indexConf, indexField, bean) && isNull;
					}catch(Exception e){
						if (hasDeletedField && fieldWhichCanBeDeleted.size() > 0 
								&& maxNbFieldDeleted < totalNbFieldWhichCanBeDeleted){
							//we can't set this record in CSV bean. Some field are deleted
							//try again
							mustretry = true;
							bean = newInstance();
							maxNbFieldDeleted++;
							mustSetField.add(fieldWhichCanBeDeleted.get(0));
							fieldWhichCanBeDeleted.clear();
							break;
						}else{
							throw new CsvBangException(String.format("A problem has occurred when we try to set CSV bean [%s] on field %s", this.beanClass, indexConf), e);
						}
					}
				}
			}
		}
		return isNull?null:bean;
	}

	
	/**
	 * Set a field to the CSV bean
	 * @param fieldConfIndex field configuration index
	 * @param fieldIndex field index
	 * @param bean the CSV bean
	 * @return true if the field is null
	 * @throws CsvBangException if a problem has occurred when we set the field to the CSV bean
	 * @since 1.0.0
	 */
	private boolean setField(final int fieldConfIndex, final int fieldIndex, final T bean) throws CsvBangException{
		//get the configuration of field
		final CsvFieldConfiguration confField = conf.fields.get(fieldConfIndex);
		
		//field from CSV file
		final FieldGrammarAction field = fields.get(fieldIndex);
		
		//parse the field value with the CsvFormatter
		final Object content = confField.format.parse(field.execute(), confField.typeOfSetter);
		if (content == null){
			return true;
		}
		if (confField.typeOfSetter.equals(content.getClass()) && !(confField.generator instanceof FactoryObjectGenerator)){
			//the type of field content which is parsed by formatter has the matched type of setter
			ReflectionUti.setValue(confField.setter, null, bean, content);
			return false;
		}
		
		//we must generate a new instance of this value in the good type
		return ReflectionUti.setValue(confField.setter, confField.generator, bean, content) == null;
	}
	
	/**
	 * Return a new instance of CSV bean
	 * @return the CSV bean
	 * @throws CsvBangException if a problem has occurred when we create the new instance.
	 * @since 1.0.0
	 */
	private T newInstance() throws CsvBangException{
		try {
			return beanClass.newInstance();
		} catch (InstantiationException e) {
			throw new CsvBangException(String.format("A problem has occurred when we instantiate the CSV type [%s]. Record between offset [%s] and [%s].", startOffset, endOffset, beanClass));
		} catch (IllegalAccessException e) {
			throw new CsvBangException(String.format("A problem has occurred when we instantiate the CSV type [%s]. Record between offset [%s] and [%s].", startOffset, endOffset, beanClass));
		}
	}


	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#isChuck(com.github.lecogiteur.csvbang.parser.CsvGrammarActionType, byte[])
	 * @since 1.0.0
	 */
	@Override
	public boolean isChuck(final CsvGrammarActionType next, final byte[] keyword) {
		return false;
	}
}
