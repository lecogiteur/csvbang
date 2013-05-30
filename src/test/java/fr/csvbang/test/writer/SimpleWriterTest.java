package fr.csvbang.test.writer;

import java.util.Collection;

import fr.csvbang.configuration.CsvBangConfiguration;
import fr.csvbang.exception.CsvBangException;
import fr.csvbang.writer.AbstractWriter;

public class SimpleWriterTest<T> extends AbstractWriter<T> {

	StringBuilder result = new StringBuilder();
	
	public SimpleWriterTest(CsvBangConfiguration conf) {
		super(conf);
	}

	@Override
	public void write(Collection<T> lines) throws CsvBangException {
		for(T line:lines){
			result.append(writeLine(line));
		}
	}
	
	public String getResult(){
		return result.toString();
	}

	@Override
	public void close() throws CsvBangException {
	}

}
