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
    private final PublishSubject<Object> loadMoreSubject = PublishSubject.create();
    @Nonnull
    private final Observable<List<FixerResponse>> dataSuccess;
    @Nonnull
    private final Observable<Throwable> dataError;

    @Inject
    FixerDao(@Nonnull final ApiService apiService,
             @Nonnull final @Named("UI") Scheduler uiScheduler,
             @Nonnull final @Named("IO") Scheduler ioScheduler) {

        final Observable<String> nextDateObservable = loadMoreSubject //todo dont trigger this when error!!!
                .throttleFirst(1, TimeUnit.SECONDS, uiScheduler)
                .startWith(((Object) null))
                .scan(DateHelper.getDateFromMillis(System.currentTimeMillis()), new Func2<String, Object, String>() {
                    @Override
                    public String call(String previousDate, Object o) {
                        return DateHelper.previousDate(previousDate);
                    }
                });

        final Observable<ResponseOrError<FixerResponse>> dataOrError = nextDateObservable
                .flatMap(new Func1<String, Observable<ResponseOrError<FixerResponse>>>() {
                    @Override
                    public Observable<ResponseOrError<FixerResponse>> call(String date) {
                        return apiService.getFixerResponse(date)
                                .compose(ResponseOrError.<FixerResponse>toResponseOrError())
                                .observeOn(uiScheduler)
                                .subscribeOn(ioScheduler);
                    }
                })
                .replay(1)
                .refCount();

        dataSuccess = dataOrError
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

        dataError = dataOrError.compose(ResponseOrError.<FixerResponse>onlyError());

    }

    @Nonnull
    public Observer<Object> getLoadMoreObserver() {
        return loadMoreSubject;
    }

    @Nonnull
    public Observable<List<FixerResponse>> getDataSuccess() {
        return dataSuccess;
    }

    @Nonnull
    public Observable<Throwable> getDataError() {
        return dataError;
    }
}
