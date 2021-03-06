/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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


import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.ALNUM;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.ALPHA;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.ASCII;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.BLANK;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.CNTRL;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.DIGIT;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.GRAPH;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.LOWER;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.PUNCT;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.SPACE;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.UNDER;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.UPPER;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.WORD;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.XDIGIT;
import static com.qiniu.lvheyang.lucene.pcre.regex.ASCII.isPrint;

import com.qiniu.lvheyang.lucene.pcre.regex.Pattern.CharPredicate;
import java.util.HashMap;

/**
 * A utility class to print out the pattern node tree.
 */

class PrintPattern {

  static HashMap<CharPredicate, String> pmap;
  private static HashMap<Pattern.Node, Integer> ids = new HashMap<>();

  static {
    pmap = new HashMap<>();
    pmap.put(Pattern.ALL(), "All");
    pmap.put(Pattern.DOT(), "Dot");
    pmap.put(Pattern.UNIXDOT(), "UnixDot");
    pmap.put(Pattern.VertWS(), "VertWS");
    pmap.put(Pattern.HorizWS(), "HorizWS");

    pmap.put(CharPredicates.ASCII_DIGIT(), "ASCII.DIGIT");
    pmap.put(CharPredicates.ASCII_WORD(), "ASCII.WORD");
    pmap.put(CharPredicates.ASCII_SPACE(), "ASCII.SPACE");
  }

  private static void print(Pattern.Node node, String text, int depth) {
    if (!ids.containsKey(node)) {
      ids.put(node, ids.size());
    }
    print("%6d:%" + (depth == 0 ? "" : depth << 1) + "s<%s>", ids.get(node), "", text);
    if (ids.containsKey(node.next)) {
      print(" (=>%d)", ids.get(node.next));
    }
    print("%n");
  }

  private static void print(String s, int depth) {
    print("       %" + (depth == 0 ? "" : depth << 1) + "s<%s>%n", "", s);
  }

  private static void print(String fmt, Object... args) {
    System.err.printf(fmt, args);
  }

  private static String toStringCPS(int[] cps) {
    StringBuilder sb = new StringBuilder(cps.length);
    for (int cp : cps) {
      sb.append(toStringCP(cp));
    }
    return sb.toString();
  }

  private static String toStringCP(int cp) {
    return (isPrint(cp) ? "" + (char) cp
        : "\\u" + Integer.toString(cp, 16));
  }

  private static String toStringRange(int min, int max) {
    if (max == Pattern.MAX_REPS) {
      if (min == 0) {
        return " * ";
      } else if (min == 1) {
        return " + ";
      }
      return "{" + min + ", max}";
    }
    return "{" + min + ", " + max + "}";
  }

  private static String toStringCtype(int type) {
    switch (type) {
      case UPPER:
        return "ASCII.UPPER";
      case LOWER:
        return "ASCII.LOWER";
      case DIGIT:
        return "ASCII.DIGIT";
      case SPACE:
        return "ASCII.SPACE";
      case PUNCT:
        return "ASCII.PUNCT";
      case CNTRL:
        return "ASCII.CNTRL";
      case BLANK:
        return "ASCII.BLANK";
      case UNDER:
        return "ASCII.UNDER";
      case ASCII:
        return "ASCII.ASCII";
      case ALPHA:
        return "ASCII.ALPHA";
      case ALNUM:
        return "ASCII.ALNUM";
      case GRAPH:
        return "ASCII.GRAPH";
      case WORD:
        return "ASCII.WORD";
      case XDIGIT:
        return "ASCII.XDIGIT";
      default:
        return "ASCII ?";
    }
  }

  private static String toString(Pattern.Node node) {
    String name = node.getClass().getName();
    return name.substring(name.lastIndexOf('$') + 1);
  }

