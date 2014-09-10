package com.jonathanfinerty.liquidity.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

class LiquidityContractBase {

    private LiquidityContractBase(){}

    private static final String AUTHORITY = "com.jonathanfinerty.liquidity.persistence";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
}
