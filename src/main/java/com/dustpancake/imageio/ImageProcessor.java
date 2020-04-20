package com.dustpancake.imageio;

import com.dustpancake.models.ImageResponse;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

import java.lang.Runtime;
import java.lang.Process;
import java.lang.InterruptedException;

import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONException;


public abstract class ImageProcessor {
	protected final String jsonBody;
	protected String inputUri;
	protected String cmd;
	protected Process p;
	protected List<String> keys;
	protected List<Double> values;
	protected String info;

	public final String outputName = "testAsync.jpg";

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

	public void process(String uri) {
		cmd = buildCmd(inputUri) + "-o " + outputName;
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch(IOException e) {
			System.out.println(e);
		}
	}

	public int finish() {
		int retval;
		try {
			retval = p.waitFor();
		} catch(InterruptedException e) {
			System.out.println(e);
			retval = 1;
		}
		return retval;
	}

	public String processBody() {
		JSONObject jobj = new JSONObject(jsonBody);
		String value;
		ListIterator<String> iter = keys.listIterator();

		try {
			inputUri = jobj.getString("uri");
		} catch(Exception e) {
			return "NO URI";
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
				return "BAD KEYS IN BODY";
			}
		}
		return "";
	}

	public String info() {
		return info;
	}

	public List<String> getKeys() {
		return keys;
	}

	public String getUri() {
		return inputUri;
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