package com.example.nidecsnipeit.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.model.CheckoutItemModel;
import com.example.nidecsnipeit.model.GetLocationParamItemModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.QRScannerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CheckoutActivity extends BaseActivity {
    private CustomItemAdapter adapter;
    private View rootView;
    private NetworkManager apiServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        rootView = findViewById(android.R.id.content);

        setupActionBar("Checkout");
        Common.showProgressDialog(this, "Loading...");

        // Get detail data
        apiServices = NetworkManager.getInstance(this);
        Intent intent = getIntent();
        int assetId = intent.getIntExtra("ASSET_ID", 0);
        String assetName = intent.getStringExtra("ASSET_NAME");
        String locationName = intent.getStringExtra("LOCATION_NAME");
        boolean checkoutAvailable = intent.getBooleanExtra("CHECKOUT_AVAILABLE", false);

        Button checkoutBtn = findViewById(R.id.checkout_btn);

        List<ListItemModel> dataList = new ArrayList<>();
        GetLocationParamItemModel locationItems = new GetLocationParamItemModel();

        // get location list and display fields checkout
        apiServices.getLocationsList(locationItems, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    SpinnerItemModel attentionLocation = new SpinnerItemModel("-99", "-- Select location --");
                    List<SpinnerItemModel> locationList = Common.convertArrayJsonToListIdName(object.getJSONArray("rows"));
                    locationList.add(0, attentionLocation);
                    dataList.add(new ListItemModel("Location", locationName, ListItemModel.Mode.DROPDOWN, locationList, true));
                    dataList.add(new ListItemModel("Asset Name", assetName, ListItemModel.Mode.EDIT_TEXT));

                    RecyclerView recyclerView = findViewById(R.id.recycler_checkout);
                    recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));
                    adapter = new CustomItemAdapter(CheckoutActivity.this, dataList, recyclerView);
                    recyclerView.setAdapter(adapter);
                    Common.hideProgressDialog();
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
                    throw new RuntimeException(e);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
                throw new RuntimeException(error);
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bind data to params
                Map<String, String> valuesMap = adapter.getAllValuesByTitle();
                int locationId = Integer.parseInt(Objects.requireNonNull(valuesMap.get("location")));
                if (locationId == -99) {
                    Common.showCustomAlertDialog(CheckoutActivity.this, null, "Please, select a location first", false, null);
                } else {
                    String asset_name = valuesMap.get("name");
                    CheckoutItemModel checkoutItems = new CheckoutItemModel(locationId, asset_name);
                    Common.showProgressDialog(CheckoutActivity.this, "Checking...");
                    if (checkoutAvailable) {
                        handleCheckOutLocation(assetId, checkoutItems);
                    } else {
                        checkinToCheckout(assetId, checkoutItems);
                    }
                }
            }
        });
    }

    public void checkinToCheckout(int id, CheckoutItemModel params) {
        apiServices.checkinItem(id, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) throws JSONException {
                if (object.has("status") && object.get("status").equals("error")) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, "That asset is not available for checkout!", Common.SnackBarType.ERROR, null);
                } else {
                    handleCheckOutLocation(id, params);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
            }
        });
    }

    public void handleCheckOutLocation (int id, CheckoutItemModel params) {
        apiServices.checkoutItem(id, params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) throws JSONException {
                if (object.has("status") && object.get("status").equals("error")) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                } else {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                        @Override
                        public void onSnackbar() {
                            Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get QR scanned value
        String scannedValue = QRScannerHelper.processScanResult(requestCode, resultCode, data);

        if (scannedValue != null) {
            // Update dropdown selection following position
            adapter.updateDropdownSelection(adapter.getCurrentPosition(), scannedValue);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}