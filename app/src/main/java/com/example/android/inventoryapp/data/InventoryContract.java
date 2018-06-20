package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

public class InventoryContract {

    public static final String DB_NAME = "Inventory";
    public static final int DB_VERSION = 1;

    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String AUTO_INCREMENT = "AUTOINCREMENT";
    public static final String NOT_NULL = "NOT NULL";
    public static final String INTEGER = "INTEGER";
    public static final String TEXT = "TEXT";
    public static final String FLOAT = "FLOAT";

    private InventoryContract(){}

    public static final class Product implements BaseColumns {

        public static final String TABLE_NAME = "Product";

        public static final String PRODUCT_NAME = "pName";
        public static final String PRODUCT_PRICE = "Price";
        public static final String PRODUCT_QUANTITY = "Quantity";
    }

    public static final class Supplier implements BaseColumns {

        public static final String TABLE_NAME = "Supplier";

        public static final String PRODUCT_ID = "Product_id";
        public static final String SUPLIER_NAME = "sName";
        public static final String SUPLIER_PHONE = "PhoneNumber";
    }
}
