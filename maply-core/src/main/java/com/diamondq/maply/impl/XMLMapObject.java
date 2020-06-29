package com.diamondq.maply.impl;

import com.diamondq.maply.spi.AbstractMapObject;
import com.google.common.net.MediaType;

import org.apache.commons.jxpath.Container;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Document;

public class XMLMapObject extends AbstractMapObject implements Container {

  private static final long serialVersionUID = 6473308300858968919L;

  public final Document     document;

  public XMLMapObject(MediaType pMediaType, String pIdentifier, Document pDocument) {
    super(pMediaType, pIdentifier);
    document = pDocument;
  }

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception {
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
