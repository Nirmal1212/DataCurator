package xyz.nirmalkumar.datacurator.controllers;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by nirmal on 8/2/16.
 */
public interface ApiManager {

    @POST("http://192.168.0.54:8000/images/test1/")
    void UploadFile(@Part("file")TypedFile file, @Part("folder")String folder, Callback<Response> callback);

}
