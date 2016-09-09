/**
 * $Id: DOM.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.util;

import java.util.Iterator;
import java.util.Vector;
import java.lang.reflect.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;

/**
 * Collection of utilities useful for working with <code>XML
 * DOM</code> trees.  
 */
public class DOM
{
    /**
     * Static utility class only.
     */
    private DOM() {}

    public static Element getRoot(String uri, boolean validate) throws Exception
    {
	return getDocument(uri, validate).getDocumentElement();
    }

    /**
     * Attempts to instantiate an XML document from the given URI
     * using three different parsers.  The first attempt is for Sun's
     * JAXP-1.0.1's
     * <code>javax.xml.parsers.DocumentBuilderFactory</code>, the
     * second attempt is for Xerces-1.2.3's
     * <code>org.apache.xerces.parsers.DOMParser</code>, and the third
     * attempt is for Sun's xml-tr2's
     * <code>com.sun.xml.tree.XmlDocument</code>.  If all attempts
     * fail, a <code>ClassNotFoundException</code> will be thrown.
     */
    public static Document getDocument(String uri, boolean validate) 
	throws ClassNotFoundException
    {
	Document doc = null;
	Vector exceptions = null;

	try {

	    // See if the JAXP parser is in the classpath
	    Class dbfClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
	    Class dbClass = Class.forName("javax.xml.parsers.DocumentBuilder");

	    // Fetch the newinstance, setValidating, and
	    // newDocumentBuilder methods from the doc builder factory
	    // class as well as the parse method from the document
	    // builder class..
	    Method newInstance = 
		dbfClass.getMethod("newInstance", null);

	    Method setValidating = 
		dbfClass.getMethod("setValidating", new Class[]{ boolean.class });

	    Method newDocumentBuilder = 
		dbfClass.getMethod("newDocumentBuilder", null);

	    Method parse = 
		dbClass.getMethod("parse", new Class[]{ String.class });

	    // Reflect the document builder object
	    Object dbfObj = newInstance.invoke(null, null);

	    // Make the factory validating
	    setValidating.invoke(dbfObj, new Object[]{ Boolean.TRUE });

	    // And get a parser
	    Object dbObj = newDocumentBuilder.invoke(dbfObj, null);

	    // Finally, parse the document
	    doc = (Document)parse.invoke(dbObj, new Object[]{ uri });

	} catch (ClassNotFoundException cnfex) {
	    if (exceptions == null)
		exceptions = new Vector();
	    exceptions.addElement(cnfex);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	// Were we successful?
	if (doc != null)
	    return doc;

	try {

	    // See if the Xerces parser is in the classpath
	    Class c = Class.forName("org.apache.xerces.parsers.DOMParser");

	    // Get the parse and getDocument methods from the class.
	    Method parse = 
		c.getMethod("parse", new Class[]{ String.class });

	    Method getDocument = 
		c.getMethod("getDocument", null);

	    // Create a new parseR
	    Object parser = c.newInstance();

	    // Call the parse method.
	    parse.invoke(parser, new Object[]{ uri });
	    
	    // Call the getDocument method to get the Document.
	    doc = (Document)getDocument.invoke(parser, null);

	} catch (ClassNotFoundException cnfex) {
	    if (exceptions == null)
		exceptions = new Vector();
	    exceptions.addElement(cnfex);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	
	// Again, was this successful?
	if (doc != null)
	    return doc;

	try {

	    // One last try.  See if Sun's xml-tr2 package is in the
	    // classpath.
	    Class c = Class.forName("com.sun.xml.tree.XmlDocument");

	    // Fetch the createXmlDocument method
	    Method createXmlDocument = 
		c.getMethod("createXmlDocument", new Class[]{ String.class, boolean.class });
	    
	    // Make new document, validating.
	    doc = (Document)createXmlDocument.invoke(null, new Object[]{ uri, new Boolean(validate) });

	} catch (ClassNotFoundException cnfex) {
	    if (exceptions == null)
		exceptions = new Vector();
	    exceptions.addElement(cnfex);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}


	if (doc != null) {
	    return doc;
	} else {

	    String msg = null;

	    if (exceptions != null) {
		StringBuffer b = 
		    new StringBuffer("I was Unable to locate appropriate XML parser " +
				     "OR I was unable to correctly instantiate the Document " + 
				     "(in which case you should refer to the stack trace). " + 
				     "Attempted reflection of:  ");
		
		for (int i = 0; i < exceptions.size(); i++) {
		    if (i > 0) b.append("; ");
		    b.append( ((Exception)exceptions.elementAt(i)).getMessage() );
		}

		msg = b.toString();

	    } else {

		msg = "An exception occurred while instantiating the XML document.  "+
		    "Please refer to the stack trace for details.";
	    }

	    // throw final exception
	    throw new ClassNotFoundException(msg);
	}
    }

    /**
     * Fetches the first child that is an <code>Element</code>.
    **/
    public static Element getFirstChildElement(Element e)
    {
	NodeList list = e.getChildNodes();
	for (int i = 0; i < list.getLength(); i++) {
	    Node n = (Node)list.item(i);
	    if (n.getNodeType() == Node.ELEMENT_NODE)
		return (Element)n;
	}
	return null;
    }

    public static String getText(Element e)
    {
	// NOTE: I was not aware that normalization eliminates newline
	// characters, which I want to preserve.  Therefore, getText
	// will not normalize.

	//e.normalize();

	NodeList nodes = e.getChildNodes();
	int len = nodes.getLength();
	
	if (len == 0) 
	    throw new IllegalArgumentException
		("The element " + e + 
		 " does not have any child nodes.");

	for (int i = 0; i < len; i++) {
	    Node node = nodes.item(i);
	    if (node.getNodeType() == Node.TEXT_NODE)
		return ((org.w3c.dom.Text)node).getData();		
	}

	throw new IllegalArgumentException
	    ("The element " + e + 
	     " does not have any text nodes.");
    }

    public static String getString(String name, Element e)
    {
	return e.getAttribute(name);
    }
    public static boolean getBoolean(String name, Element e)
    {
	String s = e.getAttribute(name);
	return Boolean.valueOf(s).booleanValue();
    }
    public static char getChar(String name, Element e)
    {
	String s = e.getAttribute(name);
	return s.charAt(0);
    }
    public static byte getByte(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Byte.valueOf(s).byteValue();
    }
    public static short getShort(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Short.valueOf(s).shortValue();
    }
    public static int getInt(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Integer.valueOf(s).intValue();
    }
    public static long getLong(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Long.valueOf(s).longValue();
    }
    public static float getFloat(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Float.valueOf(s).floatValue();
    }
    public static double getDouble(String name, Element e) throws NumberFormatException
    {
	String s = e.getAttribute(name);
	return Double.valueOf(s).doubleValue();
    }

    /**
     * Returns a tokeinzer over a set of comma-delimited strings.
     */
    public static java.util.StringTokenizer getStrings(String name, Element e)
    {
	return new java.util.StringTokenizer(e.getAttribute(name), ",");
    }

    /**
     * Modifies the StringBuffer argument such that any '&lt;',
     * '&gt;', '&amp;' characters are escaped by their entity
     * reference counterparts.  
     */
    public static void escape(StringBuffer b)
    {
        int len = b.length();
        for (int i=0; i<len; i++) {
            switch (b.charAt(i)) {
	    case '<': {
		b.deleteCharAt(i);
		b.insert(i, LT);
		break;
	    }

	    case '>': {
		b.deleteCharAt(i);
		b.insert(i, GT);
		break;
	    }

	    case '&': {
		b.deleteCharAt(i);
		b.insert(i, AMP);
		break;
	    }
            }
        }
    }

    public static Element get(int index, NodeList nodes)
    {
	return (Element)nodes.item(index);
    }

    public static Iterator getElements(String name, Element e)
    {
	return new NodeListIterator( e.getElementsByTagName(name) );
    }

    private static final char[] LT = new char[] { '&', 'l', 't', ';' };
    private static final char[] GT = new char[] { '&', 'g', 't', ';' };
    private static final char[] AMP = new char[] { '&', 'a', 'm', 'p', ';' };

    private static class NodeListIterator
	implements Iterator
    {
	NodeListIterator(NodeList nodes)
	{
	    this.nodes = nodes;
	    this.length = nodes == null ? 0 : nodes.getLength();
	    this.index = 0;
	}

	public boolean hasNext()
	{
	    return index < length;
	}

	public Object next()
	{
	    return nodes.item(index++);
	}

	public void remove()
	{
	    throw new UnsupportedOperationException();
	}

	private NodeList nodes;
	private int length;
	private int index;
    }
}

