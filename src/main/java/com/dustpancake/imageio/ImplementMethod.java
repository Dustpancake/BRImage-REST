package com.dustpancake.imageio;

import java.util.List;
import java.util.ArrayList;

import com.dustpancake.models.MethodConfig;

public class ImplementMethod extends ImageProcessor {

	public ImplementMethod(String body, String imageDirectory, MethodConfig config) {
		super(body, imageDirectory);
		keys = config.keys;
		types = config.types;
	
	}
}