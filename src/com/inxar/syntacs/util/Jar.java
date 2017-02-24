package com.inxar.syntacs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.jar.JarFile;

public class Jar {

  protected final File file;
  protected final byte[] buf;
  protected java.util.jar.JarOutputStream os;
  protected boolean isRecursive = true;

  public Jar(File file) throws java.io.IOException {
    this.file = file;
    this.buf = new byte[1024];
    this.os = new java.util.jar.JarOutputStream(new FileOutputStream(file));
  }

  public Jar(File file, boolean isRecursive) throws java.io.IOException {
    this(file);
    this.isRecursive = isRecursive;
  }

  public void close() throws java.io.IOException {
    if (os != null) {
      os.close();
      os = null;
    }
  }

  public void add(String base, File r) throws java.io.IOException {
    if (r.isFile()) {
      addFile(base, r);
    } else if (r.isDirectory()) {
      addDirectory(base, r);
    }
  }

  public void addFile(String base, File file) throws java.io.IOException {
    java.io.InputStream in = new FileInputStream(file);
    if (!file.getName().endsWith(".java")) {
      return;
    }
    java.util.jar.JarEntry e = new java.util.jar.JarEntry(getRelativePath(base, file));
    os.putNextEntry(e);

    int length;
    while ((length = in.read(buf)) >= 0) {
      os.write(buf, 0, length);
    }

    os.closeEntry();
    in.close();
  }

  public void addDirectory(String base, File dir) throws java.io.IOException {
    // Never all jarfile dir entries seems to be the lesson here.  This causes extraction errors.
    //java.util.jar.JarEntry e = new java.util.jar.JarEntry(getRelativePath(base, dir));
    //os.putNextEntry(e);
    //os.closeEntry();

    if (isRecursive) {
      for (File f : dir.listFiles()) {
        add(base, f);
      }
    }
  }

  protected String getRelativePath(String base, File file) {
    String path = file.getAbsolutePath();
    int index = path.indexOf(base);
    if (index < 0) {
      throw new IllegalArgumentException("Base " + base + " must be a relative prefix of " + file.getAbsolutePath());
    }
    path = path.substring(index + base.length());
    if (path.isEmpty()) {
      throw new IllegalArgumentException("Base " + base + " must not be a the full prefix of " + file.getAbsolutePath());
    }
    if (path.charAt(0) == File.separatorChar) {
      path = path.substring(1);
    }
    System.out.println("relativePath: " + path);
    return path;
  }
}
