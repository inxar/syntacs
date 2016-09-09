/**
 * $Id: MesoArrayDFA.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package com.inxar.syntacs.automaton.finite;

import java.io.Serializable;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.automaton.finite.*;
import com.inxar.syntacs.util.StringTools;

/**
 * Concrete <code>DFA</code> implementation which uses truncated array
 * parse tables.  The <code>MesoArrayDFA</code> has favorable memory
 * and speed characteristics and thus is used often within the STT.
 *
 * <P>
 *
 * 'Truncated' means that the ends of the array are contracted into a
 * single array element.  Another name to describe this is
 * "atelomeric".  Since each state table entry commonly has long
 * stretches of contiguously identical transitions at the beginning
 * and end of the unicode range, the next-state value for the
 * beginning and end stretch of the table can be placed in a single
 * element. 'Meso' means 'middle'.  
 *
 * <P>
 *
 * The way this works is as follows.  Each state is represented by a
 * single row in the transition table.  This table entry must
 * communicate the next_state for every possible input character over
 * the unicode input range.  One way to do this is make the array
 * 65535 elements long and explicitly say what every next state is.
 * Invariably however, most of these next_states are the DEAD_STATE
 * (or at least repetitive).  For example, consider an arbitrary state
 * '5' which has a single transition to state '8' over the character
 * 'A'.  The table entry would look like:
 * 
 * <pre>
 *     INDEX: [0, 1, 2, ... , 64, 65, 66, ..., 65532, 65533, 65534]
 *     VALUE: [X, X, X, ... ,  X,  8,  X, ...,     X,     X,     X]
 * </pre>
 *
 * where 'X' represents the dead state.  Another way to communicate
 * the same information is as follows:
 *
 * <pre>
 *     INDEX: [ 0,  1, 2, 3, 4]
 *     VALUE: [64, 66, X, X, 8]
 * </pre>
 *
 * The above says "<i>if the input character has a value equal or less
 * than 64, forget about looking it up in the array, I can tell you
 * right off the bat that the next_state value is stored in element 2.
 * Likewise, if the input character has a value greater or equal to
 * 66, don't bother looking, the next_state is at element 3.  But if
 * the value is between 64 and 66, look in the rest of array for the
 * next state, but make sure you take into account that (1) the first
 * 64 characters have been chopped off the front of the array and (2)
 * we're using the first four elements in the array for accounting
 * purposes.</i>"
 *
 * <P>
 *
 * This strategy only works effectively when states have long
 * stretches of contiguous repeats at the ends of the state tables,
 * but fortunately this is the norm not the exception.  Incidentally,
 * the ends of eukaryotic chromosomes have long stretches of
 * oligonucleotide repeats called "telomeres" that are thought to play
 * a key role in the regulation and metering of cellular aging.
 */
public class MesoArrayDFA implements DFA, Serializable
{
    private static final boolean DEBUG = false;

    /**
     * The LOWER constant is the index at which the length of the low stretch is held.
     */
    static final int LOWER = 0;
    
    /**
     * The UPPER constant is the index at which the length of the high stretch is held.
     */
    static final int UPPER = 1;
    
    /**
     * The LOWER_NEXT constant is the index at which the next state for the low stretch is held.
     */
    static final int LOWER_NEXT = 2;
    
    /**
     * The UPPER_NEXT constant is the index at which the next state for the high stretch is held.
     */
    static final int UPPER_NEXT = 3;
    
    /**
     * The START constant holds the index at which the 'meso' part of the array begins.
     */
    static final int START = 4;
    
    /**
     * Constructs the MesoArrayDFA on the given truncated transition
     * table and accepting token array.
     */
    public MesoArrayDFA(int[][] table, int[] accepts)
    {
	this.table = table;
	this.accepts = accepts;
    }
    
    public int go(int state_num, int input)
    {
	// the move is more complex now.  We need to check if the
	// input char is either below or above the literal bounds of
	// the representative array.  If it's low, we return the value
	// at [first+1], if hi return [last].
	
	if (DEBUG) writeState(state_num);
	
	int[] state = table[state_num];
	// check if the given input falls below the minimum value
	if (input < state[LOWER]) {
	    return state[LOWER_NEXT];
	}
	
	// or if the given input falls above the maximum value
        else if (input > state[UPPER]) {
	    return state[UPPER_NEXT];
	}
	
	// or not.  Return the offset value in the middle
	else {
	    return state[input - state[LOWER] + START];
	}
    }
    
