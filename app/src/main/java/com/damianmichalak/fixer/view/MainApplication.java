package com.damianmichalak.fixer.view;

import android.app.Application;
import android.content.Context;

import com.damianmichalak.fixer.dagger.NetworkModule;
import com.damianmichalak.fixer.model.FixerDao;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import rx.Scheduler;

public class MainApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerMainApplication_ApplicationComponent
                .builder()
                .mainApplicationModule(new MainApplicationModule(this))
                .networkModule(new NetworkModule())
                .build();

        applicationComponent.inject(this);

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Singleton
    @dagger.Component(
            modules = {
                    NetworkModule.class,
                    MainApplicationModule.class
            }
    )
    public interface ApplicationComponent {

        void inject(MainApplication mainApplication);

        @Named("UI")
        Scheduler schedulerUI();

        @Named("IO")
        Scheduler schedulerIO();

        FixerDao provideFixerDao();

    }

    @dagger.Module
    class MainApplicationModule {
        private final MainApplication application;

        MainApplicationModule(MainApplication application) {
            this.application = application;
        }

        @Provides
        Context provideAppContext() {
            return application;
        }

    }

}
