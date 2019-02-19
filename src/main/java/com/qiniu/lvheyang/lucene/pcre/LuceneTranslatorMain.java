package com.qiniu.lvheyang.lucene.pcre;

import com.qiniu.lvheyang.lucene.SearchMain;
import com.qiniu.lvheyang.lucene.pcre.parser.PCRELexer;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ParseContext;
import com.qiniu.lvheyang.lucene.pcre.parser.translator.LuceneTranslator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Operations;

public class LuceneTranslatorMain {

  public static void main(String[] args) throws IOException {
    String regex = "^(?P<ip>[^ ]+)[^\\[\\n]*\\[(?P<time>[^\\]]+)\\]\\s+\"(?P<method>[^ ]+)\\s+(?P<url>[^ ]+)\\s+(?P<protocol>[^\"]+)\"\\s+(?P<status>[^ ]+)\\s+(?P<size>[\\s\\S]+)";
    InputStream inputStream = new StringBufferInputStream(regex);
    Lexer lexer = new PCRELexer(CharStreams.fromStream(inputStream));
    TokenStream tokenStream = new CommonTokenStream(lexer);
    PCREParser parser = new PCREParser(tokenStream);
    @SuppressWarnings("unused")
    ParseContext antlrParser = parser.parse();

    Map<String, String> vals = new HashMap<>();
    vals.put("ip", "127.0.0.1");
    vals.put("time", "2019-02-01T17:51:15.583+08:00");

    LuceneTranslator visitor = new LuceneTranslator(vals);
    Automaton automa = visitor.visit(antlrParser);
//    System.out.println("result.toDot() = " + automa.toDot());
//    System.out.println("LuceneTranslatorMain " + Operations.run(automa, "abde"));

    IndexReader reader = StandardDirectoryReader
        .open(FSDirectory.open(Paths.get("/Users/wenzhengcui/tmp/test")));
    IndexSearcher searcher = new IndexSearcher(reader);
    Query q = new AutomatonQuery(new Term("log"), automa);

    var start = System.nanoTime();
    for (int i = 0; i < 1; i++) {
      TopDocs docs = SearchMain.search(searcher, q, 10);
      for (int j = 0; j < 10; j++) {
        System.out.println("docs.totalHits = " + reader.document(docs.scoreDocs[j].doc));
      }
    }
    System.out.println("System.nanoTime() -start = " + (System.nanoTime() - start) / 1000000);
  }

}
