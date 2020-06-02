package com.bondex.ysl.installlibrary.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


public interface NetApi {


    @Streaming
    @GET("{user}")
    Call<ResponseBody> downloadFile(@Path("user")String user);

    @GET("appversion?type=wms")
    Call<String> getVersion();

}
