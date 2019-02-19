package com.qiniu.lvheyang.lucene.fst;

import java.io.IOException;
import org.apache.lucene.store.DataInput;

public class CustomInput extends DataInput {

  @Override
  public byte readByte() throws IOException {
    return 0;
  }

  @Override
  public void readBytes(byte[] b, int offset, int len) throws IOException {

  }
}
