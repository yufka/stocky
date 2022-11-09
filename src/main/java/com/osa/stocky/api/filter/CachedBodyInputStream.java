package com.osa.stocky.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * See here : {@link CachedBodyHttpRequest}
 * @author oleksii
 * @since 7 Nov 2022
 */
public class CachedBodyInputStream extends ServletInputStream {

    private final InputStream bodyInputStream;
    
    public CachedBodyInputStream(byte[] cachedBody) {
        this.bodyInputStream = new ByteArrayInputStream(cachedBody);
    }
    
    @SuppressWarnings("all")
    @Override
    public boolean isFinished() {
        try {
            return bodyInputStream.available() == 0;
        } catch (IOException e) {
        }
        return false;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
        return bodyInputStream.read();
    }
}
