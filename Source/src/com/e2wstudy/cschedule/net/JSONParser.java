package com.e2wstudy.cschedule.net;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.e2wstudy.cschedule.R;
import com.e2wstudy.cschedule.views.ToastDialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {

	}

	public String getJsonFromURL(String url) {
		String responeStr = "";
		try {

			HttpClient mClient = new DefaultHttpClient();
			int timeoutConnection = 3000;

			HttpPost request = new HttpPost();
			request.setURI(new URI(url));
			HttpResponse response = mClient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				responeStr = EntityUtils.toString(entity);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responeStr;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String getJsonFromURLPostNameValuePair(Context mContext,
			String url, List<NameValuePair> params) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpParams timeoutParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(timeoutParam, 3000);
		HttpConnectionParams.setSoTimeout(timeoutParam, 10000);

		HttpClient client = new DefaultHttpClient(timeoutParam);

		HttpPost post = new HttpPost(url);
		post.addHeader("Content-type", "application/json");
		String content = null;
		try {
			UrlEncodedFormEntity e = new UrlEncodedFormEntity(params, "UTF-8");
			post.setEntity(e);
			ResponseHandler<String> handler = new BasicResponseHandler();
			HttpResponse httpResponse = client.execute(post);
			content = httpResponse.getStatusLine().toString();
			// HttpEntity entity = httpResponse.getEntity();
			//
			// if (entity != null) {
			// content = EntityUtils.toString(entity);
			//
			// }
			Log.d("post fb", content);
		} catch (UnknownHostException e) {
			return "unknowhost_error";
		} catch (ConnectTimeoutException e) {
			return "timeout_error";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String getJsonFromURLPostNameValuePair(String url,
			String params) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpParams timeoutParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(timeoutParam, 3000);
		HttpConnectionParams.setSoTimeout(timeoutParam, 10000);

		HttpClient client = new DefaultHttpClient(timeoutParam);

		HttpPost post = new HttpPost(url);
		post.addHeader("Content-type", "application/json");
		String content = null;
		try {
			StringEntity entity = new StringEntity(params.toString());
			post.setEntity(entity);

			ResponseHandler<String> handler = new BasicResponseHandler();
			HttpResponse httpResponse = client.execute(post);
			content = httpResponse.getStatusLine().toString();
			// HttpEntity entity = httpResponse.getEntity();
			//
			// if (entity != null) {
			// content = EntityUtils.toString(entity);
			//
			// }
			Log.d("post feedback", content);
		} catch (UnknownHostException e) {
			return "unknowhost_error";
		} catch (ConnectTimeoutException e) {
			return "timeout_error";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String deleteFromUrl(String url) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HttpParams timeoutParam = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(timeoutParam, 3000);
		HttpConnectionParams.setSoTimeout(timeoutParam, 10000);

		HttpClient client = new DefaultHttpClient(timeoutParam);

		HttpDelete post = new HttpDelete(url);
		post.addHeader("Content-type", "application/json");
		String content = null;
		try {
			// UrlEncodedFormEntity e = new UrlEncodedFormEntity(params,
			// "UTF-8");
			// post.setEntity(e);
			// ResponseHandler<String> handler = new BasicResponseHandler();
			// content = client.execute(post, handler);
			HttpResponse httpResponse = client.execute(post);
			HttpEntity entity = httpResponse.getEntity();
			Log.d("delete contact status", httpResponse.getStatusLine()
					.toString());
			if (entity != null) {
				content = EntityUtils.toString(entity);

			}
			Log.d("delete contact jsonstr", content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}