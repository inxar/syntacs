/**
 * $Id: RegexpCharClass.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.grammar.regular.RegularExpression;
import org.inxar.syntacs.grammar.regular.RegularGrammar;
import org.inxar.syntacs.grammar.regular.CharClass;

/**
 * Regexp subclass for character classes <code>[a-zA-Z]</code>.
 */
class RegexpCharClass extends RegexpList
{
    RegexpCharClass()
    {
	super(CHARCLASS);
    }

    public boolean isNegated()
    {
	return isNegated;
    }

    public void isNegated(boolean isNegated)
    {
	this.isNegated = isNegated;
    }

    public boolean hasDash()
    {
	return hasDash;
    }

    public void hasDash(boolean hasDash)
    {
	this.hasDash = hasDash;
    }

    public void setList(List list)
    {
	this.list = list;
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	b.append('[');

	if (isNegated)
	    b.append('^');

	if (hasDash)
	    b.append('-');

	for (int i = 0; i < list.size(); i++)
	    b.append(list.get(i).toString());

	b.append(']');

	return b.toString();
    }

    public RegularExpression toRegularExpression(RegularGrammar g)
    {
	CharClass cc = g.newCharClass();
	cc.isNegated(isNegated);

	if (hasDash)
	    cc.add('-');

	for (int i = 0; i < list.size(); i++) {
	    Regexp r = (Regexp)list.get(i);
	    switch (r.getRegexpType()) {
	    case ATOM:
		cc.add( ((RegexpAtom)r).getValue() );
		break;
	    case RANGE:
		RegexpRange range = (RegexpRange)r;
		cc.add( range.lo(), range.hi() );
		break;
	    default:
		throw new InternalError("Unknown regexpType for RegexpTerm: "+r.getRegexpType());
	    }
	}

	return cc;
    }

    private boolean hasDash;
    private boolean isNegated;
}
