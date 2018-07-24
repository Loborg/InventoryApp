package com.example.android.inventoryapp.ui;



import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.database.InventoryContract.*;

import static com.example.android.inventoryapp.ui.MainActivity.*;

public class ProductDetailFragement extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = ProductDetailFragement.class.getSimpleName();
    public static final String DELETE_ARG_MESSAGE = "delete_argument_message";
    public static final String DIALOG_DELETE_RECORD = "dialogDeleteRecord";

    Uri currentProductUri;

    private TextView productName;
    private TextView productPrice;
    private TextView productQuantity;
    private TextView supplierName;
    private TextView supplierPhone;
    private EditText quantityVolume;

    String productUriString;
    long productId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.product_detail_fragment, container, false);

        productName = baseView.findViewById(R.id.detail_productName);
        productPrice = baseView.findViewById(R.id.detail_price);
        productQuantity = baseView.findViewById(R.id.quantity);
        supplierName = baseView.findViewById(R.id.details_suplierName);
        supplierPhone = baseView.findViewById(R.id.detail_phoneNumber);

        productUriString = getArguments().getString(P_CONTENT_URI);
        productId = getArguments().getLong(P_ID);

        currentProductUri = ContentUris.withAppendedId(Uri.parse(productUriString), productId);

        getLoaderManager().initLoader(0, null, this);

        ImageView editButton = baseView.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragmentFromFrame(detailFragmentTag);
                ProductAddFragment addFragment = new ProductAddFragment();
                Bundle date = new Bundle();
                date.putString(P_CONTENT_URI, productUriString);
                date.putLong(P_ID, productId);
                addFragment.setArguments(date);

                populateFragmentFrame(addFragment, addFragmentTag);
            }
        });

        ImageView cancelButton = baseView.findViewById(R.id.detail_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragmentFromFrame(detailFragmentTag);
            }
        });

        ImageView plusButton = baseView.findViewById(R.id.detail_plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        ImageView minusButton = baseView.findViewById(R.id.detail_minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        ImageView deleteButton = baseView.findViewById(R.id.detail_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord();
                removeFragmentFromFrame(detailFragmentTag);

            }
        });

        supplierPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustommer();
            }
        });

        quantityVolume = baseView.findViewById(R.id.quantityVolume);

        return baseView;
    }

    private void deleteRecord(){
        DeletDialogFragment deleteDialog = new DeletDialogFragment();
        Bundle date = new Bundle();
        date.putString(DELETE_ARG_MESSAGE, getContext().getResources().getString(R.string.deleteDialog_recordMessage));
        date.putString(P_CONTENT_URI, productUriString);
        date.putLong(P_ID, productId);
        deleteDialog.setArguments(date);
        deleteDialog.show(getFragmentManager(), DIALOG_DELETE_RECORD);
    }

    private void populateFragmentFrame(Fragment fragment, String fragmentTag){
        ProductAddFragment firstFragment = new ProductAddFragment();
        firstFragment.setArguments(getActivity().getIntent().getExtras());
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_frame, fragment, fragmentTag).commit();
    }

    public void removeFragmentFromFrame(String fragmentTeg){
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(fragmentTeg);
        if (fragment != null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            hidSoftKeyboard();
        } else {
            Log.v(TAG, "Doo Nothing");
        }
    }

    private int getQuantityVolume(){
        String volume = quantityVolume.getText().toString();
        if (volume.isEmpty()){
            quantityVolume.setHint("1");
            return 1;
        } else {
            return Integer.parseInt(volume);
        }
    }

    private void increaseQuantity(){
        Integer pQuantityAsInt = Integer.parseInt(productQuantity.getText().toString());
        int qVolume = getQuantityVolume();
        pQuantityAsInt = pQuantityAsInt + qVolume;

        String pQString = pQuantityAsInt.toString();

        ContentValues values = new ContentValues();
        values.put(Product.PRODUCT_QUANTITY, pQString);
        getActivity().getContentResolver().update(currentProductUri, values,
                null, null);
    }

    private void decreaseQuantity(){
        Integer pQuantityAsInt = Integer.parseInt(productQuantity.getText().toString());
        int qVolume = getQuantityVolume();
        pQuantityAsInt = pQuantityAsInt - qVolume;

        if (pQuantityAsInt >= 0){

            String pQString = pQuantityAsInt.toString();

            ContentValues values = new ContentValues();
            values.put(Product.PRODUCT_QUANTITY, pQString);
            getActivity().getContentResolver().update(currentProductUri, values,
                    null, null);
        } else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.negative_prompt), Toast.LENGTH_SHORT).show();
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

    private void callCustommer(){
        String phone = supplierPhone.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToFirst()){
            int pNameIndex = data.getColumnIndex(Product.PRODUCT_NAME);
            int pQuantityIndex = data.getColumnIndex(Product.PRODUCT_QUANTITY);
            int pPriceIndex = data.getColumnIndex(Product.PRODUCT_PRICE);
            int sNameIndex = data.getColumnIndex(Product.SUPLIER_NAME);
            int sPhoneIndex = data.getColumnIndex(Product.SUPLIER_PHONE);

            productName.setText(data.getString(pNameIndex));
            productQuantity.setText(String.valueOf(data.getInt(pQuantityIndex)));
            productPrice.setText(String.valueOf(data.getFloat(pPriceIndex)));
            supplierName.setText(data.getString(sNameIndex));
            supplierPhone.setText(String.valueOf(data.getLong(sPhoneIndex)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText(null);
        productQuantity.setText(null);
        productPrice.setText(null);
        supplierName.setText(null);
        supplierPhone.setText(null);
    }
}