    public int output(int state)
    {
	return accepts[state];
    }
    
    public String toString()
    {
	StringBuffer b = new StringBuffer();
	table_toBuffer(b);
	tokens_toBuffer(b);
	return b.toString();
    }
    
    private void table_toBuffer(StringBuffer b)
    {
        // print header information
        b.append("================================= begin ==").append(StringTools.NEWLINE);
        b.append("ATELOMERIC DFA STATE SUMMARY").append(StringTools.NEWLINE);
        b.append("> state: t1,t2,q1,q2,qk,...,qn").append(StringTools.NEWLINE);
        b.append("> l1 is start telomere length").append(StringTools.NEWLINE);
        b.append("> l2 is end telomere length").append(StringTools.NEWLINE);
        b.append("> q1 is start telomere next_state").append(StringTools.NEWLINE);
        b.append("> q2 is end telomere next_state").append(StringTools.NEWLINE);
        b.append("> qk-qn is is the rest").append(StringTools.NEWLINE);
        b.append("> total states: ").append(table.length).append(StringTools.NEWLINE);
	
	for (int i = 0; i < table.length; i++) {
	    b.append(i).append(": ");
	    
	    for (int j=0; j<table[i].length; j++) {
		if (j > 0) b.append(',');
		b.append(table[i][j]);
	    }
	    b.append('\n');
	}
        b.append("=================================== end ==").append(StringTools.NEWLINE);
    }
    
    protected void tokens_toBuffer(StringBuffer b)
    {
        // print header information
        b.append("================================= begin ==").append(StringTools.NEWLINE);
        b.append("ACCEPTING STATE SUMMARY").append(StringTools.NEWLINE);
        b.append("> state: token_type_id").append(StringTools.NEWLINE);
        // walk the states
        for (int i=0; i<accepts.length; i++) {
	    // see if its boring
	    if (accepts[i] == Token.UNDEF)
		continue;
	    
	    b.append("> ");
	    if (i < 10)
		b.append(' ');
	    b.append(i).append(": ").append(accepts[i]).append("\n");
        }
        b.append("=================================== end ==").append(StringTools.NEWLINE);
    }
    
    private void writeState(int state)
    {
	//System.out.println("MesoArrayDFA.go(): state "+state+" is:");
	StringBuffer 
	    v = new StringBuffer(), 
	    l = new StringBuffer(), 
	    c = new StringBuffer();
	
	for (int idx = 0; idx < table[state].length; idx++) {
	    if (l.length() > 800) {
		l.append('+');
		c.append('|');
		v.append('|');
		System.out.println(l.toString());
		System.out.println(c.toString());
		System.out.println(l.toString());
		System.out.println(v.toString());
		System.out.println(l.toString());
		l.setLength(0);
		c.setLength(0);
		v.setLength(0);
	    }
	    
	    // init vars
	    int ipad = 0, vpad = 0, ilen = 1, vlen = 1, val = table[state][idx];
	    
	    // assign index length
	    if (idx >= 100) ilen = 3;
	    else if (idx >= 10) ilen = 2;

	    // assign value length
	    if (val < 0) vlen = 2;
	    else if (val >= 1000) vlen = 5;
	    else if (val >= 1000) vlen = 4;
	    else if (val >= 100) vlen = 3;
	    else if (val >= 10) vlen = 2;
	    
	    // assign paddings
	    if (ilen > vlen) vpad = ilen - vlen;
	    else if (vlen > ilen) ipad = vlen - ilen;
	    
	    // write the line
	    l.append('+'); for (int i = Math.max(ilen, vlen); i > 0; i--) l.append('-');
	    
	    // write the index
	    c.append('|'); for (int i=ipad; i > 0; i--) c.append(' '); c.append(idx);
	    
	    // write the value
	    v.append('|'); for (int i=vpad; i > 0; i--) v.append(' '); v.append(val);
	}
	l.append('+');
	c.append('|');
	v.append('|');

	System.out.println(l.toString());
	System.out.println(c.toString());
	System.out.println(l.toString());
	System.out.println(v.toString());
	System.out.println(l.toString());
    }
    
    /**
     * The accepting <code>token</code> table, where the first
     * dimension is the state number and the value at that address is
     * the <code>Token</code> type number.  
     */
    public int[] accepts;
    
    /**
     * The transition table, where the first dimension index is the
     * state number, the second dimension index is the offset unicode
     * input character, and the value at that array address is the
     * next state.  See the description or the source code of the
     * class for specifics.  
     */
    public int[][] table;
}


	


