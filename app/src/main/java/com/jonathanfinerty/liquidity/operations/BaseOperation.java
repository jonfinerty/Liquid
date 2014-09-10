package com.jonathanfinerty.liquidity.operations;

import android.app.IntentService;
import android.content.Intent;

public abstract class BaseOperation extends IntentService {

    public BaseOperation(String name) {
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
