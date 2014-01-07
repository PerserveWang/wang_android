package com.trends.photoalbum.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpC {
//	private String uri = "http://yingjietongxuntest.trends-china.com/Service.ashx?comm=user&action=verify&name=arvin&Pwd=1231223";
//	final String TAG_STRING = "TAG";
	public static String HttpA(String uri){
		try {  
	           //得到HttpClient对象  
	           HttpClient getClient = new DefaultHttpClient();  
	           //得到HttpGet对象  
	           HttpGet request = new HttpGet(uri);  
	           //客户端使用GET方式执行请教，获得服务器端的回应response  
//	           HttpEntity entity = new UrlEncodedFormEntity(uri, "gb2312");
	           HttpResponse response = getClient.execute(request);  
	           //判断请求是否成功    
	           if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){  
	               Log.i("tag", "请求服务器端成功");  
	               //获得输入流  
//	               InputStream  inStrem = response.getEntity().getContent();  
//	               int result = inStrem.read();  
//	               while (result != -1){  
//	                   System.out.print((char)result);  
//	                   result = inStrem.read();  
//	               }  
//	               //关闭输入流  
//	               inStrem.close();  
	               
	           	HttpEntity entity2 = response.getEntity();
				String content = EntityUtils.toString(entity2, HTTP.UTF_8);
				return content;
	           }else {  
	               Log.i("tag", "请求服务器端失败");  
	           }             
	       } catch (Exception e) {  
	           // TODO Auto-generated catch block  
	           e.printStackTrace();  
	       }
		return uri;  
	}
	   
}
