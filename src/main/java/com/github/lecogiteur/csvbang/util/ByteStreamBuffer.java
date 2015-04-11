/**
 *  com.github.lecogiteur.csvbang.util.ByteStreamBuffer
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
package com.github.lecogiteur.csvbang.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

import com.github.lecogiteur.csvbang.exception.CsvBangException;

/**
 * Stream of byte. Used in order to not reallocate and in order to reduce the number of time which read byte array. 
 * This class is not synchronized. 
 * @author Tony EMMA
 * @version 1.0.0
 * @since 1.0.0
 */
public class ByteStreamBuffer {

	/**
	 * A byte stream is a chain of byte array. An entry is a link of chain.
	 * @author Tony EMMA
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class Entry{
	
		/**
		 * Array of byte
		 * @since 1.0.0
		 */
		byte[] tab;
		
		/**
		 * The current index 
		 * @since 1.0.0
		 */
		int index = 0;
		
		/**
		 * Next element
		 * @since 1.0.0
		 */
		Entry next;
		
		/**
		 * Previous element
		 * @since 1.0.0
		 */
		Entry previous;
		
		/**
		 * Constructor
		 * @param capacity initial capacity of byte array
		 * @param previous the previous link
		 * @since 1.0.0
		 */
		public Entry(final int capacity, final Entry previous){
			tab = new byte[capacity];
			this.previous = previous;
		}
		
