package com.jonathanfinerty.liquidity.persistence;

import android.net.Uri;

class ContractBase {

    private ContractBase(){}

    private static final String AUTHORITY = "com.jonathanfinerty.liquidity.persistence";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
}
