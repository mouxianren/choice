package com.yiqi.choose.utils;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 网络访问工具类 需要 android.permission.INTERNET 权限
 * 
 * @author Administrator
 * 
 */
public class HttpConBase {
	// private Context context;
	// HttpConBase(Context context){
	// this.context=context;
	// }

	// 网络请求的超时时间
	private static final int timeout = 10000;

	/**
	 * 通过url地址返回输入流
	 * 
	 * @param urlStr
	 * @return
	 */
	public static InputStream getInputStreamFromUrl(String urlStr) {

		return getInputStreamFromUrl(urlStr, null);
	}

	/**
	 * 通过url地址和cookie值返回输入流
	 * 
	 * @param urlStr
	 * @param _cookieStr
	 * @return
	 */
	public static InputStream getInputStreamFromUrl(String urlStr, String _cookieStr) {
		if (urlStr != null && !urlStr.equals("")) {
			InputStream is = null;
			try {
				URL url = new URL(urlStr);
				HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
				if (_cookieStr != null && _cookieStr.trim().length() > 0) {
					urlCon.addRequestProperty("Cookie", _cookieStr);
				}
				urlCon.connect();
				urlCon.setConnectTimeout(timeout);
				if (urlCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
					is = urlCon.getInputStream();
				}
			} catch (Exception e) {
				e.printStackTrace();
				is = null;
			}
			return is;
		}
		return null;
	}

//	/**
//	 * 通过url地址GET的方式获取字符串
//	 *
//	 * @param urlStr
//	 * @return
//	 */
//	public static String getStringFromGet(String urlStr) {
//		if (urlStr == null || urlStr.equals("")) {
//			return null;
//		}
//		HttpParams httpParams = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
//		HttpConnectionParams.setSoTimeout(httpParams, timeout);
//		// 新建HttpClient对象
//		HttpClient httpClient = new DefaultHttpClient(httpParams);
//		HttpGet httpGet = new HttpGet(urlStr);
//		String json = null;
//		try {
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				HttpEntity entity = httpResponse.getEntity();
//				json = EntityUtils.toString(entity).trim().replaceFirst("<!--.*-->", "").trim();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return json;
//	}


	private final static String secret = "5e73740bbdd7940481449b44bb2aa7b8";

	public final static String md5(String plainText) {
		// 返回字符串
		String md5Str = null;
		try {
			// 操作字符串
			StringBuffer buf = new StringBuffer();
			/**
			 * MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法。
			 * 信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
			 * 
			 * MessageDigest 对象开始被初始化。 该对象通过使用 update()方法处理数据。 任何时候都可以调用
			 * reset()方法重置摘要。 一旦所有需要更新的数据都已经被更新了，应该调用digest()方法之一完成哈希计算。
			 * 
			 * 对于给定数量的更新数据，digest 方法只能被调用一次。 在调用 digest 之后，MessageDigest
			 * 对象被重新设置成其初始状态。
			 */
			MessageDigest md = MessageDigest.getInstance("MD5");

			// 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
			md.update(plainText.getBytes());
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				buf.append(Integer.toHexString(i));
			}
			// 32位的加密
			md5Str = buf.toString();
			// 16位的加密
			// md5Str = buf.toString().md5Strstring(8,24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5Str;
	}

