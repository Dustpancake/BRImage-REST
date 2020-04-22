package com.dustpancake.imageio;

import com.dustpancake.models.ImageResponse;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Base64;

import java.lang.Runtime;
import java.lang.Process;
import java.lang.InterruptedException;

import java.io.IOException;
import java.io.File;

import org.json.JSONObject;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public abstract class ImageProcessor {
	protected final String jsonBody;
	protected final String imageDirectory;
	protected String cmd;
	protected Process p;
	protected List<String> keys;
	protected List<Double> values;
	protected List<String> types;
	protected String info;

	public volatile String outputName;
	public volatile String inputUri;
	

	private String buildCmd(String inputImage) {
		String cmd = "brimage " + inputImage;
		String paramVal = "0";
		for (int i = 0; i < values.size(); i++) {
			System.out.println(types.get(i));
			if (types.get(i).equals("double")) {
				paramVal = String.valueOf((double)values.get(i));
			} else if (types.get(i).equals("int")) {
				paramVal = String.valueOf((int)((double)values.get(i)));
			}
			cmd += " --" + keys.get(i) + " " + paramVal;
		}
		return cmd + " ";
	}
	
	public ImageProcessor(String jsonBody, String imageDirectory) {
		this.imageDirectory = imageDirectory;
		this.jsonBody = jsonBody;
		values = new ArrayList<>();
	}

	public int process() {
		cmd = buildCmd(imageDirectory+inputUri) + "-o " + imageDirectory + outputName;
		System.out.println("Executing " + cmd);
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch(IOException e) {
			System.out.println(e);
			return 1;
		}

		return finish();
	}

	private int finish() {
		int retval;
		try {
			retval = p.waitFor();
		} catch(InterruptedException e) {
			System.out.println(e);
			retval = 1;
		} finally {
			new File(imageDirectory + inputUri).delete();
		}
		return retval;
	}

	public boolean processBody() {
		JSONObject jobj;
		try {
			jobj = new JSONObject(jsonBody);
		} catch(Exception e) {
			System.out.println(e);
			info = "BAD JSON";
			return false;
		}
		String value;
		ListIterator<String> iter = keys.listIterator();

		try {
			inputUri = jobj.getString("uri");
		} catch(Exception e) {
			info = "NO URI";
			return false;
		}

		generateOutput();
		if (outputName.equals("")){
			info = "BAD FILE CHECKSUM";
			return false;
		}
		String key;
		while(iter.hasNext()) {
			try {
				key = iter.next();
				value = jobj.getString(key);
				values.add(Double.parseDouble(value));
				jobj.remove(key);
			} catch(JSONException e) {
				// System.out.println(e);
				iter.remove();
			} catch(NullPointerException e) {
				iter.remove();
			} catch(Exception e) {
				System.out.println(e);
				info = "ERROR";
				return false;
			}
		}

		if (jobj.length() != 1) {
			info = "BAD KEYS";
			return false;
		}

		return true;
	}

	private void generateOutput() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch(NoSuchAlgorithmException e) {
			outputName = "";
			return;
		}
		md.update(inputUri.getBytes());
		byte[] digest = md.digest(inputUri.getBytes());
		outputName =  Base64.getEncoder().encodeToString(digest).replaceAll("[^\\w\\s]","") + ".jpg";
	}

	public String info() {
		return info;
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