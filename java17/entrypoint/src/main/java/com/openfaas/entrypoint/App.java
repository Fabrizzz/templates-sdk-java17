// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.entrypoint;

import com.openfaas.model.IHandler;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.http.spi.JettyHttpServerProvider;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) throws Exception {
        int port = 8082;
        HttpServer server = JettyHttpServerProvider.provider().createHttpServer(new InetSocketAddress(port), 0);
        HandlerProvider p = HandlerProvider.getInstance();
        IHandler handler = p.getHandler();
        InvokeHandler invokeHandler = new InvokeHandler(handler);

        server.createContext("/", invokeHandler);
        server.start();
    }

    static class InvokeHandler implements HttpHandler {
        IHandler handler;

        private InvokeHandler(IHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestBody = "";
            String method = exchange.getRequestMethod();

            if (method.equalsIgnoreCase("POST")) {
                InputStream inputStream = exchange.getRequestBody();
                requestBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                inputStream.close();
            }

            // System.out.println(requestBody);
            Headers reqHeaders = exchange.getRequestHeaders();
            Map<String, String> reqHeadersMap = new HashMap<>();

            for (Map.Entry<String, java.util.List<String>> header : reqHeaders.entrySet()) {
                java.util.List<String> headerValues = header.getValue();
                if (headerValues.size() > 0) {
                    reqHeadersMap.put(header.getKey(), headerValues.get(0));
                }
            }

            IRequest req = new Request(requestBody, reqHeadersMap, exchange.getRequestURI().getRawQuery(), exchange.getRequestURI().getPath());

            IResponse res = this.handler.Handle(req);

            String response = res.getBody();
            byte[] bytesOut = response.getBytes(StandardCharsets.UTF_8);

            Headers responseHeaders = exchange.getResponseHeaders();
            String contentType = res.getContentType();
            if (contentType.length() > 0) {
                responseHeaders.set("Content-Type", contentType);
            }

            for (Map.Entry<String, String> entry : res.getHeaders().entrySet()) {
                responseHeaders.set(entry.getKey(), entry.getValue());
            }

            exchange.sendResponseHeaders(res.getStatusCode(), bytesOut.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytesOut);
            os.close();

            System.out.println("Request / " + bytesOut.length + " bytes written.");
        }
    }

}
