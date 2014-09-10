package com.jonathanfinerty.liquidity.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ExpenseContract implements BaseColumns {

    private ExpenseContract() {}

    public static final Uri GROUP_URI = Uri.withAppendedPath(ContractBase.BASE_URI, "expenses");

    public static final Uri SINGLE_URI = Uri.withAppendedPath(GROUP_URI, "#");

    public static final String GROUP_TYPE  = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.jonathanfinerty.liquidity.expense";

    public static final String SINGLE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.jonathanfinerty.liquidity.expense";




    /* Publicly known column names */

    public static final String COLUMN_NAME_VALUE = "value";

    public static final String COLUMN_NAME_TIME = "time";

    /* Internal Database definition */

    static final String TABLE_NAME = "expense";

    static final String DEFAULT_SORT_ORDER = COLUMN_NAME_TIME + " DESC";

    static final String CREATE_SQL =
            "CREATE TABLE " + ExpenseContract.TABLE_NAME +
            " (" +
                ExpenseContract._ID + " INTEGER PRIMARY KEY," +
                ExpenseContract.COLUMN_NAME_VALUE + " REAL," +
                ExpenseContract.COLUMN_NAME_TIME + " LONG" +
            " )";
}
