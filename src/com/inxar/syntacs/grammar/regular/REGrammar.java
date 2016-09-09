/**
 * $Id: REGrammar.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.regular;

import java.io.StringReader;
import java.util.Vector;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.translator.*;
import com.inxar.syntacs.translator.regexp.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Standard <code>RegularGrammar</code> implementation.
 */
public class REGrammar
    implements RegularSet, RegularGrammar
{
    private static final boolean DEBUG = false;

    private static final Epsilon _epsilon = new REEpsilon();

    /**
     * This little number accounts helps ensure the reservation of EOF
     * as token 0 
     */
    private static final int TOKEN_ID_OFFSET = 1;

    /**
     * Constructs the <code>REGrammar</code> having the given ID and name.
     */
    public REGrammar(int id, String name)
    {
	this.id = id;
	this.name = name;

	tokens = new Vector();
	intervals = new IntervalVector(7);
	rtrans = new RegexpGrammar().newTranslator();
    }

    /**
     * Constructs the <code>REGrammar</code> having the given ID.  
     */
    public REGrammar(int id)
    {
	this(id, null);
    }

    /**
     * Constructs the <code>REGrammar</code>.
     */
    public REGrammar()
    {
	this(0, null);
    }

    public String getName()
    {
	return name;
    }

    public int getID()
    {
	return id;
    }

    public int tokens()
    {
	return tokens.size();
    }

    public Concatenation newConcatenation(RegularExpression left, RegularExpression right)
    {
	return new REConcatenation(this, left, right);
    }

    public Union newUnion()
    {
    	return new REUnion();
    }

    public Closure newClosure(RegularExpression re)
    {
    	return new REClosure(this, re);
    }

    public PositiveClosure newPositiveClosure(RegularExpression re)
    {
    	return new REPositiveClosure(this, re);
    }

    public Option newOption(RegularExpression re)
    {
    	return new REOption(this, re);
    }

    public CharString newCharString(String s)
    {
    	return new RECharString(this, s);
    }

    public CharClass newCharClass()
    {
    	return new RECharClass(this);
    }

    public Interval newInterval(char c)
    {
	return newInterval((int)c, (int)c);
    }

    public Interval newInterval(int lo, int hi)
    {
	REInterval x = new REInterval(this, intervals.size(), lo, hi);
	intervals.add(x);
	return x;
    }

    protected ExpressionTerminator newExpressionTerminator(RegularToken token)
    {
	REExpressionTerminator x = new REExpressionTerminator(this, intervals.size(), token);
	intervals.add(x);
	return x;
    }

    /*
     * Synchronized since the id of the nascent token depends on the
     * size and thus position in the vector.  
     */

    public synchronized RegularToken newToken(int tokenID, String tokenName, RegularExpression re)
    {
	if (DEBUG) 
	    log().debug()
		.write("newToken(): adding token ").write(tokenID)
		.write(": ")
		.write(re)
		.out();

	// make a new token on the synthesized id and given name.
	REToken token = new REToken(this, tokenID, tokenName);

    	// augment the regex with a terminator bound back to the token
    	// and tie the new regex back to the token
    	token.regex = newConcatenation(re, newExpressionTerminator(token));

	// add it to the vector
	tokens.addElement(token);

	// return it to caller
	return token;
    }

    public synchronized RegularToken newToken(int tokenID, String tokenName, String re)
    {
	RegularExpression regex = null;
	
	try {

	    regex = ((Regexp)rtrans.translate(new StringReader(re)))
		.toRegularExpression(this);

	} catch (TranslationException tex) {
	    throw new RuntimeTranslationException
		(tex.getAuditor(), 
		 "Could not parse regular expression.");
	}
	
	return newToken(tokenID, tokenName, regex);
    }

    public Epsilon getEpsilon()
    {
	return _epsilon;
    }

    public IntSet getStart()
    {
    	if (first == null) {

	    // make sure the follows have been done
	    follow();

	    first = new BitSetIntSet();

	    for (int i=tokens.size() - 1; i >= 0; i--) {
		first.union(( (REToken)tokens.elementAt(i) ).regex.getFirstSet());
	    }

	    if (DEBUG) 
		log().debug()
		    .write("getStart(): first set is ")
		    .write(first)
		    .out();
	}

	return first;
    }

    private void follow()
    {
	// trigger the depth first follow traversal
	int sz = tokens.size();

	if (DEBUG) 
	    log().debug()
		.write("follow(): initiated followset calculations for ")
		.write(sz).write(" tokens")
		.out();

	for (int i=0; i < sz; i++) {

	    if (DEBUG) {
		REToken t = (REToken)tokens.elementAt(i);
		log().debug()
		    .write("follow(): calculating followset for ")
		    .write(t)
		    .write(" (").write(t.getClass()).write(") with regex ")
		    .write(t.regex.getClass())
		    .out();
	    }
	    ( (REToken)tokens.elementAt(i) ).regex.follow();
	}
    }

    public Interval getInterval(int id)
    {
	return intervals.get(id);
    }

    public int intervals()
    {
	return intervals.size();
    }

    public Token getToken(int id)
    {
	Token t = null;
	// just do a linear search since this is not performance
	// demanding (debug only).
	for (int i = 0; i<tokens.size(); i++) {
	    // get next token
	    t = (Token)tokens.elementAt(i);
	    // check id
	    if (t.getID() == id)
		return t;
	}
	// no match
	return null;
    }

    public RegularSet compile()
    {
	return this;
    }

    public RegularGrammar getRegularGrammar()
    {
	return this;
    }

    public String toString()
    {
	StringBuffer b = 
	    new StringBuffer("INTERVALS (interval followset)") .append(StringTools.NEWLINE)
	    .append("-------------------").append(StringTools.NEWLINE);

	// report all the intervals and the tokens
	for (int i=0; i<intervals.size(); i++) {
	    Interval interval = intervals.get(i);
	    b.append(interval).append(": ")
		.append(interval.getFollowSet()).append(StringTools.NEWLINE);
	}

	b.append("-------------------").append(StringTools.NEWLINE)
	    .append("TOKENS").append(StringTools.NEWLINE)
	    .append("-------------------")
	    .append(StringTools.NEWLINE);

	for (int i=0; i<tokens.size(); i++) {
	    RegularToken token = (RegularToken)tokens.elementAt(i);
	    b.append(token).append(": ")
		.append(token.getRegularExpression()).append(StringTools.NEWLINE);
	}

	return b.append("-------------------").append(StringTools.NEWLINE)
	    .toString();
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("rg"+name.charAt(0), this);
	return log;
    }

    private Vector tokens;
    private IntervalVector intervals;
    private IntSet first;
    private String name;
    private int id;
    private Translator rtrans;
    private Log log;

    /**
     * A simple vector specifically for the intervals.
     */
    private static class IntervalVector
    {
	IntervalVector(int capacity)
	{
	    this.src = new Interval[capacity];
	    this.index = 0;
	}

	void add(Interval span)
	{
	    check();
	    src[index++] = span;
	}

	Interval get(int id)
	{
	    return src[id];
	}

	int size()
	{
	    return index;
	}

	private void check()
	{
	    if (index == src.length) {
		Interval[] dst = new Interval[src.length * 2];
		System.arraycopy(src, 0, dst, 0, src.length);
		src = dst;
	    }
	}

	private Interval[] src;
	private int index;
    }

    /**
     * Standard implementation of a RegularToken
     */
    private static final class REToken
	implements RegularToken
    {
	REToken(REGrammar grammar, int id, String name)
	{
	    this.grammar = grammar;
	    this.id = id;
	    this.name = name;
	}

	public String toString()
	{
	    return name;
	}

	public int getID() { return id; }
	public String getName() { return name; }
	public RegularExpression getRegularExpression() { return regex; }
	public RegularGrammar getGrammar() { return grammar; }
	public Object clone() throws CloneNotSupportedException { return super.clone(); }

	int id;
	String name;
	RegularExpression regex;
	REGrammar grammar;
    }
}




