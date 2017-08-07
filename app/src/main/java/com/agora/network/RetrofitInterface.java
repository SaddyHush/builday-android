package com.agora.network;

import com.agora.model.Event;
import com.agora.model.Response;
import com.agora.model.Status;
import com.agora.model.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Response> register(@Body User user);

    @POST("authenticate")
    Observable<Response> login();

    @POST("tokenauth/{email}")
    Observable<Response> validateToken(@Path("email") String email);

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @PUT("users/{email}")
    Observable<Response> changePassword(@Path("email") String email, @Body User user);

    @PUT ("users/{email}")
    Observable<Response> setUserChanges(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Response> resetPasswordFinish(@Path("email") String email, @Body User user);

    @POST("users/{email}/status")
    Observable<Response> createStatus(@Path("email") String email, @Body Status status);

    @GET("users/{email}/status/{num}")
    Observable<String> getStatus(@Path("email") String email, @Path("num")int num);

    @GET("users/{email}/status/size")
    Observable<Integer > getStatusAmount(@Path("email") String email);

    @POST("events/upload/{email}")
    Observable<Response> uploadEvent(@Path("email") String email, @Body Event event);

}