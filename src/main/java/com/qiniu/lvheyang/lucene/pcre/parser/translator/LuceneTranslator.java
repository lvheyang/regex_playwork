package com.qiniu.lvheyang.lucene.pcre.parser.translator;

import com.qiniu.lvheyang.lucene.pcre.parser.PCREBaseVisitor;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.AlternationContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.AtomContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.CaptureContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Cc_atomContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Cc_literalContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Character_classContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.DigitContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ElementContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.ExprContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.LetterContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Non_captureContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.QuantifierContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Shared_atomContext;
import com.qiniu.lvheyang.lucene.pcre.parser.PCREParser.Shared_literalContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Operations;

public class LuceneTranslator extends PCREBaseVisitor<Automaton> {

  private Map<String, String> vals;

  public LuceneTranslator() {
    vals = new HashMap<>();
  }

  public LuceneTranslator(Map<String, String> vals) {
    this.vals = vals;
  }

  public void reset() {
    vals = new HashMap<>();
  }

  @Override
  protected Automaton defaultResult() {
    return null;
  }

  @Override
  protected Automaton aggregateResult(Automaton aggregate, Automaton nextResult) {
    if (aggregate == null) {
      return nextResult;
    }
    if (nextResult == null) {
      return aggregate;
    }
    return Operations.concatenate(aggregate, nextResult);
  }

  @Override
  public Automaton visitAlternation(AlternationContext ctx) {
    List<ExprContext> exprs = ctx.expr();
    List<Automaton> as = new ArrayList<>();
    for (ExprContext expr : exprs) {
      as.add(visit(expr));
    }
    return Operations.union(as);
  }

  @Override
  public Automaton visitElement(ElementContext ctx) {
    // TODO  暂时将quantifier_type忽略
    Automaton a = visit(ctx.atom());

    QuantifierContext q = ctx.quantifier();
    if (q == null) {
      return a;
    }

    switch (q.getChild(0).getText()) {
      case "?":
        return Operations.repeat(a, 0, 1);
      case "+":
        return Operations.repeat(a, 1);
      case "*":
        return Operations.repeat(a);
    }

    if (q.getChild(0).getText().equals("{")) {
      if (q.getChild(2).getText().equals("}")) {
        int exact = Integer.parseInt(q.getChild(1).getText());
        return Operations.repeat(a, exact, exact);
      } else if (q.getChild(3).getText().equals("}")) {
        int atLeast = Integer.parseInt(q.getChild(1).getText());
        return Operations.repeat(a, atLeast);
      } else if (q.number().size() > 1) {
        int atLeast = Integer.parseInt(q.getChild(1).getText());
        int atMost = Integer.parseInt(q.getChild(3).getText());
        return Operations.repeat(a, atLeast, atMost);
      }
    }
    return a;
  }

  @Override
  public Automaton visitAtom(AtomContext ctx) {
    if (ctx.Dot() != null) {
      return Automata.makeAnyChar();
    }
    return super.visitAtom(ctx);
  }


  @Override
  public Automaton visitLetter(LetterContext ctx) {
    return Automata.makeString(ctx.getText());
  }

  @Override
  public Automaton visitDigit(DigitContext ctx) {
    return Automata.makeString(ctx.getText());
  }

  @Override
  public Automaton visitShared_literal(Shared_literalContext ctx) {
    if (ctx.FormFeed() != null) {
      return Automata.makeChar('\f');
    }
    if (ctx.NewLine() != null) {
      return Automata.makeChar('\n');
    }
    if (ctx.CarriageReturn() != null) {
      return Automata.makeChar('\r');
    }
    if (ctx.Tab() != null) {
      return Automata.makeChar('\t');
    }
    if (ctx.Quoted() != null) {
      TerminalNode q = ctx.Quoted();
      return Automata.makeString(q.getText().substring(1));
    }
    if (ctx.BlockQuoted() != null) {
      TerminalNode q = ctx.BlockQuoted();
      String text = q.getText();
      assert text.length() >= 4;
      return Automata.makeString(text.substring(2, text.length() - 2));
    }
    for (int i = 0; i < ctx.getChildCount(); i++) {
      ParseTree c = ctx.getChild(i);
      if (c instanceof TerminalNode && c.getText() != null && !c.getText().startsWith("\\")) {
        return Automata.makeString(c.getText());
      }
    }
    return super.visitShared_literal(ctx);
  }

