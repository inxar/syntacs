/**
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * @author Paul Cody Johnston - pcj@inxar.org
 */
package com.inxar.syntacs.translator.regexp;

import com.inxar.syntacs.automaton.finite.MesoArrayDFA;
import com.inxar.syntacs.util.Pickler;

/**
 * Automatically generated by <a href='http://www.inxar.org/syntacs'>Syntacs
 *  Translation Toolkit</a> on Fri Jul 06 12:05:08 PDT 2001
 */
class RegexpDefaultDFA extends MesoArrayDFA {
  private static final int[][] _table;
  private static final int[] _accepts;

  RegexpDefaultDFA() {
    super(_table, _accepts);
  }

  static {
    _table =
        Pickler.unpickle2D(
            " ; $! \u8001\u001f\u8001\u001f     \u0098 ) \u009c # # \" \" \" # \" # # # # # # # # # # # # # # # # # # \" # # # # # # # & \' % ) # # # # # # # # # # # # # # # # # # # ( # # # # # # # # # # # # # # # # # # # # # # # # # # # + *   # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # $ ; * @     : :   :                                     : $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     $! \u8001\u001f\u8001\u001f     \u0091 ) \u0095 1 1       1   1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1   1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 0 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 2 z ) ~     + + +   +                                     +                         -                                                                                                 , I ) M     . . .   .                                     .                         / $! \u8001\u001f\u8001\u001f     I ) M     . . .   .                                     .                         / $! \u8001\u001f\u8001\u001f     ( P S     7 7 7 7 $! \u8001\u001f\u8001\u001f     [ P \u0086     3 3 3 3 3 3 3 3 3 3               3 3 3 3 3 3                                                     3 3 3 3 3 3 [ P \u0086     4 4 4 4 4 4 4 4 4 4               4 4 4 4 4 4                                                     4 4 4 4 4 4 [ P \u0086     5 5 5 5 5 5 5 5 5 5               5 5 5 5 5 5                                                     5 5 5 5 5 5 [ P \u0086     6 6 6 6 6 6 6 6 6 6               6 6 6 6 6 6                                                     6 6 6 6 6 6 $! \u8001\u001f\u8001\u001f     , P W     8 8 8 8 8 8 8 8 , P W     9 9 9 9 9 9 9 9 $! \u8001\u001f\u8001\u001f     < ) @     : : :   :                                     :");
    _accepts =
        Pickler.unpickle1D(
            " ;\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f ! \" $ % ( ) & \'\u8001\u001f\u8001\u001f * + ,\u8001\u001f\u8001\u001f - 0 0 0\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f 2\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f 1 !");
  }
}
