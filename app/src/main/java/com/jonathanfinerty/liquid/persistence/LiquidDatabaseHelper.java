package com.jonathanfinerty.liquid.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class LiquidDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Liquid.db";

    public LiquidDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExpenseContract.CREATE_SQL);
        db.execSQL(BudgetContract.CREATE_SQL);

        InsertDefaultData(db);
    }

    private void InsertDefaultData(SQLiteDatabase db) {
        ContentValues defaultBudgetValues = new ContentValues();
        defaultBudgetValues.put(BudgetContract.COLUMN_NAME_AMOUNT, 1000);
        defaultBudgetValues.put(BudgetContract.COLUMN_NAME_DATE, 1);

        db.insert(BudgetContract.TABLE_NAME, null, defaultBudgetValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
