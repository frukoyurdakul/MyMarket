package com.furkanyurdakul.mymarket.connections;

import com.furkanyurdakul.mymarket.models.MainScreenModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConnectionInterface
{
    String BASE_URL = "https://kariyertechchallenge.mockable.io/";

    @GET(".")
    Call<List<MainScreenModel>> getData();
}
