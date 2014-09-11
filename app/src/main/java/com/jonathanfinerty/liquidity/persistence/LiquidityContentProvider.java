package com.jonathanfinerty.liquidity.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LiquidityContentProvider extends ContentProvider {

    private LiquidityDatabaseHelper expensesDatabaseHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int EXPENSES = 1;
    private static final int EXPENSE = 2;
    private static final int BUDGET = 3;

    static
    {
        uriMatcher.addURI(ContractBase.AUTHORITY, ExpenseContract.GROUP_PATH, EXPENSES);
        uriMatcher.addURI(ContractBase.AUTHORITY, ExpenseContract.SINGLE_PATH, EXPENSE);
        uriMatcher.addURI(ContractBase.AUTHORITY, BudgetContract.PATH, BUDGET);
    }

    @Override
    public boolean onCreate() {
        expensesDatabaseHelper = new LiquidityDatabaseHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case EXPENSES:
                return ExpenseContract.GROUP_TYPE;
            case EXPENSE:
                return ExpenseContract.SINGLE_TYPE;
            case BUDGET:
                return BudgetContract.TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)){
            case EXPENSES:
                SQLiteDatabase db = expensesDatabaseHelper.getWritableDatabase();
                long expenseId = db.insert(ExpenseContract.TABLE_NAME, null, values);
                return ExpenseContract.getSingleUri(expenseId);
            default:
                throw new IllegalArgumentException("Unsupported URI for insert: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = expensesDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case EXPENSES:
                builder.setTables(ExpenseContract.TABLE_NAME);

                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ExpenseContract.DEFAULT_SORT_ORDER;
                }
                break;
            case EXPENSE:
                builder.setTables(ExpenseContract.TABLE_NAME);
                builder.appendWhere(ExpenseContract._ID + " = " + uri.getLastPathSegment());
                break;
            case BUDGET:
                builder.setTables(BudgetContract.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for query: " + uri);
        }

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case BUDGET:
                SQLiteDatabase db = expensesDatabaseHelper.getWritableDatabase();
                return db.update(BudgetContract.TABLE_NAME, values, null, null);
            default:
                throw new IllegalArgumentException("Unsupported URI for update: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        switch (uriMatcher.match(uri)) {
            case EXPENSE:
                SQLiteDatabase db = expensesDatabaseHelper.getWritableDatabase();

                return db.delete(
                        ExpenseContract.TABLE_NAME,
                        ExpenseContract._ID + "=?",
                        new String[]{
                                uri.getLastPathSegment()
                        }
                );
            default:
                throw new IllegalArgumentException("Unsupported URI for delete: " + uri);
        }

    }
}
