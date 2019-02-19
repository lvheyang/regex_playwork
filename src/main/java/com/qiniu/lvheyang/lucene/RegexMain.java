package com.qiniu.lvheyang.lucene;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMain {

  public static void main(String[] args) {
    Pattern p = Pattern.compile("([a-zA-Z0-9_\\.]+)=\"?([a-zA-Z0-9_\\.]+)");
    String text = "a=1 b=2 c=fda d=fda";
    Matcher m = p.matcher(text);
    System.out.println("m.matches() = " + m.find());
    for (int i = 0; i < m.groupCount() + 1; i++) {
      System.out.println("m.group(i) = " + m.group(i));
    }
    System.out.println("m.matches() = " + m.find());
    for (int i = 0; i < m.groupCount() + 1; i++) {
      System.out.println("m.group(i) = " + m.group(i));
    }
    System.out.println("m.matches() = " + m.find());
    for (int i = 0; i < m.groupCount() + 1; i++) {
      System.out.println("m.group(i) = " + m.group(i));
    }
    System.out.println("m.matches() = " + m.find());
    for (int i = 0; i < m.groupCount() + 1; i++) {
      System.out.println("m.group(i) = " + m.group(i));
    }
    System.out.println("m.matches() = " + m.find());
    for (int i = 0; i < m.groupCount() + 1; i++) {
      System.out.println("m.group(i) = " + m.group(i));
    }
  }

}