	private static String sign(String jsonstr) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonstr);
			ArrayList<String> list = new ArrayList<String>();
			Iterator it = json.keys();
			boolean isarray = json.getString("action").equals("category_new_flag");
			for (; it.hasNext();) {
				String tmp = (String) it.next();
				if (isarray && tmp.equals("timestamp")) {
					continue;
				}
				list.add(tmp);
			}
			String tmp = "";
			String md5 = "" + secret;
			for (int i = 0; i < list.size() - 1; i++) {
				int n = i;
				tmp = list.get(n);
				for (int j = i + 1; j < list.size(); j++) {
					if (list.get(n).compareTo(list.get(j)) > 0) {
						n = j;
					}
				}
				if (n != i) {
					tmp = list.get(n);
					list.remove(n);
					list.add(i, tmp);
				}
				md5 += tmp + json.getString(tmp);
			}
			tmp = list.get(list.size() - 1);
			md5 += tmp + json.getString(tmp);
			md5 += secret;
			md5 = md5(md5).toUpperCase();
			json.put("sign", md5);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	/**
	 * 通过URL POST的方式获取List<String>集合
	 *
	 * @param _url
	 *            接口地址
	 * @param _params
	 *            传递参数
	 * @param _charSet
	 *            字符编码
	 * @return jsonData
	 */
	public static String getjsonByPost(String _url, Map<String, String> _params, String _charSet) {
		if (_charSet == null || _charSet.length() == 0) {
			_charSet = "utf-8";
		}
		String jsonData = "";
		try {
			URL httpurl = new URL(_url);

			URLConnection conn = httpurl.openConnection();

			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12");

			if (_params != null) {
				conn.setDoOutput(true);// 设置连接允许发送数据
				conn.setDoInput(true);// 设置连接允许接收数据

				PrintWriter out = new PrintWriter(conn.getOutputStream());
				int i = 0;

				// 处理post中发送的参数
				Set<Map.Entry<String, String>> set = _params.entrySet();

				for (Map.Entry<String, String> entry : set) {
					out.print(entry.getKey());

					out.print("=");
					out.print(entry.getValue());
					if (i != set.size() - 1) {
						out.print("&");
					}
					i++;
				}
				out.flush();
				out.close();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), _charSet));


			StringBuffer result = new StringBuffer();// 存放结果
			String line;// 存放读取单行拿到的数据
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			jsonData = result.toString();
			jsonData = jsonData.trim().replaceAll("<!--.*-->", "").trim();// 过滤空格和注释
			in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("发送post请求出错,url:" + _url, e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的字符集,charSet:" + _charSet, e);
		} catch (IOException e) {
			throw new RuntimeException("发送post请求IO出错,url:" + _url, e);
		}
		return jsonData;
	}
	/**
	 * 通过URL POST的方式获取JSON数据
	 * 
	 * @param _url
	 *            接口地址
	 * @param _params
	 *            传递参数
	 * @param _charSet
	 *            字符编码
	 * @return jsonData
	 */
	public static String getJsonByPost(String _url, Map<String, String> _params, String _charSet) {
		if (_charSet == null || _charSet.length() == 0) {
			_charSet = "utf-8";
		}
		String jsonData = "";
		try {
			URL httpurl = new URL(_url);
			URLConnection conn = httpurl.openConnection();
			//conn.setConnectTimeout(10000);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12");
			if (_params != null) {
				conn.setDoOutput(true);// 设置连接允许发送数据
				conn.setDoInput(true);// 设置连接允许接收数据
				PrintWriter out = new PrintWriter(conn.getOutputStream());
				int i = 0;
				// 处理post中发送的参数
				//String tmp = _params.get("key");
				//tmp = sign(tmp);


				out.print(_params);

				//Log.d("发送",_params);
				out.flush();
				out.close();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), _charSet));
			StringBuffer result = new StringBuffer();// 存放结果
			String line;// 存放读取单行拿到的数据
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			jsonData = result.toString();
			jsonData = jsonData.trim().replaceAll("<!--.*-->", "").trim();// 过滤空格和注释
			in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("发送post请求出错,url:" + _url, e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的字符集,charSet:" + _charSet, e);
		} catch (IOException e) {
			throw new RuntimeException("发送post请求IO出错,url:" + _url, e);
		}

		return jsonData;
	}

	/**
	 * 通过URL POST的方式获取JSON数据
	 * 
	 * @param _url
	 *            接口地址
	 * @param _params
	 *            传递参数
	 * @param _charSet
	 *            字符编码
	 * @return jsonData
	 */
	public static String getJsonByPost(String _url, String _params, String _charSet) {
		if (_charSet == null || _charSet.length() == 0) {
			_charSet = "utf-8";
		}
		String jsonData = "";
		try {
			URL httpurl = new URL(_url);
			URLConnection conn = httpurl.openConnection();
			//conn.setConnectTimeout(10000);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12");
			if (_params != null) {
				conn.setDoOutput(true);// 设置连接允许发送数据
				conn.setDoInput(true);// 设置连接允许接收数据
				PrintWriter out = new PrintWriter(conn.getOutputStream());
				int i = 0;
				// 处理post中发送的参数
				//out.print(sign(_params));
				out.print(_params);
			
				out.flush();
				out.close();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), _charSet));
			StringBuffer result = new StringBuffer();// 存放结果
			String line;// 存放读取单行拿到的数据
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			jsonData = result.toString();
			jsonData = jsonData.trim().replaceAll("<!--.*-->", "").trim();// 过滤空格和注释
			in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("发送post请求出错,url:" + _url, e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的字符集,charSet:" + _charSet, e);
		} catch (IOException e) {
			throw new RuntimeException("发送post请求IO出错,url:" + _url, e);
		}
		return jsonData;
	}

