/**
 * $Id: RegularGrammar.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.regular;

/**
 * The <code>RegularGrammar</code> interface represents a factory for
 * generating regular expressions, typically for the purpose of
 * constructing <code>RegularTokens</code>.  Each <code>newXXX</code>
 * method allocates and returns a new <code>RegularExpression</code>
 * object which implements the <code>XXX</code> interface.  These
 * regex <code>Objects</code> are then used to construct more complex
 * <code>RegularExpressions</code>, eventually to be resubmitted to
 * the <code>RegularGrammar</code> object with a name using the
 * <code>newToken()</code> method.  By this fashion one builds up a
 * set of named regular expressions, perhaps to be transformed to a
 * <code>DFA</code> which recognizes the tokens implied by the
 * regexes.  When the token construction phase is complete, calling
 * <code>compile()</code> returns a 'compiled' version of the language.
 * The compilation process typically involves giving the appropriate
 * objects unique integer id's such that future set manipulation can
 * be done numerically rather than using full-scale
 * <code>Objects</code>.
 * 
 * <P>
 *
 * In this way, one can think of the <code>RegularGrammar</code>
 * interface as the 'thing' humans assemble and the
 * <code>RegularSet</code> as the 'thing' machines use to do more
 * interesting things like build <code>DFA</code>s.  
 */
public interface RegularGrammar
{
    /**
     * Allocates and returns a new <code>Concatenation</code>
     * expression from the given left and right
     * <code>RegularExpressions</code>.  
     */
    Concatenation newConcatenation(RegularExpression left, RegularExpression right);

    /**
     * Allocates and returns a new <code>Closure</code> expression
     * ('*') wrapping the given <code>RegularExpression</code>.  
     */
    Closure newClosure(RegularExpression re);

    /**
     * Allocates and returns a new <code>PositiveClosure</code>
     * expression ('+') wrapping the given
     * <code>RegularExpression</code>.  Note that the
     * <code>PositiveClosure</code> is a shortcut for
     * concatenation-closure.  Therefore, a+ expands to aa*.  
     */
    PositiveClosure newPositiveClosure(RegularExpression re);

    /**
     * Allocates and returns a new <code>Interval</code> expression
     * over the given character range from lo to hi, inclusive.  
     */
    Interval newInterval(int lo, int hi);

    /**
     * Allocates and returns a new <code>Interval</code> expression
     * over the given <code>char</code>.  Note <code>newInterval(97,
     * 97)</code> has the same meaning as
     * <code>newInterval('a')</code> under the ascii or unicode
     * charset.  
     */
    Interval newInterval(char c);

    /**
     * Allocates and returns a new <code>Option</code> expression
     * ('?') wrapping the given <code>RegularExpression</code>.  Note
     * that <code>Option</code> is not an atomic
     * <code>RegularExpression</code>.  Thus, 'a?' expands to the
     * <code>Union</code> (a|<code>Epsilon</code>).  
     */
    Option newOption(RegularExpression re);

    /**
     * Allocates and returns a new <code>CharString</code> expression
     * ('+') wrapping the given <code>RegularExpression</code>. Note
     * <code>CharString</code> is not a fundamental expression.  Thus,
     * 'abc' expands to the concatenation sequence a-b-c.  
     */
    CharString newCharString(String s);

    /**
     * Allocates and returns a new <code>Union</code> expression.
     * Subsequent modification of the <code>Union</code> is required
     * (i.e. an empty union is invalid).  
     */
    Union newUnion();

    /**
     * Allocates and returns a new <code>CharClass</code> expression
     * ([^-a-z]).  Subsequent modification of the character class is
     * required (i.e. an empty character class is invalid).  
     */
    CharClass newCharClass();

    /**
     * Allocates and returns a new <code>RegularToken</code> mapping
     * the given name to the given <code>RegularExpression</code>.
     * This is the 'special' <code>newXXX()</code> method in that it
     * does not return a <code>RegularExpression</code>, but a
     * <code>Token</code>.  The <code>RegularToken</code> is returned
     * to potentially facilitate it's incorporation into other
     * languages such as the <code>
     * ContextFreeLanguage.newTerminal(Token)</code> method.
     * Therefore, calling <code>newToken()</code> not only makes a
     * <code>RegularToken</code> object on the regex, it becomes
     * associated into the grammar.  
     */
    RegularToken newToken(int tokenID, String name, RegularExpression regex);

    /**
     * Allocates a new <code>RegularToken</code> in this grammar
     * having the given tokenID number, name, and regex.  The
     * <code>RegularGrammar</code> is then responsible for parsing the
     * regex string and generating a <code>RegularExpression</code>.  
     */
    RegularToken newToken(int tokenID, String name, String regex);

    /**
     * Returns the <code>Epsilon</code> symbol in the (rare) case one
     * needs it.  
     */
    Epsilon getEpsilon();

    /**
     * When token construction is complete, <code>compile()</code>
     * compiles and returns a <code>RegularSet</code> object which can
     * be used for generation of <code>DFA</code>'s, for example.  The
     * compilation process is essentially making sure
     * <code>Intervals</code> each get a unique ID and concatenating
     * <code>ExpressionTerminators</code> where appropriate.  
     */
    RegularSet compile();
}


