package com.qiniu.lvheyang.lucene.pcre;


import com.qiniu.lvheyang.lucene.pcre.regex.Matcher;
import com.qiniu.lvheyang.lucene.pcre.regex.Pattern;

public class RegexMain {

  public static void main(String[] args) {
    final String regex = "^(?<IP>[^ ]+)[^\\[\\n]*\\[(?<time_stamp>[^\\]]+)\\]\\s+\"(?<method>[^\"]+)\"\\s+(?<requesttime>[^ ]+)\\s+(?<bodybytessent>[^ ]+)\\s+(?<status>[^ ]+)\\s+(?<bytessent>[^ ]+)\\s+\"\\-\"\\s+\"(?<useragent>[^\"]+)";
    final String string = "221.12.12.194 - - [10/Jul/2015 15:51:09 +0800] \"GET /ubuntu.iso HTTP/1.0\" 0.000 129 404 168 \"-\" \"Wget/1.11.4 Red Hat modified\"\n";

    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(string);

    while (matcher.find()) {
      System.out.println("Full match: " + matcher.group(0));
      for (int i = 1; i <= matcher.groupCount(); i++) {
        System.out.println("Group " + i + ": " + matcher.group(i));
      }
    }
  }

}
