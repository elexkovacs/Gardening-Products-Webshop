package com.example.gardeningproductswebshop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShoppingActivity extends AppCompatActivity implements SensorEventListener {
    private static final String LOG_TAG = ShoppingActivity.class.getName();
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView countTextView;
    public static int cartItems = 0;
    private int gridNumber = 1;
    private Integer itemLimit = 30;
    boolean isRunning = false;
    MediaPlayer mp;

    public static boolean nullaz = false;

    // Member variables.
    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    public ArrayList<ShoppingItem> mItemsPurchase;
    private ShoppingItemAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHandler mNotificationHandler;
    private AlarmManager mAlarmManager;
    private JobScheduler mJobScheduler;
    private SharedPreferences preferences;

    private boolean viewRow = true;

    Sensor sensor;
    SensorManager sensorManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        // preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        // if(preferences != null) {
        //     cartItems = preferences.getInt("cartItems", 0);
        //     gridNumber = preferences.getInt("gridNum", 1);
        // }

        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        // Initialize the ArrayList that will contain the data.
        mItemsData = new ArrayList<>();
        mItemsPurchase = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ShoppingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

        // Intent intent = new Intent("CUSTOM_MOBALKFEJL_BROADCAST");
        // LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        mNotificationHandler = new NotificationHandler(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        // setAlarmManager();
        setJobScheduler();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction == null)
                return;

            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    itemLimit = 30;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    itemLimit = 10;
                    queryData();
                    break;
            }
        }
    };

    private void initializeData() {
        // Get the resources from the XML file.
        String[] itemsList = getResources()
                .getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResources =
                getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

        // Create the ArrayList of Sports objects with the titles and
        // information about each sport.
        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShoppingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemRate.getFloat(i, 0),
                    itemsImageResources.getResourceId(i, 0),
                    0));
        }

        // Recycle the typed array.
        itemsImageResources.recycle();
    }

    public void queryData() {
        mItemsData.clear();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(itemLimit).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ShoppingItem item = document.toObject(ShoppingItem.class);
                        item.setId(document.getId());
                        mItemsData.add(item);
                    }

                    if (mItemsData.size() == 0) {
                        initializeData();
                        queryData();
                    }

                    // Notify the adapter of the change.
                    mAdapter.notifyDataSetChanged();
                });
    }

    public void deleteItem(ShoppingItem item) {
        //DocumentReference ref = mItems.document(item._getId());
        removeUpdateAlertIcon(item);

        /*
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

         */


        queryData();
        mNotificationHandler.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked!");
                Intent intent = new Intent(this, CartActivity.class);
                //setP
                intent.putExtra("Purchase", mItemsPurchase);
                startActivity(intent);
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item) {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        //System.out.println(item.getName());
        mItemsPurchase.add(item);

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName());
        queryData();
    }

    public void removeUpdateAlertIcon(ShoppingItem item) {
        //System.out.println(item.getName());

        for (ShoppingItem it : mItemsPurchase) {
            //System.out.println(it.getName());
            //System.out.println(item.getName());
            if (it.getName().equals(item.getName())) {
                mItemsPurchase.remove(it);
                cartItems = (cartItems - 1);
            }
            break;
        }


        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);


        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });



        mNotificationHandler.send(item.getName());
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);

    }

    private void setAlarmManager() {
        long repeatInterval = 60000; // AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        mAlarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                repeatInterval,
                pendingIntent);


        mAlarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setJobScheduler() {
        // SeekBar, Switch, RadioButton
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        Boolean isDeviceCharging = true;
        int hardDeadline = 5000; // 5 * 1000 ms = 5 sec.

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceName)
                .setRequiredNetworkType(networkType)
                .setRequiresCharging(isDeviceCharging)
                .setOverrideDeadline(hardDeadline);

        JobInfo jobInfo = builder.build();
        mJobScheduler.schedule(jobInfo);

        // mJobScheduler.cancel(0);
        // mJobScheduler.cancelAll();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] > 40 && isRunning == false) {
            isRunning = true;
            mp = new MediaPlayer();
            try {
                mp.setDataSource("http://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Kangaroo_MusiQue_-_The_Neverwritten_Role_Playing_Game.mp3");
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}