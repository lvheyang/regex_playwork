package com.qiniu.lvheyang.lucene.fsa;

import com.qiniu.lvheyang.lucene.SearchMain;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Operations;

public class AutomatonMain {

  public static void main(String[] args) throws IOException {
    IndexReader reader = StandardDirectoryReader
        .open(FSDirectory.open(Paths.get("/Users/wenzhengcui/tmp/test")));
    IndexSearcher searcher = new IndexSearcher(reader);
//    Query q = new AutomatonQuery(new Term("log", ""),
//        stringUnionAutomaton(
//            "127.0.0.1 - - [2019-01-31T20:37:10.543+08:00] \"GET /index.php HTTP/1.1\" 404 207",
//            "127.0.0.1 - - [2019-02-01T17:51:16.095+08:00] \"GET /index.php HTTP/1.1\" 404 207"));
    Query q = new AutomatonQuery(new Term("log", ""),
        Operations.concatenate(Automata.makeString("127.0.0.1 - - [2019-01-31T20:37:10.5"),
            Automata.makeAnyString()));

    int numHits = 10;
    for (int i = 0; i < 1; i++) {
      TopDocs docs = SearchMain.search(searcher, q, numHits);
      System.out.println("docs.totalHits = " + docs.totalHits);
      long num = Math.min(docs.totalHits, numHits);
      for (int j = 0; j < num; j++) {
        System.out.println("docs.totalHits = " + reader.document(docs.scoreDocs[j].doc));
      }
    }
  }

}