  static void walk(Pattern.Node node, int depth) {
    depth++;
    while (node != null) {
      String name = toString(node);
      String str;
      if (node instanceof Pattern.Prolog) {
        print(node, name, depth);
        // print the loop here
        Pattern.Loop loop = ((Pattern.Prolog) node).loop;
        name = toString(loop);
        str = name + " " + toStringRange(loop.cmin, loop.cmax);
        print(loop, str, depth);
        walk(loop.body, depth);
        print("/" + name, depth);
        node = loop;
      } else if (node instanceof Pattern.Loop) {
        return;  // stop here, body.next -> loop
      } else if (node instanceof Pattern.Curly) {
        Pattern.Curly c = (Pattern.Curly) node;
        str = "Curly " + c.type + " " + toStringRange(c.cmin, c.cmax);
        print(node, str, depth);
        walk(c.atom, depth);
        print("/Curly", depth);
      } else if (node instanceof Pattern.GroupCurly) {
        Pattern.GroupCurly gc = (Pattern.GroupCurly) node;
        str = "GroupCurly " + gc.groupIndex / 2 +
            ", " + gc.type + " " + toStringRange(gc.cmin, gc.cmax);
        print(node, str, depth);
        walk(gc.atom, depth);
        print("/GroupCurly", depth);
      } else if (node instanceof Pattern.GroupHead) {
        Pattern.GroupHead head = (Pattern.GroupHead) node;
        Pattern.GroupTail tail = head.tail;
        print(head, "Group.head " + (tail.groupIndex / 2), depth);
        walk(head.next, depth);
        print(tail, "/Group.tail " + (tail.groupIndex / 2), depth);
        node = tail;
      } else if (node instanceof Pattern.GroupTail) {
        return;  // stopper
      } else if (node instanceof Pattern.Ques) {
        print(node, "Ques " + ((Pattern.Ques) node).type, depth);
        walk(((Pattern.Ques) node).atom, depth);
        print("/Ques", depth);
      } else if (node instanceof Pattern.Branch) {
        Pattern.Branch b = (Pattern.Branch) node;
        print(b, name, depth);
        int i = 0;
        while (true) {
          if (b.atoms[i] != null) {
            walk(b.atoms[i], depth);
          } else {
            print("  (accepted)", depth);
          }
          if (++i == b.size) {
            break;
          }
          print("-branch.separator-", depth);
        }
        node = b.conn;
        print(node, "/Branch", depth);
      } else if (node instanceof Pattern.BranchConn) {
        return;
      } else if (node instanceof Pattern.CharProperty) {
        str = pmap.get(((Pattern.CharProperty) node).predicate);
        if (str == null) {
          str = toString(node);
        } else {
          str = "Single \"" + str + "\"";
        }
        print(node, str, depth);
      } else if (node instanceof Pattern.SliceNode) {
        str = name + "  \"" +
            toStringCPS(((Pattern.SliceNode) node).buffer) + "\"";
        print(node, str, depth);
      } else if (node instanceof Pattern.CharPropertyGreedy) {
        Pattern.CharPropertyGreedy gcp = (Pattern.CharPropertyGreedy) node;
        String pstr = pmap.get(gcp.predicate);
        if (pstr == null) {
          pstr = gcp.predicate.toString();
        } else {
          pstr = "Single \"" + pstr + "\"";
        }
        str = name + " " + pstr + ((gcp.cmin == 0) ? "*" : "+");
        print(node, str, depth);
      } else if (node instanceof Pattern.BackRef) {
        str = "GroupBackRef " + ((Pattern.BackRef) node).groupIndex / 2;
        print(node, str, depth);
      } else if (node instanceof Pattern.LastNode) {
        print(node, "END", depth);
      } else if (node == Pattern.accept) {
        return;
      } else {
        print(node, name, depth);
      }
      node = node.next;
    }
  }

  public static void main(String[] args) {
    Pattern p = Pattern.compile(args[0]);
    System.out.println("   Pattern: " + p);
    walk(p.root, 0);
  }
}
