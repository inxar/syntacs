/**
 * $Id: RegexpRange.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.regular.RegularGrammar;
import org.inxar.syntacs.grammar.regular.RegularExpression;

/**
 * Regexp subclass for ranges within character classes.
 */
class RegexpRange extends Regexp
{
    RegexpRange(char lo, char hi)
    {
	super(Regexp.RANGE);
	this.lo = new RegexpAtom(lo);
	this.hi = new RegexpAtom(hi);
    }

    RegexpRange(RegexpAtom lo, RegexpAtom hi)
    {
	super(Regexp.RANGE);
	this.lo = lo;
	this.hi = hi;
    }

    public String toString()
    {
	return new StringBuffer()
	    .append(lo.toString()).append('-').append(hi.toString())
	    .toString();
    }

    public char lo()
    {
	return lo.getValue();
    }

    public char hi()
    {
	return hi.getValue();
    }

    public RegularExpression toRegularExpression(RegularGrammar g)
    {
	return g.newInterval(lo.getValue(), hi.getValue());
    }

    private RegexpAtom lo;
    private RegexpAtom hi;
}
