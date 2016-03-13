import java.util.Map;
import java.util.HashMap;

class ConfigParams {
	private Map<String, String> stringParams;
	private Map<String, Integer> intParams;
	private Map<String, Double> doubleParams;

	void addStringParam(String name, String value) {
		if (stringParams == null) {
			stringParams = new HashMap<String, String>();
		}
		stringParams.put(name, value);
	}

	void removeStringParam(String name) {
		stringParams.remove(name);
	}

	String getStringParam(String name) {
		return stringParams.get(name);
	}

	Map<String, String> getStringParams() {
		return stringParams;
	}

	void addIntParam(String  name, int value) {
		if (intParams == null) {
			intParams = new HashMap<String, Integer>();
		}
		intParams.put(name, value);
	}

	void removeIntParam(String name) {
		intParams.remove(name);
	}

	int getIntParam(String name) {
		return intParams.get(name);
	}

	Map<String, Integer> getIntParams() {
		return intParams;
	}

	void addDoubleParam(String  name, double value) {
		if (doubleParams == null) {
			doubleParams = new HashMap<String, Double>();
		}
		doubleParams.put(name, value);
	}

	void removeDoubleParam(String name) {
		doubleParams.remove(name);
	}

	double getDoubleParam(String name) {
		return doubleParams.get(name);
	}

	Map<String, Double> getDoubleParams() {
		return doubleParams;
	}
}