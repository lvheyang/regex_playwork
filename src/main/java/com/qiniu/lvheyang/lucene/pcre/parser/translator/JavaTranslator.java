package com.qiniu.lvheyang.lucene.pcre.parser.translator;

import com.qiniu.lvheyang.lucene.pcre.parser.PCREBaseVisitor;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.AlternationContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.AtomContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.BackreferenceContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Backreference_or_octalContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.CaptureContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Character_classContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.CommentContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ConditionalContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ExprContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Look_aroundContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.NameContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Non_captureContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.NumberContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Octal_charContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.OptionContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.QuantifierContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Shared_atomContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Shared_literalContext;
import com.qiniu.lvheyang.lucene.pcre.parser.RegexException;
import java.util.List;
import java.util.StringJoiner;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JavaTranslator extends PCREBaseVisitor<String> {

  @Override
  protected String defaultResult() {
    return "";
  }

  @Override
  protected String aggregateResult(String aggregate, String nextResult) {
    return aggregate + nextResult;
  }

  @Override
  public String visitAlternation(AlternationContext ctx) {
    List<ExprContext> exprs = ctx.expr();
    StringJoiner sj = new StringJoiner("|");
    for (ExprContext expr : exprs) {
      sj.add(visit(expr));
    }
    return sj.toString();
  }

  @Override
  public String visitQuantifier(QuantifierContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitCharacter_class(Character_classContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitCapture(CaptureContext ctx) {
    if (ctx.name() != null) {
      return "(?<" + visit(ctx.name()) + ">" + visit(ctx.alternation()) + ")";
    } else {
      return "(" + visit(ctx.alternation()) + ")";
    }
  }

  @Override
  public String visitName(NameContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitNumber(NumberContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitShared_literal(Shared_literalContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitOctal_char(Octal_charContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitBackreference_or_octal(Backreference_or_octalContext ctx) {
    return ctx.getText();
  }


  @Override
  public String visitBackreference(BackreferenceContext ctx) {
    // TODO test
    if (ctx.name() != null) {
      return "\\k<" + ctx.name() + ">";
    } else if (ctx.backreference_or_octal() != null) {
      return visit(ctx.backreference_or_octal());
    } else if (ctx.number() != null) {
      return "\\" + visit(ctx.number());
    }
    return super.visitBackreference(ctx);
  }

  @Override
  public String visitNon_capture(Non_captureContext ctx) {
    return "(?:" + visit(ctx.alternation()) + ")";
  }

  @Override
  public String visitComment(CommentContext ctx) {
    // java does not support comment
    throw new RegexException("Comment not supported:" + ctx.getText());
  }

  @Override
  public String visitOption(OptionContext ctx) {
    throw new RegexException("Option not supported:" + ctx.getText());
  }

  @Override
  public String visitLook_around(Look_aroundContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitConditional(ConditionalContext ctx) {
    throw new RegexException("Conditional not supported:" + ctx.getText());
  }

  @Override
  public String visitShared_atom(Shared_atomContext ctx) {
    return ctx.getText();
  }


  @Override
  public String visitAtom(AtomContext ctx) {
    for (int i = 0; i < ctx.getChildCount(); i++) {
      ParseTree child = ctx.getChild(i);
      if (child instanceof TerminalNode) {
        return ((TerminalNode) child).getSymbol().getText();
      }
    }
    return super.visitAtom(ctx);
  }
}
