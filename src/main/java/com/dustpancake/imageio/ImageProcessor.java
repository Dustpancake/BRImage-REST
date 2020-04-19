package com.dustpancake.imageio;

import com.dustpancake.models.ImageResponse;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

import java.lang.Runtime;
import java.lang.Process;

import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;


public abstract class ImageProcessor {
	protected final String jsonBody;
	protected String inputUri;
	protected List<String> keys;
	protected List<Double> values;

	private String buildCmd(String inputImage) {
		String cmd = "brimage " + inputImage;
		for (int i = 0; i < values.size(); i++) {
			cmd += " --" + keys.get(i) + " " + String.valueOf(values.get(i));
		}
		return cmd + " ";
	}
	
	public ImageProcessor(String jsonBody) {
		this.jsonBody = jsonBody;
		values = new ArrayList<>();
	}

	public void process() {
		// TODO
		// fetch image from uri
		String cmd = buildCmd(inputUri) + "-o test.jpg";
		// get new URI
		try {
			Process p = Runtime.getRuntime().exec(cmd);
		} catch(IOException e) {
			System.out.println(e);
		}
		// return URI
	}

	public boolean processBody() {
		JSONObject jobj = new JSONObject(jsonBody);
		String value;
		ListIterator<String> iter = keys.listIterator();

		try {
			inputUri = jobj.getString("uri");
		} catch(Exception e) {
			return false;
		}

		while(iter.hasNext()) {
			try {
				value = jobj.getString(iter.next());
				values.add(Double.parseDouble(value));
			} catch(JSONException e) {
				iter.remove();
			} catch(NullPointerException e) {
				iter.remove();
			} catch(Exception e) {
				System.out.println(e);
				return false;
			}
		}
		return true;
	}

	public List<String> getKeys() {
		return keys;
	}

	public String getParamsJSON() {
		JSONObject jobj = new JSONObject();
		for (int i = 0; i < values.size(); i++) {
			jobj.put(
				keys.get(i), values.get(i)
			);
		}
		return jobj.toString();
	}
}