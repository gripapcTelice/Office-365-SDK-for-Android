/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.directoryservices.odata;

import com.google.common.util.concurrent.*;
import com.microsoft.services.odata.interfaces.*;
import com.microsoft.directoryservices.*; 
import com.microsoft.directoryservices.*;		

/**
 * The type  SubscribedSkuFetcher.
 */
public class SubscribedSkuFetcher extends ODataEntityFetcher<SubscribedSku,SubscribedSkuOperations> implements Readable<SubscribedSku> {

     /**
     * Instantiates a new SubscribedSkuFetcher.
     *
     * @param urlComponent the url component
     * @param parent the parent
     */
	 public SubscribedSkuFetcher(String urlComponent, ODataExecutable parent) {
		super(urlComponent, parent, SubscribedSku.class,SubscribedSkuOperations.class);
    }

	}