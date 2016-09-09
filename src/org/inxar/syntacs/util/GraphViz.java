/**
 * $Id: GraphViz.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.util;

/**
 * <code>GraphViz</code> instances are used to generate <a href =
 * "http://www.research.att.com/sw/tools/graphviz/">graphviz</a> dot
 * files.  These text files can be converted to a number of formats --
 * notably postscript -- and further converted to images such as PNG.  
 */
public interface GraphViz
{
    /**
     * Returns the name of the graph.
     */ 
    String getName();

    /**
     * The the name of the graph.
     */
    void setName(String name);

    /**
     * Returns <code>true</code> if this is a digraph instance (for
     * input to the <i>dot</i> program, <code>false</code> if it is a
     * non-directed graph (for input to the <i>neato</i> program).
     */
    boolean isDirected();

    /**
     * Setter method for the directed flag.
     */
    void isDirected(boolean value);
    
    /**
     * Sets the comment for the graph.
     */
    GraphViz comment(String text);
    
    /**
     * Adds a new attribute to the graph and returns the graph
     * instance. 
     */
    GraphViz attr(String key, String value);
    
    /**
     * Adds a new <code>Node</code> to the graph and returns it to the
     * caller for further refinement.
     */
    Node node(String name);
    
    /**
     * Adds a new <code>Edge</code> to the graph and returns it to the
     * caller for further refinement.  
     */
    Edge edge(String src, String dst);
    
    /**
     * Adds a new subgraph to the graph and returns it to the caller
     * for further refinement.
     */
    GraphViz subgraph(String name);

    /**
     * A Node is a tuple (name, attrs) where attrs is a list of
     * attributes.
     */
    interface Node
    {
	/**
	 * Returns the given name of the <code>Node</code>.
	 */
	String getName();

	/**
	 * Adds a new attribute to the node and returns the
	 * <code>Edge</code> to the caller such that
	 * <code>StringBuffer</code>-like programming style is
	 * supported.
	 */
	Node attr(String key, String value);
    }

    /**
     * An Edge is a triple (source, destination, attrs) where attrs is
     * a list of attributes.
     */
    interface Edge
    {
	/**
	 * Gets the source node name.
	 */
	String getSrc();

	/**
	 * Gets the destination node name.
	 */
	String getDst();

	/**
	 * Adds a new attribute to the edge and returns the
	 * <code>Node</code> to the caller such that
	 * <code>StringBuffer</code>-like programming style is
	 * supported.  
	 */
	Edge attr(String key, String value);
    }
}












