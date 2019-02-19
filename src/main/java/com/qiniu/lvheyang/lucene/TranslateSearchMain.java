package com.qiniu.lvheyang.lucene;

import com.qiniu.lvheyang.lucene.pcre.parser.PCRELexer;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ParseContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREVisitor;
import com.qiniu.lvheyang.lucene.pcre.parser.translator.LuceneTranslator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.file.Paths;
import java.util.Collections;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class TranslateSearchMain {

  public static void main(String[] args) throws IOException, QueryNodeException {

    String regex = "^(?P<ip>[^ ]+)[^\\[\\n]*\\[(?P<time>[^\\]]+)\\]\\s+\"(?P<method>[^ ]+)\\s+(?P<url>[^ ]+)\\s+(?P<protocol>[^\"]+)\"\\s+(?P<status>[^ ]+)\\s+(?P<size>[\\s\\S]+)";
    InputStream inputStream = new StringBufferInputStream(regex);
    Lexer lexer = new PCRELexer(CharStreams.fromStream(inputStream));
    TokenStream tokenStream = new CommonTokenStream(lexer);
    PCREParser pcreParser = new PCREParser(tokenStream);
    @SuppressWarnings("unused")
    ParseContext mainQ = pcreParser.parse();
//    PCREVisitor<String> visitor = new LuceneTranslator(Collections.emptyMap());
//    String result = visitor.visit(mainQ);
//    System.out.println("result = " + result);
//
//    IndexReader reader = StandardDirectoryReader
//        .open(FSDirectory.open(Paths.get("/Users/wenzhengcui/tmp/test")));
//    IndexSearcher searcher = new IndexSearcher(reader);
//    StandardQueryParser parser = new StandardQueryParser();
//    Query q = parser.parse("/^([^ ]+)[^\\[\\n]*\\[([^\\]]+)\\][ \\t\\n\\x0B\\f\\r]+\"([^ ]+)[ \\t\\n\\x0B\\f\\r]+([^ ]+)[ \\t\\n\\x0B\\f\\r]+([^\"]+)\"[ \\t\\n\\x0B\\f\\r]+([^ ]+)[ \\t\\n\\x0B\\f\\r]+(.+)/", "log");
//
//    var start = System.nanoTime();
//    for (int i = 0; i < 1; i++) {
//      TopDocs docs = SearchMain.search(searcher, q, 10);
//      for (int j = 0; j < 10; j++) {
//        System.out.println("docs.totalHits = " + reader.document(docs.scoreDocs[j].doc));
//      }
//    }
//    System.out.println("System.nanoTime() -start = " + (System.nanoTime() - start) / 1000000);
  }


}
