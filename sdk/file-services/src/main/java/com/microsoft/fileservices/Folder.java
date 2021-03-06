/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information.
 ******************************************************************************/
package com.microsoft.fileservices;

/**
 * The type Folder.
*/
public class Folder extends Item {

	public Folder(){
		setODataType("#Microsoft.FileServices.Folder");
	}

	private Integer childCount;

	/**
	* Gets the child Count.
	*
	* @return the Integer
	*/
	public Integer getchildCount() {
		return this.childCount; 
	}

	/**
	* Sets the child Count.
	*
	* @param value the Integer
	*/
	public void setchildCount(Integer value) { 
		this.childCount = value; 
	}
}