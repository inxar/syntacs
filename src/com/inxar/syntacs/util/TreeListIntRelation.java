/**
 * $Id: TreeListIntRelation.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>IntRelation</code> which uses
 * as a binary tree such that <code>ListIntSet</code> instances are
 * implicitly created at each tree node.  
 */
public class TreeListIntRelation
    implements IntRelation
{
    /**
     * Constructs the <code>TreeListIntRelation</code>.
     */
    public TreeListIntRelation()
    {
	this.keys = new ListIntSet();
    }

    public IntSet keys()
    {
	return new ImmutableIntSet(keys);
    }

    public boolean isEmpty()
    {
	return keys.size() == 0;
    }

    public void put(int key, int val)
    {
	fetch(key).set.put(val);
    }

    public void set(int key, IntSet set)
    {
	fetch(key).set = set;
	hasChanged = true;
    }

    public IntSet get(int key)
    {
	return keys.contains(key) ? fetch(key).set : null;
    }

    private Node fetch(int key)
    {
	if (root == null) {
	    hasChanged = true;
	    keys.put(key);
	    root = new Node(key);
	    return root;
	}

	return fetch(key, root);
    }

    private Node fetch(int key, Node node)
    {
	if (node.key == key)
	    return node;

	if (node.key < key) {
	    if (node.left == null) {
		hasChanged = true;
		keys.put(key);
		node.left = new Node(key);
		return node.left;
	    } else {
		return fetch(key, node.left);
	    }
	} else {
	    if (node.right == null) {
		hasChanged = true;
		keys.put(key);
		node.right = new Node(key);
		return node.right;
	    } else {
		return fetch(key, node.right);
	    }
	}
    }

    public synchronized Reiterator reiterator()
    {
	if (hasChanged) {
	    list = null;
	    reiterate(root);
	    hasChanged = false;
	}

	return new Linkerator(list);
    }

    private void reiterate(Node node)
    {
	if (node == null)
	    return;
	
	reiterate(node.left);
	list = new Link(node, list);
	reiterate(node.right);
    }
    
    private IntSet keys;
    private Node root;
    private Link list;
    private boolean hasChanged;

    private static class Node
    {
    	Node(int key)
    	{
	    this.key = key;
	    this.set = new ListIntSet();
    	}

	int key;
	IntSet set;
    	Node left;
    	Node right;
    }

    private static class Link
    {
	Link(Node node, Link next)
	{
	    this.node = node;
	    this.next = next;
	}

	Node node;
	Link next;
    }

    private static class Linkerator implements Reiterator
    {
    	Linkerator(Link root)
    	{
	    this.root = root;
    	}

	public boolean hasNext()
	{
	    return root != null;
	}

	public void next()
	{
	    root = root.next;
	}

	public int key()
	{
	    return root.node.key;
	}

	public IntSet values()
	{
	    return root.node.set;
	}

	Link root;
    }


}



