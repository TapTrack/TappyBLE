package com.taptrack.tcmptappy.domain.preferencepersistence.mock;

import com.taptrack.tcmptappy.domain.preferencepersistence.AppPreferenceService;

import rx.Observable;

public class NothingPreferencePersistenceService implements AppPreferenceService {
    @Override
    public void setCommunicationActive(boolean isActive) {

    }

    @Override
    public Observable<Boolean> getCommunicationActive() {
        return Observable.just(Boolean.FALSE);
    }

    @Override
    public void setNdefBackgroundLaunch(boolean launchNdef) {

    }

    @Override
    public Observable<Boolean> getBackgroundNdefLaunch() {
        return Observable.just(Boolean.FALSE);
    }
}
