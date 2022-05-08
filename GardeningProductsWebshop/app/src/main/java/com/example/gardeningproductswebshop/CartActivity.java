package com.example.gardeningproductswebshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static final String LOG_TAG = CartActivity.class.getName();

    private PurchaseItemAdapter mAdapter;
    private boolean viewRow = true;
    private RecyclerView mRecyclerView;
    public ArrayList<ShoppingItem> mItemsPurchase;
    private CollectionReference mItems;
    private NotificationHandler mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mItemsPurchase = (ArrayList<ShoppingItem>) getIntent().getSerializableExtra("Purchase");
        /*for (ShoppingItem it : mItemsPurchase) {
            System.out.println(it.getName());
        }*/
        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, 1));
        // Initialize the ArrayList that will contain the data.
        //mItemsPurchase = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new PurchaseItemAdapter(this, mItemsPurchase);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cart_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_purchase:
                Log.d(LOG_TAG, "Send clicked!");
                ShoppingActivity.cartItems = 0;
                Intent intent = new Intent(this, ShoppingActivity.class);
                startActivity(intent);
                return true;
            case R.id.cancel_purchase:
                Log.d(LOG_TAG, "Cancel clicked!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}