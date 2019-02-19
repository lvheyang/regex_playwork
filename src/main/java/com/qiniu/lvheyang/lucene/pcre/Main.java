package com.qiniu.lvheyang.lucene.pcre;

import com.qiniu.lvheyang.lucene.pcre.parser.PCRELexer;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ParseContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREVisitor;
import com.qiniu.lvheyang.lucene.pcre.parser.translator.JavaTranslator;
import com.qiniu.lvheyang.lucene.pcre.regex.Matcher;
import com.qiniu.lvheyang.lucene.pcre.regex.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;

public class Main {

  public static void main(String[] args) throws IOException {
    /* get the input file as an InputStream
     */

    String regex = "^(?P<ip>[^ ]+)[^\\[\\n]*\\[(?P<time>[^\\]]+)\\]\\s+\"(?P<method>[^ ]+)\\s+(?P<url>[^ ]+)\\s+(?P<protocol>[^\"]+)\"\\s+(?P<status>[^ ]+)\\s+(?P<size>[\\s\\S]+)";
    InputStream inputStream = new StringBufferInputStream(regex);
    /*
     * make Lexer
     */
    Lexer lexer = new PCRELexer(CharStreams.fromStream(inputStream));
    /*
     * get a TokenStream on the Lexer
     */
    TokenStream tokenStream = new CommonTokenStream(lexer);
    /*
     * make a Parser on the token stream
     */
    PCREParser parser = new PCREParser(tokenStream);
    /*
     * get the top node of the AST. This corresponds to the topmost rule of equation.q4, "equation"
     */
    @SuppressWarnings("unused")
    ParseContext mainQ = parser.parse();

    PCREVisitor<String> visitor = new JavaTranslator();
    String result = visitor.visit(mainQ);
    System.out.println("result = " + result);
    System.out.println("regex = " + regex);
    final String string = "127.0.0.1 - - [2019-02-01T17:51:15.583+08:00] \"GET /index.php HTTP/1.1\" 404 207";
    final Pattern pattern = Pattern.compile(result, Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(string);

    while (matcher.find()) {
      System.out.println("Full match: " + matcher.group(0));
      for (int i = 1; i <= matcher.groupCount(); i++) {
        System.out.println("Group " + i + ": " + matcher.group(i));
      }
    }

  }


}



