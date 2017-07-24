package com.damianmichalak.fixer.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.dagger.ActivityScope;
import com.damianmichalak.fixer.model.FixerResponse;
import com.damianmichalak.fixer.presenter.MainActivityPresenter;

import javax.inject.Inject;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Inject
    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerMainActivity_Component.builder()
                .applicationComponent(((MainApplication) getApplication()).getApplicationComponent())
                .build()
                .inject(this);

        presenter.getDataSuccess().subscribe(new Action1<FixerResponse>() {
            @Override
            public void call(FixerResponse fixerResponse) {
                ((TextView) findViewById(R.id.text)).setText(fixerResponse.toString());
            }
        });

    }

    @dagger.Component(dependencies = MainApplication.ApplicationComponent.class)
    @ActivityScope
    interface Component {

        void inject(MainActivity activity);
    }

}
