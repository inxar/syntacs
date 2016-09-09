/**
 * $Id: StandardAuditor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator;

import java.io.*;
import java.util.*;
import org.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.translator.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Standard implementation of <code>Auditor</code>.
 */
public class StandardAuditor 
    implements Auditor, Serializable
{
    private static final boolean DEBUG = false;
    
    public StandardAuditor()
    {
	this.errors = new ArrayList();
	this.warnings = new ArrayList();
    }
    
    public void setSource(String src)
    {
	this.src = src;
    }

    public String getSource()
    {
	return this.src;
    }

    public void setProperties(Properties p)
    {
	this.p = p;
    }

    public Properties getProperties()
    {
	return this.p;
    }

    public boolean isEmpty()
    {
	return errors.isEmpty() && warnings.isEmpty();
    }

    public int complaints()
    {
	return errors.size() + warnings.size();
    }

    public int errors()
    {
	return errors.size();
    }

    public int warnings()
    {
	return warnings.size();
    }

    public boolean hasErrors()
    {
	return ! errors.isEmpty();
    }

    public boolean hasWarnings()
    {
	return ! warnings.isEmpty();
    }

    public List getErrors()
    {
	return Collections.unmodifiableList(errors);
    }

    public List getWarnings()
    {
	return Collections.unmodifiableList(warnings);
    }

    public Complaint notify(int type, int line, String msg)
    {
	return notify(this.new StandardComplaint(type, line, msg));
    }

    public Complaint notify(int type, String msg)
    {
	return notify(this.new StandardComplaint(type, msg));
    }

    public Complaint notify(int type, String msg, Input in, int off, int len)
    {
	return notify(this.new StandardComplaint(type, in.atln(), msg, 
					   trace(in, off, len)));
    }

    public Complaint notify(Complaint d)
    {
	switch (d.getType()) {
	case Complaint.SEMANTIC_WARNING:  
	    warnings.add(d);
	    break;
	case Complaint.LEXICAL_ERROR:   
	case Complaint.SYNTACTIC_ERROR:   
	case Complaint.SEMANTIC_ERROR:    
	case Complaint.UNSPECIFIED_ERROR:    
	    errors.add(d);
	    break;
	default:
	    throw new IllegalArgumentException
		("Unknown Complaint Type: "+d.getType());
	}
	return d;
    }

    protected String trace(Input in, int off, int len)
    {
	if (DEBUG) 
	    log().debug()
		.write("Trace request at off ").write(off)
		.write(" and len ").write(len)
		.out();
	
	StringBuffer b = new StringBuffer();

	int beg = off;
	int end = off;

	// Find the beginning of the line.
	try {

	out:
	    while (--beg >= 0) {
		switch (in.retch(beg)) {
		case '\n': case '\r':
		    break out;
		}
	    }
	    
	} catch (ArrayIndexOutOfBoundsException aiiobex) {
	    aiiobex.printStackTrace();
	    beg = off - 1;
	}

	//if (beg != offset)
	beg++;

	StringBuffer head = new StringBuffer()
	    .append("  \\   Line ")
	    .append(in.atln())
	    .append(": ");
	
	// Find the end of the line.
	try {
	    while (in.retch(end++) != '\n');
	} catch (Exception ex) {
	} finally {
	    --end;
	}

	if (DEBUG) 
	    try {
		char c1 = in.retch(beg);
		char c2 = in.retch(end);
		log().debug()
		    .write("beginning-of-line = ").write(beg)
		    .write(" #").write((int)c1)
		.write(", end-of-line = ").write(end)
		    .write(" #").write((int)c2)
		    .out();
	    } catch (Exception ex) {
	    }

	if (DEBUG) 
	    log().debug()
		.write("line is ")
		.quote(in.stretch(beg, end - beg))
		.out();
	
	b.append(head);
	b.append(in.stretch(beg, end - beg));
	b.append(StringTools.NEWLINE);

	int headlen = head.length();

	if (DEBUG) 
	    log().debug()
		.write("headlen = ").write(headlen)
		.write(", off + headlen = ").write(off + headlen)
		.out();

	int i = 0;
	while ( i++ < (off - beg) + headlen ) {
	    switch (i) {
	    case 4: 
		b.append('\\'); break;
	    case 5: case 6: case 7:
		b.append('_'); break;
	    default:
		b.append(' '); 
	    }
	}

	if (len == 0)
	    b.append('*');

	else if (len == 1)
	    b.append('^');

	else {
	    b.append('<');
	    
	    while ( len-- > 2 )
		b.append('-');
	    
	    b.append('>');
	}

	return b.toString();
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	if (errors.size() > 0) {
	    b.append(errors.size())
		.append(errors.size() == 1 ? " Error" : " Errors");
	    
	    if (src != null)
		b.append(" in ").append(src).append(':');
	    else
		b.append(':');

	    b.append(StringTools.NEWLINE);
	    for (int i = 0; i < errors.size(); i++)
		b.append(errors.get(i).toString()).append(StringTools.NEWLINE);
	    b.append(StringTools.NEWLINE);
	}

	if (warnings.size() > 0) {
	    b.append(warnings.size())
		.append(warnings.size() == 1 ? " Warning" : " Warnings");
	    
	    if (src != null)
		b.append(" in ").append(src).append(':');
	    else
		b.append(':');

	    b.append(StringTools.NEWLINE);
	    for (int i = 0; i < warnings.size(); i++)
		b.append(warnings.get(i).toString()).append(StringTools.NEWLINE);
	    b.append(StringTools.NEWLINE);
	}

	return b.toString();

    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("err", this);
	return log;
    }

    protected String src;
    protected List errors;
    protected List warnings;
    protected Properties p;
    private Log log;

    public class StandardComplaint implements Complaint, Serializable
    {
	StandardComplaint(int type, String msg) 
	{
	    this(type, -1, msg, null);
	}

	StandardComplaint(int type, int line, String msg) 
	{
	    this(type, line, msg, null);
	}

	StandardComplaint(int type, int line, String msg, String trace) 
	{
	    this.type = type;
	    this.line = line;
	    this.msg = msg;
	    this.trace = trace;
	    init();
	}

	private void init()
	{
	    StringBuffer b = new StringBuffer();
	    
	    b.append(StringTools.NEWLINE);

	    switch (type) {
	    case Complaint.SEMANTIC_WARNING:  b.append("*-- Semantic Warning"); break;
	    case Complaint.LEXICAL_ERROR:     b.append("*-- Lexical Error"); break;
	    case Complaint.SYNTACTIC_ERROR:   b.append("*-- Syntactic Error"); break;
	    case Complaint.SEMANTIC_ERROR:    b.append("*-- Semantic Error"); break;
	    case Complaint.UNSPECIFIED_ERROR: b.append("*-- Error"); break;
	    default:
		throw new InternalError("Unknown Complaint Type: "+type);
	    }

//  	    if (line > 0)
//  		b.append(" at line ").append(line).append(": ");
//  	    else 
	    b.append(": ");
	    
	    b.append(msg);

	    if (trace != null)
		b.append(StringTools.NEWLINE)
		    .append(" \\").append(StringTools.NEWLINE)
		    .append(trace);
	    else 
		b.append(StringTools.NEWLINE).append(" \\___ ");

	    this.trace = b.toString();
	}

	public int getType()
	{
	    return type;
	}

	public int getLineNumber()
	{
	    return line;
	}

	public String getSource()
	{
	    return src;
	}

	public String getMessage()
	{
	    return msg;
	}

	public void printMessage()
	{
	    printMessage(System.out);
	}

	public void printMessage(PrintStream out)
	{
	    out.println(trace);
	}
	
	public String toString()
	{
	    return trace;
	}

	int type;
	int line;
	String msg;
	String trace;
    }
}


