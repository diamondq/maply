package com.diamondq.maply.pdf;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class PDFNodePointerFactory implements NodePointerFactory {

  @Inject
  public PDFNodePointerFactory() {
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#getOrder()
   */
  @Override
  public int getOrder() {
    return 0;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.QName,
   *      java.lang.Object, java.util.Locale)
   */
  @Override
  public @Nullable NodePointer createNodePointer(QName pName, Object pObject, Locale pLocale) {
    return null;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.model.NodePointer,
   *      org.apache.commons.jxpath.ri.QName, java.lang.Object)
   */
  @Override
  public @Nullable NodePointer createNodePointer(NodePointer pParent, QName pName, Object pObject) {
    // return pObject instanceof PDFMapObject ? new PDFNodePointer(pParent, pName, (PDFMapObject) pObject) : null;
    return null;
  }

}
