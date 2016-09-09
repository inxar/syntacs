/**
 * $Id: ConstantSymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer;

import org.inxar.syntacs.analyzer.Symbol;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>Symbol</code> which needs no
 * internal state other than the symbol type.  This implementation
 * might be used for tokens like "COLON_CHAR" or other punctuation
 * symbols whose definition can be derived solely from its type.
 */
public class ConstantSymbol extends AbstractSymbol

{
    /**
     * Constructs an <code>ConstantSymbol</code> with the given type.
     */
    public ConstantSymbol(int type)
    {
	super(type);
    }

    public String toString()
    {
	return "("+type+", CONSTANT)";
    }


    public void toTree(Tree t)
    {
	String name = null;

	getGrammar();
	if (g != null) {
	    name = g.getTerminal(type);

	    if (name == null)
		name = g.getTerminal(type);
	} 

	if (name == null)
	    name = "constant node "+type;
	
	
	t.add(name);
    }

    public boolean equals(Object other)
    {
	// obviously true
	if (this == other)
	    return true;

	// obviously false
	if (other == null || !(other instanceof Symbol))
	    return false;

	// ok to narrow
	Symbol that = (Symbol)other;

	// compare fields
	return this.type == that.getSymbolType();
    }
}
