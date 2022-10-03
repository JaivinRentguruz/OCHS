package com.ochs.app.apicall;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.ochs.app.adapters.CustomToast;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.ochs.app.apicall.ApiEndPoint.UPLOAD_BASE_URL;


public class ApiService
{
   /* private static String key ="Och5735171600";
    private static String pass = "Eee6qYg1eXqhRr8yoEN42Q==";*/

     private static String key ="Car4446668888";
    private static String pass = "1KzWo/O7IeX5GoOQ/U2S/Q==";

    private OnResponseListener onResponseListener;
    public ApiService (){ }
    public ApiService(OnResponseListener locationList, RequestType get, String list, HashMap<String, String> stringStringHashMap, JSONObject bodyParam)
    {
    }
    public ApiService(OnResponseListener onResponseListener, RequestType requestType, String endPoint,String baseurl,
                      HashMap<String, String> header, JSONObject bodyParam)
    {
        this.onResponseListener = onResponseListener;

        switch (requestType)
        {
            case GET:
                GET_REQUEST(baseurl,endPoint, header, null);
                break;
            case POST:
                POST_REQUEST(baseurl,endPoint, header, bodyParam);
                break;
            case PUT:
                PUT_REQUEST(baseurl,endPoint, header, bodyParam);
                break;
            case DELETE:
                DELETE_REQUEST(baseurl,endPoint, header, bodyParam);
                break;
            default:
                break;
        }
    }

    public ApiService(OnResponseListener onResponseListener, RequestType requestType, String endPoint,String baseurl,
                      HashMap<String, String> header, String bodyParam)
    {
        this.onResponseListener = onResponseListener;

        switch (requestType)
        {
            case GET:
                GET_REQUEST(baseurl,endPoint, header, bodyParam);
                break;
            default:
                break;
        }
    }

    public void GET_REQUEST(String baseurl,String endPoint, HashMap<String, String> header, String bodyParam)
    {
        String credentials = Credentials.basic(key,pass);
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        header.put("Authorization", credentials);
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.get(baseurl + endPoint+"?"+bodyParam)
                .addHeaders(header)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener()
                {
                    @Override
                    public void onResponse(Response okHttpResponse, String response)
                    {
                        if (!TextUtils.isEmpty(response))
                        {
                            if (onResponseListener != null)
                            {
                                onResponseListener.onSuccess(response, convertHeadersToHashMap(okHttpResponse.headers()));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError)
                    {
                        if (onResponseListener != null) onResponseListener.onError(anError.getErrorBody());
                    }
                });
    }

    private void POST_REQUEST(String baseurl, String endPoint, HashMap<String, String> header, JSONObject bodyParam)
    {
        String credentials = Credentials.basic(key,pass);
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        header.put("Authorization", credentials);

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.post(baseurl + endPoint)
                .addHeaders(header)
                .addJSONObjectBody(bodyParam)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            if (onResponseListener != null) {
                                onResponseListener.onSuccess(response,
                                        convertHeadersToHashMap(okHttpResponse.headers()));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError)
                    {
                        if (onResponseListener != null) onResponseListener.onError(anError.getErrorBody());
                    }
                });
    }

    private HashMap<String, String> convertHeadersToHashMap(Headers headers)
    {
        HashMap<String, String> result = new HashMap<>();
        for (int i = 0; i < headers.size(); i++)
        {
            result.put(headers.name(i), headers.value(i));
        }
        return result;
    }

    private void PUT_REQUEST(String baseurl,String endPoint, HashMap<String, String> header, JSONObject bodyParam)
    {
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        String credentials = Credentials.basic(key,pass);
        header.put("Authorization", credentials);
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.put(baseurl + endPoint)
                .addHeaders(header)
                .addJSONObjectBody(bodyParam)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener()
                {
                    @Override
                    public void onResponse(Response okHttpResponse, String response)
                    {
                        if (!TextUtils.isEmpty(response))
                        {
                            if (onResponseListener != null)
                            {
                                onResponseListener.onSuccess(response,
                                        convertHeadersToHashMap(okHttpResponse.headers()));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (onResponseListener != null)
                            onResponseListener.onError(anError.getErrorBody());
                    }
                });
    }

    private void DELETE_REQUEST(String baseurl,String endPoint, HashMap<String, String> header, JSONObject bodyParam)
    {
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/json");
        String credentials = Credentials.basic(key,pass);
        header.put("Authorization", credentials);
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.delete(baseurl + endPoint)
                .addHeaders(header)
                .addJSONObjectBody(bodyParam)
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener()
                {
                    @Override
                    public void onResponse(Response okHttpResponse, String response)
                    {
                        if (!TextUtils.isEmpty(response)) {
                            if (onResponseListener != null) {
                                onResponseListener.onSuccess(response,
                                        convertHeadersToHashMap(okHttpResponse.headers()));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError)
                    {
                        if (onResponseListener != null)
                            onResponseListener.onError(anError.getErrorBody());
                    }
                });
    }

    public void UPLOAD_REQUEST(final OnResponseListener onResponseListener,
                               String endPoint, HashMap<String, String> header,
                               File fileUpload) {
        //header.put("Accept", "application/json");
        header.put("Content-Type", "multipart/form-data");
        String credentials = Credentials.basic(key  ,pass);
        header.put("Authorization", credentials);
        AndroidNetworking.upload(UPLOAD_BASE_URL + endPoint)
                .addHeaders(header)
                .addMultipartFile("profile_video", fileUpload)
                .setOkHttpClient(getConfigOkHttpClient())
                .setTag("uploadArtistData")
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .getAsOkHttpResponseAndString(new OkHttpResponseAndStringRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            if (onResponseListener != null) {
                                onResponseListener.onSuccess(response,
                                        convertHeadersToHashMap(okHttpResponse.headers()));
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error)
                    {
                        System.out.println(error.getErrorDetail());
                        if (onResponseListener != null)
                            onResponseListener.onError(error.getErrorBody());
                    }
                });
    }

    public static OkHttpClient getConfigOkHttpClient()
    {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(true)
                .build();
    }
}
//        String credentials = Credentials.basic("Och5735171600","Eee6qYg1eXqhRr8yoEN42Q==");
//        String credentials = Credentials.basic("Car4446668888","1KzWo/O7IeX5GoOQ/U2S/Q==");