package com.damianmichalak.fixer.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("all")
public class FixerDaoTest {

    @Mock
    ApiService apiService;

    final TestScheduler scheduler = new TestScheduler();
    final DateHelper dateHelper = new DateHelper() {
        @Override
        public String today() {
            return "2000-02-08";
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


    }

    FixerResponse getFixerResponse() {
        return new FixerResponse("base", "date", new HashMap<String, Float>());
    }

    @Test
    public void testAfterSubscribe_returnValueOnce() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);


        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        verify(apiService, times(1)).getFixerResponse(anyString());
    }

    @Test
    public void testAfterMultipleSubscribtions_returnValueOnce() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);


        final TestSubscriber<List<FixerResponse>> subscriber1 = new TestSubscriber<>();
        final TestSubscriber<List<FixerResponse>> subscriber2 = new TestSubscriber<>();
        final TestSubscriber<List<FixerResponse>> subscriber3 = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber1);
        fixerDao.getDataSuccess().subscribe(subscriber2);
        fixerDao.getDataSuccess().subscribe(subscriber3);

        scheduler.triggerActions();

        verify(apiService, times(1)).getFixerResponse(anyString());
    }

    @Test
    public void testAfterSubscribe_andMultipleScheduler_returnValueOnce() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);


        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();
        scheduler.triggerActions();
        scheduler.triggerActions();
        scheduler.triggerActions();

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
        verify(apiService, times(1)).getFixerResponse(anyString());
    }

    @Test
    public void testAfterSubscribe_andNotTriggeringScheduler_dontReturnValues() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);

        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber);

        assert_().that(subscriber.getOnNextEvents()).isEmpty();
    }

    @Test
    public void testWhenApiReturnsError_successIsEmpty() throws Exception {
        final Observable<FixerResponse> error = Observable.error(new Exception());
        when(apiService.getFixerResponse(anyString())).thenReturn(error);

        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);

        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();

        assert_().that(subscriber.getOnNextEvents()).isEmpty();
    }

    @Test
    public void testWhenApiReturnsError_errorIsNotEmpty() throws Exception {
        final Observable<FixerResponse> error = Observable.error(new Exception());
        when(apiService.getFixerResponse(anyString())).thenReturn(error);
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);

        final TestSubscriber<Throwable> subscriber = new TestSubscriber<>();
        fixerDao.getDataError().subscribe(subscriber);

        scheduler.triggerActions();

        assert_().that(subscriber.getOnNextEvents()).hasSize(1);
    }

    @Test
    public void testWhenLoadMoreObserverEmits_getNewValues() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);

        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();

        assert_().that(subscriber.getOnNextEvents()).hasSize(4);
    }

    @Test
    public void testWhenOneReqeustConstantlyFails_repeatUntilSuccess_andDontMoveToNextDate() throws Exception {

        when(apiService.getFixerResponse("2000-02-08")).thenReturn(Observable.just(getFixerResponse()));
        when(apiService.getFixerResponse("2000-02-07")).thenReturn(Observable.just(getFixerResponse()));
        final Observable<FixerResponse> error = Observable.error(new Throwable());
        when(apiService.getFixerResponse("2000-02-06")).thenReturn(error);
        when(apiService.getFixerResponse("2000-02-05")).thenReturn(Observable.just(getFixerResponse()));

        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);

        final TestSubscriber<List<FixerResponse>> successSubscriber = new TestSubscriber<>();
        final TestSubscriber<Throwable> errorSubscriber = new TestSubscriber<>();
        fixerDao.getDataSuccess().subscribe(successSubscriber);
        fixerDao.getDataError().subscribe(errorSubscriber);

        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();

        assert_().that(successSubscriber.getOnNextEvents()).hasSize(2);
        assert_().that(errorSubscriber.getOnNextEvents()).hasSize(3);

        verify(apiService, times(1)).getFixerResponse("2000-02-08");
        verify(apiService, times(1)).getFixerResponse("2000-02-07");
        verify(apiService, times(3)).getFixerResponse("2000-02-06");
    }

    @Test
    public void testStupidCache() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);


        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        final TestSubscriber<List<FixerResponse>> subscriber2 = new TestSubscriber<>();
        final Subscription subscription = fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();
        subscription.unsubscribe();

        fixerDao.getDataSuccess().subscribe(subscriber2);

        assert_().that(subscriber.getOnNextEvents()).isEqualTo(subscriber2.getOnNextEvents());
        verify(apiService, times(1)).getFixerResponse(anyString());
    }

    @Test
    public void testStupidCache_returnsAllElements() throws Exception {
        when(apiService.getFixerResponse(anyString())).thenReturn(Observable.just(getFixerResponse()));
        final FixerDao fixerDao = new FixerDao(apiService, scheduler, scheduler, dateHelper);


        final TestSubscriber<List<FixerResponse>> subscriber = new TestSubscriber<>();
        final TestSubscriber<List<FixerResponse>> subscriber2 = new TestSubscriber<>();
        final Subscription subscription = fixerDao.getDataSuccess().subscribe(subscriber);

        scheduler.triggerActions();
        fixerDao.getLoadMoreObserver().onNext(null);
        scheduler.triggerActions();

        subscription.unsubscribe();

        fixerDao.getDataSuccess().subscribe(subscriber2);

        assert_().that(subscriber.getOnNextEvents().get(1))
                .isEqualTo(subscriber2.getOnNextEvents().get(0));
        verify(apiService, times(2)).getFixerResponse(anyString());
    }
}
