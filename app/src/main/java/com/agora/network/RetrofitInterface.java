package com.agora.network;

import com.agora.model.Event;
import com.agora.model.FCMToken;
import com.agora.model.Notification;
import com.agora.model.Response;
import com.agora.model.Status;
import com.agora.model.User;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Response> register(@Body User user);

    @GET("users/user/{email}")
    Observable<User> getOtherUserProfile(@Path("email") String email);

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

    @GET("users/{email}/event/{num}")
    Observable<String> getEventIDFromUser(@Path("email") String email, @Path("num")int num);

    @GET("users/{email}/event/amount")
    Observable<Integer> getEventAmount(@Path("email") String email);

    @POST("events/upload/{email}")
    Observable<Response> uploadEvent(@Path("email") String email, @Body Event event);

    @GET("events")
    Observable<Event[]> getEvents();

    @GET("events/{id}")
    Observable<Event> getEvent(@Path("id") String email);

    @POST("events/{eid}/{uid}")
    Observable<Response> addUserToEvent(@Path("eid") String eid, @Path("uid") String uid);

    @PUT("events/{eid}/{uid}")
    Observable<Response> deleteUserFromEvent(@Path("eid") String eid, @Path("uid") String uid);

    @PUT("notifications/token/update")
    Observable<Response> updateFCMToken(@Body FCMToken token);

    @GET("notifications/{id}/last/{num}")
    Observable<Notification[]> getLastXNotifications(@Path("id") String id, @Path("num") int num);

    @POST("event/acceptUser/{uid}/{eid}")
    Observable<Response> acceptUserToEvent(@Path("uid") String userID, @Path("eid") String eventID);

    @POST("event/declineUser{uid}/{eid}")
    Observable<Response> declineUserToEvent(@Path("uid") String userID, @Path("eid") String eventID);
}