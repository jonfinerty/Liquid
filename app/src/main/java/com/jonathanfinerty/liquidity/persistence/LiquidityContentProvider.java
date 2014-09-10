package com.jonathanfinerty.liquidity.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.jonathanfinerty.liquidity.domain.Expense;

public class LiquidityContentProvider extends ContentProvider {

    private LiquidityDatabaseHelper expensesDatabaseHelper;

    @Override
    public boolean onCreate() {
        expensesDatabaseHelper = new LiquidityDatabaseHelper(getContext());

        return true;
    }

    // todo: this needs to be better, ugly if else if
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = expensesDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        if (uri.equals(ExpenseContract.GROUP_URI)){

            builder.setTables(ExpenseContract.TABLE_NAME);

            if (TextUtils.isEmpty(sortOrder)) {
                sortOrder = ExpenseContract.DEFAULT_SORT_ORDER;
            }

        } else if (uri.equals(ExpenseContract.SINGLE_URI)) {

            builder.setTables(ExpenseContract.TABLE_NAME);

            builder.appendWhere(ExpenseContract._ID + " = " + uri.getLastPathSegment());

        } else if (uri.equals(BudgetContract.URI)) {



        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        if (ExpenseContract.GROUP_URI.equals(uri)) {

            return ExpenseContract.GROUP_TYPE;

        } else if (ExpenseContract.SINGLE_URI.equals(uri)) {

            return ExpenseContract.SINGLE_TYPE;

        }

        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (uri.equals(ExpenseContract.GROUP_URI) == false) {
            throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        SQLiteDatabase db = expensesDatabaseHelper.getWritableDatabase();
        long expenseId = db.insert(ExpenseContract.TABLE_NAME, null, values);

        return getUriForId(uri, expenseId);
    }

    private Uri getUriForId(Uri uri, long expenseId) {
        Uri uriWithId = ContentUris.withAppendedId(uri, expenseId);
        getContext().getContentResolver().notifyChange(uriWithId, null);
        return uriWithId;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = expensesDatabaseHelper.getWritableDatabase();

        return db.delete(ExpenseContract.TABLE_NAME,
                ExpenseContract._ID + "=?",
                new String[]{ uri.getLastPathSegment() });
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
