/**
 * $Id: StandardSession.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import com.inxar.syntacs.*;
import org.inxar.syntacs.util.*;

/**
 * Standard implementation of <code>Session</code>.
 */
public class StandardSession implements Session 
{
    private static final boolean DEBUG = false;

    /**
     * Constructs the <code>Session</code> which writes to <code>System.out</code>. 
     */
    public StandardSession()
    {
	this(null);
    }

    /**
     * Constructs the <code>Session</code> which writes to the given
     * <code>Writer</code>.  If the given <code>Writer</code> is not
     * an <code>instanceof</code> <code>BufferedWriter</code> it will
     * be wrapped with one.  
     */
    public StandardSession(Writer out) 
    {
	this.map = new HashMap();
	this.logs = new TreeMap();
	this.framebuf = new Buffer(4);
	this.regbuf = new Buffer();
	this.linebuf = new Buffer(5).add("[000]");
	this.lineno = 1;
	
	if (out == null)
	    this.out = new BufferedWriter
		(new OutputStreamWriter(System.out));	
	else if (!(out instanceof BufferedWriter))
	    this.out = new BufferedWriter(out);
	else
	    this.out = out;

	write(new Buffer()
	    .add("Session initiated at ")
	    .add(new java.util.Date().toString())
	    .ln()
	    .add("---------------------------------------------")
	    .ln());
    }

    public synchronized Log log(String name, Object obj)
    {
	StandardLog log = (StandardLog)logs.get(name);
	Buffer buf = new Buffer();
	
	if (log == null) {
	    log = this.new StandardLog(buf);
	    logs.put(name, log);
	}
	
	name += ++log.count;

	buf .add('[')
	    .add(name)
	    .add(']');

	regbuf
	    .add('"')
	    .add(name)
	    .add('"')
	    .add(" was ")
	    .add(obj.getClass().getName())
	    .ln();

	return log;
    }

    private synchronized void newline()
    {
	String str = String.valueOf(lineno++);
	int strlen = str.length();

	switch (strlen) {
	case 1:
	    linebuf.set(3, str);
	    break;
	case 2:
	    linebuf.set(2, str);
	    break;
	case 3:
	    linebuf.set(1, str);
	    break;
	default:
	    throw new InternalError();
	}

	if (lineno > 998)
	    lineno = 1;
    }

    private synchronized void write(Buffer msg)
    {
	try {

	    synchronized (out) {
		msg.toWriter(out);
		out.flush();
	    }

	} catch (IOException ioex) {
	    ioex.printStackTrace();
	} catch (NullPointerException npex) {
	    System.err.println("Error: Session has been closed.");
	}
    }

    private synchronized void write(StandardChannel channel)
    {
	write(channel, true);
    }

    private synchronized void write(StandardChannel channel, boolean writeln)
    {
	if (meter != null && meter.isActive())
	    unmeter();

	try {

	    newline();

	    synchronized (out) {

		header(channel);
		body(channel);

		if (writeln)
		    out.write(StringTools.NEWLINE);

		out.flush();
	    }

	} catch (IOException ioex) {
	    ioex.printStackTrace();
	} catch (NullPointerException npex) {
	    System.err.println("Error: Session has been closed.");
	}
    }

    private void header(StandardChannel channel) throws IOException
    {
	framebuf.toWriter(out);
	linebuf.toWriter(out);
	channel.name.toWriter(out);
	channel.level.toWriter(out);
	channel.tab.toWriter(out);
    }

    private void body(StandardChannel channel) throws IOException
    {
	Buffer buf = channel.buf;

	int off = 0;
	int len = buf.length();
	char[] src = buf.getCharArray();
	
	for (int i = 0; i < len; i++) {
	    
	    switch (src[i]) {
		
	    case '\r':
		if (off != i) {
		    out.write(src, off, i - off);
		}

		off = i + 1;
		out.write(StringTools.NEWLINE);
		header(channel);

		continue;
		
	    case '\n':
		// If this is the tail of a CRLF, keep going to
		// the next char, but reset the offset of the next
		// sequence up by one.
		if (i != 0 && src[i - 1] == '\r') {
		    ++off;
		    continue;
		}
		
		if (off != i)
		    out.write(src, off, i - off);
		
		off = i + 1;
		out.write(StringTools.NEWLINE);
		header(channel);
	    }
	}
	
	if (off != len) 
	    out.write(src, off, len - off);
    }

    private void unmeter()
    {
	StandardChannel c = (StandardChannel)stack.peek();

	if (c.meter != null) {
	    c.meter.stop();
	    c.meter = null;
	    c.line = lineno - 1;
	    write(new Buffer().ln());
	    framebuf.add(' ');
	}
    }

    private synchronized void stack(StandardChannel channel)
    {
	write(channel, false);

	if (stack == null)
	    stack = new Stack();

	if (meter == null)
	    meter = new Meter();

	synchronized (channel) {
	    channel.buf.length(0);
	    channel.time = System.currentTimeMillis();
	    channel.isStacked = true;
	    channel.meter = meter;
	    meter.start();
	}

	stack.push(channel);
	}

