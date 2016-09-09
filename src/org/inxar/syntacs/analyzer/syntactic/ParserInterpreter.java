/**
 * $Id: ParserInterpreter.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.syntactic;

import org.inxar.syntacs.analyzer.Symbol;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;

/**
 * The <code>ParserInterpreter</code> interface is reponsible for
 * handling "parser events".  A parser "event" is triggered when a
 * reduction occurs.  The <code>ParserInterpreter</code> is informed of
 * the reduction type (the unique ID of a production in the grammar)
 * and the top of the parse symbol stack (the <code>Sentence</code>).
 * A <code>Symbol</code> object is returned back to the parser which
 * is placed on the symbol stack.  
 */
public interface ParserInterpreter extends LRTranslationComponent
{
    /**
     * <code>ParserInterpreter.reduce(int, Sentence)</code> is called
     * when a reduction occurs. The type of the reduction (from the
     * grammar) as well as the top of the parse stack is passed to the
     * method.  The <code>ParserInterpreter</code> is expected to
     * construct a <code>Symbol</code> and return it back to the
     * <code>Parser</code> (to be placed at the top of the parse
     * stack).
     */
    Symbol reduce(int type, Sentence left_context) throws TranslationException;
    
    /**
     * This method will be called by the parser when an
     * <code>ERROR</code> instruction is hit.  The argument is the
     * <code>Symbol</code> type that was being parsed at the time of
     * error.  The return value should be a <code>Symbol</code> to try
     * instead of the one that invoked the error.  If the return value
     * is <code>null</code>, the error will be ignored.  If the return
     * value is not null and another error is encountered, one symbol
     * will be removed from the stack and the parser will try again
     * wil the given return symbol.  This will happen recursively
     * until the error is resolved and the parse can continue or the
     * stack is empty.  
     */
    Recovery recover(int type, Sentence left_context) throws TranslationException;

    /**
     * This method will be called by the parser when the
     * <code>ACCEPT</code> instruction occurs.  
     */
    void accept() throws TranslationException;
}


