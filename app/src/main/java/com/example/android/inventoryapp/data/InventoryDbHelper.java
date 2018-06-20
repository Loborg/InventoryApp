package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.inventoryapp.data.InventoryContract.*;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
            + Product.TABLE_NAME + " ("
            + Product._ID + " " + INTEGER + " " + PRIMARY_KEY + " " + AUTO_INCREMENT + ", "
            + Product.PRODUCT_NAME + " " + TEXT + " " + NOT_NULL + ", "
            + Product.PRODUCT_PRICE + " " + FLOAT + ", "
            + Product.PRODUCT_QUANTITY + " " + INTEGER + ")";

    private final String CREATAE_TABLE_SUPPLIER = "CREATE TABLE "
            + Supplier.TABLE_NAME + " ("
            + Supplier._ID + " " + INTEGER + " " + PRIMARY_KEY + " " + AUTO_INCREMENT + ", "
            + Supplier.PRODUCT_ID + " " + INTEGER + " " + NOT_NULL + ", "
            + Supplier.SUPLIER_NAME + " " + TEXT + ", "
            + Supplier.SUPLIER_PHONE + " " + INTEGER + ")";

    private final String DROP_PRODUCT = "DROP TABLE IF EXISTS " + Product.TABLE_NAME;
    private final String DROP_SUPPLIER = "DROP TABLE IF EXISTS " + Supplier.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATAE_TABLE_SUPPLIER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_PRODUCT);
        db.execSQL(DROP_SUPPLIER);
        onCreate(db);
    }
}
