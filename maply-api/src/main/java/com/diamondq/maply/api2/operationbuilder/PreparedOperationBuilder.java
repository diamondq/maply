package com.diamondq.maply.api2.operationbuilder;

import com.diamondq.maply.api2.PreparedOperation;

public interface PreparedOperationBuilder {

  public PreparedOperation build();

  public XPathBuilder<PreparedOperationBuilder> want();

  public PreparedOperationBuilder want(String pXPath);

}
