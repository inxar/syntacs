/**
 * $Id: Mission.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.*;
import java.util.*;
import org.inxar.syntacs.util.*;

/**
 * "Mission Control" holds the global <code>Session</code> instance
 * which is consulted throughout the API.  
 */
public class Mission  
{
    /**
     * Sets the global <code>Session</code> to the given instance.  
     */
    public static void control(Session session)
    {
	Mission.session = session;
    }

    /**
     * Gets the global <code>Session</code> instance.
     */
    public static Session control()
    {
	if (session == null)
	    session = new StandardSession();

	return session;
    }

    /**
     * Returns <code>true</code> if the <code>Session</code> instance
     * has been set (is not <code>null</code>).  
     */
    public static boolean isActivated()
    {
	return session != null;
    }

    /**
     * Closes and nullifies the current <code>Session</code> instance.  
     */
    public static void deactivate()
    {
	if (session != null) {
	    session.close();
	    session = null;
	}
    }

    private static Session session = null;
}

