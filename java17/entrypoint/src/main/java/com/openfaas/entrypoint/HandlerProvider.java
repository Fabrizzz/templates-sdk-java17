// Copyright (c) OpenFaaS Author(s) 2018. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.openfaas.entrypoint;

import com.openfaas.model.AbstractHandler;

import java.util.ServiceLoader;

public class HandlerProvider {
    private static HandlerProvider provider;
    private final ServiceLoader<AbstractHandler> loader;

    private HandlerProvider() {
        loader = ServiceLoader.load(AbstractHandler.class);
    }

    public static HandlerProvider getInstance() {
        if (provider == null) {
            provider = new HandlerProvider();
        }
        return provider;
    }

    public AbstractHandler getHandler() {
        AbstractHandler service = loader.iterator().next();
        if (service != null) {
            return service;
        } else {
            throw new java.util.NoSuchElementException(
                    "No implementation for HandlerProvider");
        }
    }
}