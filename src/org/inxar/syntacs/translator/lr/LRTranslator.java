/**
 * $Id: LRTranslator.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator.lr;

import java.io.*;
import java.util.*;
import org.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.lexical.*;
import org.inxar.syntacs.analyzer.syntactic.*;
import org.inxar.syntacs.translator.*;

/**
 * A <code>LRTranslator</code> is a translator which uses LR-parsing.
 */
public interface LRTranslator extends Translator
{
    /**
     * Sets the <code>Input</code> instance.  
     */
    void setInput(Input in);

    /**
     * Sets the <code>Lexer</code> instance.  
     */
    void setLexer(Lexer lexer);

    /**
     * Sets the <code>LRTranslatorInterpreter</code> instance.  
     */
    void setLRTranslatorInterpreter(LRTranslatorInterpreter interpreter);

    /**
     * Sets the <code>Parser</code> instance.  
     */
    void setParser(Parser parser);

    /**
     * Sets the <code>LRTranslatorGrammar</code> instance.  
     */
    void setLRTranslatorGrammar(LRTranslatorGrammar grammar);

    /**
     * Sets the <code>Properties</code> instance.
     */
    void setProperties(Properties p);
}
