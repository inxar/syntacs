/**
 * $Id: REExpressionTerminator.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.grammar.regular.*;

/**
 * Standard <code>ExpressionTerminator</code> implementation.
 */
public class REExpressionTerminator
    extends REInterval
    implements ExpressionTerminator
{
    /**
     * Constructs the <code>REExpressionTerminator</code> on the given
     * <code>REGrammar</code>, allocated ID number, and the
     * <code>RegularToken</code> to which this <code>ExpressionTerminator</code>
     * corresponds.  
     */
    public REExpressionTerminator(REGrammar grammar, int id, RegularToken token)
    {
	/*
	  explanation of why the lo and hi int values are the negative
	  token type id:  

	  -- the new minizing dfa contruction method 

	*/

	super(grammar, id, -token.getID(), -token.getID());

	this.token = token;
    }

    public String toString()
    {
	return "#{" + token.getID() + ':' + token + "}";
    }

    public boolean isTerminator()
    {
    	return true;
    }

    public RegularToken getToken()
    {
    	return token;
    }

    public Object clone() throws CloneNotSupportedException
    {
	// clone self
	REExpressionTerminator clone = (REExpressionTerminator)super.clone();
	// clone the token
	clone.token = (RegularToken)this.token.clone();
	// done
	return clone;
    }

    private RegularToken token;
}


