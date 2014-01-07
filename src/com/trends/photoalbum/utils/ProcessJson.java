package com.trends.photoalbum.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProcessJson {

	public static ArrayList<HashMap<String, String>> doParseGetLikeNums(
			JSONObject jsonObject) {
		ArrayList<HashMap<String, String>> likeNums = new ArrayList<HashMap<String, String>>();
		String status;
		try {
			status = jsonObject.getString("status");
			System.out.println("status空异->" + status);
			if (status.equals("1")) {
				JSONArray array = jsonObject.getJSONArray("data");
				for (int j = 0; j < array.length(); j++) {
					JSONObject jsonItem = array.getJSONObject(j);
					HashMap<String, String> map = new HashMap<String, String>();
					String topicid = jsonItem.getString("topicid");
					String likeCount = jsonItem.getString("FavourableCount");
					map.put("topicid", topicid);
					map.put("likeCount", likeCount);
					likeNums.add(map);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return likeNums;
	}

	public static int doParseGetFavorite(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				return 1;
			}

		} catch (JSONException e) {

			e.printStackTrace();
			return -1;
		}

		return 0;

	}

	// 意见反馈
	public static int doSubmitSuggest(JSONObject jsonObject) {

		try {
			String status = jsonObject.getString("status");
			if (status.equals("1")) {
				return 1;
			}

		} catch (Exception e) {
			e.printStackTrace();// 是否抛出异常
			return -1;
		}
		return 0;

	}
}