		/**
		 * Constructor
		 * @param previous the previous link
		 * @since 1.0.0
		 */
		public Entry(final Entry previous){
			this.previous = previous;
		}
		
	}
	
	/**
	 * First entry of chain
	 * @since 1.0.0
	 */
	private Entry first;
	
	/**
	 * Last entry
	 * @since 1.0.0
	 */
	private Entry last;
	
	/**
	 * Total length of stream
	 * @since 1.0.0
	 */
	private int totalLength = 0;
	
	/**
	 * Current byte array to read
	 * @since 1.0.0
	 */
	private Entry current;
	
	/**
	 * Current index of byte to read in the current entry
	 * @since 1.0.0
	 */
	private int readIndex = 0;
	
	/**
	 * Position of the cursor in buffer
	 * @since 1.0.0
	 */
	private int readPosition = 0;
	
	
	/**
	 * Constructor
	 * @param capacity the initial capacity of a byte array
	 * @since 1.0.0
	 */
	public ByteStreamBuffer(final int capacity){
		int cap = capacity < 10?IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE/3:capacity;
		final Entry entry = new Entry(cap, null);
		first = entry;
		last = entry;
		reset();
	}
	
	
	/**
	 * Constructor
	 * @param capacity the initial capacity of a byte array
	 * @since 1.0.0
	 */
	public ByteStreamBuffer(final byte[] initialBuffer){
		final Entry entry = new Entry(null);
		if (initialBuffer != null && initialBuffer.length > 0){
			entry.tab = initialBuffer;
			entry.index = initialBuffer.length;
			totalLength = initialBuffer.length;
		}else{
			entry.tab = new byte[IConstantsCsvBang.DEFAULT_BYTE_BLOCK_SIZE/3];
		}
		first = entry;
		last = entry;
		reset();
	}
	
	/**
	 * Add a byte to stream
	 * @param b the byte to add
	 * @since 1.0.0
	 */
	public void add(final byte b){
		if (last.index == last.tab.length){
			final Entry entry = new Entry(last.tab.length, last);
			last.next = entry;
			last = entry;
		}
		last.tab[last.index++] = b;
		totalLength++;
	}
	
	
	/**
	 * Add a byte array
	 * @param array the byte array to add
	 * @since 1.0.0
	 */
	public void add(final byte[] array){
		if (array.length > 0){
			final Entry e = new Entry(last);
			e.tab = array;
			e.index = array.length;
			last.next = e;
			last = e;
		}
	}
	
	/**
	 * Add stream before this stream
	 * @param stream byte stream to add
	 * @since 1.0.0
	 */
	public void addBefore(final ByteStreamBuffer stream){
		first.previous = stream.last;
		stream.last.next = first;
		first = stream.first;
		totalLength += stream.totalLength;
		stream.clear();
	}
	
	/**
	 * Add stream after this stream
	 * @param stream byte stream to add
	 * @since 1.0.0
	 */
	public void addAfter(final ByteStreamBuffer stream){
		last.next = stream.first;
		stream.first.previous = last;
		last = stream.last;
		totalLength += stream.totalLength;
		stream.clear();
	}
	
	
	/**
	 * Delete last byte
	 * @return the last byte deleted
	 * @since 1.0.0
	 */
	public Byte deleteLastByte(){
		Entry e = last;
		do{
			if (e.index > 0){
				e.index--;
				totalLength--;
				return e.tab[e.index];
			}
			e = e.previous;
		}while (e.previous != null);
		return null;
	}
	
	/**
	 * Verify if the buffer is empty
	 * @return true if buffer is empty
	 * @since 1.0.0
	 */
	public boolean isEmpty(){
		return first == last && first.index == 0;
	}
	
	
	/**
	 * Generate a string of byte stream
	 * @param charset the charset to use
	 * @return the string representation
	 * @throws CsvBangException If a problem has occurred when we decode a byte array
	 * @since 1.0.0
	 */
	public String toString(final Charset charset) throws CsvBangException{
		if (isEmpty()){
			//empty buffer
			return "";
		}
		
		final CharsetDecoder dec = charset.newDecoder();
		final CharBuffer charResult = CharBuffer.allocate(totalLength);
		final ByteBuffer currentBuffer = ByteBuffer.allocate(totalLength);
		Entry e = first;
		while(e != null){
			
			if (e.index == 0 && e != last){
				//empty entry in buffer
				e = e.next;
				continue;
			}
			
			//add new byte array
			currentBuffer.put(e.tab, 0, e.index);
			
			//save position
			final int lastPositionCharBuffer = charResult.position();
			
			//decode byte array
			currentBuffer.flip();
		    final CoderResult result = dec.decode(currentBuffer, charResult, e == last);
			
			if (result.isError() || result.isMalformed() || result.isOverflow() || result.isUnmappable()){
				//error
				throw new CsvBangException(String.format("A problem has occurred when we decode a byte array. We can't decode with charset [%s] the byte buffer [%s]. We have decoded this part: %s", 
						charset, Arrays.toString(currentBuffer.array()), charResult.toString()));
			}
			
		    if (result.isUnderflow() && charResult.position() == lastPositionCharBuffer) {
		        // Underflow, prepare the buffer for more writing
		    	currentBuffer.position(currentBuffer.limit());
		    }else{
		    	if (currentBuffer.position() == currentBuffer.limit()){
		    		//The ByteBuffer is completely decoded
		    		currentBuffer.clear();
		    	}else{
		    		//a part of ByteBuffer is decoded. We keep only bytes which are not decoded
		    		final byte[] b = currentBuffer.array();
		    		final int pos = currentBuffer.position();
		    		final int length = currentBuffer.limit() - currentBuffer.position();
		    		currentBuffer.clear();
		    		currentBuffer.put(b, pos, length);
		    	}
		    }
		    
		    currentBuffer.limit(currentBuffer.capacity());
			e = e.next;
		}
		
		if (currentBuffer.position() > 0){
			//the current buffer is not empty some bytes can't be decoded. we can't generate a correct character sequence of byte buffer.
			throw new CsvBangException(String.format("The byte buffer is not complete. It miss some byte in order to decode the buffer. We can't decode with charset [%s] the byte buffer [%s]. We have decoded this part: %s", 
					charset, Arrays.toString(currentBuffer.array()), charResult.toString()));
		}
		
		//generate result
		try{
			dec.flush(charResult);
		}catch(IllegalStateException e1){
			throw new CsvBangException(String.format("The byte buffer is not complete. It miss some byte in order to decode the buffer. We can't decode with charset [%s] the byte buffer [%s]. We have decoded this part: %s", 
					charset, Arrays.toString(currentBuffer.array()), charResult.toString()), e1);
		}
		charResult.flip();
		return charResult.toString();
	}
	
	/**
	 * Clear the stream buffer. All byte array will be lost
	 * @since 1.0.0
	 */
	public void clear(){
		final Entry entry = new Entry(last.tab.length, null);
		first = entry;
		last = entry;
		totalLength = 0;
		reset();
	}
	
	/**
	 * Reset read index of stream 
	 * @since 1.0.0
	 */
	public void reset(){
		current = first;
		readIndex =0;
		readPosition=0;
	}
	
	
	/**
	 * Read byte of the byte stream at current position
	 * @return the current byte
	 * @throws CsvBangException if you are out of bound
	 * @since 1.0.0
	 */
	public byte read() throws CsvBangException{
		readPosition++;
		if (readIndex < current.index){
			return current.tab[readIndex++];
		}
		
		while(current != last){
			current = current.next;
			readIndex = 0;
			if (readIndex < current.index){
				return current.tab[readIndex++];
			}
		}
		
		throw new CsvBangException("We have read all byte in buffer. No more to read.");
	}
	
	/**
	 * Read byte from the current position of read cursor in buffer. Not change the current position of cursor.
	 * The cursor is always at the position on next byte. So a step equals to 0, means to read the next byte.
	 * @param step gap from current position. Number of byte between the last byte which read and the byte you want to read.
	 * @return the byte
	 * @throws CsvBangException
	 * @since 1.0.0
	 */
	public byte readFromPosition(final int step) throws CsvBangException{
		int index = readIndex + step;
		
		if (index < current.index){
			return current.tab[index];
		}
		
		Entry e = current;
		while(e != last){
			index = index - e.index;
			e = e.next;
			if (index < e.index){
				return e.tab[index];
			}
		}
		
		throw new CsvBangException("We have read all byte in buffer. No mare to read.");
	}
	
	/**
	 * Retrieve the current position of read cursor in buffer
	 * @return the current position
	 * @since 1.0.0
	 */
	public int readPosition(){
		return readPosition;
	}
	
	/**
	 * Set the current position of read cursor in buffer.
	 * @param position the buffer
	 * @since 1.0.0
	 */
	public void setPosition(final int position){
		readIndex = readIndex + position - readPosition;
		readPosition = position;
		
		if (readIndex < current.index){
			return;
		}
		
		while(current != last){
			readIndex = readIndex - current.index;
			current = current.next;
			if (readIndex < current.index){
				return;
			}
		}
	}
	
	/**
	 * Verify that you have not read the whole buffer.
	 * @return True if you have read the whole buffer.
	 * @since 1.0.0
	 */
	public boolean isReadWholeBuffer(){
		return totalLength <= readPosition;
	}
	
	/**
	 * Get the number of byte added to this buffer
	 * @return the size of byte inserted
	 * @since 1.0.0
	 */
	public int length(){
		return totalLength;
	}
}
