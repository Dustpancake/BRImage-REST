package com.dustpancake.imageio;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;


public class FrequencyModulation extends ImageProcessor {
	public FrequencyModulation() {
		this("");
	}

	public FrequencyModulation(String body) {
		super(body);
		keys = new ArrayList<>(Arrays.asList("omega", "phase", "lowpass", "pquantize"));
	}

}