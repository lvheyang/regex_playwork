package com.qiniu.lvheyang.lucene.queryparser;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;

public class QueryParserMain {

  public static void main(String[] args) {
    StandardQueryParser parser = new StandardQueryParser();
    Query q;
    try {
      q = parser.parse("(machine:sg* AND service:\"BOOTS-BROKER\" ) AND response_code:> OR 200", "");
    } catch (QueryNodeException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
    System.out.println("q = " + q);
  }

}
