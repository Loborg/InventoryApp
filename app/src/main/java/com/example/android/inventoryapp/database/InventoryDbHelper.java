package com.example.android.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.inventoryapp.database.InventoryContract.*;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public InventoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
                + Product.TABLE_NAME + " ("
                + Product._ID + " INTEGER " + " PRIMARY KEY " + " AUTOINCREMENT " + " NOT NULL, "
                + Product.PRODUCT_NAME + " VARCHAR, "
                + Product.PRODUCT_PRICE + " FLOAT, "
                + Product.PRODUCT_QUANTITY + " INTEGER, "
                + Product.SUPLIER_NAME + " VARCHAR, "
                + Product.SUPLIER_PHONE + " INTEGER);";

        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
