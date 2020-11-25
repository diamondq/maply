package com.diamondq.maply.pdf;

import com.diamondq.common.mediatype.CommonMediaTypes;
import com.diamondq.maply.advapi.MapContext;
import com.diamondq.maply.advapi.MapObject;
import com.diamondq.maply.spi.old.MapBytesData;
import com.diamondq.maply.spi.old.MapObjectContentLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

@Singleton
public class PDFMapObjectContentLoader implements MapObjectContentLoader {

  private static final Set<MediaType> sSUPPORTED_MEDIA_TYPES;

  static {
    sSUPPORTED_MEDIA_TYPES = ImmutableSet.of(CommonMediaTypes.APPLICATION_PDF);
  }

  @Inject
  public PDFMapObjectContentLoader() {
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#getSupportedMediaTypes()
   */
  @Override
  public Set<MediaType> getSupportedMediaTypes() {
    return sSUPPORTED_MEDIA_TYPES;
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#supportsContent(boolean, com.google.common.net.MediaType)
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
      PDDocument pdfDocument = PDDocument.load(pBytesData.data);
      return new PDFMapObject(pBytesData.contentType, pIdentifier, pdfDocument);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.maply.spi.old.MapObjectContentLoader#save(com.diamondq.maply.advapi.MapContext,
   *      com.google.common.net.MediaType, com.diamondq.maply.advapi.MapObject)
   */
  @Override
  public byte[] save(MapContext pContext, MediaType pMediaType, MapObject pMapObject) {
    try {
      if ((pMapObject instanceof PDFMapObject) == false)
        throw new IllegalArgumentException("Only PDFMapObject's are supported");
      PDFMapObject pmo = (PDFMapObject) pMapObject;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      pmo.document.save(baos);
      return baos.toByteArray();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
