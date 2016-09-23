/**
 * $Id: TreeDFA.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.finite;

import java.util.Vector;
import java.util.Properties;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.automaton.finite.DFA;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import org.inxar.syntacs.util.GraphViz;
import org.inxar.syntacs.util.Vizualizable;
import org.inxar.syntacs.util.Log;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;

/**
 * Concrete <code>DFA</code> implementation which uses tree-based
 * parse tables.  The <code>TreeDFA</code> data structure is a
 * flexible general purpose DFA representation that is fast to compile
 * and memory conscious but slower than it could be when used by a
 * Lexer (binary search versus array indexing). Thus, TreeDFA and its
 * inner classes are used often in construction and transformation
 * algorithms, but generally "burned" to a MesoArrayDFA when used by a
 * Lexer.
 */
public class TreeDFA
    implements DFA, Vizualizable
{
    private static final boolean DEBUG = false;

    /**
     * Constructs the <code>TreeDFA</code> from the given
     * <code>State</code> table.
     */
    public TreeDFA(State[] table)
    {
	this.table = table;
    }

    public int go(int state, int input)
    {
	// fetch the root edge tree node
	Edge edge = table[state].tree;

	// binary search the tree for
	// an inclusive match
	while (true) {
	    if (edge == null) {

		if (DEBUG)
		    log().debug()
			.write("got null")
			.out();

		return DFA.DEAD_STATE;

	    } else if (input < edge.lo) {

		if (DEBUG)
		    log().debug()
			.write("going left on input ").write(input)
			.write(", edge.lo ").write(edge.lo)
			.out();

		edge = edge.left; continue;

	    } else if (input > edge.hi) {

		if (DEBUG)
		    log().debug()
			.write("going right on input ").write(input)
			.write(", edge.hi ").write(edge.hi)
			.out();

		edge = edge.right; continue;

	    } else {
		return edge.next;
	    }
	}
    }

    public int output(int state)
    {
	return table[state].output;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();

    	table_toBuffer(b);
    	tokens_toBuffer(b);

        return b.toString();
    }

    protected void table_toBuffer(StringBuffer b)
    {
        // print header information
        b.append("================================= begin ==").append(StringTools.NEWLINE);
        b.append("DFA STATE SUMMARY").append(StringTools.NEWLINE);
        b.append("> state: input = next_state").append(StringTools.NEWLINE);
        b.append("> total states: ").append(table.length).append(StringTools.NEWLINE);

        // iterate table of states
        for (int i=0; i<table.length; i++) {
	    b.append("> ").append(i).append(": ").append(StringTools.NEWLINE);
	    table[i].toBuffer(b);
        }
        b.append("=================================== end ==").append(StringTools.NEWLINE);
    }

    protected void tokens_toBuffer(StringBuffer b)
    {
        // print header information
        b.append("================================= begin ==").append(StringTools.NEWLINE);
        b.append("ACCEPTING STATE SUMMARY").append(StringTools.NEWLINE);
        b.append("> state: token_type_id").append(StringTools.NEWLINE);
        // walk the states
        for (int i=0; i<table.length; i++) {
	    // see if its boring
	    if (table[i].output == Token.UNDEF)
		continue;

	    b.append("> ");
	    if (i < 10)
		b.append(' ');
	    b.append(i).append(": ").append(table[i].output).append("\n");
        }
        b.append("=================================== end ==").append(StringTools.NEWLINE);
    }

    public void vizualize(GraphViz dot)
    {
	LRTranslatorGrammar g = (LRTranslatorGrammar)
	    Mission.control().get("_lr-translator-grammar");

	String size, label, rankdir, nc, ns, ec, es;
	GraphViz.Node node;
	GraphViz.Edge edge;

	rankdir = Mission.control().getString
	    ("viz-dfa-rankdir", "LR");
	nc = Mission.control().getString
	    ("viz-dfa-node-color", "black");
	ns = Mission.control().getString
	    ("viz-dfa-node-shape", "circle");
	ec = Mission.control().getString
	    ("viz-dfa-edge-color", "steelblue1");
	es = Mission.control().getString
	    ("viz-dfa-edge-style", "solid");

	size = Mission.control().getString("viz-dfa-size");
	if (size != null)
	    dot.attr("size", size);

	if (Mission.control().isTrue("viz-dfa-concentrate-edges"))
	    dot.attr("concentrate", "true");

	dot.attr("rankdir", rankdir);
	node = dot.node("node");
	node.attr("color", nc);
	node.attr("shape", ns);

	node = dot.node("k0");
	node.attr("style", "invis");

	edge = dot.edge("k0", "s"+1);
	edge.attr("label", "start");

	node = dot.node("edge");
	node.attr("color", ec);
	node.attr("style", es);

        for (int i = 1;  i < table.length; i++)
	    table[i].vizualize(dot);
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("tfa", this); // Tree Finite Automata
	return log;
    }

    Log log;
    State[] table;

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------

    /**
     * The <code>TreeDFA.State</code> class is models a single state
     * as tuple <code>(output, edge_tree)</code> where
     * <code>output</code> is an integer which records the accepting
     * NFA state and <code>edge_tree</code> holds the outgoing edges
     * of the state tree.
     */
    public static class State
    {
	/**
	 * Constructs the <code>State</code> on the given
	 * <code>Edge</code> tree and <code>int</code> output.
	 */
	public State(Edge tree, int output)
	{
	    this.tree = tree;
	    this.output = output;
	}

	public void toBuffer(StringBuffer b)
	{
            toBuffer(b, tree);
	}

	public void toBuffer(StringBuffer b, Edge edge)
	{
            if (edge == null)
            	return;

            toBuffer(b, edge.left);
            edge.toBuffer(b);
            toBuffer(b, edge.right);
	}

	public void vizualize(GraphViz dot)
	{
            vizualize(dot, tree);
	}

	public void vizualize(GraphViz dot, Edge edge)
	{
            if (edge == null)
            	return;

            vizualize(dot, edge.left);
            edge.vizualize(dot);
            vizualize(dot, edge.right);
	}

	public int output;
        public Edge tree;
    }

    // ------------------------------------------------------------------------
    // Edge
    // ------------------------------------------------------------------------
    /**
     * The <code>TreeDFA.Edge</code> class is models a single edge as
     * a node in an binary interval tree.  Each edge records the upper
     * and lower boundaries of the unicode range which it covers as
     * well as the next state.
     */
    public static class Edge
    {
	/**
	 * Constructs the Edge with the given hi and lo rangepoints,
	 * the next and "previous" state, and the left and right
	 * binary tree children.  The "previous" state number
	 * corresponds to the number of the State which the edge
	 * originates from.
	 */
	public Edge(int lo, int hi, int prev, int next, Edge left, Edge right)
	{
	    this.lo = lo;
	    this.hi = hi;
	    this.prev = prev;
	    this.next = next;
	    this.left = left;
	    this.right = right;
	}

	/**
	 * Makes an edge suitable for a dead-end state (though not
	 * necessarily *the* dead state).
         */
	public Edge(int prev)
	{
	    this(Character.MIN_VALUE, Character.MAX_VALUE, prev, DFA.DEAD_STATE, null, null);
	}

	public void toBuffer(StringBuffer b)
	{
            if (lo < 0) {
            	b.append("[ ACCEPT "+(-lo)+" ]").append(StringTools.NEWLINE);
            	return;
            }

            if (lo == hi)
            	b.append("[ ("+lo+") -> "+next+" ]").append(StringTools.NEWLINE);
            else
            	b.append("[ ("+lo+","+hi+") -> "+next+" ]").append(StringTools.NEWLINE);
	}

	public void vizualize(GraphViz dot)
	{
            if (lo < 0) {
		LRTranslatorGrammar g = (LRTranslatorGrammar)
		    Mission.control().get("_lr-translator-grammar");

		String lec = Mission.control().getString
		    ("viz-dfa-label-edge-color", "black");
		String les = Mission.control().getString
		    ("viz-dfa-label-edge-style", "dotted");

		GraphViz.Node node1 = dot.node("s"+prev);
		node1.attr("shape", "doublecircle");

		GraphViz.Node node2 = dot.node("a"+prev);
		node2.attr("shape", "plaintext");
		node2.attr("label", g.getTerminal((-lo)));

		GraphViz.Edge edge = dot.edge("s"+prev, "a"+prev);
		edge.attr("dir", "none");
		edge.attr("color", lec);
		edge.attr("style", les);

            	return;
            }

	    GraphViz.Edge edge = dot.edge("s"+prev, "s"+next);

            if (lo == hi)
		edge.attr("label", str(lo));
            else
		edge.attr("label", "["+str(lo)+"-"+str(hi)+"]");

	}

	private String str(int val)
	{
	    switch (val) {
	    case '"':
	    case '\\':
		return String.valueOf(val);
	    default:
		return val > 0x20 && val <= 0xFF
		    ? String.valueOf((char)val)
		    : String.valueOf(val);
	    }
	}


	/**
	 * The inclusive hi endpoint of the Unicode interval this
	 * <code>Edge</code> covers.
	 */
	public int lo;

	/**
	 * The inclusive lo endpoint of the Unicode interval this
	 * <code>Edge</code> covers.
	 */
	public int hi;

	/**
	 * The number of the <code>State</code> to which the
	 * <code>Edge</code> is rooted.
	 */
	public int prev;

	/**
	 * The number of the <code>State</code> to which this
	 * <code>Edge</code> traverses.
	 */
	public int next;

	/**
	 * The left binary tree child (less than).
	 */
	public Edge left;

	/**
	 * The right binary tree child (greater than).
	 */
	public Edge right;

	/**
	 * The number of the <code>Token</code> which is returned by
	 * the <code>output(int state)</code> method.
	 */
	public int id;
    }

    /**
     * Given a (possibly unbalanced) binary edge tree, balance the
     * tree such that a Red-Black tree would be pleased with the
     * output.
     */
    public static Edge balance(Edge tree)
    {
	// first we make an array out of it.
	Vector v = new Vector();

	// put all the nodes doing an in-order traversal
	inOrderDump(tree, v);

	// make an array
	Edge[] edges = new Edge[v.size()];

	// iterate vector
	for (int i=0; i<v.size(); i++)
            edges[i] = (Edge)v.elementAt(i);

        // and rebuild the tree
        return balance(edges, 0, edges.length);
    }

    /**
     * Recursively flatten the given <code>Edge</code> tree into the
     * given <code>Vector</code>.
     */
    public static void inOrderDump(Edge tree, Vector v)
    {
        if (tree == null)
	    return;

        inOrderDump(tree.left, v);
        v.addElement(tree);
        inOrderDump(tree.right, v);
    }

    /**
     * Recursively print the tree in-order.
     */
    public static void toStringInOrder(Edge tree, StringBuffer b, int level)
    {
	if (tree == null)
	    return;

	level++;
	toStringInOrder(tree.left, b, level);

	for (int i=0; i<level; i++)
	    b.append('-');
        b.append("("+tree.lo+","+tree.hi+","+tree.next+")").append(StringTools.NEWLINE);

	toStringInOrder(tree.right, b, level);
	level--;
    }

    /**
     * Recursively balance the section of the Edge array specified.
     */
    public static Edge balance(Edge[] edges, int off, int len)
    {
	if (len == 0)
	    return null;

	int mid  = off + len / 2;      	// idx middle segment: the integral midpoint
	int loff = off;                	// offset of the left segment
	int llen = mid - loff;        	// distance to the mid segment
	int roff = mid + 1;          	// offset of right segment
	int rlen = off + len - roff; 	// distance from the roff to the end

	edges[mid].left =  balance(edges, loff, llen);
	edges[mid].right = balance(edges, roff, rlen);

	return edges[mid];
    }
}
