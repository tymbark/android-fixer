package com.damianmichalak.fixer.model;


import javax.annotation.Nullable;

import rx.Observable;
import rx.functions.Func1;

public class ResponseOrError<T> {

    @Nullable
    private final T data;
    @Nullable
    private final Throwable error;

    public ResponseOrError(@Nullable T data, @Nullable Throwable error) {
        this.data = data;
        this.error = error;
    }

    public boolean isData() {
        return data != null;
    }

    public boolean isErorr() {
        return error != null;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public static <T> Observable.Transformer<T, ResponseOrError<T>> toResponseOrError() {
        return new Observable.Transformer<T, ResponseOrError<T>>() {
            @Override
            public Observable<ResponseOrError<T>> call(final Observable<T> tObservable) {
                return tObservable
                        .map(new Func1<T, ResponseOrError<T>>() {
                            @Override
                            public ResponseOrError<T> call(T t) {
                                return new ResponseOrError<>(t, null);
                            }
                        })
                        .onErrorResumeNext(new Func1<Throwable, Observable<ResponseOrError<T>>>() {
                            @Override
                            public Observable<ResponseOrError<T>> call(Throwable throwable) {
                                return Observable.just(new ResponseOrError<T>(null, throwable));
                            }
                        });
            }
        };
    }

    public static <T> Observable.Transformer<ResponseOrError<T>, T> onlySuccess() {
        return new Observable.Transformer<ResponseOrError<T>, T>() {
            @Override
            public Observable<T> call(Observable<ResponseOrError<T>> responseOrErrorObservable) {
                return responseOrErrorObservable
                        .filter(new Func1<ResponseOrError<T>, Boolean>() {
                            @Override
                            public Boolean call(ResponseOrError<T> tResponseOrError) {
                                return tResponseOrError.isData();
                            }
                        })
                        .map(new Func1<ResponseOrError<T>, T>() {
                            @Override
                            public T call(ResponseOrError<T> tResponseOrError) {
                                return tResponseOrError.data;
                            }
                        });
            }
        };
    }

    public static <T> Observable.Transformer<ResponseOrError<T>, Throwable> onlyError() {
        return new Observable.Transformer<ResponseOrError<T>, Throwable>() {
            @Override
            public Observable<Throwable> call(Observable<ResponseOrError<T>> responseOrErrorObservable) {
                return responseOrErrorObservable
                        .filter(new Func1<ResponseOrError<T>, Boolean>() {
                            @Override
                            public Boolean call(ResponseOrError<T> tResponseOrError) {
                                return tResponseOrError.isErorr();
                            }
                        })
                        .map(new Func1<ResponseOrError<T>, Throwable>() {
                            @Override
                            public Throwable call(ResponseOrError<T> tResponseOrError) {
                                return tResponseOrError.error;
                            }
                        });
            }
        };
    }

    @Override
    public String toString() {
        return "ResponseOrError{" +
                "data=" + data +
                ", error=" + error +
                '}';
    }
}
