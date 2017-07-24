package com.damianmichalak.fixer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

@Singleton
public class FixerDao {

    @Nonnull
    private final Observable<ResponseOrError<FixerResponse>> dataOrError;
    @Nonnull
    private final PublishSubject<Object> loadMoreSubject = PublishSubject.create();

    @Inject
    FixerDao(@Nonnull final ApiService apiService,
             @Nonnull final @Named("UI") Scheduler uiScheduler,
             @Nonnull final @Named("IO") Scheduler ioScheduler) {

        final Observable<String> nextDateObservable = loadMoreSubject
                .throttleFirst(1, TimeUnit.SECONDS, uiScheduler)
                .startWith(((Object) null))
                .scan(DateHelper.getDateFromMillis(System.currentTimeMillis()), new Func2<String, Object, String>() {
                    @Override
                    public String call(String previousDate, Object o) {
                        return DateHelper.previousDate(previousDate);
                    }
                });

        dataOrError = nextDateObservable
                .flatMap(new Func1<String, Observable<ResponseOrError<FixerResponse>>>() {
                    @Override
                    public Observable<ResponseOrError<FixerResponse>> call(String date) {
                        return apiService.getFixerResponse(date)
                                .compose(ResponseOrError.<FixerResponse>toResponseOrError())
                                .observeOn(uiScheduler)
                                .subscribeOn(ioScheduler);
                    }
                });

        dataOrError
                .compose(ResponseOrError.<FixerResponse>onlySuccess())
                .scan(new ArrayList<FixerResponse>(), new Func2<List<FixerResponse>, FixerResponse, List<FixerResponse>>() {
                    @Override
                    public List<FixerResponse> call(List<FixerResponse> oldResponses, FixerResponse newResponse) {
                        final List<FixerResponse> newItems = new ArrayList<>(oldResponses.size() + 1);
                        newItems.addAll(oldResponses);
                        newItems.add(newResponse);
                        return newItems;
                    }
                });


    }

    @Nonnull
    public Observer<Object> getLoadMoreObserver() {
        return loadMoreSubject;
    }

    @Nonnull
    public Observable<ResponseOrError<FixerResponse>> getDataOrError() {
        return dataOrError;
    }
}
