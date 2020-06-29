package com.diamondq.maply.impl;

import com.diamondq.common.mediatype.CommonMediaTypes;
import com.diamondq.maply.api.MapContext;
import com.diamondq.maply.api.MapObject;
import com.diamondq.maply.spi.MapBytesData;
import com.diamondq.maply.spi.MapObjectContentLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Singleton
public class XMLMapObjectContentLoader implements MapObjectContentLoader {

  private static final Set<MediaType> sSUPPORTED_MEDIA_TYPES;

  static {
    ImmutableSet.Builder<MediaType> builder = ImmutableSet.builder();
    builder.add(CommonMediaTypes.APPLICATION_XML);
    builder.add(CommonMediaTypes.TEXT_XML);
    sSUPPORTED_MEDIA_TYPES = builder.build();
  }

  @Inject
  public XMLMapObjectContentLoader() {
  }

  /**
   * @see com.diamondq.maply.spi.MapObjectContentLoader#getSupportedMediaTypes()
   */
  @Override
  public Set<MediaType> getSupportedMediaTypes() {
    return sSUPPORTED_MEDIA_TYPES;
  }

  /**
   * @see com.diamondq.maply.spi.MapObjectContentLoader#supportsContent(boolean, com.google.common.net.MediaType)
   */
  @Override
  public boolean supportsContent(boolean pIsLoad, MediaType pMediaType) {
    return true;
  }

  /**
   * @see com.diamondq.maply.spi.MapObjectContentLoader#load(com.diamondq.maply.api.MapContext, java.net.URI,
   *      java.lang.String, com.diamondq.maply.spi.MapBytesData)
   */
  @Override
  public MapObject load(MapContext pContext, URI pOriginalURI, String pIdentifier, MapBytesData pBytesData) {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      try (ByteArrayInputStream bais = new ByteArrayInputStream(pBytesData.data)) {
        Document document = documentBuilder.parse(bais);
        return new XMLMapObject(pBytesData.contentType, pIdentifier, document);
      }
    }
    catch (IOException | SAXException | ParserConfigurationException ex) {
      throw new RuntimeException(ex);
    }

  }

  /**
   * @see com.diamondq.maply.spi.MapObjectContentLoader#save(com.diamondq.maply.api.MapContext,
   *      com.google.common.net.MediaType, com.diamondq.maply.api.MapObject)
   */
  @Override
  public byte[] save(MapContext pContext, MediaType pMediaType, MapObject pMapObject) {
    throw new UnsupportedOperationException();
  }

}