//	/**
//	 * 通过HTTPPOST的方式获取JSON数据
//	 *
//	 * @param _url
//	 *            接口地址
//	 * @param _params
//	 *            传递参数
//	 * @param _charSet
//	 *            字符编码
//	 * @return jsonData
//	 */
//	public static String getJsonByHttpPost(String _url, Map<String, String> _params) {
//		if (_url == null || "".equals(_url)) {
//			return "";
//		}
//		String jsonData = "";
//		try {
//			// 1.2同HTTPGET
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost httpPost = new HttpPost(_url);
//			// 3.封装参数
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			Set<String> keySet = _params.keySet();
//			for (String key : keySet) {
//				String value = _params.get(key);
//				params.add(new BasicNameValuePair(key, value));
//			}
//			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//			// 4同HTTPGET
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			if (httpResponse.getStatusLine().getStatusCode() == 200) {
//				HttpEntity entity = httpResponse.getEntity();
//				jsonData = EntityUtils.toString(entity).trim().replaceAll("<!--.*-->", "").trim();
//			}
//		} catch (ClientProtocolException e) {
//			throw new RuntimeException("HttpClient的协议错误", e);
//		} catch (IOException e) {
//			throw new RuntimeException("网络数据流读取错误", e);
//		}
//		return jsonData;
//	}

	/**
	 * 通过URL POST的方式获取List<String>集合
	 * 
	 * @param _url
	 *            接口地址
	 * @param _params
	 *            传递参数
	 * @param _charSet
	 *            字符编码
	 * @return jsonData
	 */
	public static List<String> getListByPost(String _url, Map<String, String> _params, String _charSet) {
		if (_charSet == null || _charSet.length() == 0) {
			_charSet = "utf-8";
		}
		List<String> strList = null;
		try {
			URL httpurl = new URL(_url);
			URLConnection conn = httpurl.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/535.12 (KHTML, like Gecko) Chrome/18.0.966.0 Safari/535.12");
			if (_params != null) {
				conn.setDoOutput(true);// 设置连接允许发送数据
				conn.setDoInput(true);// 设置连接允许接收数据
				PrintWriter out = new PrintWriter(conn.getOutputStream());
				int i = 0;
				// 处理post中发送的参数
				Set<Map.Entry<String, String>> set = _params.entrySet();
				for (Map.Entry<String, String> entry : set) {
					out.print(entry.getKey());
					out.print("=");
					out.print(entry.getValue());
					if (i != set.size() - 1) {
						out.print("&");
					}
					i++;
				}
				out.flush();
				out.close();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), _charSet));
			String line;// 存放读取单行拿到的数据
			strList = new ArrayList<String>();
			while ((line = in.readLine()) != null) {
				strList.add(line);
			}
			in.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("发送post请求出错,url:" + _url, e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的字符集,charSet:" + _charSet, e);
		} catch (IOException e) {
			throw new RuntimeException("发送post请求IO出错,url:" + _url, e);
		}
		return strList;
	}

