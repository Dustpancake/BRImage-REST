package com.dustpancake.models;

import java.util.List;

public class MethodConfig {

	public List<String> keys;
	public List<String> types;

	public MethodConfig(List<String> keys, List<String> types) {
		this.keys = keys;
		this.types = types;
	}

}