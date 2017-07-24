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
    private final Observable<List<BaseAdapterItem>> dataSuccess;
    @Nonnull
    private final Observable<Throwable> dataError;
    @Nonnull
    private final FixerDao fixerDao;
    @Nonnull
    private final PublishSubject<Object> loadMoreSubject = PublishSubject.create();
    @Nonnull
    private final Subscription subscription;

    @Inject
    MainActivityPresenter(@Nonnull FixerDao fixerDao) {
        this.fixerDao = fixerDao;

        dataSuccess = fixerDao.getDataSuccess()
                .map(new Func1<List<FixerResponse>, List<BaseAdapterItem>>() {
                    @Override
                    public List<BaseAdapterItem> call(List<FixerResponse> fixerResponses) {

                        final List<BaseAdapterItem> items = new ArrayList<>();

                        for (FixerResponse fixerResponse : fixerResponses) {
                            items.add(new DateAdapterItem(fixerResponse.getDate()));

                            for (String key : fixerResponse.getRates().keySet()) {
                                final Float value = fixerResponse.getRates().get(key);
                                items.add(new RatingAdapterItem(key, value));
                            }
                        }

                        items.add(new ProgressLoadingItem(loadMoreSubject));
                        return items;
                    }
                });

        dataError = fixerDao.getDataError();

        subscription = loadMoreSubject.subscribe(fixerDao.getLoadMoreObserver());
    }

    @Nonnull
    public Subscription getSubscription() {
        return subscription;
    }

    @Nonnull
    public Observable<List<BaseAdapterItem>> getDataSuccess() {
        return dataSuccess;
    }

    @Nonnull
    public Observable<Throwable> getDataError() {
        return dataError;
    }

    public class DateAdapterItem extends BaseAdapterItem {
        private final String date;

        DateAdapterItem(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }

    public class RatingAdapterItem extends BaseAdapterItem {
        private final String name;
        private final Float number;

        RatingAdapterItem(String name, Float number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public Float getNumber() {
            return number;
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
    }

}
