package com.damianmichalak.fixer.presenter;

import com.damianmichalak.fixer.model.FixerDao;
import com.damianmichalak.fixer.model.FixerResponse;
import com.damianmichalak.fixer.model.ResponseOrError;
import com.damianmichalak.fixer.view.BaseAdapterItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class MainActivityPresenter {

    @Nonnull
    private final Observable<List<BaseAdapterItem>> dataSuccess;
    @Nonnull
    private final Observable<Throwable> dataError;

    @Inject
    public MainActivityPresenter(FixerDao fixerDao) {
        dataSuccess = fixerDao.getDataOrError()
                .compose(ResponseOrError.<FixerResponse>onlySuccess())
                .map(new Func1<FixerResponse, List<BaseAdapterItem>>() {
                    @Override
                    public List<BaseAdapterItem> call(FixerResponse fixerResponse) {
                        final List<BaseAdapterItem> items = new ArrayList<>();

                        items.add(new DateAdapterItem(fixerResponse.getDate()));

                        for (String key : fixerResponse.getRates().keySet()) {
                            final Float value = fixerResponse.getRates().get(key);
                            items.add(new RatingAdapterItem(key, value));
                        }

                        return items;

                    }
                });

        dataError = fixerDao.getDataOrError().compose(ResponseOrError.<FixerResponse>onlyError());
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

        public DateAdapterItem(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }
    }

    public class RatingAdapterItem extends BaseAdapterItem {
        private final String name;
        private final Float number;

        public RatingAdapterItem(String name, Float number) {
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

}