//	/**
//	 * 判断文件是否存在，若不存在就下载该文件
//	 *
//	 * @param urlStr
//	 * @param txtDirPath
//	 * @param _separator
//	 *            文件分隔符
//	 * @return
//	 */
//	public static String storageTxt(String urlStr, String txtDirPath, String _separator) {
//		String txtPath = null;
//		if (_separator == null || _separator.trim().length() <= 0) {
//			_separator = "/";
//		}
//		String txtName = urlStr.substring(urlStr.lastIndexOf(_separator) + 1);
//		File txtFileDir = new File(txtDirPath);
//		if (!txtFileDir.exists()) {
//			txtFileDir.mkdirs();
//		}
//		File txtFilePath = new File(txtDirPath + File.separator + txtName + ".txt");
//		if (!txtFilePath.exists()) {
//			String str = getStringFromGet(urlStr);
//			if (str == null || str.length() < 3) {
//				return null;
//			} else {
//				BufferedReader br = null;
//				BufferedWriter bw = null;
//				try {
//					txtFilePath.createNewFile();
//					br = new BufferedReader(new StringReader(str));
//					bw = new BufferedWriter(new FileWriter(txtFilePath));
//					String temp = "";
//					while ((temp = br.readLine()) != null) {
//						bw.write(temp);
//					}
//					bw.flush();
//				} catch (Exception ex) {
//					FileUtils.deleteFile(txtFilePath);
//					ex.printStackTrace();
//					txtPath = null;
//				} finally {
//					try {
//						br.close();
//						bw.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} else {
//			txtPath = txtFilePath.getAbsolutePath();
//		}
//		return txtPath;
//	}

//	／
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

			JSONArray jsonObjs = new JSONObject(_jsonData).getJSONArray("result");
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

//	public static String getHttpJsonFromGet(String url) {
//		if ((url == null) || (url.trim().equals(""))) {
//			return null;
//		} else {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpGet httpGet = new HttpGet(url);
//			String jsonData = "";
//			try {
//				HttpResponse response = httpClient.execute(httpGet);
//				HttpEntity entity = response.getEntity();
//				jsonData = EntityUtils.toString(entity).trim().substring(13).trim();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				jsonData = "";
//			}
//			return jsonData;
//		}
//
//	}
public static String sendGet(String url) {
	String result = "";
	BufferedReader in = null;

	try {
		String urlNameString = url;
		URL realUrl = new URL(urlNameString);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		// 设置通用的请求属性

		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 建立实际的连接
		connection.connect();

		// 获取所有响应头字段
          /*
           * Map<String, List<String>> map = connection.getHeaderFields(); //
           * 遍历所有的响应头字段 for (String key : map.keySet()) {
           * System.out.println(key + "--->" + map.get(key)); }
           */
		// 定义 BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
            result += line;
        }
	} catch (Exception e) {
		e.printStackTrace();

	}

	// 使用finally块来关闭输入流
	finally {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	return result;
}
	/**
	 * 通过URL GET的方式获取JSON数据
	 * 
	 * @param _url
	 *            接口地址
	 * @param _charSet
	 *            字符编码
	 * @return jsonData
	 */
	public static String getJsonByGet(String _url, String _charSet) {
		if (_charSet == null || _charSet.length() == 0) {
			_charSet = "utf-8";
		}
		String jsonData = "";
		BufferedReader br = null;
		try {
			URL url = new URL(_url);
			URLConnection conn = url.openConnection();
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), _charSet));
			StringBuffer result = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
			jsonData = result.toString();
			jsonData = jsonData.trim().replaceAll("<!--.*-->", "").trim();// 过滤空格和注释
			br.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("发送GET请求出错,url:" + _url, e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的字符集,charSet:" + _charSet, e);
		} catch (IOException e) {
			throw new RuntimeException("发送GET请求IO出错,url:" + _url, e);
		}

		return jsonData;
	}

}