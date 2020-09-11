package com.moviired.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

final class MutableHttpServletRequest extends HttpServletRequestWrapper {
    private Map<String, String> headerMap = new HashMap<>();

    /**
     * construct a wrapper for this request
     *
     * @param request
     */
    MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * add a header with given name and value
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headerMap.containsKey(name)) {
            headerValue = headerMap.get(name);
        }
        return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        for (String name : headerMap.keySet()) {
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = new ArrayList<>();
        String val = headerMap.get(name);
        if (val != null) {
            values.add(val);
        } else {
            values = Collections.list(super.getHeaders(name));
        }

        return Collections.enumeration(values);
    }
}

