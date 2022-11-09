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
 * Since I have to read request Body in {@link QuotaFilter} to check if request is going to "stock" resource,
 * I need to access ether the Reader or the InputStream of request, which is more-or-less a one-time operation, 
 * so I need to "cache" it. This wrapper basically makes my call to input stream of request possible.
 * 
 * Class connected with {@link CachedBodyInputStream}
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
