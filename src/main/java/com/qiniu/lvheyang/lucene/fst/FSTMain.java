package com.qiniu.lvheyang.lucene.fst;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.FST.BytesReader;
import org.apache.lucene.util.fst.FST.INPUT_TYPE;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;

public class FSTMain {

  public static void main(String[] args) throws IOException {
    // Input values (keys). These must be provided to Builder in Unicode sorted order!
    BytesRef inputValues[] = {
        new BytesRef("1aaaaaaaaaa"),
        new BytesRef("2aaaaaaaaaa"),
        new BytesRef("3aaaaaaaaaa"),
        new BytesRef("4aaaaaaaaaa"),
        new BytesRef("5aaaaaaaaaa"),
        new BytesRef("6aaaaaaaaaa"),
        new BytesRef("7aaaaaaaaaa"),
        new BytesRef("8aaaaaaaaaa"),
        new BytesRef("9aaaaaaaaaa"),
    };
    long outputValues[] = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
    Builder<Long> builder = new Builder<>(INPUT_TYPE.BYTE1, outputs);
    BytesRef scratchBytes = new BytesRef();
    BytesRefBuilder scratchBytesBuilder = new BytesRefBuilder();
    IntsRefBuilder scratchInts = new IntsRefBuilder();
    for (int i = 0; i < inputValues.length; i++) {
      scratchBytesBuilder.copyBytes(inputValues[i]);
      scratchBytes = scratchBytesBuilder.toBytesRef();
      builder.add(Util.toIntsRef(scratchBytes, scratchInts), outputValues[i]);
    }
    FST<Long> fst = builder.finish();

    // Retrieval by key:
    BytesRef input = new BytesRef("cat5");
    System.out.println("input = " + input);
//    Long value = Util.get(fst, new BytesRef("cat1"));
    final BytesReader fstReader = fst.getBytesReader();

    // TODO: would be nice not to alloc this on every lookup
    final FST.Arc<Long> arc = fst.getFirstArc(new FST.Arc<>());

    // Accumulate output as we go
    Long output = fst.outputs.getNoOutput();
    for (int i = 0; i < input.length; i++) {
      if (fst.findTargetArc(input.bytes[i + input.offset] & 0xFF, arc, arc, fstReader) == null) {
        return;
      }
      output = fst.outputs.add(output, arc.output);
    }
    if (arc.isFinal()) {
      fst.outputs.add(output, arc.nextFinalOutput);
    }
    System.out.println(output); // 7

    // Retrieval by value:
//    IntsRef key = Util.getByOutput(fst, 12);
//    System.out.println(Util.toBytesRef(key, scratchBytesBuilder).utf8ToString()); // dogs

    // Like TermsEnum, this also supports seeking (advance)
//    BytesRefFSTEnum<Long> iterator = new BytesRefFSTEnum<>(fst);
//    while (iterator.next() != null) {
//      InputOutput<Long> mapEntry = iterator.current();
//      System.out.println(mapEntry.input.utf8ToString());
//      System.out.println(mapEntry.output);
//    }

  }

}
