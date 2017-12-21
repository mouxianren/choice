package com.yiqi.choose.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON解析工具类
 * 
 * @author Administrator
 * 
 */
public class ParseJsonCommon {

	/**
	 * 通过JSON获取Object数组
	 * 
	 * @param _jsonData
	 * @param _class
	 * @return 如果resultCode为200表示请求的返回正常,如果为300表示请求返回的数据为空，如果为400表示服务器接收数据时发生异常
	 */
	public static List<Object> parseJson(String _jsonData, Class<?> _class) {
		if (_jsonData == null || _jsonData.equals("")) {
			return null;
		}
		List<Object> list = null;
		try {
			JSONObject jObject = new JSONObject(_jsonData);
			String requestCode = jObject.getString("resultCode").trim();
			if (requestCode.equals(NetWork.RESULT_EMPTY)) {
				return new ArrayList<Object>();
			} else if (requestCode.equals(NetWork.RESULT_ERROR)) {
				return null;
			} else if (requestCode.equals(NetWork.RESULT_OK)) {
				Gson gson = new Gson();
				Object object = null;
				list = new ArrayList<Object>();
				JSONArray jsonObjs = jObject.getJSONArray("resultContent");
				// 2.通过遍历JSONArray对象获得JSONObject对象，并将其转为字符串
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
					// 3.使用Gson类的fromJson对象将json数据和实体类进行映射
					object = gson.fromJson(jsonObj.toString(), _class);
					list.add(object);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
		}
		return list;
	}

	/**
	 * 通过JSON获取Object数组
	 * 
	 * @param _jsonData
	 * @param _class
	 * @return 如果resultCode为200表示请求的返回正常,如果为300表示请求返回的数据为空，如果为400表示服务器接收数据时发生异常
	 */
	public static List<Object> parseJsonMain(String type, String _jsonData,
											 Class<?> _class) {
		if (_jsonData == null || _jsonData.equals("")) {
			return null;
		}
		List<Object> list = null;
		try {
			JSONObject jObject = new JSONObject(_jsonData);

			Gson gson = new Gson();
			Object object = null;
			list = new ArrayList<Object>();
			JSONArray jsonObjs = jObject.getJSONArray(type);
			// 2.通过遍历JSONArray对象获得JSONObject对象，并将其转为字符串
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				// 3.使用Gson类的fromJson对象将json数据和实体类进行映射
				object = gson.fromJson(jsonObj.toString(), _class);
				list.add(object);
			}

		} catch (Exception e) {
			throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
		}
		return list;
	}

