/**
 * $Id: RegexpTerm.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * <code>Regexp</code> subclass which can be quantified with one of
 * the quantification operators '<code>+</code>', '<code>*</code>',
 * '<code>?</code>'.  It contains an internal <code>Regexp</code>.  
 */
class RegexpTerm extends Regexp
{
    RegexpTerm(int regexpType)
    {
	super(regexpType);
    }

    RegexpTerm(int regexpType, Regexp internal)
    {
	super(regexpType);
	this.internal = internal;
    }

    void setInternal(Regexp internal)
    {
	this.internal = internal;
    }

    Regexp getInternal()
    {
	return this.internal;
    }

    public String toString()
    {
	switch (regexpType) {
	case GROUP: return " (" + internal.toString() + ") ";
	case OPTIONAL: return internal.toString() + "?";
	case CLOSURE: return internal.toString() + "*";
	case PCLOSURE: return internal.toString() + "+";
	}
	return internal.toString();
    }

    public RegularExpression toRegularExpression(RegularGrammar g)
    {
	switch (regexpType) {
	case GROUP: return internal.toRegularExpression(g);
	case OPTIONAL: return g.newOption(internal.toRegularExpression(g));
	case CLOSURE: return g.newClosure(internal.toRegularExpression(g));
	case PCLOSURE: return g.newPositiveClosure(internal.toRegularExpression(g));
	default:
	    throw new InternalError("Unknown regexpType for RegexpTerm: "+regexpType);
	}
    }

    private Regexp internal;
}
