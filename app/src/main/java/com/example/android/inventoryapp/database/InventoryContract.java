package com.example.android.inventoryapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    public static final String DB_NAME = "Inventory";
    public static final int DB_VERSION = 1;

    private InventoryContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTET_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCT = "Product";

    public static final class Product implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTET_URI, PATH_PRODUCT);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY
                + "/" + PATH_PRODUCT;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY
                + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "Product";
        public static final String PRODUCT_NAME = "pName";
        public static final String PRODUCT_PRICE = "Price";
        public static final String PRODUCT_QUANTITY = "Quantity";
        public static final String SUPLIER_NAME = "sName";
        public static final String SUPLIER_PHONE = "PhoneNumber";
    }
}
