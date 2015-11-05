package com.jasonko.morestandingapp;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kolichung on 7/21/15.
 */
public class API {

    public static final String  TAG   = "API";
    public static final String API_URL = "http://114.35.74.182:8086";

    public static boolean login(String account, String password){
//        httpPost(account, password);
        String jsonString = httpGetLogin(account, password);
        try {
            JSONObject obj = new JSONObject(jsonString);
            String message = obj.getString("message");
            if (message.equals("OK")){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){

        }
        return false;

    }

    public static void postRegID(String account, String regID){
        httpPostRegID(account, regID);
    }

    public static void pushGCM(String regID, String message, String title, String text){
        httpPostGCMMessageToServer(regID, message, title, text);
    }

    private static String httpPostGCMMessageToServer(String regID, String message, String title, String text) {
        String result = "" ;
        // 第一步，创建HttpPost对象
        HttpPost httpPost = new HttpPost( API_URL + "/GCM_Send.php" );

        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("regId", regID));
        params.add(new BasicNameValuePair("message", message));
        params.add(new BasicNameValuePair("content_title", title));
        params.add(new BasicNameValuePair("content_text", text));

        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
            HttpClient httpClient = new DefaultHttpClient() ;
            // 请求超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000 ) ;
            // 读取超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

            httpResponse = httpClient.execute( httpPost ) ;
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                result  = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;
    }


    public static String httpUpdateLocation(String account, String password, String lat, String lng){
        String result = "" ;
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL( API_URL + "/app/login/"+ account + "/" + password +"?lat="+lat+"&long="+lng) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader( is , "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private static String httpGetLogin(String account, String password) {
        String result = "" ;
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL( API_URL + "/app/login/"+ account + "/" + password ) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader( is , "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private static String httpPostRegID(String account, String regID) {

        String result = "" ;
        // 第一步，创建HttpPost对象
        HttpPost httpPost = new HttpPost( API_URL + "/app/gcm/reg" );

        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("account", account));
        params.add(new BasicNameValuePair("reg_Id", regID));

        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
            HttpClient httpClient = new DefaultHttpClient() ;
            // 请求超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000 ) ;
            // 读取超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

            httpResponse = httpClient.execute( httpPost ) ;
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                result  = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  result;

    }

    public static String httpPost(String account, String password){
        String result = "" ;
        // 第一步，创建HttpPost对象
        HttpPost httpPost = new HttpPost( API_URL + "/auth/login" );

        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("account", account));
        params.add(new BasicNameValuePair("password", password));

        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity( params , HTTP.UTF_8 ));
            HttpClient httpClient = new DefaultHttpClient() ;
            // 请求超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000 ) ;
            // 读取超时  10s
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

            httpResponse = httpClient.execute( httpPost ) ;
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                result  = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result ;
    }


    public String httpGet( String httpUrl ){
        String result = "" ;
        try {
            BufferedReader reader = null;
            StringBuffer sbf = new StringBuffer() ;

            URL url  = new URL( httpUrl ) ;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection() ;
            //设置超时时间 10s
            connection.setConnectTimeout(10000);
            //设置请求方式
            connection.setRequestMethod( "GET" ) ;
            connection.connect();
            InputStream is = connection.getInputStream() ;
            reader = new BufferedReader(new InputStreamReader( is , "UTF-8" )) ;
            String strRead = null ;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
