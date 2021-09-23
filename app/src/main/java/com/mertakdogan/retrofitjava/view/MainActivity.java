package com.mertakdogan.retrofitjava.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mertakdogan.retrofitjava.R;
import com.mertakdogan.retrofitjava.adapter.RecyclerViewAdapter;
import com.mertakdogan.retrofitjava.model.CryptoModel;
import com.mertakdogan.retrofitjava.service.CryptoAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ArrayList<CryptoModel> cryptoModels; //indireceğimiz data için bir arrayList oluşturuyoruz
    private String BASE_URL = "https://api.nomics.com/v1/";
    Retrofit retrofit;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    CompositeDisposable compositeDisposable;
    //eski işlemleri temizlememiz gerekiyor ki hafızayı daha verimli kullanabilelim RxJava burda devreye giriyor
    //birden fazla kullan at objesini aynı yere koyup aynı anda temizleyebiliyoruz, call ile aynı manaya geliyor
    //rxjava ile işlem yapıyorsak call ı kullanmamalıyız onun yerine Observable yi kullanıyoruz.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);


        //Retrofit & JSON

        Gson gson = new GsonBuilder().setLenient().create();
        //setLenient: bu JSON ı aldığını gösteriyor ve create diyince bizim için json u oluşturuyor.

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                //addConverterFactory: json modeli yazmıştık CrptoModel sınıfında serializedName göre alacak demiştik işte bunu retrofite de bildirmemiz gerekiyor
                .build();
        //şimdilik bir model çekeceğimizi bildirdik ve bu modelin nerden çekileceğini söyledik
        //json formatında geleceğini söyledik, BASE_URL dan çekileceğini söyledik ve oluşturduk

        loadData();
    }


    //VERİ ALMA loadData olan
    private void loadData(){

        final CryptoAPI cryptoAPI = retrofit.create(CryptoAPI.class); //servisi oluşturmuş oluyoruz

        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(cryptoAPI.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this :: handleResponse));
        /*
        Call<List<CryptoModel>> call = cryptoAPI.getData(); //veriyi call metoduyla çekmeye başlıyoruz

        call.enqueue(new Callback<List<CryptoModel>>() { //enqueue: assenkron bir şekilde işlemi yapmamıza bloklamamamıza işe yarıyor ve gelecek veriyi gösteriyor.
            @Override
            public void onResponse(Call<List<CryptoModel>> call, Response<List<CryptoModel>> response) {
                //responce gelen cevap demek
                if (response.isSuccessful()){ //gelen cevap doğruysa
                    List<CryptoModel> responseList = response.body(); //body: bize kripto model listesi veriyor
                    cryptoModels = new ArrayList<>(responseList); //arraylist in içine normal list olan responce list i atıyoruz yani listeyi array list e çevirecek

                    //Recyclerview
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerViewAdapter = new RecyclerViewAdapter(cryptoModels);
                    recyclerView.setAdapter(recyclerViewAdapter);


//                    for (CryptoModel cryptoModel : cryptoModels) {
//                        System.out.println(cryptoModel.currency);
//                        System.out.println(cryptoModel.price);
//                    }


                }
            }

            @Override
            public void onFailure(Call<List<CryptoModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });

         */
    }

    private void  handleResponse(List<CryptoModel> cryptoModelList){

        cryptoModels = new ArrayList<>(cryptoModelList); //arraylist in içine normal list olan responce list i atıyoruz yani listeyi array list e çevirecek

        //Recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerViewAdapter = new RecyclerViewAdapter(cryptoModels);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }
}