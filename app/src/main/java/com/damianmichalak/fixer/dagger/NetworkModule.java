package com.damianmichalak.fixer.dagger;


import com.damianmichalak.fixer.model.ApiService;
import com.damianmichalak.fixer.model.Constants;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@dagger.Module
public class NetworkModule {

    @Named("UI")
    @Provides
    @Nonnull
    @Singleton
    Scheduler provideUiScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Named("IO")
    @Provides
    @Nonnull
    @Singleton
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @Provides
    @Nonnull
    @Singleton
    ApiService provideApiService() {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .baseUrl(Constants.API_BASE_URL)
                .build()
                .create(ApiService.class);
    }


}
