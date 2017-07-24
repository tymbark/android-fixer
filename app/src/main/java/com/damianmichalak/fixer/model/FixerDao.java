package com.damianmichalak.fixer.model;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;

@Singleton
public class FixerDao {

    @Nonnull
    private final Observable<ResponseOrError<FixerResponse>> dataOrError;

    @Inject
    public FixerDao(@Nonnull ApiService apiService,
                    @Nonnull @Named("UI") Scheduler uiScheduler,
                    @Nonnull @Named("IO") Scheduler ioScheduler) {

        dataOrError = apiService.getFixerResponse("2000-01-03")
                .compose(ResponseOrError.<FixerResponse>toResponseOrError())
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler);
    }

    @Nonnull
    public Observable<ResponseOrError<FixerResponse>> getDataOrError() {
        return dataOrError;
    }
}
