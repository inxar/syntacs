/**
 * $Id: RegexpAtom.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.regular.RegularExpression;
import org.inxar.syntacs.grammar.regular.RegularGrammar;

/**
 * <code>Regexp</code> subclass which holds a single
 * <code>char</code>.
 */
class RegexpAtom extends Regexp
{
    RegexpAtom()
    {
	super(Regexp.ATOM);
    }

    RegexpAtom(char value)
    {
	super(Regexp.ATOM);
	this.value = value;
    }

    void setValue(char value)
    {
	this.value = value;
    }

    char getValue()
    {
	return this.value;
    }

    public String toString()
    {
	switch (value) {
	case '\n': return "\\n";
	case '\r': return "\\r";
	case '\t': return "\\t";
	case '\013': return "\\v";
	case ' ' : return "\\s";
	case '[' : return "\\[";
	case ']' : return "\\]";
	case '(' : return "\\(";
	case ')' : return "\\)";
	case '|' : return "\\|";
	case '*' : return "\\*";
	case '+' : return "\\+";
	case '?' : return "\\?";
	case '\\': return "\\\\";
	}
	return String.valueOf(value);
    }

    public RegularExpression toRegularExpression(RegularGrammar g)
    {
	return g.newInterval(value);
    }

    private char value;
}
