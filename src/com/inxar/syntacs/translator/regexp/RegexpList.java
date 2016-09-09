/**
 * $Id: RegexpList.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.List;
import java.util.ArrayList;
import org.inxar.syntacs.grammar.regular.RegularGrammar;
import org.inxar.syntacs.grammar.regular.RegularExpression;
import org.inxar.syntacs.grammar.regular.Union;

/**
 * Regexp subclass for list constructs such as union (alternation) and
 * concatention.
 */
class RegexpList extends Regexp
{
    RegexpList(int regexpType)
    {
	super(regexpType);
	this.list = new ArrayList();
    }

    void addRegexp(Regexp regexp)
    {
	list.add(regexp);
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	b.append('(');
	switch (regexpType) {
	case UNION:
	    for (int i = 0; i < list.size(); i++) {
		if (i > 0)
		    b.append('|');
		b.append(list.get(i).toString());
	    }
	    break;
	case CONCAT:
	    for (int i = 0; i < list.size(); i++)
		b.append(list.get(i).toString());
	    break;
	default:
	    throw new InternalError("Unknown regexpType for RegexpTerm: "+regexpType);
	}
	b.append(')');

	return b.toString();
    }

    public RegularExpression toRegularExpression(RegularGrammar g)
    {
	switch (regexpType) {
	case UNION:
	    Union union = g.newUnion();
	    for (int i = 0; i < list.size(); i++)
		union.addAllele( ((Regexp)list.get(i)).toRegularExpression(g) );
	    return union;
	case CONCAT:
	    RegularExpression re = ((Regexp)list.get(0)).toRegularExpression(g);
	    for (int i = 1; i < list.size(); i++)
		re = g.newConcatenation
		    (re, ((Regexp)list.get(i)).toRegularExpression(g));
	    return re;
	default:
	    throw new InternalError("Unknown regexpType for RegexpTerm: "+regexpType);
	}
    }

    protected List list;
}
