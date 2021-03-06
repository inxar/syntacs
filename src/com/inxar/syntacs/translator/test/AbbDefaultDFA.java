/**
 * Copyright (C) 2016 Paul Cody Johnston - pcj@inxar.org
 * @author Paul Cody Johnston - pcj@inxar.org
 */
package com.inxar.syntacs.translator.test;

import com.inxar.syntacs.automaton.finite.MesoArrayDFA;
import com.inxar.syntacs.util.Pickler;

/**
 * Automatically generated by <a href='http://www.inxar.org/syntacs'>Syntacs
 *  Translation Toolkit</a> on Tue Oct 11 20:47:25 MDT 2016
 */
class AbbDefaultDFA
extends MesoArrayDFA
{
    private static final int[][] _table;
    private static final int[] _accepts;
    
    AbbDefaultDFA()
    {
        super(_table, _accepts);
    }
    
    static {
        _table = Pickler.unpickle2D(" % $! \u8001\u001f\u8001\u001f     & \u0081 \u0082     \" ! & \u0081 \u0082     \" # & \u0081 \u0082     \" $ & \u0081 \u0082     \" !");
        _accepts = Pickler.unpickle1D(" %\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f !");
    } 
}