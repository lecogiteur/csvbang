/**
 *  com.github.lecogiteur.csvbang.util.EndLineType
 * 
 *  Copyright (C) 2013-2014  Tony EMMA
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

import java.nio.charset.Charset;

/**
 * Enumeration of end line type. Before to use a end line type verify if charset contains this character.
 * @author Tony EMMA
 * @version 0.1.0
 * @since 0.1.0
 */
public enum EndLineType {

	/**
	 * The classic carriage return character (CR). Unicode value: 0x000D
	 * @since 0.1.0
	 */
	CARRIAGE_RETURN(new char[]{0x000D}),
	
	/**
	 * The classic line feed character (LF). Unicode value: 0x000A
	 * @since 0.1.0
	 */
	LINE_FEED(new char[]{0x000A}),
	
	/**
	 * A carriage return following by line feed character (CRLF)
	 * @since 0.1.0
	 */
	CARRIAGE_RETURN_LINE_FEED(new char[]{0x000D, 0x000A}),
	
	/**
	 * A line feed following by carriage return character (LFCR)
	 * @since 0.1.0
	 */
	LINE_FEED_CARRIAGE_RETURN(new char[]{0x000A, 0x000D}),
	
	/**
	 * The next line character (NEL). Unicode value: 0x0085
	 * @since 0.1.0
	 */
	NEXT_LINE(new char[]{0x0085}),
	
	/**
	 * The vertical tab character (VT). Unicode value: 0x000B
	 * @since 0.1.0
	 */
	VERTICAL_TAB(new char[]{0x000B}),
	
	/**
	 * The form feed character (FF). Unicode value: 0x000C
	 * @since 0.1.0
	 */
	FORM_FEED(new char[]{0x000C}),
	
	/**
	 * The line separator character (LS). Unicode value: 0x2028
	 * @since 0.1.0
	 */
	LINE_SEPARATOR(new char[]{0x2028}),
	
	/**
	 * The paragraph separator character (PS). Unicode value: 0x2029
	 * @since 0.1.0
	 */
	PARAGRAPH_SEPARATOR(new char[]{0x2029});
	
	/**
	 * Sequence of characters corresponding to the end line
	 * @since 0.1.0
	 */
	private final char[] characters;
	
	/**
	 * String format of end line characters
	 * @since 0.1.0
	 */
	private final String charToString;

	/**
	 * Constructor
	 * @param characters Sequence of characters corresponding to the end line
	 * @since 0.1.0
	 */
	private EndLineType(char[] characters) {
		this.characters = characters;
		charToString = new String(characters);
	}

	/**
	 * Get the {@link #characters}
	 * @return the {@link #characters}
	 * @since 0.1.0
	 */
	public char[] getCharacters() {
		return characters;
	}
	
	/**
	 * {@inheritDoc}
	 * Get the {@link #charToString}. It represent the end line characters.
	 * @see java.lang.Enum#toString()
	 * @since 0.1.0
	 */
	@Override
	public String toString() {
		return charToString;
	}

	/**
	 * Convert character to byte array
	 * @param charset the charset
	 * @return the byte array
	 * @since 0.1.0
	 */
	public byte[] toBytes(final Charset charset){
		final String s = new String(characters);
		return s.getBytes(charset);
	}

	
	
	
}
