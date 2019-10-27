package com.cu.thesis.WuMuBPMN.helper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
//import java.util.stream.IntStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

public class Input implements LSInput {

    private static final Logger LOGGER=LoggerFactory.getLogger(Input.class);

    private String publicId;

    private String systemId;

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getBaseURI() {
        return null;
    }

    public InputStream getByteStream() {
        return null;
    }

    public boolean getCertifiedText() {
        return false;
    }

    public Reader getCharacterStream() {
        return null;
    }

    public String getEncoding() {
        return null;
    }

    public String getStringData() {
        synchronized (inputStream) {
            String contents = "";
            try {
                byte[] input = new byte[inputStream.available()];
                inputStream.read(input);
                contents = new String(input, StandardCharsets.UTF_8);
                contents = contents.trim();
                if (contents.charAt(0) != '<')
                {
                    contents = contents.substring(1, contents.length());
                }
                return contents;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception " + e);
                LOGGER.debug("getStringData", e);
                return null;
            } catch (Exception e){
                System.out.println("getStringData Exception " + e);
                LOGGER.debug("getStringData ex", e);
                return null;
            }
        }
    }

    public static InputStream toUTF8InputStream(String str) {
        InputStream is = null;
        try {
          is = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
          // UTF-8 should always be supported
          throw new AssertionError();
        }
        return is;
      }
    
    public void setBaseURI(String baseURI) {
    }
    
    public void setByteStream(InputStream byteStream) {
    }
    
    public void setCertifiedText(boolean certifiedText) {
    }
    
    public void setCharacterStream(Reader characterStream) {
    }
    
    public void setEncoding(String encoding) {
    }
    
    public void setStringData(String stringData) {
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public BufferedInputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    private BufferedInputStream inputStream;
    
    public Input(String publicId, String sysId, InputStream input) {
        this.publicId = publicId;
        this.systemId = sysId;
        this.inputStream = new BufferedInputStream(input);
    }
}