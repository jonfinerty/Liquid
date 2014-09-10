package com.jonathanfinerty.liquidity.persistence;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BudgetContract implements BaseColumns {

    public static final Uri URI = Uri.withAppendedPath(ContractBase.BASE_URI, "budget");

    public static final String TYPE  = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.jonathanfinerty.liquidity.budget";

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


    /*public static String PREFERENCES = "budget preferences";

    public static String DATE_PREFERENCE = "budget date";

    public static String AMOUNT_PREFERENCE = "budget amount";*/

}
