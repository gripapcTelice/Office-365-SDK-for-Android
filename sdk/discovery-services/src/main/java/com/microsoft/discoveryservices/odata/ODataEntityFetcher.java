/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.discoveryservices.odata;

import com.google.common.util.concurrent.*;
import com.microsoft.services.odata.interfaces.*;

import java.util.HashMap;
import java.util.Map;

import static com.microsoft.services.odata.EntityFetcherHelper.addEntityResultCallback;
import static com.microsoft.services.odata.EntityFetcherHelper.addNullResultCallback;
import static com.microsoft.services.odata.Helpers.serializeToJsonByteArray;
import static com.microsoft.services.odata.Helpers.addCustomParametersToODataURL;

/**
 * The type ODataEntityFetcher.
 * @param <E>  the type parameter
 * @param <V>  the type parameter
 */
public abstract class ODataEntityFetcher<E, V> extends ODataExecutable implements Readable<E> {

    protected String urlComponent;
    protected ODataExecutable parent;
    private Class<E> clazz;
    private V operations;

	 /**
     * Instantiates a new ODataEntityFetcher.
     *
     * @param urlComponent the url component
     * @param parent the parent
     * @param clazz the clazz
     * @param operationClazz the operation clazz
     */
    public ODataEntityFetcher(String urlComponent, ODataExecutable parent, Class<E> clazz, Class<V> operationClazz) {
        this.urlComponent = urlComponent;
        this.parent = parent;
        this.clazz = clazz;

        try {
            this.operations = operationClazz.getConstructor(String.class,
                    ODataExecutable.class).newInstance("", this);
        } catch (Throwable ignored) {
        }
    }

	public ODataEntityFetcher<E,V> addHeader(String name, String value) {
        this.addCustomHeader(name, value);
		return this;
    }

    @Override
    ListenableFuture<byte[]> oDataExecute(ODataURL path, byte[] content, HttpVerb verb) {
		path.prependPathComponent(urlComponent);
        addCustomParametersToODataURL(path, getCustomParameters(), getResolver());
        return parent.oDataExecute(path, content, verb);
    }

    @Override
    DependencyResolver getResolver() {
        return parent.getResolver();
    }

	/**
     * Updates the given entity.
     *
     * @param updatedEntity the updated entity
     * @return the listenable future
     */
    public ListenableFuture<E> update(E updatedEntity) {
	    final SettableFuture<E> result = SettableFuture.create();
        byte[] payloadBytes = serializeToJsonByteArray(updatedEntity, getResolver());
        ListenableFuture<byte[]> future = oDataExecute(getResolver().createODataURL(), payloadBytes, HttpVerb.PATCH);
        addEntityResultCallback(result, future, getResolver(), clazz);
        return result;
    }

	 /**
     * Deletes
     *
     * @return the listenable future
     */
    public ListenableFuture delete() {
	    final SettableFuture<E> result = SettableFuture.create();
        ListenableFuture<byte[]> future = oDataExecute(getResolver().createODataURL(), null, HttpVerb.DELETE);
        addNullResultCallback(result, future);
        return result;
    }

	 /**
     * Reads
     *
     * @return the listenable future
     */
    public ListenableFuture<E> read() {
	    final SettableFuture<E> result = SettableFuture.create();
        ListenableFuture<byte[]> future = oDataExecute(getResolver().createODataURL(), null, HttpVerb.GET);
        addEntityResultCallback(result, future, getResolver(), clazz);
        return result;
    }

	 /**
     * Gets operations.
     *
     * @return the operations
     */
    public V getOperations() {
        return this.operations;
    }
}