package com.diamondq.maply.pdf;

import java.io.File;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.pdfbox.pdmodel.PDDocument;

public class JXTest {

  // documentCatalog/acroForm/fields[fullyQualifiedName='Given Name Text Box']/value

  public static void maina(String[] args) throws Throwable {

    PDDocument pdfDocument = PDDocument.load(new File(args[0]));

    JXPathContext context = JXPathContext.newContext(pdfDocument);
    System.out.println("XPath: " + args[1]);
    Pointer pointer = context.getPointer(args[1]);
    if (pointer == null)
      System.out.println("NULL");
    else {
      System.out.println(pointer.getClass().getSimpleName() + " => " + pointer.toString());
      Object value = pointer.getValue();
      System.out.println(
        "Value = " + (value == null ? "(NULL)" : value.getClass().getSimpleName() + " => " + value.toString()));
    }
  }

  public static void main(String[] args) throws Throwable {

    PDDocument pdfDocument = PDDocument.load(new File(args[0]));

    JXPathContext context = JXPathContext.newContext(pdfDocument);
    System.out.println("XPath: " + args[1]);
    context.setValue(args[1], "Test");
  }

}
