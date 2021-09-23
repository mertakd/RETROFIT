package com.mertakdogan.retrofitjava.service;

import com.mertakdogan.retrofitjava.model.CryptoModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoAPI {

    //GET, POST, UPDATE, DELETE
    //get veriyi almak için
    //post sunucuya bir veri yazmak için kullanılır

    //URL BASE -> www.website.com
    //GET -> price?key=xxx
    //biz burada price ile başlayan kısmı alırız

    @GET("prices?key=47df3d2fd9b17aeaf46f9e473190ac960046a19f") //parantez içindeki adrese get isteği yolla
    //Call<List<CryptoModel>> getData(); //Call: assenkron bir şekilde ne indireceğimizi ve hangi metod içinde indireceğimizi söylüyor.

    //bana liste içinde kripto modelleri gelecek diyorum, tüm bu metodun adına getData diyorum

    Observable<List<CryptoModel>> getData();
}
