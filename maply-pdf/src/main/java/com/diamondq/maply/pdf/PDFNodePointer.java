package com.diamondq.maply.pdf;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFNodePointer extends NodePointer {

  private static final long   serialVersionUID = -1296008897669489549L;

  @SuppressWarnings("unused")
  private static final Logger sLogger          = LoggerFactory.getLogger(PDFNodePointer.class);

  private final PDDocument    mDocument;

  private final QName         mName;

  public PDFNodePointer(NodePointer pParent, QName pName, PDFMapObject pMapObject) {
    super(pParent);
    mName = pName;
    mDocument = pMapObject.document;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#isLeaf()
   */
  @Override
  public boolean isLeaf() {
    return false;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#isCollection()
   */
  @Override
  public boolean isCollection() {
    return true;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#getLength()
   */
  @Override
  public int getLength() {
    return 1;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#getName()
   */
  @Override
  public @Nullable QName getName() {
    return mName;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#getBaseValue()
   */
  @Override
  public Object getBaseValue() {
    return mDocument;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#getImmediateNode()
   */
  @Override
  public Object getImmediateNode() {
    return mDocument;
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#setValue(java.lang.Object)
   */
  @Override
  public void setValue(@Nullable Object pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#childIterator(org.apache.commons.jxpath.ri.compiler.NodeTest,
   *      boolean, org.apache.commons.jxpath.ri.model.NodePointer)
   */
  @Override
  public NodeIterator childIterator(@Nullable NodeTest pTest, boolean pReverse, @Nullable NodePointer pStartWith) {
    throw new UnsupportedOperationException();

  }

  /**
   * @see org.apache.commons.jxpath.ri.model.NodePointer#compareChildNodePointers(org.apache.commons.jxpath.ri.model.NodePointer,
   *      org.apache.commons.jxpath.ri.model.NodePointer)
   */
  @Override
  public int compareChildNodePointers(NodePointer pPointer1, NodePointer pPointer2) {
    throw new UnsupportedOperationException();

  }

}
