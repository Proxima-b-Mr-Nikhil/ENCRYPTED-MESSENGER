package com.proxima_b.encryptedmessenger.Fragments;

import com.proxima_b.encryptedmessenger.Notifications.MyResponse;
import com.proxima_b.encryptedmessenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA0Zgy38s:APA91bHBx2WyArrNz0NzKBVffQeWa8ST76aKZBUm65odzHKLWsix_-N62PD7KOYC4Ly07HKoyJCl9fT4u-lKztxvNpbKv77zdBAMioSpUs06i8aa8HfI3q3L8SO-SAhDrOLSfCjT-aqQ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