  @Override
  public Automaton visitShared_atom(Shared_atomContext ctx) {
    if (ctx.DecimalDigit() != null) {
      return Automata.makeCharRange('0', '9');
    }
    if (ctx.NotDecimalDigit() != null) {
      return Operations
          .complement(Automata.makeCharRange('0', '9'), Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    }
    if (ctx.WhiteSpace() != null) {
      return Operations.union(Arrays.asList(
          Automata.makeChar(' '),
          Automata.makeChar('\t'),
          Automata.makeChar('\n'),
          Automata.makeChar('\f'),
          Automata.makeChar('\r')
      ));
    }
    if (ctx.NotWhiteSpace() != null) {
      return Operations.complement(
          Operations.union(Arrays.asList(
              Automata.makeChar(' '),
              Automata.makeChar('\t'),
              Automata.makeChar('\n'),
              Automata.makeChar('\f'),
              Automata.makeChar('\r')
          )), Operations.DEFAULT_MAX_DETERMINIZED_STATES);

    }
    if (ctx.WordChar() != null) {
      return Operations.union(Arrays.asList(
          Automata.makeCharRange('a', 'z'),
          Automata.makeCharRange('A', 'Z'),
          Automata.makeCharRange('0', '9'),
          Automata.makeChar('_')
      ));
    }
    if (ctx.NotWordChar() != null) {
      return Operations.complement(
          Operations.union(Arrays.asList(
              Automata.makeCharRange('a', 'z'),
              Automata.makeCharRange('A', 'Z'),
              Automata.makeCharRange('0', '9'),
              Automata.makeChar('_')
          )), Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    }
    return super.visitShared_atom(ctx);
  }

  @Override
  public Automaton visitCharacter_class(Character_classContext ctx) {
    // TODO 只支持 [cc_atom+] 与 [^cc_atom+] 两种
    boolean negate = false;
    if (ctx.getText().startsWith("[^")) {
      negate = true;
    }
    List<Automaton> unions = new ArrayList<>();
    for (Cc_atomContext atom : ctx.cc_atom()) {
      unions.add(visit(atom));
    }
    if (negate) {
      return Operations.intersection(
          Automata.makeAnyChar(),
          Operations
              .complement(Operations.union(unions), Operations.DEFAULT_MAX_DETERMINIZED_STATES)
      );
    }
    return Operations.union(unions);
  }

  @Override
  public Automaton visitCc_atom(Cc_atomContext ctx) {
    if (ctx.cc_literal() != null && ctx.cc_literal().size() > 0) {
      List<Cc_literalContext> literals = ctx.cc_literal();
      if (literals.size() >= 2) {
        return Automata.makeCharRange(literals.get(0).getText().charAt(0),
            literals.get(1).getText().charAt(0));
      }
    }
    return super.visitCc_atom(ctx);
  }

  @Override
  public Automaton visitCc_literal(Cc_literalContext ctx) {
    if (ctx.shared_literal() != null) {
      return visit(ctx.shared_literal());
    }
    if (ctx.Dot() != null) {
      return Automata.makeAnyChar();
    }
    return super.visitCc_literal(ctx);
  }

  @Override
  public Automaton visitNon_capture(Non_captureContext ctx) {
    if (ctx.alternation() != null) {
      return visit(ctx.alternation());
    }
    return super.visitNon_capture(ctx);
  }

  @Override
  public Automaton visitCapture(CaptureContext ctx) {
    if (ctx.name() != null) {
      String name = ctx.name().getText();
      if (vals != null && vals.containsKey(name)) {
        System.out.println("vals.get(name) = " + vals.get(name));
        return Automata.makeString(vals.get(name));
      }
    }
    return visit(ctx.alternation());
  }
}
