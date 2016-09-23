/**
 * $Id: ArrayDPAConstructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.pushdown;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import org.inxar.syntacs.grammar.context_free.ContextFreeSet;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import org.inxar.syntacs.automaton.pushdown.Action;
import org.inxar.syntacs.automaton.pushdown.DPA;
import org.inxar.syntacs.automaton.pushdown.DPAConstructor;
import org.inxar.syntacs.automaton.pushdown.AmbiguityException;
import org.inxar.syntacs.util.Log;
import org.inxar.syntacs.util.IntIterator;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.Vizualizable;
import org.inxar.syntacs.util.GraphViz;
import org.inxar.syntacs.util.AlgorithmException;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;


/**
 * Concrete implementation of <code>DPAConstructor</code> which
 * builds an <code>ArrayDFA</code>. This class handles state
 * management and parse table encoding functionality and is typically
 * employed compositionally by other "higher-level" LR construction
 * algorithms.  Thus, this is one of the inner layers of the onion.
 */
public class ArrayDPAConstructor
    implements DPAConstructor, Vizualizable
{
    private static final boolean DEBUG = false;
    private static final boolean WARNING = true;

    //==================================================================
    // construction and setup methods
    //==================================================================

    public ArrayDPAConstructor(ContextFreeSet grammar)
    {
	this.grammar = grammar;
	this.actions = new Vector();
    	this.action = new int[13][];
    	this.go = new int[7][];
	this.actionMap = new HashMap();
	// save error to the actions container to make sure we reserve
	// spot zero
    	this.actions.addElement(ArrayDPA.ERROR);
    }

    // ================================================================
    // action management methods
    // ================================================================

    public void shift(int state, int symbol, int nextState)
	throws AmbiguityException
    {
    	Action action = getAction(state, symbol);
    	if (action.getType() == Action.ERROR)
	    allocAction(state, symbol, Action.SHIFT, nextState);
	else
	    if (action.getType() != Action.SHIFT && action.getValue() != nextState)
		conflict(state, symbol, Action.SHIFT, nextState, action);
    }

    public void reduce(int state, int symbol, int production)
	throws AmbiguityException
    {
    	Action action = getAction(state, symbol);
    	if (action.getType() == Action.ERROR)
	    allocAction(state, symbol, Action.REDUCE, production);
	else
	    if (action.getType() != Action.REDUCE && action.getValue() != production)
		conflict(state, symbol, Action.REDUCE, production, action);
    }

    public void accept(int state, int symbol)
	throws AmbiguityException
    {
    	Action action = getAction(state, symbol);
    	if (action.getType() == Action.ERROR)
	    allocAction(state, symbol, Action.ACCEPT, 0);
	else
	    if (action.getType() != Action.ACCEPT)
		conflict(state, symbol, Action.ACCEPT, 0, action);
    }

    protected void conflict(int state, int symbol, int type, int register, Action action)
    {
	if (DEBUG)
	    log().debug()
		.write("conflict: ")
		.write(action.getType())
		.write(", ")
		.write(type)
		.out();

	StringBuffer b = new StringBuffer();

	// we want to figure out what kind of conflict this is.  There
	// are many permutations.

	if (action.getType() == type) {

	    // PART 1: check for SHIFT::SHIFT and REDUCE::REDUCE conflicts
	    switch (type) {
	    // SHIFT::SHIFT conflict
	    case Action.SHIFT:
		{
		    b.append("SHIFT::SHIFT conflict detected at state ")
			.append(state)
			.append(".  ")
			.append("Cannot decide whether to SHIFT and move to state ")
			.append(action.getValue())
			.append(" or SHIFT and move to state ")
			.append(register)
			.append(" upon seeing input symbol ")
			.append(grammar.getGrammarSymbol(symbol))
			.append(".");
		    break;
		}
	    // REDUCE::REDUCE conflict
	    case Action.REDUCE:
		{
		    b.append("REDUCE::REDUCE conflict detected at state ")
			.append(state)
			.append(".  ")
			.append("Cannot decide whether to REDUCE ")
			.append(grammar.getProduction(action.getValue()))
			.append(" or to REDUCE ")
			.append(grammar.getProduction(register))
			.append(" upon seeing input symbol ")
			.append(grammar.getGrammarSymbol(symbol))
			.append(".");
		    break;
		}
	    default:
		throw new InternalError("Unexpected conflict combination.");
	    }

	} else {

	    // PART 2: check for SHIFT::REDUCE, REDUCE::SHIFT,
	    // SHIFT::ACCEPT, ACCEPT::SHIFT, REDUCE::ACCEPT,
	    // ACCEPT::REDUCE conflicts
	    switch (action.getType()) {
		// SHIFT::* conflicts
	    case Action.SHIFT:
		{
		    switch (type) {
		    case Action.REDUCE:
			{
			    b.append("SHIFT::REDUCE conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to SHIFT and move to state ")
				.append(action.getValue())
				.append(" or to REDUCE ")
				.append(grammar.getProduction(register))
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    case Action.ACCEPT:
			{
			    b.append("SHIFT::ACCEPT conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to SHIFT move to state ")
				.append(action.getValue())
				.append(" or ACCEPT")
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    default:
			throw new InternalError("Unexpected conflict combination.");
		    }
		    break;
		}
	    case Action.REDUCE:
		{
		    switch (type) {
		    case Action.SHIFT:
			{
			    b.append("REDUCE::SHIFT conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to REDUCE ")
				.append(grammar.getProduction(action.getValue()))
				.append(" or to SHIFT and move to state ")
				.append(register)
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    case Action.ACCEPT:
			{
			    b.append("REDUCE::ACCEPT conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to REDUCE ")
				.append(grammar.getProduction(action.getValue()))
				.append(" or ACCEPT")
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    default:
			throw new InternalError("Unexpected conflict combination.");
		    }
		    break;
		}
	    case Action.ACCEPT:
		{
		    switch (type) {
		    case Action.SHIFT:
			{
			    b.append("ACCEPT::SHIFT conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to ACCEPT or to SHIFT and move to state ")
				.append(register)
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    case Action.REDUCE:
			{
			    b.append("ACCEPT::REDUCE conflict detected at state ")
				.append(state)
				.append(".  ")
				.append("Cannot decide whether to ACCEPT or REDUCE ")
				.append(grammar.getProduction(register))
				.append(" upon seeing input symbol ")
				.append(grammar.getGrammarSymbol(symbol))
				.append(".");
			    break;
			}
		    default:
			throw new InternalError("Unexpected conflict combination.");
		    }
		    break;
		}
	    default:
		throw new InternalError("Unexpected conflict combination.");
	    }
	}

	b.append("  Recovered by choosing the first option.");
	if (WARNING)
	    log.warn()
		.write( b.toString() )
		.out();
    }

    protected void allocAction(int state, int symbol, int type, int register)
    {
    	// make a new action
    	Action a = new StandardAction(type, register);

	// Check if this action is already in the map.
	Integer index = (Integer)actionMap.get(a);

	if (index == null) {
	    index = new Integer(actions.size());
	    actionMap.put(a, index);
	    actions.addElement(a);
	} else {
	}

	// make sure the 2d array is big enough for these dimensions
	action = ensureSize(state, symbol, action);
	// set it there
        action[state][symbol] = index.intValue();
    }

    protected Action getAction(int state, int symbol)
    {
	action = ensureSize(state, symbol, action);

    	if (action[state][symbol] == 0) {
	    return ArrayDPA.ERROR;
    	} else {
	    return (Action)actions.elementAt(action[state][symbol]);
    	}
    }

    /*
     * Do we need to check for goto/goto conflicts?  Currently we are
     * not.
     */
    public void go(int state, int symbol, int nextState)
	throws AmbiguityException
    {
	// make sure goto table is big enough for this coordinate
	go = ensureSize(state, symbol, go);
	// put the nextState there
        go[state][symbol] = nextState;
    }

    // ================================================================
    // DPA constructor routines
    // ================================================================

    public DPA construct(ContextFreeSet grammar) throws AlgorithmException
    {
	this.grammar = grammar;

    	// make a new ArrayDPA
    	return new ArrayDPA( action, go, getActions() );
    }

    protected Action[] getActions()
    {
        // make a new int array to fit all productions
        Action[] _actions = new StandardAction[ actions.size() ];

        // iterate them
        for (int i=0; i<actions.size(); i++)
	    _actions[i] = (Action)actions.elementAt(i);

        // done.
        return _actions;
    }

    // ================================================================
    // general reporting methods
    // ================================================================

    protected String show(IntSet set)
    {
	StringBuffer buf = new StringBuffer();
	IntIterator iter = set.iterator();
	while ( iter.hasNext() )
	    buf.append(grammar.getItem(iter.next())).append(StringTools.NEWLINE);
	return buf.toString();
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
    	showActionTable(buf);
    	showGotoTable(buf);
    	showActions(buf);
    	return buf.toString();
    }

    protected void showActionTable(StringBuffer buf)
    {
        buf.append(StringTools.NEWLINE)
	    .append("Parse table is as follows")
	    .append(StringTools.NEWLINE);

        Action a = null;

        for (int i=0; i < action.length; i++) {
            if (action[i] == null)
            	continue;

	    buf.append(StringTools.NEWLINE);

	    for (int j = 0; j < action[i].length; j++) {
		a = getAction(i, j);
		if (a == null || a.getType() == Action.ERROR)
		    continue;

		buf.append('[').append(i).append(",");
		try {
		    buf.append(grammar.getGrammarSymbol(j).toString()+":"+j);
		} catch (ArrayIndexOutOfBoundsException aioobex) {
		    buf.append("x");
		}
		buf.append("]: ").append(a).append(StringTools.NEWLINE);
	    }
        }
    }

    protected void showGotoTable(StringBuffer buf)
    {
        buf.append(StringTools.NEWLINE)
	    .append("Goto table is as follows")
	    .append(StringTools.NEWLINE);

        for (int i=0; i < go.length; i++) {
            if (go[i] == null)
            	continue;

	    buf.append(StringTools.NEWLINE);

	    for (int j = 0; j < go[i].length; j++) {

		// no need to view error transitions
		if (go[i][j] == 0)
		    continue;

		buf.append('[').append(i).append(",");
		try {
		    buf.append(grammar.getGrammarSymbol(j)+":"+j);
		} catch (ArrayIndexOutOfBoundsException aioobex) {
		    buf.append("x");
		}
		buf.append("]: ").append(go[i][j]).append(StringTools.NEWLINE);
	    }
        }
    }

    protected void showActions(StringBuffer buf)
    {
        buf.append(StringTools.NEWLINE)
	    .append("Actions array is as follows")
	    .append(StringTools.NEWLINE);

        for (int i=0; i < actions.size(); i++) {
	    buf.append(StringTools.NEWLINE).append('[').append(i).append("]: ")
		.append(actions.elementAt(i));
        }
    }

    // ================================================================
    // instance fields
    // ================================================================
    public void vizualize(GraphViz dot)
    {
	LRTranslatorGrammar g = (LRTranslatorGrammar)
	    Mission.control().get("_lr-translator-grammar");

	GraphViz.Node node;
	GraphViz.Edge edge;
	String size, rankdir, label, nc, ns, ec, es;

	rankdir = Mission.control().getString
	    ("viz-dpa-rankdir", "LR");
	nc = Mission.control().getString
	    ("viz-dpa-node-color", "black");
	ns = Mission.control().getString
	    ("viz-dpa-node-shape", "circle");

	size = Mission.control().getString("viz-dpa-size");
	if (size != null)
	    dot.attr("size", size);

	if (Mission.control().isTrue("viz-dpa-concentrate-edges"))
	    dot.attr("concentrate", "true");

	dot.attr("rankdir", rankdir);
	node = dot.node("node");
	node.attr("shape", ns);
	node.attr("color", nc);

	node = dot.node("k0");
	node.attr("style", "invis");

	edge = dot.edge("k0", "s"+0);
	edge.attr("label", "start");

	if (Mission.control().isNotTrue("viz-dpa-hide-terminal-edges")) {

	    ec = Mission.control().getString
		("viz-dpa-terminal-edge-color", "steelblue1");
	    es = Mission.control().getString
		("viz-dpa-terminal-edge-style", "solid");

	    node = dot.node("edge");
	    node.attr("color", ec);
	    node.attr("style", es);

	outer:
	    for (int i = 0; i < action.length; i++) {
		if (action[i] == null)
		    continue outer;

	    inner:
		for (int j = 0; j < action[i].length; j++) {
		    if (action[i][j] == 0)
			continue inner;

		    Action a =  (Action)actions.elementAt(action[i][j]);

		    switch (a.getType()) {
		    case Action.SHIFT:
			edge = dot.edge("s"+i, "s"+a.getValue());
			label = g.getTerminal(j);
			if (label == null)
			    label = g.getNonTerminal(j);
			edge.attr("label", label);
			break;

		    case Action.REDUCE:
			break;

		    case Action.ACCEPT:
			node = dot.node("s"+i);
			node.attr("shape", "doublecircle");
			break;

		    default:
			throw new InternalError();
		    }
		}
	    }
	}

	if (Mission.control().isNotTrue("viz-dpa-hide-nonterminal-edges")) {

	    ec = Mission.control().getString
		("viz-dpa-nonterminal-edge-color", "royalblue");

	    es = Mission.control().getString
		("viz-dpa-nonterminal-edge-style", "solid");

	    node = dot.node("edge");
	    node.attr("color", ec);
	    node.attr("style", es);

	outer:
	    for (int i = 0; i < go.length; i++) {
		if (go[i] == null)
		    continue outer;
	    inner:
		for (int j = 0; j < go[i].length; j++) {
		    if (go[i][j] == 0)
			continue inner;

		    edge = dot.edge("s"+i, "s"+go[i][j]);
		    label = g.getNonTerminal(j);
		    edge.attr("label", label);
		}
	    }
	}
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("apc", this); // Array Pushdown Constructor
	return log;
    }

    // ================================================================
    // instance fields
    // ================================================================

    protected ContextFreeSet grammar;
    protected Vector actions;
    protected Map actionMap;
    protected int[][] action;
    protected int[][] go;
    private Log log;

    // ================================================================
    // some static utility methods which do 2D array size management
    // ================================================================

    protected static int[][] ensureSize(int dim1, int dim2, int[][] aa)
    {
        if (dim1 >= aa.length)
	    aa = grow2(dim1, aa);

        if (aa[dim1] == null)
	    aa[dim1] = new int[dim2+1];

        if (dim2 >= aa[dim1].length)
	    aa[dim1] = grow(dim2, aa[dim1]);

        return aa;
    }

    protected static int[][] grow2(int len, int[][] src)
    {
	int factor = len / src.length;
	int[][] dst = new int[src.length * 2 * factor][];
	System.arraycopy(src, 0, dst, 0, src.length);
	return dst;
    }

    protected static int[] grow(int len, int[] src)
    {
	int factor = len / src.length;
	int[] dst = new int[src.length * 2 * factor];
	System.arraycopy(src, 0, dst, 0, src.length);
	return dst;
    }
}
