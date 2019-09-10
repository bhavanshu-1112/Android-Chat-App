package com.example.mychat.Fragments;

import com.example.mychat.Notifications.MyResponse;
import com.example.mychat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {
	@Header(
			{
					"Content-Type:application/json",
					"Authorization:key=AAAAIvq80mI:APA91bEs8NkjrZD_ro9RFaM2KHGAzOBgWM8kc_3TE9dyvpyVgqfYDkY3nmX8FHGiAznVdtpBppq0h4Kw_iDM_RpY-vvDPrMxgCSnrjYUlv-lZfHYB9z63J2s7hxQVjQcQxzqMko5-o_P"

			}

	)
	@POST("fcm/send")
	Call<MyResponse> sendNotification(@Body Sender body);

}
