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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
	 * True if CSV bean has field of Collection (or array) type
	 * @since 1.0.0
	 */
	private final boolean hasFieldCollection;

	/**
	 * Constructor
	 * @param beanClass type of CSV bean
	 * @param conf configuration of CSV bean
	 * @param totalNbFieldWhichCanBeDeleted total number of field which can be deleted
	 * @param numberOfFieldCollection True if CSV bean has field of Collection (or array) type
	 * @since 1.0.0
	 */
	public RecordGrammarAction(final Class<T> beanClass, final CsvBangConfiguration conf, 
			final int totalNbFieldWhichCanBeDeleted, final boolean hasFieldCollection) {
		super();
		this.beanClass = beanClass;
		this.conf = conf;
		this.fields = new ArrayList<FieldGrammarAction>();
		this.fields.add(new FieldGrammarAction(conf, 100));
		this.totalNbFieldWhichCanBeDeleted = totalNbFieldWhichCanBeDeleted;
		this.hasFieldCollection = hasFieldCollection;
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
		}else if (hasCollectionOrArray() && (conf.fields.get(conf.fields.size() - 1).parameterizedCollectionType != null)){
			boolean b = conf.fields.size() <= conf.fields.size();
			int idx = fields.size() - 1;
			int idxC = conf.fields.size() - 1;
			while (b){
				while(b){
					try{
						generateFieldElement(idxC, idx);
						b = false;
					}catch(Exception e){
						final Character a = fields.get(idx).deleteLastChar();
						if (a == null){
							b = conf.fields.get(idx).isDeleteFieldIfNull ||  conf.fields.size() <= conf.fields.size();
							if (b){
								fields.remove(idx);
								foot.addBefore(conf.delimiter);
								idx--;
							}
						}else{
							foot.addBefore(a);
						}
					}
				}
			}
			foot.setStartOffset(fields.get(idx).getStartOffset());
			fields.get(idx).setEndOffset(foot.getEndOffset());
			endOffset = foot.getEndOffset();
			
		}else if (fields.size() == conf.fields.size()){
			//we verify if the last field of last record can be set
			final T bean = newInstance();
			boolean b = true;
			int idx = fields.size() - 1;
			while (b){
				while(b){
					try{
						if ( conf.fields.get(idx).parameterizedCollectionType != null){
							generateFieldElement(idx, idx);
						}else{
							setField(idx, idx, bean);
						}
						b = false;
					}catch(Exception e){
						final Character a = fields.get(idx).deleteLastChar();
						if (a == null){
							b = conf.fields.get(idx).isDeleteFieldIfNull;
							if (b){
								fields.remove(idx);
								foot.addBefore(conf.delimiter);
								idx--;
							}
						}else{
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
		return (isTerminated || (conf.fields.size() == fields.size() && !hasDeletedField() && !hasCollectionOrArray()) || CsvGrammarActionType.RECORD.equals(next) 
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
	 * Verify if this record has fields of type collection or array
	 * @return True if this record has fields of type collection or array
	 * @since 1.0.0
	 */
	private boolean hasCollectionOrArray(){
		return hasFieldCollection && ((!hasDeletedField() && fields.size() >= conf.fields.size()) || hasDeletedField());
	}

	/**
	 * {@inheritDoc}
	 * @see com.github.lecogiteur.csvbang.parser.CsvGrammarAction#execute()
	 * @since 1.0.0
	 */
	@Override
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
			
			//True if some fields of this record have collection or array
			boolean hasCollection = hasCollectionOrArray();
			
			//true: try to set the bean
			boolean mustretry = true;
			
			if (fields.size() == 1 && fields.get(0).execute() == null){
				//CSV bean null. Don't return it
				return null;
			}
			
			if (conf.fields.size() != fields.size()){
				//the number of field retrieved in CSV file is different than the number field defined on configuration.
				//verify if there is deleted field
				if (!hasDeletedField && !hasCollection){
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
			//list of index. 0: index of field configuration and 1: index of field value
			final int[] index = new int[2];
			//init wrapper for collection or array field
			final CollectionWrapper wrapper = new CollectionWrapper();
			
			while (mustretry){
				//init
				mustretry = false;
				isNull = true;
				index[0] = 0; //index of field configuration
				index[1] = 0; //index of field value
				
				//set all field to CSV bean
				for (; index[0]<conf.fields.size() && index[1]<fields.size(); index[0]++,index[1]++){
					
					if (hasDeletedField && !mustSetField.contains(index[0]) 
							&& maxNbFieldDeleted >  fieldWhichCanBeDeleted.size() 
							&& conf.fields.get(index[0]).isDeleteFieldIfNull){
						//this field can be deleted
						fieldWhichCanBeDeleted.add(index[0]);
						index[1]--;
						continue;
					}
					

					//if the field is a collection or an array
					if (hasCollection){
						//manage collection field
						if (manageCollectionAndArray(index, wrapper, bean)){
							//the collection is completed so we set the field of CSV bean
							isNull = setCollectionOrArrayField(index[0], bean, wrapper.collection) && isNull;
							wrapper.collection = null;
							continue;
						}else if (wrapper.collection != null){
							//it's not the end. The collection is not completed. We must continue add element to it.
							continue;
						}
					}
					
					try{
						//try to set a simple field
						isNull = setField(index[0], index[1], bean) && isNull;
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
							throw new CsvBangException(String.format("A problem has occurred when we try to set CSV bean [%s] on field %s", 
									this.beanClass, index[0]), e);
						}
					}
				}
				
				if (!hasDeletedField && (index[0] < conf.fields.size() || index[1] < fields.size())){
					final StringBuilder s = new StringBuilder();
					for (final FieldGrammarAction field:fields){
						s.append(field.execute()).append(conf.delimiter);
					}
					throw new CsvBangException(String.format("A problem has occurred between offset [%s] and [%s] for a record. "
							+ "The number of fields configurated [%s] is different than the number of field find in file [%s] "
							+ "for type [%s]. The generation of the new instance failed. The record: %s", 
							startOffset, endOffset, conf.fields.size(), fields.size(), beanClass, s));
					
				}
			}
		}
		return isNull?null:bean;
	}
	
	/**
	 * Manage a collection or array field of CSV bean
	 * @param index the list of index (index of field configuration and index of field value)
	 * @param wrapper the collection wrapped
	 * @param bean the bean
	 * @return True if the collection is completed and must be setted to the CSV bean field.
	 * @throws CsvBangException if a problem has occurred when we try to add element to the collection
	 * @since 1.0.0
	 */
	private boolean manageCollectionAndArray(final int[] index, CollectionWrapper wrapper, final T bean) throws CsvBangException{
		final CsvFieldConfiguration confField = conf.fields.get(index[0]);
		if (confField.parameterizedCollectionType != null && conf.fields.size() - index[0] <= fields.size() - index[1]){
			if (wrapper.collection == null){
				//we create a new instance of collection or array
				wrapper.collection = ReflectionUti.newInstanceCollectionOrArray(confField.typeOfSetter);
			}
			try{
				//we add the value of field to the collection
				addToCollectionOrArray(index[0], index[1], wrapper.collection);
				if (!(fields.size() > index[1] + 1)){
					//last field of record
					return true;
				}else{
					//we don't change field. It's a collection
					index[0]--;
				}
			}catch(Exception e){
				//we can't add the field to the collection or array. So the collection or array is complete.
				manageLastElementOfCollection(index, wrapper);
				return true;
			}
		}else if (wrapper.collection != null){
			//the collection is completed, we set it
			manageLastElementOfCollection(index, wrapper);
			return true;
		}
		return false;
	}
	
	/**
	 * Manage the last element of the collection. 
	 * Verify if the field after the current is the same type that the parameterized type of collection. 
	 * Can remove in this case the last element of the collection in order to set the next field
	 * @param index the list of index (index of field configuration and index of field value)
	 * @param wrapper the collection wrapped
	 * @throws CsvBangException if a problem has occurred when we try to manage the last element
	 * @since 1.0.0
	 */
	private void manageLastElementOfCollection(final int[] index, final CollectionWrapper wrapper) throws CsvBangException{
		//get the configuration of field
		final CsvFieldConfiguration confField = conf.fields.get(index[0]);
		if (index[0] + 1 <conf.fields.size() && wrapper.collection.size() > 0 
				&& confField.parameterizedCollectionType.equals(conf.fields.get(index[0] + 1).typeOfSetter)){
			//the parameterized type of collection is the same that the last collection
			try{
				addToCollectionOrArray(index[0], index[1], wrapper.collection);
			}catch(Exception e){
				//the next field value can't be added to the collection. Perhaps this value is not the same type that the next field
				//we remove the last element of the collection (it's the previous field value)
				index[1]--;
			}
			//we remove the last element
			final Object lastElement = generateFieldElement(index[0], index[1]);
			wrapper.collection.remove(lastElement);
		}
		//the current field value has not be added, so it's for the next field of CSV bean
		index[1]--;
	}
	
	/**
	 * Add to a collection or an array, the value of a field.
	 * @param fieldConfIndex index of field configuration
	 * @param fieldIndex index of field value
	 * @param col the collection or array
	 * @return the collection
	 * @throws CsvBangException if a problem has occurred
	 * @since 1.0.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	private void addToCollectionOrArray(final int fieldConfIndex, final int fieldIndex, Collection col) throws CsvBangException{
		//generate content
		final Object content = generateFieldElement(fieldConfIndex, fieldIndex);
		
		if (content != null){
			col.add(content);
		}
	}
	
	/**
	 * Generate the element of a collection for a field
	 * @param fieldConfIndex index of configuration field
	 * @param fieldIndex index of field value
	 * @return the element for collection
	 * @throws CsvBangException if an errore as occurred when we generate the value
	 * @since 1.0.0
	 */
	private Object generateFieldElement(final int fieldConfIndex, final int fieldIndex) throws CsvBangException{
		//get the configuration of field
		final CsvFieldConfiguration confField = conf.fields.get(fieldConfIndex);

		//field from CSV file
		final FieldGrammarAction field = fields.get(fieldIndex);

		//generate content
		final Object content = confField.format.parse(field.execute(), confField.parameterizedCollectionType);

		if (content != null){
			if (confField.parameterizedCollectionType.equals(content.getClass()) && !(confField.generator instanceof FactoryObjectGenerator)){
				//the type of field content which is parsed by formatter has the matched type of collection
				return content;
			}

			//we must generate a new instance of this value in the correct type for collection
			if (confField.generator != null){
				return confField.generator.generate(content);
			}
		}
		return content;
	}
	
	/**
	 * Set the collection or array to CSV bean field
	 * @param fieldConfIndex index of field configuration
	 * @param bean CSV bean instance
	 * @param col the collection or array to set
	 * @return true if the field is null
	 * @throws CsvBangException
	 * @since 1.0.0
	 */
	private boolean setCollectionOrArrayField(final int fieldConfIndex, final T bean, final Collection<?> col) throws CsvBangException{
		//get the configuration of field
		final CsvFieldConfiguration confField = conf.fields.get(fieldConfIndex);
		
		if (col.isEmpty()){
			//empty collection so the field is null
			return true;
		}
		
		if (confField.typeOfSetter.isArray()){
			//field of type array
			final Object array = Array.newInstance(confField.parameterizedCollectionType, col.size());
			final Iterator<?> it = col.iterator();
			for (int i = 0; i<col.size(); i++){
				Array.set(array, i, it.next());
			}
			ReflectionUti.setValue(confField.setter, null, bean, array);
			return false;
		}
		
		//field is collection
		ReflectionUti.setValue(confField.setter, null, bean, col);
		return false;
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
		
		//we must generate a new instance of this value in the correct type for setter
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
	
	/**
	 * wrapper used in order to construct field of collection or array type.
	 * Used in order to not loose reference
	 * @author Tony EMMA
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class CollectionWrapper{
		
		/**
		 * The collection wrapped
		 * @since 1.0.0
		 */
		private Collection<?> collection;
	}
}
