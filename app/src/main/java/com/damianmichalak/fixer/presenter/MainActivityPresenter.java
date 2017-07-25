package com.damianmichalak.fixer.presenter;

import com.damianmichalak.fixer.model.FixerDao;
import com.damianmichalak.fixer.model.FixerResponse;
import com.damianmichalak.fixer.view.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class MainActivityPresenter {

    @Nonnull
    private final Observable<List<BaseAdapterItem>> dataSuccessObservable;
    @Nonnull
    private final Observable<Boolean> emptyObservable;
    @Nonnull
    private final Observable<Boolean> progressObservable;
    @Nonnull
    private final Observable<Throwable> dataErrorObservable;
    @Nonnull
    private final PublishSubject<OpenDetailsActivityArguments> itemClickObservable = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> loadMoreSubject = PublishSubject.create();
    @Nonnull
    private final Subscription subscription;

    @Inject
    MainActivityPresenter(@Nonnull FixerDao fixerDao) {

        dataSuccessObservable = fixerDao.getDataSuccess()
                .map(new Func1<List<FixerResponse>, List<BaseAdapterItem>>() {
                    @Override
                    public List<BaseAdapterItem> call(List<FixerResponse> fixerResponses) {

                        final List<BaseAdapterItem> items = new ArrayList<>();

                        for (FixerResponse fixerResponse : fixerResponses) {
                            items.add(new DateAdapterItem(fixerResponse.getDate()));

                            for (String key : fixerResponse.getRates().keySet()) {
                                final Float value = fixerResponse.getRates().get(key);
                                items.add(new RatingAdapterItem(key, value, fixerResponse.getDate()));
                            }
                        }

                        if (!items.isEmpty()) {
                            items.add(new ProgressLoadingItem(loadMoreSubject));
                        }

                        return items;
                    }
                });

        dataErrorObservable = fixerDao.getDataError();

        emptyObservable = dataSuccessObservable
                .map(new Func1<List<BaseAdapterItem>, Boolean>() {
                    @Override
                    public Boolean call(List<BaseAdapterItem> baseAdapterItems) {
                        return baseAdapterItems.isEmpty();
                    }
                })
                .startWith(false);

        progressObservable = dataSuccessObservable
                .map(new Func1<List<BaseAdapterItem>, Boolean>() {
                    @Override
                    public Boolean call(List<BaseAdapterItem> baseAdapterItems) {
                        return false;
                    }
                })
                .startWith(true);

        subscription = loadMoreSubject.subscribe(fixerDao.getLoadMoreObserver());
    }

    @Nonnull
    public Observable<OpenDetailsActivityArguments> getItemClickObservable() {
        return itemClickObservable;
    }

    @Nonnull

    public Observable<Boolean> getEmptyObservable() {
        return emptyObservable;
    }

    @Nonnull
    public Observable<Boolean> getProgressObservable() {
        return progressObservable;
    }

    @Nonnull
    public Subscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getDataSuccessObservable() {
        return dataSuccessObservable;
    }

    @Nonnull
    public Observable<Throwable> getDataErrorObservable() {
        return dataErrorObservable;
    }

    public class DateAdapterItem extends BaseAdapterItem {
        private final String date;

        DateAdapterItem(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        @Override
        public boolean isHeader() {
            return true;
        }
    }

    public class RatingAdapterItem extends BaseAdapterItem {
        private final String name;
        private final String date;
        private final Float number;

        RatingAdapterItem(String name, Float number, String date) {
            this.date = date;
            this.name = name;
            this.number = number;
        }

        public String getDate() {
            return date;
        }

        public String getName() {
            return name;
        }

        public Float getNumber() {
            return number;
        }

        @Override
        public boolean isHeader() {
            return false;
        }
    }

    public class ProgressLoadingItem extends BaseAdapterItem {

        private final Observer<Object> loadMore;

        ProgressLoadingItem(Observer<Object> loadMore) {
            this.loadMore = loadMore;
        }

        public Observer<Object> getLoadMore() {
            return loadMore;
        }

        @Override
        public boolean isHeader() {
            return false;
        }
    }

}
