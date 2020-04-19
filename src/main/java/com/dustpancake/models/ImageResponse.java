package com.dustpancake.models;

public class ImageResponse {
	private final String newUri;
	private final String metaData;

	public ImageResponse(String newUri, String metaData) {
		this.newUri = newUri;
		this.metaData = metaData;
	}

	public String getMetaData() {
		return metaData;
	}

	public String getNewUri() {
		return newUri;
	}
}