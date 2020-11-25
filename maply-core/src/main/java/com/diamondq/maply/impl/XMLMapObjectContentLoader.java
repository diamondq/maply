package com.diamondq.maply.impl;

import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapObject;
import com.diamondq.maply.spi.old.MapBytesData;
import com.diamondq.maply.spi.old.MapObjectContentLoader;
import com.google.common.collect.ImmutableSet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tika.mime.MediaType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Singleton
public class XMLMapObjectContentLoader implements MapObjectContentLoader {

  private static final Set<MediaType> sSUPPORTED_MEDIA_TYPES;

  static {
    ImmutableSet.Builder<MediaType> builder = ImmutableSet.builder();
    builder.add(MediaType.APPLICATION_XML);
    builder.add(MediaType.text("xml"));
    sSUPPORTED_MEDIA_TYPES = builder.build();
  }

  @Inject
  public XMLMapObjectContentLoader() {
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#getSupportedMediaTypes()
   */
  @Override
  public Set<MediaType> getSupportedMediaTypes() {
    return sSUPPORTED_MEDIA_TYPES;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#supportsContent(boolean, org.apache.tika.mime.MediaType)
   */
  @Override
  public boolean supportsContent(boolean pIsLoad, MediaType pMediaType) {
    return true;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#load(com.diamondq.maply.advapi.MapContext, java.net.URI,
   *      java.lang.String, com.diamondq.maply.spi.old.MapBytesData)
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
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#save(com.diamondq.maply.advapi.MapContext,
   *      org.apache.tika.mime.MediaType, com.diamondq.maply.advapi.MapObject)
   */
  @Override
  public byte[] save(MapContext pContext, MediaType pMediaType, MapObject pMapObject) {
    throw new UnsupportedOperationException();
  }

}
