package com.diamondq.maply.pdf;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

public class DumpModel {

  private static Map<Object, String> sObjVisited = new HashMap<>();

  public static void main(String[] args) throws Throwable {

    PDDocument pdfDocument = PDDocument.load(new File(args[0]));

    pdfDocument.getDocumentCatalog();
    recurse("", pdfDocument);

  }

  private static void recurse(String pPath, Object pObj) {
    if (sObjVisited.putIfAbsent(pObj, pPath) != null) {
      System.out.println("Same 'obj' detected at " + sObjVisited.get(pObj));
      return;
    }
    Class<?> clazz = pObj.getClass();
    for (Method method : clazz.getMethods()) {
      if ((method.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
        continue;
      String methodName = method.getName();
      if ((methodName.startsWith("get") == true) && (method.getParameterCount() == 0)) {
        if ((methodName.equals("getClass") == true) || (methodName.equals("getCOSObject") == true))
          continue;
        String name = methodName.substring(3, 4).toLowerCase(Locale.ENGLISH) + methodName.substring(4);
        Object result;
        try {
          result = method.invoke(pObj, (Object[]) null);
        }
        catch (InvocationTargetException ex) {
          result = null;
        }
        catch (IllegalAccessException | IllegalArgumentException ex) {
          throw new RuntimeException(ex);
        }
        if (result == null) {
          String path = pPath + "/" + name;
          System.out.println(path + " = null");
        }
        else {
          String path = pPath + "/" + name;
          if ((result instanceof Integer) || (result instanceof Long) || (result instanceof Float)
            || (result instanceof Double) || (result instanceof String)) {
            System.out.println(path + " = " + result.toString());
          }
          else if (result instanceof List) {
            System.out.println(path + " = (" + result.getClass().getSimpleName() + ")");
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) result;
            int offset = -1;
            for (Object obj : list) {
              offset++;
              String childPath = path + "[" + String.valueOf(offset) + "]";
              System.out.println(childPath + " = (" + obj.getClass().getSimpleName() + ")");
              recurse(childPath, obj);
            }
          }
          else {
            System.out.println(path + " = (" + result.getClass().getSimpleName() + ")");
            recurse(path, result);
          }
        }
      }
    }

  }
}
