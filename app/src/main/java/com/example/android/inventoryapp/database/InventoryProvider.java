package com.example.android.inventoryapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.inventoryapp.R;

import static com.example.android.inventoryapp.database.InventoryContract.*;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT, PRODUCT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    InventoryDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                cursor = database.query(
                        Product.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[] {
                  String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(Product.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
                default:
                    throw new IllegalArgumentException(getContext().getString(R.string.uri_error_message) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return Product.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return Product.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri + " whith matcher" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri, values);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values){

        String name = values.getAsString(Product.PRODUCT_NAME);
        Float price = values.getAsFloat(Product.PRODUCT_PRICE);
        Integer quantity = values.getAsInteger(Product.PRODUCT_QUANTITY);
        String supplierName = values.getAsString(Product.SUPLIER_NAME);
        Integer supplierPhone = values.getAsInteger(Product.SUPLIER_PHONE);


        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(Product.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri,
                      ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {


        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri,
                               ContentValues values,
                               String selection,
                               String[] selectionArgs){

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowUpdate = database.update(Product.TABLE_NAME, values, selection, selectionArgs);
        if(rowUpdate != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdate;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDelete;
        switch (match){
            case PRODUCT:
                rowsDelete = database.delete(Product.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = Product._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDelete = database.delete(Product.TABLE_NAME, selection, selectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDelete != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDelete;
    }
}
