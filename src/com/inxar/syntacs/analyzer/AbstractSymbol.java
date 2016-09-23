/**
 * $Id: AbstractSymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import com.inxar.syntacs.util.Arboreal;
import com.inxar.syntacs.util.Mission;

/**
 * Base implementation of <code>Symbol</code>.
 */
public abstract class AbstractSymbol implements Symbol, Arboreal
{
    protected AbstractSymbol(int type)
    {
	this.type = type;
    }

    public int getSymbolType()
    {
	return type;
    }

    public void setSymbolType(int type)
    {
	this.type = type;
    }

    protected int type;

    protected static LRTranslatorGrammar getGrammar()
    {
	if (g == null)
	    g = (LRTranslatorGrammar)
		Mission.control().get("_lr-translator-grammar");
	return g;
    }

    protected static LRTranslatorGrammar g;
}
