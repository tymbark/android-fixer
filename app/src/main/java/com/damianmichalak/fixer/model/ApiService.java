package com.damianmichalak.fixer.model;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface ApiService {

    @GET("/{date}?base=PLN")
    Observable<FixerResponse> getFixerResponse(@Path("date") String date);

}
