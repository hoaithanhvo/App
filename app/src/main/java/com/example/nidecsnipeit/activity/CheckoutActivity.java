package com.example.nidecsnipeit.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.model.CheckinItemModel;
import com.example.nidecsnipeit.model.CheckoutItemModel;
import com.example.nidecsnipeit.model.GetLocationParamItemModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.model.BasicItemModel;
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
    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;
    public static final int TRANSFER = 3;
    private CustomItemAdapter adapter;
    List<ListItemModel> dataList;
    private View rootView;
    private NetworkManager apiServices;
    private int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        rootView = findViewById(android.R.id.content);

        Common.showProgressDialog(this, "Loading...");

        // Get detail data
        apiServices = NetworkManager.getInstance(this);
        Intent intent = getIntent();
        mode = intent.getIntExtra("CHECKOUT_MODE", CHECK_OUT);
        int assetId = intent.getIntExtra("ASSET_ID", 0);
        String companyId = intent.getStringExtra("COMPANY_ID");
        String assetName = intent.getStringExtra("ASSET_NAME");

        Button checkoutBtn = findViewById(R.id.checkout_btn);
        // setup title for checkout mode
        if (mode == CHECK_OUT) {
            setupActionBar("Checkout");
            checkoutBtn.setText(R.string.check_out);
        } else if (mode == CHECK_IN) {
            setupActionBar("Check-in");
            checkoutBtn.setText(R.string.check_in);
        } else {
            setupActionBar("Transfer location");
            checkoutBtn.setText(R.string.transfer);
        }

        dataList = new ArrayList<>();
        GetLocationParamItemModel locationItems = new GetLocationParamItemModel(companyId);

        RecyclerView recyclerView = findViewById(R.id.recycler_checkout);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckoutActivity.this));
        adapter = new CustomItemAdapter(CheckoutActivity.this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);

        // get location list and display fields checkout
        apiServices.getLocationsList(locationItems, new NetworkResponseListener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                        List<BasicItemModel> locationList = Common.convertArrayJsonToListIdName(object.getJSONArray("payload"));
                        dataList.add(new ListItemModel("Location", "", ListItemModel.Mode.AUTOCOMPLETE_TEXT, locationList, true));
                        if (mode == CHECK_OUT) {
                            dataList.add(new ListItemModel("Asset Name", assetName, ListItemModel.Mode.EDIT_TEXT));
                        } else {
                            List<BasicItemModel> statusItems = new ArrayList<>();
                            statusItems.add(new BasicItemModel("2", "Ready to Deploy"));
                            statusItems.add(new BasicItemModel("4", "In Stock"));
                            dataList.add(new ListItemModel("Status", "", ListItemModel.Mode.DROPDOWN, statusItems));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    Common.hideProgressDialog();
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
            }
        });

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // bind data to params
                Map<String, String> valuesMap = adapter.getAllValuesByTitle();
                int locationId = Integer.parseInt(Objects.requireNonNull(valuesMap.get("location")));
                if (locationId == -1) {
                    Common.showCustomAlertDialog(CheckoutActivity.this, null, "Please, select a location first", false, null);
                } else if (locationId == -99) {
                    Common.showCustomAlertDialog(CheckoutActivity.this, null, "This location is not in the list", false, null);
                } else {
                    String asset_name = valuesMap.get("name");
                    CheckoutItemModel checkoutItems = new CheckoutItemModel(locationId, asset_name);
                    if (mode == CHECK_OUT) {
                        Common.showProgressDialog(CheckoutActivity.this, "Checking...");
                        handleCheckOutLocation(assetId, checkoutItems);
                    } else {
                        // handle logic transfer and checkin
                        int status_id = Integer.parseInt(Objects.requireNonNull(valuesMap.get("status")));
                        CheckinItemModel checkinItems = new CheckinItemModel(status_id, String.valueOf(locationId));
                        Common.showProgressDialog(CheckoutActivity.this, "Checking...");
                        if (mode == CHECK_IN) {
                            handleCheckInLocation(assetId, checkinItems); // Check-in
                        } else {
                            handleTransferLocation(assetId, checkinItems); // Transfer
                        }
                    }
                }
            }
        });
    }

    public void handleCheckOutLocation (int id, CheckoutItemModel params) {
        apiServices.checkoutItem(id, params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                        Common.hideProgressDialog();
                        String messageSuccessful = "Asset checked out successfully.";
                        if (mode == CHECK_IN) {
                            messageSuccessful = "Asset checked in successfully.";
                        }
                        Common.showCustomSnackBar(rootView, messageSuccessful, Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                            @Override
                            public void onSnackbar() {
                                Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
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

    public void handleCheckInLocation (int id, CheckinItemModel params) {
        apiServices.checkinItem(id, params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                        Common.hideProgressDialog();
                        String messageSuccessful = "Asset checked in successfully.";
                        Common.showCustomSnackBar(rootView, messageSuccessful, Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                            @Override
                            public void onSnackbar() {
                                Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
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

    public void handleTransferLocation (int id, CheckinItemModel params) {
        apiServices.transferItem(id, params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                        Common.hideProgressDialog();
                        String messageSuccessful = "Asset transfer location successfully.";
                        Common.showCustomSnackBar(rootView, messageSuccessful, Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                            @Override
                            public void onSnackbar() {
                                Intent intent = new Intent(CheckoutActivity.this, MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
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

    @Override
    public void onScanDataReceived(String qrContent) {
        if (qrContent != null) {
            dataList.get(0).setValue(qrContent);
            adapter.notifyItemChanged(0);
        }
    }
}