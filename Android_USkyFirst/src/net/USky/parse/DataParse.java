package net.USky.parse;

import org.json.JSONException;
import org.json.JSONObject;

public class DataParse {
	public static String toJson(String type, String username ,String text) {
		String result = "";
		try {
			JSONObject object2 = new JSONObject();
			object2.put("name", username);
			object2.put("text", text);
			JSONObject object = new JSONObject();
			object.put("setp", type);
			object.put("data", object2);
			// object.put("code", code);
			result = object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
