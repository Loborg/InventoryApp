package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.inventoryapp.database.InventoryContract.*;

public class InventoryCursorAdapter extends CursorAdapter {

    TextView productName;
    TextView price;
    TextView quantity;
    ImageView salesButton;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        productName = view.findViewById(R.id.productName);
        price = view.findViewById(R.id.price);
        quantity = view.findViewById(R.id.quantity);
        salesButton = view.findViewById(R.id.sale);

        int pIndex = cursor.getColumnIndex(Product._ID);
        int pNameIndex = cursor.getColumnIndex(Product.PRODUCT_NAME);
        int pPriceIndex= cursor.getColumnIndex(Product.PRODUCT_PRICE);
        int pQuantity = cursor.getColumnIndex(Product.PRODUCT_QUANTITY);

        final long index = cursor.getLong(pIndex);
        final String quantityString = cursor.getString(pQuantity);
        productName.setText(cursor.getString(pNameIndex));
        price.setText(cursor.getString(pPriceIndex));
        quantity.setText(quantityString);


        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(quantityString, context, index);
            }
        });
    }

    private void decreaseQuantity(String quantityString, Context context, long index){
        Integer qInt = Integer.parseInt(quantityString);

        if (qInt > 0){
            qInt--;
            String pQString = qInt.toString();

            ContentValues values = new ContentValues();
            values.put(Product.PRODUCT_QUANTITY, pQString);
            String selection = Product._ID + "=?";
            String[] selectionArgs = new String[]{String.valueOf(index)};
            Uri currentProductUri = ContentUris.withAppendedId(Product.CONTENT_URI, index);
            context.getContentResolver().update(currentProductUri, values,
                    selection, selectionArgs);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.negative_prompt), Toast.LENGTH_SHORT).show();
        }
    }
}
