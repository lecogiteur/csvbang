/**
 *  fr.csvbang.test.formatter.BooleanCsvFormatter
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
package fr.csvbang.test.formatter;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import junit.framework.Assert;
import fr.csvbang.formatter.CsvFormatter;
import fr.csvbang.formatter.BooleanCsvFormatter;

/**
 * @author Tony EMMA
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BooleanCsvFormatterTest {
	
	@Test
	public void booleanFormatNoPatternTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("true", format.format(true, "default"));
		Assert.assertEquals("false", format.format(false, "default"));
		Assert.assertEquals("false", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("false", format.format(0, "default"));
		Assert.assertEquals("true", format.format(1, "default"));
		Assert.assertEquals("false", format.format("Off", "default"));
		Assert.assertEquals("true", format.format("ON", "default"));
		Assert.assertEquals("false", format.format("F", "default"));
		Assert.assertEquals("true", format.format("T", "default"));
		Assert.assertEquals("false", format.format("off", "default"));
		Assert.assertEquals("true", format.format("on", "default"));
		Assert.assertEquals("false", format.format("f", "default"));
		Assert.assertEquals("true", format.format("t", "default"));
		Assert.assertEquals("true", format.format("y", "default"));
		Assert.assertEquals("false", format.format("n", "default"));
		Assert.assertEquals("true", format.format("Y", "default"));
		Assert.assertEquals("false", format.format("N", "default"));
		Assert.assertEquals("true", format.format("o", "default"));
		Assert.assertEquals("false", format.format("n", "default"));
		Assert.assertEquals("true", format.format("yes", "default"));
		Assert.assertEquals("false", format.format("NO", "default"));
		Assert.assertEquals("true", format.format("Oui", "default"));
		Assert.assertEquals("false", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternBooleanTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("boolean");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("true", format.format(true, "default"));
		Assert.assertEquals("false", format.format(false, "default"));
		Assert.assertEquals("false", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("false", format.format(0, "default"));
		Assert.assertEquals("true", format.format(1, "default"));
		Assert.assertEquals("false", format.format("Off", "default"));
		Assert.assertEquals("true", format.format("ON", "default"));
		Assert.assertEquals("false", format.format("F", "default"));
		Assert.assertEquals("true", format.format("T", "default"));
		Assert.assertEquals("false", format.format("off", "default"));
		Assert.assertEquals("true", format.format("on", "default"));
		Assert.assertEquals("false", format.format("f", "default"));
		Assert.assertEquals("true", format.format("t", "default"));
		Assert.assertEquals("true", format.format("y", "default"));
		Assert.assertEquals("false", format.format("n", "default"));
		Assert.assertEquals("true", format.format("Y", "default"));
		Assert.assertEquals("false", format.format("N", "default"));
		Assert.assertEquals("true", format.format("o", "default"));
		Assert.assertEquals("false", format.format("n", "default"));
		Assert.assertEquals("true", format.format("yes", "default"));
		Assert.assertEquals("false", format.format("NO", "default"));
		Assert.assertEquals("true", format.format("Oui", "default"));
		Assert.assertEquals("false", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("Boolean");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("True", format.format(true, "default"));
		Assert.assertEquals("False", format.format(false, "default"));
		Assert.assertEquals("False", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("False", format.format(0, "default"));
		Assert.assertEquals("True", format.format(1, "default"));
		Assert.assertEquals("False", format.format("Off", "default"));
		Assert.assertEquals("True", format.format("ON", "default"));
		Assert.assertEquals("False", format.format("F", "default"));
		Assert.assertEquals("True", format.format("T", "default"));
		Assert.assertEquals("False", format.format("off", "default"));
		Assert.assertEquals("True", format.format("on", "default"));
		Assert.assertEquals("False", format.format("f", "default"));
		Assert.assertEquals("True", format.format("t", "default"));
		Assert.assertEquals("True", format.format("y", "default"));
		Assert.assertEquals("False", format.format("n", "default"));
		Assert.assertEquals("True", format.format("Y", "default"));
		Assert.assertEquals("False", format.format("N", "default"));
		Assert.assertEquals("True", format.format("o", "default"));
		Assert.assertEquals("False", format.format("n", "default"));
		Assert.assertEquals("True", format.format("yes", "default"));
		Assert.assertEquals("False", format.format("NO", "default"));
		Assert.assertEquals("True", format.format("Oui", "default"));
		Assert.assertEquals("False", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("BOOLEAN");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("TRUE", format.format(true, "default"));
		Assert.assertEquals("FALSE", format.format(false, "default"));
		Assert.assertEquals("FALSE", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("FALSE", format.format(0, "default"));
		Assert.assertEquals("TRUE", format.format(1, "default"));
		Assert.assertEquals("FALSE", format.format("Off", "default"));
		Assert.assertEquals("TRUE", format.format("ON", "default"));
		Assert.assertEquals("FALSE", format.format("F", "default"));
		Assert.assertEquals("TRUE", format.format("T", "default"));
		Assert.assertEquals("FALSE", format.format("off", "default"));
		Assert.assertEquals("TRUE", format.format("on", "default"));
		Assert.assertEquals("FALSE", format.format("f", "default"));
		Assert.assertEquals("TRUE", format.format("t", "default"));
		Assert.assertEquals("TRUE", format.format("y", "default"));
		Assert.assertEquals("FALSE", format.format("n", "default"));
		Assert.assertEquals("TRUE", format.format("Y", "default"));
		Assert.assertEquals("FALSE", format.format("N", "default"));
		Assert.assertEquals("TRUE", format.format("o", "default"));
		Assert.assertEquals("FALSE", format.format("n", "default"));
		Assert.assertEquals("TRUE", format.format("yes", "default"));
		Assert.assertEquals("FALSE", format.format("NO", "default"));
		Assert.assertEquals("TRUE", format.format("Oui", "default"));
		Assert.assertEquals("FALSE", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternBTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("b");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("t", format.format(true, "default"));
		Assert.assertEquals("f", format.format(false, "default"));
		Assert.assertEquals("f", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("f", format.format(0, "default"));
		Assert.assertEquals("t", format.format(1, "default"));
		Assert.assertEquals("f", format.format("Off", "default"));
		Assert.assertEquals("t", format.format("ON", "default"));
		Assert.assertEquals("f", format.format("F", "default"));
		Assert.assertEquals("t", format.format("T", "default"));
		Assert.assertEquals("f", format.format("off", "default"));
		Assert.assertEquals("t", format.format("on", "default"));
		Assert.assertEquals("f", format.format("f", "default"));
		Assert.assertEquals("t", format.format("t", "default"));
		Assert.assertEquals("t", format.format("y", "default"));
		Assert.assertEquals("f", format.format("n", "default"));
		Assert.assertEquals("t", format.format("Y", "default"));
		Assert.assertEquals("f", format.format("N", "default"));
		Assert.assertEquals("t", format.format("o", "default"));
		Assert.assertEquals("f", format.format("n", "default"));
		Assert.assertEquals("t", format.format("yes", "default"));
		Assert.assertEquals("f", format.format("NO", "default"));
		Assert.assertEquals("t", format.format("Oui", "default"));
		Assert.assertEquals("f", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("B");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("T", format.format(true, "default"));
		Assert.assertEquals("F", format.format(false, "default"));
		Assert.assertEquals("F", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("F", format.format(0, "default"));
		Assert.assertEquals("T", format.format(1, "default"));
		Assert.assertEquals("F", format.format("Off", "default"));
		Assert.assertEquals("T", format.format("ON", "default"));
		Assert.assertEquals("F", format.format("F", "default"));
		Assert.assertEquals("T", format.format("T", "default"));
		Assert.assertEquals("F", format.format("off", "default"));
		Assert.assertEquals("T", format.format("on", "default"));
		Assert.assertEquals("F", format.format("f", "default"));
		Assert.assertEquals("T", format.format("t", "default"));
		Assert.assertEquals("T", format.format("y", "default"));
		Assert.assertEquals("F", format.format("n", "default"));
		Assert.assertEquals("T", format.format("Y", "default"));
		Assert.assertEquals("F", format.format("N", "default"));
		Assert.assertEquals("T", format.format("o", "default"));
		Assert.assertEquals("F", format.format("n", "default"));
		Assert.assertEquals("T", format.format("yes", "default"));
		Assert.assertEquals("F", format.format("NO", "default"));
		Assert.assertEquals("T", format.format("Oui", "default"));
		Assert.assertEquals("F", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternIntegerTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("integer");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("1", format.format(true, "default"));
		Assert.assertEquals("0", format.format(false, "default"));
		Assert.assertEquals("0", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("0", format.format(0, "default"));
		Assert.assertEquals("1", format.format(1, "default"));
		Assert.assertEquals("0", format.format("Off", "default"));
		Assert.assertEquals("1", format.format("ON", "default"));
		Assert.assertEquals("0", format.format("F", "default"));
		Assert.assertEquals("1", format.format("T", "default"));
		Assert.assertEquals("0", format.format("off", "default"));
		Assert.assertEquals("1", format.format("on", "default"));
		Assert.assertEquals("0", format.format("f", "default"));
		Assert.assertEquals("1", format.format("t", "default"));
		Assert.assertEquals("1", format.format("y", "default"));
		Assert.assertEquals("0", format.format("n", "default"));
		Assert.assertEquals("1", format.format("Y", "default"));
		Assert.assertEquals("0", format.format("N", "default"));
		Assert.assertEquals("1", format.format("o", "default"));
		Assert.assertEquals("0", format.format("n", "default"));
		Assert.assertEquals("1", format.format("yes", "default"));
		Assert.assertEquals("0", format.format("NO", "default"));
		Assert.assertEquals("1", format.format("Oui", "default"));
		Assert.assertEquals("0", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternLetterONTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("o/n");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("o", format.format(true, "default"));
		Assert.assertEquals("n", format.format(false, "default"));
		Assert.assertEquals("n", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("n", format.format(0, "default"));
		Assert.assertEquals("o", format.format(1, "default"));
		Assert.assertEquals("n", format.format("Off", "default"));
		Assert.assertEquals("o", format.format("ON", "default"));
		Assert.assertEquals("n", format.format("F", "default"));
		Assert.assertEquals("o", format.format("T", "default"));
		Assert.assertEquals("n", format.format("off", "default"));
		Assert.assertEquals("o", format.format("on", "default"));
		Assert.assertEquals("n", format.format("f", "default"));
		Assert.assertEquals("o", format.format("t", "default"));
		Assert.assertEquals("o", format.format("y", "default"));
		Assert.assertEquals("n", format.format("n", "default"));
		Assert.assertEquals("o", format.format("Y", "default"));
		Assert.assertEquals("n", format.format("N", "default"));
		Assert.assertEquals("o", format.format("o", "default"));
		Assert.assertEquals("n", format.format("n", "default"));
		Assert.assertEquals("o", format.format("yes", "default"));
		Assert.assertEquals("n", format.format("NO", "default"));
		Assert.assertEquals("o", format.format("Oui", "default"));
		Assert.assertEquals("n", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("O/N");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("O", format.format(true, "default"));
		Assert.assertEquals("N", format.format(false, "default"));
		Assert.assertEquals("N", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("N", format.format(0, "default"));
		Assert.assertEquals("O", format.format(1, "default"));
		Assert.assertEquals("N", format.format("Off", "default"));
		Assert.assertEquals("O", format.format("ON", "default"));
		Assert.assertEquals("N", format.format("F", "default"));
		Assert.assertEquals("O", format.format("T", "default"));
		Assert.assertEquals("N", format.format("off", "default"));
		Assert.assertEquals("O", format.format("on", "default"));
		Assert.assertEquals("N", format.format("f", "default"));
		Assert.assertEquals("O", format.format("t", "default"));
		Assert.assertEquals("O", format.format("y", "default"));
		Assert.assertEquals("N", format.format("n", "default"));
		Assert.assertEquals("O", format.format("Y", "default"));
		Assert.assertEquals("N", format.format("N", "default"));
		Assert.assertEquals("O", format.format("o", "default"));
		Assert.assertEquals("N", format.format("n", "default"));
		Assert.assertEquals("O", format.format("yes", "default"));
		Assert.assertEquals("N", format.format("NO", "default"));
		Assert.assertEquals("O", format.format("Oui", "default"));
		Assert.assertEquals("N", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternLetterYNTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("y/n");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("y", format.format(true, "default"));
		Assert.assertEquals("n", format.format(false, "default"));
		Assert.assertEquals("n", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("n", format.format(0, "default"));
		Assert.assertEquals("y", format.format(1, "default"));
		Assert.assertEquals("n", format.format("Off", "default"));
		Assert.assertEquals("y", format.format("ON", "default"));
		Assert.assertEquals("n", format.format("F", "default"));
		Assert.assertEquals("y", format.format("T", "default"));
		Assert.assertEquals("n", format.format("off", "default"));
		Assert.assertEquals("y", format.format("on", "default"));
		Assert.assertEquals("n", format.format("f", "default"));
		Assert.assertEquals("y", format.format("t", "default"));
		Assert.assertEquals("y", format.format("y", "default"));
		Assert.assertEquals("n", format.format("n", "default"));
		Assert.assertEquals("y", format.format("Y", "default"));
		Assert.assertEquals("n", format.format("N", "default"));
		Assert.assertEquals("y", format.format("o", "default"));
		Assert.assertEquals("n", format.format("n", "default"));
		Assert.assertEquals("y", format.format("yes", "default"));
		Assert.assertEquals("n", format.format("NO", "default"));
		Assert.assertEquals("y", format.format("Oui", "default"));
		Assert.assertEquals("n", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("Y/N");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("Y", format.format(true, "default"));
		Assert.assertEquals("N", format.format(false, "default"));
		Assert.assertEquals("N", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("N", format.format(0, "default"));
		Assert.assertEquals("Y", format.format(1, "default"));
		Assert.assertEquals("N", format.format("Off", "default"));
		Assert.assertEquals("Y", format.format("ON", "default"));
		Assert.assertEquals("N", format.format("F", "default"));
		Assert.assertEquals("Y", format.format("T", "default"));
		Assert.assertEquals("N", format.format("off", "default"));
		Assert.assertEquals("Y", format.format("on", "default"));
		Assert.assertEquals("N", format.format("f", "default"));
		Assert.assertEquals("Y", format.format("t", "default"));
		Assert.assertEquals("Y", format.format("y", "default"));
		Assert.assertEquals("N", format.format("n", "default"));
		Assert.assertEquals("Y", format.format("Y", "default"));
		Assert.assertEquals("N", format.format("N", "default"));
		Assert.assertEquals("Y", format.format("o", "default"));
		Assert.assertEquals("N", format.format("n", "default"));
		Assert.assertEquals("Y", format.format("yes", "default"));
		Assert.assertEquals("N", format.format("NO", "default"));
		Assert.assertEquals("Y", format.format("Oui", "default"));
		Assert.assertEquals("N", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternOnOffTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("on/off");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("on", format.format(true, "default"));
		Assert.assertEquals("off", format.format(false, "default"));
		Assert.assertEquals("off", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("off", format.format(0, "default"));
		Assert.assertEquals("on", format.format(1, "default"));
		Assert.assertEquals("off", format.format("Off", "default"));
		Assert.assertEquals("on", format.format("ON", "default"));
		Assert.assertEquals("off", format.format("F", "default"));
		Assert.assertEquals("on", format.format("T", "default"));
		Assert.assertEquals("off", format.format("off", "default"));
		Assert.assertEquals("on", format.format("on", "default"));
		Assert.assertEquals("off", format.format("f", "default"));
		Assert.assertEquals("on", format.format("t", "default"));
		Assert.assertEquals("on", format.format("y", "default"));
		Assert.assertEquals("off", format.format("n", "default"));
		Assert.assertEquals("on", format.format("Y", "default"));
		Assert.assertEquals("off", format.format("N", "default"));
		Assert.assertEquals("on", format.format("o", "default"));
		Assert.assertEquals("off", format.format("n", "default"));
		Assert.assertEquals("on", format.format("yes", "default"));
		Assert.assertEquals("off", format.format("NO", "default"));
		Assert.assertEquals("on", format.format("Oui", "default"));
		Assert.assertEquals("off", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("On/Off");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("On", format.format(true, "default"));
		Assert.assertEquals("Off", format.format(false, "default"));
		Assert.assertEquals("Off", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("Off", format.format(0, "default"));
		Assert.assertEquals("On", format.format(1, "default"));
		Assert.assertEquals("Off", format.format("Off", "default"));
		Assert.assertEquals("On", format.format("ON", "default"));
		Assert.assertEquals("Off", format.format("F", "default"));
		Assert.assertEquals("On", format.format("T", "default"));
		Assert.assertEquals("Off", format.format("off", "default"));
		Assert.assertEquals("On", format.format("on", "default"));
		Assert.assertEquals("Off", format.format("f", "default"));
		Assert.assertEquals("On", format.format("t", "default"));
		Assert.assertEquals("On", format.format("y", "default"));
		Assert.assertEquals("Off", format.format("n", "default"));
		Assert.assertEquals("On", format.format("Y", "default"));
		Assert.assertEquals("Off", format.format("N", "default"));
		Assert.assertEquals("On", format.format("o", "default"));
		Assert.assertEquals("Off", format.format("n", "default"));
		Assert.assertEquals("On", format.format("yes", "default"));
		Assert.assertEquals("Off", format.format("NO", "default"));
		Assert.assertEquals("On", format.format("Oui", "default"));
		Assert.assertEquals("Off", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("ON/OFF");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("ON", format.format(true, "default"));
		Assert.assertEquals("OFF", format.format(false, "default"));
		Assert.assertEquals("OFF", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("OFF", format.format(0, "default"));
		Assert.assertEquals("ON", format.format(1, "default"));
		Assert.assertEquals("OFF", format.format("Off", "default"));
		Assert.assertEquals("ON", format.format("ON", "default"));
		Assert.assertEquals("OFF", format.format("F", "default"));
		Assert.assertEquals("ON", format.format("T", "default"));
		Assert.assertEquals("OFF", format.format("off", "default"));
		Assert.assertEquals("ON", format.format("on", "default"));
		Assert.assertEquals("OFF", format.format("f", "default"));
		Assert.assertEquals("ON", format.format("t", "default"));
		Assert.assertEquals("ON", format.format("y", "default"));
		Assert.assertEquals("OFF", format.format("n", "default"));
		Assert.assertEquals("ON", format.format("Y", "default"));
		Assert.assertEquals("OFF", format.format("N", "default"));
		Assert.assertEquals("ON", format.format("o", "default"));
		Assert.assertEquals("OFF", format.format("n", "default"));
		Assert.assertEquals("ON", format.format("yes", "default"));
		Assert.assertEquals("OFF", format.format("NO", "default"));
		Assert.assertEquals("ON", format.format("Oui", "default"));
		Assert.assertEquals("OFF", format.format("non", "default"));
	}
	
	@Test
	public void booleanFormatPatternLitteralTest(){
		CsvFormatter format = new BooleanCsvFormatter();
		format.setPattern("litteral");
		format.setLocal(Locale.ENGLISH);
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("yes", format.format(true, "default"));
		Assert.assertEquals("no", format.format(false, "default"));
		Assert.assertEquals("no", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("no", format.format(0, "default"));
		Assert.assertEquals("yes", format.format(1, "default"));
		Assert.assertEquals("no", format.format("Off", "default"));
		Assert.assertEquals("yes", format.format("ON", "default"));
		Assert.assertEquals("no", format.format("F", "default"));
		Assert.assertEquals("yes", format.format("T", "default"));
		Assert.assertEquals("no", format.format("off", "default"));
		Assert.assertEquals("yes", format.format("on", "default"));
		Assert.assertEquals("no", format.format("f", "default"));
		Assert.assertEquals("yes", format.format("t", "default"));
		Assert.assertEquals("yes", format.format("y", "default"));
		Assert.assertEquals("no", format.format("n", "default"));
		Assert.assertEquals("yes", format.format("Y", "default"));
		Assert.assertEquals("no", format.format("N", "default"));
		Assert.assertEquals("yes", format.format("o", "default"));
		Assert.assertEquals("no", format.format("n", "default"));
		Assert.assertEquals("yes", format.format("yes", "default"));
		Assert.assertEquals("no", format.format("NO", "default"));
		Assert.assertEquals("yes", format.format("Oui", "default"));
		Assert.assertEquals("no", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("Litteral");
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("Yes", format.format(true, "default"));
		Assert.assertEquals("No", format.format(false, "default"));
		Assert.assertEquals("No", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("No", format.format(0, "default"));
		Assert.assertEquals("Yes", format.format(1, "default"));
		Assert.assertEquals("No", format.format("Off", "default"));
		Assert.assertEquals("Yes", format.format("ON", "default"));
		Assert.assertEquals("No", format.format("F", "default"));
		Assert.assertEquals("Yes", format.format("T", "default"));
		Assert.assertEquals("No", format.format("off", "default"));
		Assert.assertEquals("Yes", format.format("on", "default"));
		Assert.assertEquals("No", format.format("f", "default"));
		Assert.assertEquals("Yes", format.format("t", "default"));
		Assert.assertEquals("Yes", format.format("y", "default"));
		Assert.assertEquals("No", format.format("n", "default"));
		Assert.assertEquals("Yes", format.format("Y", "default"));
		Assert.assertEquals("No", format.format("N", "default"));
		Assert.assertEquals("Yes", format.format("o", "default"));
		Assert.assertEquals("No", format.format("n", "default"));
		Assert.assertEquals("Yes", format.format("yes", "default"));
		Assert.assertEquals("No", format.format("NO", "default"));
		Assert.assertEquals("Yes", format.format("Oui", "default"));
		Assert.assertEquals("No", format.format("non", "default"));

		format = new BooleanCsvFormatter();
		format.setPattern("LITTERAL");
		format.setLocal(Locale.ENGLISH);
		format.init();
		Assert.assertEquals("default", format.format(null, "default"));
		Assert.assertEquals("unkowningBoolean", format.format("string", "default"));
		Assert.assertEquals("YES", format.format(true, "default"));
		Assert.assertEquals("NO", format.format(false, "default"));
		Assert.assertEquals("NO", format.format(Boolean.FALSE, "default"));
		Assert.assertEquals("NO", format.format(0, "default"));
		Assert.assertEquals("YES", format.format(1, "default"));
		Assert.assertEquals("NO", format.format("Off", "default"));
		Assert.assertEquals("YES", format.format("ON", "default"));
		Assert.assertEquals("NO", format.format("F", "default"));
		Assert.assertEquals("YES", format.format("T", "default"));
		Assert.assertEquals("NO", format.format("off", "default"));
		Assert.assertEquals("YES", format.format("on", "default"));
		Assert.assertEquals("NO", format.format("f", "default"));
		Assert.assertEquals("YES", format.format("t", "default"));
		Assert.assertEquals("YES", format.format("y", "default"));
		Assert.assertEquals("NO", format.format("n", "default"));
		Assert.assertEquals("YES", format.format("Y", "default"));
		Assert.assertEquals("NO", format.format("N", "default"));
		Assert.assertEquals("YES", format.format("o", "default"));
		Assert.assertEquals("NO", format.format("n", "default"));
		Assert.assertEquals("YES", format.format("yes", "default"));
		Assert.assertEquals("NO", format.format("NO", "default"));
		Assert.assertEquals("YES", format.format("Oui", "default"));
		Assert.assertEquals("NO", format.format("non", "default"));
	}
}
