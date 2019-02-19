package com.qiniu.lvheyang.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.CollectorManager;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

public class SearchMain {

  public static void main(String[] args) throws IOException, QueryNodeException {
    IndexReader reader = StandardDirectoryReader
        .open(FSDirectory.open(Paths.get("/Users/wenzhengcui/tmp/test")));
    IndexSearcher searcher = new IndexSearcher(reader);
    StandardQueryParser parser = new StandardQueryParser();
//    Query q = parser.parse("log:/127\\.0\\.0\\.1 - - \\[2019-01-31T20:37:10\\.543.*/", "log");
//    Query q = parser.parse("log:/.*10\\.543.*/", "log");
    Query q = parser.parse("log:/.*/", "log");

    var start = System.nanoTime();
    for (int i = 0; i < 1; i++) {
      TopDocs docs = SearchMain.search(searcher, q, 10);
      for (int j = 0; j < 10; j++) {
        System.out.println("docs.totalHits = " + reader.document(docs.scoreDocs[j].doc));

      }
    }
    System.out.println("System.nanoTime() -start = " + (System.nanoTime() - start) / 1000000);
  }

  public static TopDocs search(IndexSearcher searcher, Query query, int numHits)
      throws IOException {
    final int limit = Math.max(1, searcher.getIndexReader().maxDoc());
    final int cappedNumHits = Math.min(numHits, limit);

    final CollectorManager<TopScoreDocCollector, TopDocs> manager = new CollectorManager<>() {

      @Override
      public TopScoreDocCollector newCollector() {
        return TopScoreDocCollector.create(cappedNumHits, null);
      }

      @Override
      public TopDocs reduce(Collection<TopScoreDocCollector> collectors) throws IOException {
        final TopDocs[] topDocs = new TopDocs[collectors.size()];
        int i = 0;
        for (TopScoreDocCollector collector : collectors) {
          topDocs[i++] = collector.topDocs();
        }
        return TopDocs.merge(0, cappedNumHits, topDocs, true);
      }

    };

    return searcher.search(query, manager);
  }


}
