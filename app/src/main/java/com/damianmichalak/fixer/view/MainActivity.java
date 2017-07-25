package com.damianmichalak.fixer.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.dagger.ActivityScope;
import com.damianmichalak.fixer.presenter.MainActivityPresenter;
import com.jakewharton.rxbinding.view.RxView;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Provides;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.main_progress_view)
    View progress;
    @BindView(R.id.main_empty_view)
    View emptyView;

    @Inject
    MainActivityPresenter presenter;
    @Inject
    UniversalAdapter adapter;

    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
            getSupportActionBar().setElevation(0);

        ButterKnife.bind(this);

        DaggerMainActivity_Component.builder()
                .applicationComponent(((MainApplication) getApplication()).getApplicationComponent())
                .module(new Module())
                .build()
                .inject(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HeaderDecoration(adapter));
        recyclerView.setAdapter(adapter);

        subscription.add(Subscriptions.from(
                presenter.getSubscription(),
                presenter.getDataSuccessObservable().subscribe(adapter),
                presenter.getDataErrorObservable()
                        .subscribe(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Snackbar.make(recyclerView, R.string.api_error, Snackbar.LENGTH_SHORT).show();
                            }
                        }),
                presenter.getEmptyObservable()
                        .subscribe(RxView.visibility(emptyView)),
                presenter.getProgressObservable()
                        .subscribe(RxView.visibility(progress))
        ));

    }

    @Override
    protected void onDestroy() {
        subscription.clear();
        super.onDestroy();
    }

    @dagger.Component(
            dependencies = MainApplication.ApplicationComponent.class,
            modules = Module.class)
    @ActivityScope
    interface Component {
        void inject(MainActivity activity);
    }

    @dagger.Module
    class Module {

        @Provides
        @Nonnull
        LayoutInflater provideInflater() {
            return getLayoutInflater();
        }
    }

}
