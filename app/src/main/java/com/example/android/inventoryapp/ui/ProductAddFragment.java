package com.example.android.inventoryapp.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.database.InventoryContract.*;

import static com.example.android.inventoryapp.ui.MainActivity.P_CONTENT_URI;
import static com.example.android.inventoryapp.ui.MainActivity.P_ID;
import static com.example.android.inventoryapp.ui.MainActivity.addFragmentTag;

public class ProductAddFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ProductAddFragment.class.getSimpleName();

    Uri currentProductUri;

    private EditText vProductName;
    private EditText vProductPrice;
    private EditText vProductQuantity;
    private EditText vSupplierName;
    private EditText vSupplierPhone;

    String productUriString;
    long productId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.product_add_fragment, container, false);

        vProductName = baseView.findViewById(R.id.add_productName);
        vProductPrice = baseView.findViewById(R.id.add_price);
        vProductQuantity = baseView.findViewById(R.id.add_quantity);
        vSupplierName = baseView.findViewById(R.id.add_suplierName);
        vSupplierPhone = baseView.findViewById(R.id.add_supplierPhoneNumber);

        try {
            productUriString = getArguments().getString(P_CONTENT_URI);
            productId = getArguments().getLong(P_ID);

            currentProductUri = ContentUris.withAppendedId(Uri.parse(productUriString), productId);

            getLoaderManager().initLoader(0, null, this);
        } catch (NullPointerException e){
        }

        ImageView cancel = baseView.findViewById(R.id.add_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragmentFromFrame();
            }
        });

        ImageView add = baseView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInventoryItem();
            }
        });
        return baseView;
    }



    public void removeFragmentFromFrame(){
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(addFragmentTag);
        if (fragment != null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
            Log.v(TAG, "Doo Nothing");
        }
    }

    private void addInventoryItem(){
        String productName = vProductName.getText().toString().trim();
        String productPrice = vProductPrice.getText().toString().trim();
        String productQuantity = vProductQuantity.getText().toString().trim();
        String supplierName = vSupplierName.getText().toString().trim();
        String supplierPhone = vSupplierPhone.getText().toString().trim();

        String toastBase = "Plese specifie the ";
        StringBuilder toastString = new StringBuilder();
        toastString.append(toastBase);

        ContentValues valuesProduct = new ContentValues();

        if(productQuantity.isEmpty()) {
            productQuantity = "1";
            valuesProduct.put(Product.PRODUCT_QUANTITY, productQuantity);
        } else {
            valuesProduct.put(Product.PRODUCT_QUANTITY, productQuantity);
        }
        if (productName.isEmpty()){
            toastString.append("product name, ");
        } else {
            valuesProduct.put(Product.PRODUCT_NAME, productName);
        }
        if (productPrice.isEmpty()){
            toastString.append("product price, ");
        } else {
            valuesProduct.put(Product.PRODUCT_PRICE, productPrice);
        }
        if (supplierName.isEmpty()){
            toastString.append("supplier name, ");
        } else {
            valuesProduct.put(Product.SUPLIER_NAME, supplierName);
        }
        if (supplierPhone.isEmpty()) {
            toastString.append("supplier phone number.");
        } else {
            valuesProduct.put(Product.SUPLIER_PHONE, supplierPhone);
        }
        if (productName.isEmpty() || productPrice.isEmpty() || supplierName.isEmpty() || supplierPhone.isEmpty()){
            Toast.makeText(getContext(), toastString.toString(), Toast.LENGTH_SHORT).show();
        }

        if (valuesProduct.containsKey(Product.PRODUCT_NAME)
                && valuesProduct.containsKey(Product.PRODUCT_PRICE)
                && valuesProduct.containsKey(Product.PRODUCT_QUANTITY)
                && valuesProduct.containsKey(Product.SUPLIER_NAME)
                && valuesProduct.containsKey(Product.SUPLIER_PHONE)){
            if (currentProductUri == null){
                getActivity().getContentResolver().insert(Product.CONTENT_URI, valuesProduct);
                removeFragmentFromFrame();
                hidSoftKeyboard();
            } else {
                getActivity().getContentResolver().update(currentProductUri, valuesProduct,
                        null, null);
                removeFragmentFromFrame();
                hidSoftKeyboard();
            }
        }
    }

    private void hidSoftKeyboard(){
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException e){
            Log.v(TAG, "Doo Nothing");
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                Product._ID,
                Product.PRODUCT_NAME,
                Product.PRODUCT_QUANTITY,
                Product.PRODUCT_PRICE,
                Product.SUPLIER_NAME,
                Product.SUPLIER_PHONE};

        return new CursorLoader(getActivity(),
                currentProductUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            int pNameIndex = data.getColumnIndex(Product.PRODUCT_NAME);
            int pQuantityIndex = data.getColumnIndex(Product.PRODUCT_QUANTITY);
            int pPriceIndex = data.getColumnIndex(Product.PRODUCT_PRICE);
            int sNameIndex = data.getColumnIndex(Product.SUPLIER_NAME);
            int sPhoneIndex = data.getColumnIndex(Product.SUPLIER_PHONE);

            vProductName.setText(data.getString(pNameIndex));
            vProductQuantity.setText(String.valueOf(data.getInt(pQuantityIndex)));
            vProductPrice.setText(String.valueOf(data.getFloat(pPriceIndex)));
            vSupplierName.setText(data.getString(sNameIndex));
            vSupplierPhone.setText(String.valueOf(data.getLong(sPhoneIndex)));
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}
