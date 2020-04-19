package com.dustpancake.models;

import java.util.Arrays;
import java.util.List;
import com.dustpancake.imageio.ImageProcessor;

public class MethodInterface {
	private ImageProcessor method;
	private boolean badMethod;

	public MethodInterface(ImageProcessor method) {
		this.method = method;
		badMethod = false;
	}

	public MethodInterface() {
		badMethod = true;
	}

	public List<String> getKeys() {
		if (badMethod) {
			return Arrays.asList("");
		} else {
			return method.getKeys();
		}
	}
}