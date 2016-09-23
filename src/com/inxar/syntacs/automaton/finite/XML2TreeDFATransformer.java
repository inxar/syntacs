/**
 * $Id: XML2TreeDFATransformer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package com.inxar.syntacs.automaton.finite;

import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.automaton.finite.DFA;
import com.inxar.syntacs.util.DOM;

/**
 * <code>XML2TreeDFATransformer</code> is a tool for parsing an XML
 * elements representing dfa state machines and generating a
 * <code>TreeDFA</code>.  The DTD implied for use with this class is
 * "<code>dfa.dtd</code>".
 */
public class XML2TreeDFATransformer {
  private static final boolean DEBUG = false;

  /**
   * Initialize the translator.
   */
  public XML2TreeDFATransformer() {
    hash = new Hashtable();
  }

  public DFA transform(Element element) {
    this.element = element;

    // first lets get all the state nodes
    NodeList states = element.getElementsByTagName("state");
    // see how many
    int len = states.getLength();

    // and make the array
    State[] table = new State[len];

    Element e = null;
    String name = null;
    int output;

    // ok, now iterate each state element and preocess it
    for (int i = 0; i < len; i++) {
      // get each one
      e = (Element) states.item(i);
      // the name
      name = DOM.getString("name", e);
      // setup the output
      output = Token.UNDEF;
      try {
        output = DOM.getInt("output", e);
      } catch (NumberFormatException nfe) {
      }

      // make a new state with null tree and integral output
      table[i] = new State(i, name, output, e);
      // now put the has for faster future lookups
      hash.put(name, table[i]);
    }

    // fine.  We have all the states in.  Now we can go back over
    // it and process the edges
    for (int i = 0; i < len; i++) makeTree(table[i]);

    // done.  Now make the dfa
    return new TreeDFA(table);
  }

  private void makeTree(State state) {
    // first get the list
    NodeList edges = state.elem.getElementsByTagName("edge");
    // see how many
    int len = edges.getLength();
    // make the array
    TreeDFA.Edge[] tree = new TreeDFA.Edge[len];

    Element edge = null;
    String next_name = null;
    State next = null;

    // iterate the list
    for (int i = 0; i < len; i++) {
      // fetch each one
      edge = (Element) edges.item(i);
      // collect its data
      int lo = -1;
      int hi = -1;

      // see if val exists, otw get lo and hi
      try {
        int val = DOM.getInt("val", edge);
        lo = val;
        hi = val;
      } catch (NumberFormatException nfe) {
        lo = DOM.getInt("lo", edge);
        hi = DOM.getInt("hi", edge);
      }
      // now get the next
      next_name = DOM.getString("next", edge);
      // and fetch that state
      next = (State) hash.get(next_name);
      // make sure it exists
      if (next == null)
        throw new RuntimeException(
            " Bad graph linkage.  State "
                + state.name
                + " can't find state "
                + next_name
                + " for edge "
                + lo
                + ","
                + hi);

      // and make the edge
      tree[i] = new TreeDFA.Edge(lo, hi, state.id, next.id, null, null);
    }

    // finally balance the tree
    state.tree = TreeDFA.balance(tree, 0, len);

    // done now.  Drop some references
    state.elem = null;
  }

  Element element;
  Hashtable hash;

  static class State extends TreeDFA.State {
    State(int id, String name, int output, Element elem) {
      // initially make the state with a null tree
      super(null, output);
      // save the stuff
      this.id = id;
      this.name = name;
      this.elem = elem;
    }

    int id;
    String name;
    Element elem;
  }
}
