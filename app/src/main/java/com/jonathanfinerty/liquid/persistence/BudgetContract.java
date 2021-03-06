package com.jonathanfinerty.liquid.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BudgetContract implements BaseColumns {

    static final String PATH = "budget";

    public static final Uri URI = Uri.withAppendedPath(ContractBase.BASE_URI, PATH);

    public static final String TYPE  = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.jonathanfinerty.liquid.budget";

    public static final String COLUMN_NAME_AMOUNT = "amount";

    public static final String COLUMN_NAME_DATE = "date";

    /* internal database definition */
    static final String TABLE_NAME = "budget";

    static final String CREATE_SQL =
            "CREATE TABLE " + BudgetContract.TABLE_NAME +
            " (" +
                BudgetContract._ID + " INTEGER PRIMARY KEY," +
                BudgetContract.COLUMN_NAME_AMOUNT + " REAL," +
                BudgetContract.COLUMN_NAME_DATE + " LONG" +
            " )";
}
