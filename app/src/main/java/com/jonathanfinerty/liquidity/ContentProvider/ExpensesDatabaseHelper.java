package com.jonathanfinerty.liquidity.ContentProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpensesDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Liquidity.db";

    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + LiquidityContract.Expense.TABLE_NAME + " (" +
                LiquidityContract.Expense._ID + " INTEGER PRIMARY KEY," +
                LiquidityContract.Expense.COLUMN_NAME_VALUE + " REAL," +
                LiquidityContract.Expense.COLUMN_NAME_TIME + " LONG" +
        " )";

    public ExpensesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
