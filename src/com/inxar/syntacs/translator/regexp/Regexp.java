/**
 * $Id: Regexp.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.regexp;

import java.util.*;
import org.inxar.syntacs.grammar.*;
import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.analyzer.*;
import com.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.lexical.*;
import com.inxar.syntacs.analyzer.lexical.*;
import org.inxar.syntacs.analyzer.syntactic.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Base class for all regular expressions in the
 * <code>com.inxar.syntacs.translator.regexp</code> package used
 * during parsing.  A <code>Regexp</code> is notable for its ability
 * to be transformed into a
 * <code>org.inxar.syntacs.grammar.regular.RegularExpression</code>,
 * which is the representation used by <code>DFA</code> construction
 * algorithms.  
 */
public abstract class Regexp implements Symbol
{
    public static final int EPSILON = 1;
    public static final int ATOM = 2;
    public static final int CLOSURE = 3;
    public static final int PCLOSURE = 4;
    public static final int OPTIONAL = 5;
    public static final int GROUP = 6;
    public static final int CHARCLASS = 7;
    public static final int CONCAT = 8;
    public static final int UNION = 9;
    public static final int RANGE = 10;

    Regexp(int regexpType)
    {
	this.regexpType = regexpType;
    }

    public int getRegexpType()
    {
	return regexpType;
    }

    public void setRegexpType(int regexpType)
    {
	this.regexpType = regexpType;
    }

    public int getSymbolType()
    {
	return symbolType;
    }

    public void setSymbolType(int symbolType)
    {
	this.symbolType = symbolType;
    }

    public abstract RegularExpression toRegularExpression(RegularGrammar g);

    protected int regexpType;
    private int symbolType;

    public static Regexp toConcat(String s) 
    {
	RegexpList concat = new RegexpList(CONCAT);

	for (int i = 0; i < s.length(); i++) 
	    concat.addRegexp( new RegexpAtom(s.charAt(i)) );

	return concat;
    }
}
