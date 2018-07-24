package com.example.android.inventoryapp.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.InventoryCursorAdapter;
import com.example.android.inventoryapp.R;
import static com.example.android.inventoryapp.database.InventoryContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String addFragmentTag = "AddFragment";
    public static final String detailFragmentTag = "DetailFragment";
    public static final String P_CONTENT_URI = "pContentUri";
    public static final String P_ID = "p_id";
    public static final String DIALOG_DELETE_ALL = "dialogDeleteAll";
    private InventoryCursorAdapter adapter;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView headerText = findViewById(R.id.header_title);
        drawerLayout = findViewById(R.id.main_drawer);

        ImageView addButton = findViewById(R.id.add);
        ListView productList = findViewById(R.id.product_list);
        TextView emptyView = findViewById(R.id.empty);

        adapter = new InventoryCursorAdapter(this, null);
        productList.setAdapter(adapter);
        productList.setEmptyView(emptyView);

        getLoaderManager().initLoader(0, null, this);

        if (findViewById(R.id.fragment_frame) != null) {
            if (savedInstanceState != null) {
                return;
            }

            /*Setting up the MainActivity's add button
            to open the ProductAddFragment, whit a blank Inventory item*/
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddFragment();
                }
            });

            productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                    openEditFragment(id);
                }
            });

            headerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });

            setupNavigationDrower();
        }
    }

    /*Populating the FrameLayout in the MainActivity whit the given Fragment*/
    private void populateFragmentFrame(Fragment fragment, String fragmentTag){
        ProductAddFragment firstFragment = new ProductAddFragment();
        firstFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_frame, fragment, fragmentTag).commit();
    }

    private void setupNavigationDrower(){
        NavigationView navView = findViewById(R.id.nav_view);

        //Handles the navigationViews item selection
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.add:
                        openAddFragment();
                        drawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.delete:
                        deleteAll();
                        drawerLayout.closeDrawer(Gravity.START);
                        break;
                }
                return true;
            }
        });
    }

    private void openAddFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(detailFragmentTag);

        if (getSupportFragmentManager().findFragmentByTag(addFragmentTag) == null
                && getSupportFragmentManager().findFragmentByTag(detailFragmentTag) == null){
            populateFragmentFrame(new ProductAddFragment(), addFragmentTag);
        } else if (fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            populateFragmentFrame(new ProductAddFragment(), addFragmentTag);
        } else {
            Toast.makeText(MainActivity.this,
                    getResources().getString(R.string.alreadyAdd),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openEditFragment(long id){
        ProductDetailFragement detailFragement = new ProductDetailFragement();
        Bundle date = new Bundle();
        date.putString(P_CONTENT_URI, Product.CONTENT_URI.toString());
        date.putLong(P_ID, id);
        detailFragement.setArguments(date);

        if (getSupportFragmentManager().getFragments() != null){
            for (Fragment fragment:getSupportFragmentManager().getFragments()){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }
        populateFragmentFrame(detailFragement, detailFragmentTag);
    }

    private void deleteAll(){
        if (adapter.getCount() == 0){
            Toast.makeText(MainActivity.this, "There is no record to delete", Toast.LENGTH_SHORT).show();
        } else {
            DeletDialogFragment dialog = new DeletDialogFragment();
            Bundle data = new Bundle();
            data.putString(ProductDetailFragement.DELETE_ARG_MESSAGE, getResources().getString(R.string.deleteDialog_allMessage));
            data.putString(P_CONTENT_URI, Product.CONTENT_URI.toString());
            dialog.setArguments(data);
            dialog.show(getSupportFragmentManager(), DIALOG_DELETE_ALL);
        }
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


        return new CursorLoader(this,
                Product.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
