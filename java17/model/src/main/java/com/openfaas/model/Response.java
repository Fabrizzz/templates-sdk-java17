// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.model;

import java.util.HashMap;
import java.util.Map;

public class Response implements IResponse {

    private final Map<String, String> headers;
    private int statusCode = 200;
    private String body;
    private String contentType;

    public Response() {
        this.body = "";
        this.contentType = "";
        this.headers = new HashMap<>();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeader(String key, String value) {
        if (value == null) {
            if (this.headers.containsKey(key)) {
                this.headers.remove(key);
                return;
            }
        }
        this.headers.put(key, value);
    }

    public String getHeader(String key) {
        if (!this.headers.containsKey(key)) {
            return null;
        }

        return this.headers.get(key);
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
