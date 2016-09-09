/**
 * $Id: XML2RegexpTransformer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * <code>XML2RegexpTransformer</code> is a tool for parsing an XML
 * trees representing regular expressions and generating Regexp
 * objects from them.  The DTD implied for use with this class is
 * "<code>translator.dtd</code>".  
 */
public class XML2RegexpTransformer
{
    private static final boolean DEBUG = false;

    /**
     * Constructs a new <code>XML2RegexpTransformer</code>.
     */ 
    public XML2RegexpTransformer() 
    {
    }
    
    public Regexp transform(Element e)
    {
	Regexp r = null;
        
	// get the name of tag
        String name = e.getTagName();

        // "switch" on the name of the element
        if ("char".equals(name)) 
	    r = new RegexpAtom( DOM.getChar("value", e) );

        else if ("epsilon".equals(name)) 
	    r = new RegexpEpsilon();

        else if ("interval".equals(name)) 
	    r = new RegexpRange( DOM.getChar("lo", e), DOM.getChar("hi", e) );

        else if ("string".equals(name)) 
	    r = Regexp.toConcat( DOM.getString("value", e) );

        else if ("option".equals(name)) 
	    r = new RegexpTerm( Regexp.OPTIONAL, transform(DOM.getFirstChildElement(e)) );

	else if ("closure".equals(name)) 
	    r = new RegexpTerm( Regexp.CLOSURE, transform(DOM.getFirstChildElement(e)) );

        else if ("positive-closure".equals(name)) 
	    r = new RegexpTerm( Regexp.PCLOSURE, transform(DOM.getFirstChildElement(e)) );

        else if ("class".equals(name)) 
	    r = transformCharClass(e);

        else if ("list".equals(name)) 
	    r = transformList(Regexp.CONCAT, e);

        else if ("union".equals(name)) 
	    r = transformList(Regexp.UNION, e);

        else 
	    throw new IllegalArgumentException("Unrecognized tag having name \""+name+"\"");

	if (DEBUG)
	    log().debug()
		.write("in parse(Element): built ")
		.write(r)
		.out();

        return r;
    }

    protected Regexp transformCharClass(Element e)
    {
	// make the class
       	RegexpCharClass cc = new RegexpCharClass();
       	cc.isNegated(DOM.getBoolean("negated", e));
	
	// get all the children
        NodeList list = e.getChildNodes();
        // see how many
        int len = list.getLength();
        // ok, now iterate each state element and preocess it
        for (int i=0; i<len; i++) {
	    Node n = (Node)list.item(i);
	    if (n.getNodeType() != Node.ELEMENT_NODE)
		continue;
	    e = (Element)n;
	    // get the tag name
	    String name = e.getTagName();
	    // switch on the name of the tag.
	    if ("char".equals(name)) {
		char c = DOM.getChar("value", e);
		if (c == '-')
		    cc.hasDash(true);
		else
		    cc.addRegexp( new RegexpAtom(c) );
	    } else {
		// must be an interval element
		cc.addRegexp( new RegexpRange( DOM.getChar("lo", e), DOM.getChar("hi", e) ) );
	    }
        }
        // done.
        return cc;
    }

    protected Regexp transformList(int type, Element e)
    {
       	RegexpList list = new RegexpList(type);
        NodeList nodes = e.getChildNodes();
        int len = nodes.getLength();

        for (int i=0; i<len; i++) {
	    Node n = (Node)nodes.item(i);
	    if (n.getNodeType() != Node.ELEMENT_NODE)
		continue;
	    list.addRegexp( transform((Element)n) );
	}

        return list;
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("x2r", this);
	return log;
    }

    private Log log;
}









