package com.diamondq.maply.pdf;

import com.diamondq.maply.spi.AbstractMapObject;
import com.google.common.net.MediaType;

import org.apache.commons.jxpath.Container;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PDFMapObject extends AbstractMapObject implements Container {

  private static final long serialVersionUID = -4549384711631090013L;

  public final PDDocument   document;

  public PDFMapObject(MediaType pMediaType, String pIdentifier, PDDocument pDocument) {
    super(pMediaType, pIdentifier);
    document = pDocument;
  }

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
    document.close();
  }

  /**
   * @see org.apache.commons.jxpath.Container#getValue()
   */
  @Override
  public Object getValue() {
    return document;
  }

  /**
   * @see org.apache.commons.jxpath.Container#setValue(java.lang.Object)
   */
  @Override
  public void setValue(@Nullable Object pValue) {
    throw new UnsupportedOperationException();
  }
}
