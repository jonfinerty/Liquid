package com.jonathanfinerty.liquidity.loaders;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

class CallbackContentObserver extends ContentObserver {

    private final ChangeObserver changeObserver;

    public interface ChangeObserver {
        public void onChange();
    }

    public CallbackContentObserver(Handler handler, ChangeObserver changeObserver)
    {
        super(handler);
        this.changeObserver = changeObserver;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        changeObserver.onChange();
    }
}