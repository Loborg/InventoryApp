package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryDbHelper;

import static com.example.android.inventoryapp.data.InventoryContract.*;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;
    private TextView queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryResult = findViewById(R.id.query_result);

        mDbHelper = new InventoryDbHelper(this);
        insertTestColumn();
    }

    private void insertTestColumn(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues valuesProduct = new ContentValues();
        valuesProduct.put(Product.PRODUCT_NAME, "Glasses");
        valuesProduct.put(Product.PRODUCT_PRICE, 25.49);
        valuesProduct.put(Product.PRODUCT_QUANTITY, 50);
        db.insert(Product.TABLE_NAME, null, valuesProduct);

        ContentValues valuesSupplier = new ContentValues();
        valuesSupplier.put(Supplier.PRODUCT_ID, 1);
        valuesSupplier.put(Supplier.SUPLIER_NAME, "Dom Dodom");
        valuesSupplier.put(Supplier.SUPLIER_PHONE, "0630555666");
        db.insert(Supplier.TABLE_NAME, null, valuesSupplier);
    }

    public void query(View v){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String rowQuery =
                "SELECT p."
                        + Product.PRODUCT_NAME + ", p."
                        + Product.PRODUCT_PRICE+ ", s."
                        + Supplier.SUPLIER_NAME + " " +
                "FROM " + Product.TABLE_NAME + " p " +
                "INNER JOIN "
                        + Supplier.TABLE_NAME + " s ON p."
                        + Product._ID + " = s."
                        + Supplier.PRODUCT_ID;

        Cursor cursor = db.rawQuery(rowQuery, null);

        try{
            //Nem lehet két azonos oszlopnév két külön táblában mert a kurzor indexelésnél
            //nem tudja eldönteni a kurzor hogy melyiket válassza.
            int pNameIndex = cursor.getColumnIndex(Product.PRODUCT_NAME);
            int pPriceIndex = cursor.getColumnIndex(Product.PRODUCT_PRICE);
            int sNameIndex = cursor.getColumnIndex(Supplier.SUPLIER_NAME);

            while (cursor.moveToNext()){
                String pName = cursor.getString(pNameIndex);
                float pPrice = cursor.getFloat(pPriceIndex);
                String sName = cursor.getString(sNameIndex);

                queryResult.append(pName + " - " + pPrice + " - " + sName + "\n");
            }
        } finally {
            cursor.close();
        }


    }
}
