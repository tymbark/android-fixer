package com.damianmichalak.fixer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.dagger.ActivityScope;
import com.damianmichalak.fixer.presenter.MainActivityPresenter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Provides;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_recycler_view)
    RecyclerView recyclerView;

    @Inject
    MainActivityPresenter presenter;
    @Inject
    UniversalAdapter adapter;

    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DaggerMainActivity_Component.builder()
                .applicationComponent(((MainApplication) getApplication()).getApplicationComponent())
                .module(new Module())
                .build()
                .inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        subscription.add(
                presenter.getDataSuccess().subscribe(adapter)
        );

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