	/**
	 * 将json数据和实体类进行映射 注：需要Google的gson*.jar包
	 * 
	 * @param _jsonData
	 *            json数据
	 * @param _class
	 *            实体类
	 * @return 映射数据后的实体类列表
	 */
	public static List<Object> parseJsonData22(String _jsonData, Class<?> _class) {
		Object object = null;
		List<Object> list = new ArrayList<Object>();
		Gson gson = new Gson();
		try {
			// 1.对_jsonData进行一个简单的组装，可以通过result获得JSONArray对象
			if (_jsonData.contains("[{")) {// mulitply
				_jsonData = "{\"result\":" + _jsonData + "}";
			} else {// single
				_jsonData = "{\"result\":[" + _jsonData + "]}";
			}

			// System.out.println("jsonData====="+_jsonData);
			JSONArray jsonObjs = new JSONObject(_jsonData)
					.getJSONArray("result");
			// 2.通过遍历JSONArray对象获得JSONObject对象，并将其转为字符串
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				// 3.使用Gson类的fromJson对象将json数据和实体类进行映射
				object = gson.fromJson(jsonObj.toString(), _class);
				list.add(object);
			}
		} catch (Exception e) {
			throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
		}
		return list;
	}

	/**
	 * 解析json数据，返回一个对象
	 */
	public static Object parseJsonDataToObject(String _jsonData, Class<?> _class) {
		if (_jsonData == null || _jsonData.equals("")) {
			return null;
		}
		Object obj = null;
		try {
			JSONObject jObject = new JSONObject(_jsonData);
			String requestCode = jObject.getString("code").trim();
			if (requestCode.equals(900002 + "")) {
				return new Object();
			} else if (requestCode.equals(NetWork.RESULT_ERROR + "")) {
				return null;
			} else if (requestCode.equals(NetWork.RESULT_OK + "")) {
				Gson gson = new Gson();
				JSONObject jsonObj = jObject.getJSONObject("resultContent");
				obj = gson.fromJson(jsonObj.toString(), _class);
			} else {
				return null;
			}
		} catch (Exception e) {
			// throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
		}
		return obj;
	}

	// /**
	// * 解析json数据，返回一个对象
	// */
	// public static LoginInfo parseJsonDataToObject1(String _jsonData) {
	// if (_jsonData == null || _jsonData.equals("")) {
	// return null;
	// }
	// LoginInfo loginInfo=new LoginInfo();
	// try {
	// JSONObject jObject = new JSONObject(_jsonData);
	// String time=jObject.getString("t").trim();
	// String code = jObject.getString("code").trim();
	// String msg = jObject.getString("msg").trim();
	// String infocontent=jObject.getString("info").trim();
	// loginInfo.setTime(time);
	// loginInfo.setCode(code);
	// loginInfo.setMsg(msg);
	// loginInfo.setInfo(infocontent);
	//
	// } catch (Exception e) {
	// //throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
	// }
	// return loginInfo;
	// }


	/**
	 * 通过JSON获取Object数组
	 * 
	 * @param _jsonData
	 * @param _class
	 * @return 如果resultCode为200表示请求的返回正常,如果为300表示请求返回的数据为空，如果为400表示服务器接收数据时发生异常
	 */
	public static List<Object> parseJiheJson(String _jsonData, Class<?> _class) {
		if (_jsonData == null || _jsonData.equals("")) {
			return null;
		}
		List<Object> list = null;
		try {
			JSONObject jObject = new JSONObject(_jsonData);
			// String requestCode = jObject.getString("resultCode").trim();
			// if (requestCode.equals(NetWork.RESULT_EMPTY)) {
			// return new ArrayList<Object>();
			// } else if (requestCode.equals(NetWork.RESULT_ERROR)) {
			// return null;
			// } else if (requestCode.equals(NetWork.RESULT_OK)) {
			Gson gson = new Gson();
			Object object = null;
			list = new ArrayList<Object>();

			JSONArray jsonObjs = jObject.getJSONArray("content");
			// 2.通过遍历JSONArray对象获得JSONObject对象，并将其转为字符串
			// for (int i = 0; i < jsonObjs.length(); i++) {
			// JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
			// // 3.使用Gson类的fromJson对象将json数据和实体类进行映射
			// object = gson.fromJson(jsonObj.toString(), _class);
			// list.add(object);
			// }
			// }
			// } else {
			// return null;
			// }
		} catch (Exception e) {
			// throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);

		}
		return list;
	}

	/**
	 * 通过JSON获取Object数组
	 * 
	 * @param _jsonData
	 * @param _class
	 * @return 如果resultCode为200表示请求的返回正常,如果为300表示请求返回的数据为空，如果为400表示服务器接收数据时发生异常
	 */
	public static List<Object> parseArrayJson(String _jsonData, Class<?> _class) {
		if (_jsonData == null || _jsonData.equals("")) {
			return null;
		}
		List<Object> list = null;
		try {
			Gson gson = new Gson();
			Object object = null;
			list = new ArrayList<Object>();
			JSONArray jsonObjs = new JSONArray(_jsonData);
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				object = gson.fromJson(jsonObj.toString(), _class);
				list.add(object);
			}
		} catch (Exception e) {
			System.out.println("json数据转换为响应类数据时出现异常，无法实现转换!");
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 将json数据和实体类进行映射 注：需要Google的gson*.jar包
	 * 
	 * @param _jsonData
	 *            json数据
	 * @param _class
	 *            实体类
	 * @return 映射数据后的实体类列表
	 */
	public static List<Object> parseJsonData(String _jsonData, Class<?> _class) {
		Object object = null;
		List<Object> list = new ArrayList<Object>();
		Gson gson = new Gson();
		try {
			// 1.对_jsonData进行一个简单的组装，可以通过result获得JSONArray对象
			if (_jsonData.contains("[{")) {// mulitply
				_jsonData = "{\"result\":" + _jsonData + "}";
			} else {// single
				_jsonData = "{\"result\":[" + _jsonData + "]}";
			}
			JSONArray jsonObjs = new JSONObject(_jsonData)
					.getJSONArray("result");
			// 2.通过遍历JSONArray对象获得JSONObject对象，并将其转为字符串
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				// 3.使用Gson类的fromJson对象将json数据和实体类进行映射
				object = gson.fromJson(jsonObj.toString(), _class);
				list.add(object);
			}
		} catch (Exception e) {
			throw new RuntimeException("json数据转换为响应类数据时出现异常，无法实现转换!" + e);
		}
		return list;
	}


}