package com.damianmichalak.fixer.presenter;

import com.damianmichalak.fixer.model.FixerDao;
import com.damianmichalak.fixer.model.FixerResponse;
import com.damianmichalak.fixer.model.ResponseOrError;

import javax.inject.Inject;

import rx.Observable;

public class MainActivityPresenter {

    private FixerDao fixerDao;

    @Inject
    public MainActivityPresenter(FixerDao fixerDao) {


        this.fixerDao = fixerDao;
    }

    public Observable<FixerResponse> data() {
        return fixerDao.getDataOrError().compose(ResponseOrError.<FixerResponse>onlySuccess());
    }

    public Observable<ResponseOrError<FixerResponse>> dataOrError() {
        return fixerDao.getDataOrError();
    }

}
