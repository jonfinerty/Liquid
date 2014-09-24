package com.jonathanfinerty.liquid.services;

import android.app.IntentService;
import android.content.Intent;

abstract class BaseService extends IntentService {

    public BaseService(String name) {
        super(name);
    }

    protected abstract boolean ValidateIntentExtras(Intent intent);

    protected abstract void ExecuteOperation();

    @Override
    protected void onHandleIntent(Intent intent) {

        boolean extrasValid = ValidateIntentExtras(intent);

        if (extrasValid) {
            ExecuteOperation();
        }

    }
}
