package com.osa.stocky.api.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

/**
 *
 * @author oleksii
 * @since 7 Nov 2022
 */
public class CachedBodyHttpRequest extends HttpServletRequestWrapper {

    private byte[] body;
    
    public CachedBodyHttpRequest(HttpServletRequest request) throws IOException {
        super(request);
        this.body = StreamUtils.copyToByteArray(request.getInputStream());
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyInputStream(this.body);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.body)));
    }
    
}
