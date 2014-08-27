package com.jonathanfinerty.liquidity.ContentProvider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class LiquidityContract {

    private LiquidityContract(){}

    public static final String AUTHORITY = "com.jonathanfinerty.liquidity.provider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Expense implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(LiquidityContract.CONTENT_URI, "expenses");

        public static final Uri CONTENT_ITEM_URI = Uri.withAppendedPath(LiquidityContract.CONTENT_URI, "expenses/#");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.jonathanfinerty.liquidity.expense";

        public static final String CONTENT_ITEM_TYPE  = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.jonathanfinerty.liquidity.expense";

        public static final String TABLE_NAME = "expense";

        public static final String COLUMN_NAME_VALUE = "value";

        public static final String COLUMN_NAME_TIME = "time";

        public static final String SORT_ORDER_DEFAULT = COLUMN_NAME_TIME + " DESC";

    }
}
