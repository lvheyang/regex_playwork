package com.qiniu.lvheyang.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.joda.time.DateTime;

public class WriteMain {

  public static void main(String[] args) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig();
    IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get("/Users/wenzhengcui/tmp/test")),
        config);
    for (int i = 0; i < 1000000; i++) {
      Document doc = new Document();
      String log = String
          .format("127.0.0.%s - - [%s] \"GET /index.php HTTP/1.1\" 404 207", i, new DateTime());
      doc.add(new StringField("log", log, Store.YES));
      writer.addDocument(doc);
      System.out.println("log = " + log);
    }
//    writer.forceMerge(1);
    writer.close();
  }
}


