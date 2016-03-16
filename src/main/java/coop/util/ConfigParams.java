package coop.util;

import java.util.Map;
import java.util.HashMap;

public class ConfigParams {
	private Map<String, String> stringParams;
	private Map<String, Integer> intParams;
	private Map<String, Double> doubleParams;

	public void addStringParam(String name, String value) {
		if (stringParams == null) {
			stringParams = new HashMap<String, String>();
		}
		stringParams.put(name, value);
	}

	public void removeStringParam(String name) {
		stringParams.remove(name);
	}

	public String getStringParam(String name) {
		return stringParams.get(name);
	}

	public Map<String, String> getStringParams() {
		return stringParams;
	}

	public void addIntParam(String  name, int value) {
		if (intParams == null) {
			intParams = new HashMap<String, Integer>();
		}
		intParams.put(name, value);
	}

	public void removeIntParam(String name) {
		intParams.remove(name);
	}

	public int getIntParam(String name) {
		return intParams.get(name);
	}

	public Map<String, Integer> getIntParams() {
		return intParams;
	}

	public void addDoubleParam(String  name, double value) {
		if (doubleParams == null) {
			doubleParams = new HashMap<String, Double>();
		}
		doubleParams.put(name, value);
	}

	public void removeDoubleParam(String name) {
		doubleParams.remove(name);
	}

	public double getDoubleParam(String name) {
		return doubleParams.get(name);
	}

	public Map<String, Double> getDoubleParams() {
		return doubleParams;
	}
}