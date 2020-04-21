package com.dustpancake.propertyread;

import org.springframework.stereotype.Component;

import org.springframework.context.annotation.PropertySource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.dustpancake.propertyread.JsonFactory;

import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import com.dustpancake.models.MethodConfig;

@Component
@PropertySource(
	value = "classpath:method.json",
	factory = JsonFactory.class
	)
@ConfigurationProperties
public class MethodReader {

	private LinkedHashMap<String,?> methods;

	public LinkedHashMap<String,?> getMethods() {
		return methods;
	}

	public void setMethods(LinkedHashMap<String,?> methods) {
		this.methods = methods;
	}

	public MethodConfig getConfigOf(String method) {
		LinkedHashMap<String,?> choice = getInterface(method);
		List<?> kvList = (List<?>) choice.get("params");
		List<String> keys = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for (int i = 0; i < kvList.size(); i++) {
			keys.add(
				(String)((LinkedHashMap<String,?>)kvList.get(i)).get("name")
			);
			types.add(
				(String)((LinkedHashMap<String,?>)kvList.get(i)).get("type")
			);
		}
		return new MethodConfig(keys, types);
	}

	public LinkedHashMap<String, ?> getInterface(String method) {
		return (LinkedHashMap<String,?>) getMethods().get(method);
	}
}