/*

 - Syntax Error: Unexpected String Ignored

    Line 2: abababababababababababababaaaaaaaaa
            <--------------------------------->

+-- Syntax Error: Unexpected string ignored
 \  Line 2: abababababababababababababaaaaaaaaa
  \         <--------------------------------->  
   \
    \_______

+-- Syntax Error: Unexpected string ignored
 \  Line 2: abababababababababababababaaaaaaaaa
  \         <--------------------------------->  
   \
    \_______

    ____________________________________________
   / Syntax Error: Unexpected string ignored
  /
 /
| abababababababababababababaaaaaaaaa
| <--------------------------------->  
 \
  \ The String was Ignored.
      

+----------------------------------------------------
 \ Syntax Error
  \
 2 | abababababababababababababaaaaaaaaa
 2 | <--------------------------------->  
  /
 / String was ignored
+-----------------------------------------------------

+-- Syntax Error: Unexpected string ignored
 \
  \  Line 2: abababababababababababababaaaaaaaaa
   \___      <--------------------------------->  


+-- Syntax Error: Unexpected string ignored
 \                              <--------------|
 /  Line 2:                    adasdas2asdasdad
 \  Line 3: abababababab
  \___     |---------->

+-- Syntax Error: Unexpected string ignored
 \                              <--------------|
  \  Line 2:                    adasdas2asdasdad
  /  Line 3: ababababababasdadkajslkfjsdflksjfls
  \  Line 4: ababababababasdadkajslkfjsdflksjfls
  /  Line 5: ababababababasdadkajslkfjsdflksjfls
  \  Line 6: abababababab
   \___      |---------->

+-- Syntax Error: Unexpected string ignored
 \                              <--------------|
  \  Line 2:                    adasdas2asdasdad
  /  Line 3: ababababababasdadkajslkfjsdflksjfls
  \  Line 4: ababababababasdadkajslkfjsdflksjfls
  /  Line 5: ababababababasdadkajslkfjsdflksjfls
  \  Line 6: abababababab
   \___      |---------->

+-- Syntax Error: Unexpected string ignored
 \                              <--------------|
  \  Line 2:                    adasdas2asdasdad
  /  Line 3: ababababababasdadkajslkfjsdflksjfls
  \  Line 4: ababababababasdadkajslkfjsdflksjfls
  /  Line 5: ababababababasdadkajslkfjsdflksjfls
  \  Line 6: abababababab
   \___      |---------->

 
/ Syntax Error: Unexpected string ignored           \
\                                                   /
 \  Line 2: abababababababababababababaaaaaaaaa    /
  \__       <---------------------------------> __/

*-- Syntax Error: Unexpected string ignored
 \___


*/