    private synchronized void unstack(StandardChannel channel)
    {
	StandardChannel c = (StandardChannel)stack.pop();
	
	if (c != channel) {
	    if (meter != null)
		meter.stop();

	    while (!stack.isEmpty()) {
		c = (StandardChannel)stack.pop();
		if (c == channel)
		    break;
		unstack(c);
	    }

	    log().warn().write("Mismatched debug stack management").out();
	}

	synchronized (c) {
	    c.isStacked = false;
	    
	    if (c.meter != null) {
		c.meter.stop();
		c.meter = null;
		writeTime1(c);
	    } else {
		framebuf.length(framebuf.length() - 1);
		writeTime2(c);
	    }
	}
    }

    private void writeTime1(StandardChannel channel)
    {
	long time = System.currentTimeMillis() - channel.time;
	
	write(channel.buf
	      .add(" [")
	      .add(time)
	      .add(" ms]")
	      .ln());

	channel.buf.length(0);
    }

    private void writeTime2(StandardChannel channel)
    {
	long time = System.currentTimeMillis() - channel.time;
	
	Buffer b = channel.buf
	    .add("Done [")
	    .add(time)
	    .add(" ms]");

	write(channel);
	channel.buf.length(0);
    }

    public synchronized void close()
    {
	if (out != null) {

	    try {

		if (meter != null)
		    meter.stop();
		    
		write(new Buffer()
		    .add("---------------------------------------------").ln());

		write(regbuf);

		write(new Buffer(45)
		    .add("Session ended at ")
		    .add(new java.util.Date().toString())
		    .ln());
	    
		synchronized (out) {
		    out.close(); 
		    out = null;
		}

	    } catch (IOException ioex) {
		if (out != null)
		    try { out.close(); out = null; } 
		    catch (Exception ex) {}
		ioex.printStackTrace();
	    }

	}
    }

    public void put(String key, Object val)
    {
	if (DEBUG) {
	    Channel debug = log().debug();
	    debug.write("storing ").quote(key)
		.write(" with hashcode ")
		.write(key.hashCode())
		.write(" as ");

	    if (val instanceof String)
		debug.quote(val);
	    else
		debug.quote(val.getClass().toString());
	    
	    debug.out();
	}

	map.put(key, val);
    }

    public Object remove(String key)
    {
	return map.remove(key);
    }

    public Object get(String key)
    {
	if (DEBUG) {

	    Object val = map.get(key);
	    
	    if (val == null)
		log().warn()
		    .write("request for ").quote(key)
		    .write(" with hashcode ")
		    .write(key.hashCode())
		    .write(" was null!")
		    //.write(map)
		    .out();
	    
	    return val;

	} else
	    return map.get(key);
    }

    public String getString(String key)
    {
	return (String)map.get(key);
    }

    public String getString(String key, String def)
    {
	
	Object val = map.get(key);
	return val == null ? def : (String)val;
    }

    public boolean getBoolean(String key)
    {
	Object val = map.get(key);
	if (val instanceof Boolean) 
	    return ((Boolean)val).booleanValue();
	else
	    throw new NullPointerException("No boolean value under "+key);
    }

    public boolean getBoolean(String key, boolean def)
    {
	Object val = map.get(key);
	if (val instanceof Boolean) 
	    return ((Boolean)val).booleanValue();
	else
	    return def;
    }

    public int getInt(String key)
    {
	Object val = map.get(key);
	if (val instanceof Integer) 
	    return ((Integer)val).intValue();
	else
	    throw new NullPointerException("No int value under "+key);
    }

    public int getInt(String key, int def)
    {
	Object val = map.get(key);
	if (val instanceof Integer) 
	    return ((Integer)val).intValue();
	else
	    return def;
    }

    public boolean contains(String key)
    {
	return map.containsKey(key);
    }

    private boolean getTrue(String key)
    {
	Object o = map.get(key);
	
	if (o == null)
	    return false;

	if (o instanceof Boolean) 
	    return ((Boolean)o).booleanValue();
	
	if (!(o instanceof String))
	    return false;
 
	String val = ((String)o).toLowerCase();
	
	boolean bval = "true".equals(val) || "yes".equals(val);
	map.put(key, bval ? Boolean.TRUE : Boolean.FALSE);

	return bval;
    }

    private boolean getFalse(String key)
    {
	Object o = map.get(key);
	
	if (o == null)
	    return false;

	if (o instanceof Boolean) 
	    return ((Boolean)o).booleanValue();
	
	if (!(o instanceof String))
	    return false;
 
	String val = ((String)o).toLowerCase();
	
	boolean bval = "false".equals(val) || "no".equals(val);
	map.put(key, bval ? Boolean.TRUE : Boolean.FALSE);

	return bval;
    }

    public boolean isTrue(String key)
    {
	return getTrue(key);
    } 

    public boolean isFalse(String key)
    {
	return getFalse(key);
    } 

