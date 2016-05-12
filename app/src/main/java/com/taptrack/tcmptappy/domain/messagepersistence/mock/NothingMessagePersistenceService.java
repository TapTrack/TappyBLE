package com.taptrack.tcmptappy.domain.messagepersistence.mock;

import com.taptrack.tcmptappy.data.ParsedTcmpMessage;
import com.taptrack.tcmptappy.data.SavedTcmpMessage;
import com.taptrack.tcmptappy.domain.messagepersistence.TCMPMessagePersistenceService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class NothingMessagePersistenceService implements TCMPMessagePersistenceService {
    @Override
    public void saveTcmpMessage(SavedTcmpMessage message) {

    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedSavedTcmpMessages() {
        return Observable.just((List<SavedTcmpMessage>) new ArrayList<SavedTcmpMessage>(0));
    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedLimitedSavedTcmpMessages(int limit) {
        return Observable.just((List<SavedTcmpMessage>) new ArrayList<SavedTcmpMessage>(0));
    }

    @Override
    public Observable<List<SavedTcmpMessage>> getSortedLimitedThrottledSavedTcmpMessages(int limit, int rate) {
        return Observable.just((List<SavedTcmpMessage>) new ArrayList<SavedTcmpMessage>(0));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedResolvedTcmpMessages() {
        return Observable.just((List<ParsedTcmpMessage>) new ArrayList<ParsedTcmpMessage>(0));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedResolvedTcmpMessages(int limit) {
        return Observable.just((List<ParsedTcmpMessage>) new ArrayList<ParsedTcmpMessage>(0));
    }

    @Override
    public Observable<List<ParsedTcmpMessage>> getSortedLimitedThrottledResolvedTcmpMessages(int limit, int rate) {
        return Observable.just((List<ParsedTcmpMessage>) new ArrayList<ParsedTcmpMessage>(0));
    }

    @Override
    public void clearMessageDatabase() {

    }
}
