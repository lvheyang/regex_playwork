package com.qiniu.lvheyang.lucene.pcre;

import com.qiniu.lvheyang.lucene.pcre.parser.PCRELexer;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ParseContext;
import com.qiniu.lvheyang.lucene.pcre.parser.translator.LuceneTranslator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Operations;

public class AutomatonMain {

  public static void main(String[] args) throws IOException {
    String regex = "[^ab]+";

    Reader r = new StringReader(regex);
    Lexer lexer = new PCRELexer(CharStreams.fromReader(r));
    TokenStream tokenStream = new CommonTokenStream(lexer);
    PCREParser parser = new PCREParser(tokenStream);
    @SuppressWarnings("unused")
    ParseContext antlrParser = parser.parse();

    LuceneTranslator l = new LuceneTranslator();
    Automaton a=  l.visit(antlrParser);
    System.out.println("a.toDot() = " + a.toDot());
    System.out.println("a = " + Operations.run(a, "a")); // false
    System.out.println("a = " + Operations.run(a, "b")); // false
    System.out.println("a = " + Operations.run(a, "d")); // true
    System.out.println("a = " + Operations.run(a, "aabba")); // false
    System.out.println("a = " + Operations.run(a, "aabda")); // false
    System.out.println("a = " + Operations.run(a, "defd")); // true


  }

}
