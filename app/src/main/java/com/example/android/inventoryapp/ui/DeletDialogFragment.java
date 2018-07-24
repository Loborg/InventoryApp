package com.example.android.inventoryapp.ui;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.database.InventoryContract;

import static com.example.android.inventoryapp.ui.MainActivity.*;

public class DeletDialogFragment extends DialogFragment {

    Uri currentProductUri;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String dialogMessage = getArguments().getString(ProductDetailFragement.DELETE_ARG_MESSAGE);
        String productUriString = getArguments().getString(P_CONTENT_URI);


        if (getArguments().containsKey(P_ID)){
            long productId = getArguments().getLong(P_ID);
            currentProductUri = ContentUris.withAppendedId(Uri.parse(productUriString), productId);
        } else {
            currentProductUri = InventoryContract.Product.CONTENT_URI;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(dialogMessage)
                .setPositiveButton(R.string.deleteDialog_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().getContentResolver().delete(currentProductUri,
                                null, null);
                    }
                })
                .setNegativeButton(R.string.deleteDialog_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