    public boolean isNotTrue(String key)
    {
	return ! getTrue(key);
    } 

    public boolean isNotFalse(String key)
    {
	return ! getFalse(key);
    } 

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("ssn", this);
	return log;
    }
    
    private int lineno;
    private Map map, logs;
    private Buffer framebuf;
    private Buffer linebuf;
    private Buffer regbuf;
    private Writer out;
    private Stack stack;
    private Meter meter;
    private Log log;

    private class StandardLog
	implements Log
    {
	StandardLog(Buffer name)
	{
	    this.name = name; 
	}

	public Channel trace()
	{
	    if (trace == null) 
		trace = instantiate("[trce]: ");
	    return trace;
	}

	public Channel debug()
	{
	    if (debug == null) 
		debug = instantiate("[dbug]: ");
	    return debug;
	}

	public Channel info()
	{
	    if (info == null) 
		info = instantiate("[info]: ");
	    return info;
	}

	public Channel warn()
	{
	    if (warn == null) 
		warn = instantiate("[warn]: ");
	    return warn;
	}

	public Channel critical()
	{
	    if (critical == null) 
		critical = instantiate("[crit]: ");
	    return critical;
	}

	public Channel catastrophe()
	{
	    if (catastrophe == null) 
		catastrophe = instantiate("[cata]: ");
	    return catastrophe;
	}

	private Channel instantiate(String level)
	{
	    return StandardSession.this.new StandardChannel
		(name, new Buffer().add(level));
	}

	private Buffer name; 
	private Channel trace;
	private Channel debug;
	private Channel info;
	private Channel warn;
	private Channel critical;
	private Channel catastrophe;

	// the number of instaces having the same base name.
	private int count;	
    }

    private class StandardChannel implements Channel
    {
	StandardChannel(Buffer name, Buffer level)
	{
	    this.name = name;
	    this.level = level;
	    this.tab = new Buffer(7);
	    this.buf = new Buffer(127);
	}

	private void reset()
	{
	    tab.length(0);
	    buf.length(0);
	}

	public Channel write(boolean msg)
	{
	    touch();
	    buf.add(msg);
	    return this;
	}

	public Channel write(char msg)
	{
	    touch();
	    buf.add(msg);
	    return this;
	}

	public Channel write(int msg)
	{
	    touch();
	    buf.add(msg);
	    return this;
	}

	public Channel write(String msg)
	{
	    touch();
	    buf.add(msg);
	    return this;
	}

	public Channel write(Object msg)
	{
	    touch();
	    buf.add(String.valueOf(msg));
	    return this;
	}

	public Channel spc()
	{
	    touch();
	    buf.add(' ');
	    return this;
	}

	public Channel quote(Object msg)
	{
	    touch();

	    buf.add('"');
	    buf.add(msg.toString());
	    buf.add('"');

	    return this;
	}

	public Channel stripe(char c, int n)
	{
	    touch();
	    while (--n >= 0)
		buf.add(c);
	    return this;
	}

	public Channel writeln()
	{
	    touch();
	    buf.ln();
	    return this;
	}
	
	public Channel over()
	{
	    tab.add(" ");
	    return this;
	}

	public Channel back()
	{
	    if (tab.length() >= 1)
		tab.length(tab.length() - 1);
	    return this;
	}

	public void time()
	{
	    touch();
	    stack(this);
	}

	public void touch()
	{
	    if (isStacked) 
		unstack(this);
	}

	public void out()
	{
	    StandardSession.this.write(this);
	    buf.length(0);
	}

	Buffer name;
	Buffer level;
	Buffer tab;
	Buffer buf;

	boolean isStacked;
	Meter meter;
	int line;
	long time;

	// count the number of log instances having the same name
	// prefix such to disambiguate them.
	private int count = 1;
    }
    
    private class Meter implements Runnable
    {
	Meter()
	{
	    this.buf = new Buffer(3);
	    this.delay = Mission.control().getInt("session-meter-delay", 50);
	}

	public void start()
	{
	    if (t == null) {
		write(buf.add("  "));

		buf.setCharAt(0, '\b');
		buf.setCharAt(2, ' ');

		t = new Thread(this);
		t.start();
	    }
	}
	
	public void stop()
	{
	    if (t != null) {
		t = null;
		
		buf.set(0, "\b\b");
		write(buf);
		buf.length(0);
	    }
	}

	public boolean isActive()
	{
	    return t != null;
	}

	public void run()
	{
	    while(Thread.currentThread() == t) {
		
		char c = '-';
		
		switch (++p) {
		case 1: break;
		case 2: c = '\\'; break;
		case 3: c = '|'; break;
		case 4: c = '/'; /* fall */
		default: 
		    p = 0; 
		}

		buf.setCharAt(1, c);
		write(buf);

		try {
		    t.sleep(delay);
		} catch (InterruptedException iex) {
		}
	    }
	}

	private int p;		// current state
	private Buffer buf;	// charbuffer
	private Thread t;	// internal thread
	private int delay;	// sleep time
    }

}


