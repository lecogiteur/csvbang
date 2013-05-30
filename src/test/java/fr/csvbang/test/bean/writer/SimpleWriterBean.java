package fr.csvbang.test.bean.writer;

import java.util.Calendar;

import fr.csvbang.annotation.CsvField;
import fr.csvbang.annotation.CsvFormat;
import fr.csvbang.annotation.CsvType;
import fr.csvbang.annotation.CsvFormat.TYPE_FORMAT;

@CsvType
public class SimpleWriterBean {
	
	public static final String DATE_PATTERN = "dd/MM/yyyy";
	
	@CsvField(position=1, deleteIfNull=true)
	private Integer id;
	
	private String name;
	
	
	@CsvField(position=4, defaultIfNull="no date")
	@CsvFormat(type=TYPE_FORMAT.DATE, pattern=DATE_PATTERN)
	private Calendar birthday;
	
	@CsvField(position=5)
	private Double price;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@CsvField(position=2)
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the birthday
	 */
	public Calendar getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	
	@CsvField(position=3)
	public String getPublicName(){
		return "public Name: " + name;
	}

}
