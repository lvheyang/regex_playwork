/*
 * Copyright (c) 1999, 2008, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package com.qiniu.lvheyang.lucene.pcre.regex;


/**
 * Unchecked exception thrown to indicate a syntax error in a regular-expression pattern.
 *
 * @author unascribed
 * @spec JSR-51
 * @since 1.4
 */

public class PatternSyntaxException
    extends IllegalArgumentException {

  private static final long serialVersionUID = -3864639126226059218L;
  private static final String nl = System.getProperty("line.separator");
  private final String desc;
  private final String pattern;
  private final int index;

  /**
   * Constructs a new instance of this class.
   *
   * @param desc A description of the error
   * @param regex The erroneous pattern
   * @param index The approximate index in the pattern of the error, or {@code -1} if the index is
   * not known
   */
  public PatternSyntaxException(String desc, String regex, int index) {
    this.desc = desc;
    this.pattern = regex;
    this.index = index;
  }

  /**
   * Retrieves the error index.
   *
   * @return The approximate index in the pattern of the error, or {@code -1} if the index is not
   * known
   */
  public int getIndex() {
    return index;
  }

  /**
   * Retrieves the description of the error.
   *
   * @return The description of the error
   */
  public String getDescription() {
    return desc;
  }

  /**
   * Retrieves the erroneous regular-expression pattern.
   *
   * @return The erroneous pattern
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Returns a multi-line string containing the description of the syntax error and its index, the
   * erroneous regular-expression pattern, and a visual indication of the error index within the
   * pattern.
   *
   * @return The full detail message
   */
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append(desc);
    if (index >= 0) {
      sb.append(" near index ");
      sb.append(index);
    }
    sb.append(nl);
    sb.append(pattern);
    if (index >= 0) {
      sb.append(nl);
      for (int i = 0; i < index; i++) {
        sb.append(' ');
      }
      sb.append('^');
    }
    return sb.toString();
  }

}
