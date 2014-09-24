package com.jonathanfinerty.liquid.persistence;

import android.net.Uri;

class ContractBase {

    private ContractBase(){}

    static final String AUTHORITY = "com.jonathanfinerty.liquid.persistence.LiquidContentProvider";

    static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
}
