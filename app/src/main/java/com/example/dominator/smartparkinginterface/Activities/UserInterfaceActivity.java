package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.dominator.smartparkinginterface.Entities.Booking;
import com.example.dominator.smartparkinginterface.Entities.CarSlotCounter;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.Owner;
import com.example.dominator.smartparkinginterface.Entities.ParkingLot;
import com.example.dominator.smartparkinginterface.Entities.ParkingSlot;
import com.example.dominator.smartparkinginterface.Entities.PasswordChanger;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.R;
import com.example.dominator.smartparkinginterface.Retrofit.APIClient;
import com.example.dominator.smartparkinginterface.Retrofit.APIInterface;
import com.example.dominator.smartparkinginterface.utils.DirectionsJSONParser;
import com.example.dominator.smartparkinginterface.utils.HttpUtils;
import com.example.dominator.smartparkinginterface.utils.NumberUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserInterfaceActivity
        extends
        AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    //View
    private View hView;
    private ViewFlipper vf;
    private FloatingActionButton btnShowDirection;
    private FloatingActionButton btnRefreshMap;
    private TextView header;
    private TextView ownerText;
    private TextView carparkText;
    private TextView addressText;
    private TextView telText;
    private Button slotButton;
    private TextView timeText;
    private EditText phoneNumberText;
    private EditText plateNumberText;
    private TextView emailText;
    private EditText firstNameText;
    private EditText lastNameText;
    private Button changeButton;
    private TextView reminder;
    private TextView changeReminder;
    private EditText oldPass;
    private EditText newPass;
    private EditText confirmNewPass;
    private ImageView blackScreen;
    private ImageView passBlackScreen;
    private ImageView currentAvatar;
    private ImageView sidebarAvatar;
    private TextView sidebarName;
    private TextView sidebarEmail;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView carparkImage;
    private TextView priceText;
    private TextView totalSlot;
    private ImageView parkBlackScreen;

    //API
    private APIClient apiClient;
    private APIInterface apiInterface;
    private InformationAccount account;

    //Data
    private List<ParkingLot> parkingLots;
    private List<ParkingSlot> parkingSlots;

    //Google Map
    //private GPSTracker gpstracker;
    private Polyline directionPolyline;
    private GoogleMap map;
    PlaceAutocompleteFragment placeAutoComplete;

    //Flags
    private Marker selectedMarker = null;
    private ParkingLot selectedLot = null;
    private boolean isLotsReady = false;
    private boolean isMapReady = false;
    private boolean existCheck = false;
    private boolean withSlotRender;

    //Slot scroll view
    ScrollView slotScrollView;

    //Extra values
    int freeSlots;
    ParkingSlot currentSlot;

    //Location
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location currentLocation;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long SMALLEST_DISPLACEMENT = 10;

    //QR code
    MultiFormatWriter multiFormatWriter;
    BitMatrix bitMatrix;
    BarcodeEncoder barcodeEncoder;

    //avatar
    private Bitmap avatar;
    private Dialog fbDialogue;

    //user payment
    private TextView cashText;
    private CheckBox reviewBox;
    private Button acceptButton;
    private TextView paymentReminder;
    private Spinner amountSpinner;
    private int topupAmount;
    private ImageView momoLoading;
    private TextView momoPassword;

    // Booking
    private Booking booking;
    private List<Booking> bookList;
    private List<Booking> useList;
    private List<Booking> finishList;
    String counter = "";
    Menu bookingMenu;
    Menu usingMenu;
    long remainTime;
    int positionInABookList;
    int sizeOfABookList;
    int positionInAParkList;
    int sizeOfAParkList;
    ImageView showImage;
    List<CountDownTimer> existTimer;
    ScrollView historyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_user_interface);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        bindView();
        account = (InformationAccount) getIntent().getSerializableExtra("ACCOUNT_INFO");
        if(account.getAvatar() == null && avatar == null){
            avatar = BitmapFactory.decodeResource(getResources(),R.drawable.default_avatar);
            account.setAvatar(apiClient.bitmapToByte(avatar));
        }
        else{
            avatar = apiClient.byteToBitmap(account.getAvatar());
        }
        currentAvatar.setImageBitmap(avatar);
        sidebarAvatar.setImageBitmap(avatar);
        sidebarEmail.setText(account.getEmail());
        sidebarName.setText(account.getFullName());
        init();
        startLocationUpdates();
        getAllParkingLots();
        getBooking();
        getParking();
        new clearPort(this).execute();

    }

    public void getAllParkingLots(){
        apiInterface.doGetAllParkingLot().enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG", response.code() + "");
                Log.d("TAG", response.raw() + "");
                Log.d("TAG", response.body() + "");
                Log.d("TAG", getResources().getString(R.string.success_message));
                parkingLots = new ArrayList<>();
                try {
                    for (int i = 0; i < ((List) response.body().getObjectResponse()).size(); i++) {
                        parkingLots.add((ParkingLot) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new ParkingLot()));
                    }
                    isLotsReady = true;
                    addMarkers();
                } catch (Exception e) {
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    Log.d("TAG", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
                Log.d("TAG", getResources().getString(R.string.fail_message));
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerPosition = marker.getPosition();
        for (ParkingLot lot : parkingLots) {
            if (lot.getLatitude() == markerPosition.latitude && lot.getLongitude() == markerPosition.longitude) {
                selectedLot = lot;
                break;
            }
        }
        selectedMarker = marker;
        marker.showInfoWindow();
        btnShowDirection.show();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map != null) {
            map.setContentDescription(getString(R.string.map_description));

            isMapReady = true;
            addMarkers();

            map.setOnMarkerClickListener(this);

            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (selectedLot != null) {
                        viewParkingLot(selectedLot);
                    }
                }
            });

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //DO NOTHING
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_profile) {
            viewInfo(hView);
        }
        else if (id == R.id.nav_history) {
            makeHistory(hView);
        } /*else if (id == R.id.nav_news) {

        } else if (id == R.id.nav_rate_us) {

        }
        */
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backButton(View view) {
        currentAvatar.setImageBitmap(avatar);
        vf.setDisplayedChild(getResources().getInteger(R.integer.MAP_SCREEN));
        header.setText(getResources().getString(R.string.home));
    }

    public void changePassword(View view) {
        header.setText(getResources().getString(R.string.change_password));
        vf.setDisplayedChild(getResources().getInteger(R.integer.CHANGE_PASS_SCREEN));
        changeReminder.setText("");
    }

    @SuppressLint("DefaultLocale")
    public void viewParkingLot(final ParkingLot parkingLot) {
        slotButton.setClickable(false);
        freeSlots = 0;
        getSlotFromALot(parkingLot.getParkingLotId(), parkingLot.getBookingSlot());
        header.setText(parkingLot.getDisplayName());
        vf.setDisplayedChild(getResources().getInteger(R.integer.CARPARK_SCREEN));
        Owner owner = parkingLot.getOwner();
        ownerText.setText(owner.getFullName());
        carparkText.setText(parkingLot.getDisplayName());
        addressText.setText(parkingLot.getAddress());
        telText.setText(parkingLot.getPhoneNumber());
        slotButton.setText("Calculating...");
        timeText.setText(parkingLot.getTimeOfOperation());
        priceText.setText(parkingLot.getPrice() + " VND");
        if (parkingLot.getParklotImage() != null) {
            carparkImage.setImageBitmap(apiClient.byteToBitmap(parkingLot.getParklotImage()));
        } else {
            carparkImage.setImageResource(R.drawable.persuo_carpark);
        }

    }

    public void getSlotFromALot(int id, final int bookingSlot){
        apiInterface.doGetCarparkSlots(id).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG", response.code() + "");
                Log.d("TAG", response.raw() + "");
                Log.d("TAG", response.body() + "");
                Log.d("TAG", getResources().getString(R.string.success_message));
                try {
                    parkingSlots = new ArrayList<ParkingSlot>();
                    if (response.body().getObjectResponse() != null){
                        for (int i = 0; i < ((List) response.body().getObjectResponse()).size(); i++) {
                            currentSlot = (ParkingSlot) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new ParkingSlot());
                            if (currentSlot.getStatus().equals(getResources().getString(R.string.available))) {
                                freeSlots = freeSlots + 1;
                            }
                            parkingSlots.add(currentSlot);
                        }
                    }
                    slotButton.setText(" "+ (freeSlots - bookingSlot) + " bookable / " + freeSlots + " Available");
                    if(freeSlots == 0 || freeSlots - bookingSlot <=0){
                        slotButton.setEnabled(false);
                        slotButton.setClickable(false);
                    }
                    else {
                        slotButton.setEnabled(true);
                        slotButton.setClickable(true);
                    }
                    if(withSlotRender == true){
                        totalSlot.setText("Total slot: " + parkingSlots.size());
                        carSlotDetail();
                        withSlotRender = false;
                    }
                } catch (Exception e) {
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    Log.d("TAG", e.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
                Log.d("TAG", getResources().getString(R.string.fail_message));
            }
        });
    }

    public void viewInfo(View view) {
        apiInterface.doGetDriver(account.getAccountId()).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                if (response.isSuccessful() == true) {
                    account = (InformationAccount) apiClient.ObjectConverter((response.body().getObjectResponse()), new InformationAccount());
                    avatar = apiClient.byteToBitmap(account.getAvatar());
                    currentAvatar.setImageBitmap(avatar);
                    sidebarAvatar.setImageBitmap(avatar);
                    drawer.closeDrawer(GravityCompat.START);
                    header.setText(getResources().getString(R.string.user_info));
                    vf.setDisplayedChild(getResources().getInteger(R.integer.USER_INFO_SCREEN));
                    //get info from outer resource
                    plateNumberText.setText(account.getPlateNumber());
                    phoneNumberText.setText(account.getPhoneNumber());
                    emailText.setText(account.getEmail());
                    firstNameText.setText(account.getFirstName());
                    lastNameText.setText(account.getLastName());

                    cashText.setText("Wallet: " + account.getCash() + " VND");

                    changeButton.setEnabled(false);
                    changeButton.setTextColor(Color.parseColor("#999999"));
                    reminder.setText("");

                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            changeButton.setEnabled(true);
                            changeButton.setTextColor(Color.parseColor("#ffffff"));
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    };
                    phoneNumberText.addTextChangedListener(textWatcher);
                    firstNameText.addTextChangedListener(textWatcher);
                    lastNameText.addTextChangedListener(textWatcher);
                    plateNumberText.addTextChangedListener(textWatcher);
                } else {
                    Toast.makeText(UserInterfaceActivity.this, "User session doesn't exist", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserInterfaceActivity.this, LoginActivity.class);
                    UserInterfaceActivity.this.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
                Toast.makeText(UserInterfaceActivity.this, "Connection's failed! Please check your connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveInfo(View view) {
        blackScreen.setVisibility(View.VISIBLE);
        reminder.setTextColor(Color.parseColor("#D81B60"));
        reminder.setText("");
        preventClick();
        if (firstNameText.getText().toString().isEmpty()
                || lastNameText.getText().toString().isEmpty()
                || plateNumberText.getText().toString().isEmpty()
                || phoneNumberText.getText().toString().isEmpty()) {
            reminder.setText(getResources().getString(R.string.empty_field));
            blackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        } else if (!phoneNumberText.getText().toString().matches("^[+]?[0-9]{10,13}$") || !Patterns.PHONE.matcher(phoneNumberText.getText().toString()).matches() || !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumberText.getText().toString())) {
            reminder.setText(getResources().getString(R.string.invalid_phone));
            blackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        } else if (changeButton.isEnabled()) {
            final InformationAccount user = new InformationAccount();
            user.setPlateNumber(plateNumberText.getText().toString());
            user.setLastName(lastNameText.getText().toString());
            user.setFirstName(firstNameText.getText().toString());
            user.setPhoneNumber(phoneNumberText.getText().toString());
            user.setAvatar(apiClient.bitmapToByte(((BitmapDrawable) (currentAvatar.getDrawable())).getBitmap()));
            apiInterface.doUpdateUser(account.getAccountId(), user).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                    Log.d("TAG1", response.code() + "");
                    Log.d("TAG2", response.raw() + "");
                    Log.d("TAG3", response.body() + "");
                    Log.d("TAG4", response.message() + "");
                    Log.d("TAG5", response.headers() + "");
                    Log.d("TAG6", getResources().getString(R.string.success_message));
                    if (response.isSuccessful()) {
                        account.setFirstName(user.getFirstName());
                        account.setLastName(user.getLastName());
                        account.setPhoneNumber(user.getPhoneNumber());
                        account.setAvatar(user.getAvatar());
                        reminder.setTextColor(Color.parseColor("#4ddbff"));
                        reminder.setText(getResources().getString(R.string.update_success));
                        changeButton.setEnabled(false);
                        changeButton.setTextColor(Color.parseColor("#999999"));
                        blackScreen.setVisibility(View.INVISIBLE);
                        resumeClick();
                        sidebarAvatar.setImageBitmap(apiClient.byteToBitmap(user.getAvatar()));
                        sidebarName.setText(account.getFullName());
                    } else {
                        reminder.setText(getResources().getString(R.string.update_fail));
                        blackScreen.setVisibility(View.INVISIBLE);
                        resumeClick();
                    }
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    reminder.setText(getResources().getString(R.string.connection_failed));
                    blackScreen.setVisibility(View.INVISIBLE);
                    resumeClick();
                }
            });
        }
    }

    public void confirmChangePassword(View view) {
        passBlackScreen.setVisibility(View.VISIBLE);
        changeReminder.setTextColor(Color.parseColor("#D81B60"));
        changeReminder.setText("");
        preventClick();
        if (oldPass.getText().toString().equals(account.getPassword()) && !oldPass.getText().toString().isEmpty()) {
            if (newPass.getText().toString().equals(confirmNewPass.getText().toString()) && !newPass.getText().toString().isEmpty()) {
                PasswordChanger passwordChanger = new PasswordChanger(
                        account.getAccountId(),
                        oldPass.getText().toString(),
                        newPass.getText().toString(),
                        confirmNewPass.getText().toString()
                );
                apiInterface.doChangePassword(passwordChanger).enqueue(new Callback<ResponseTemplate>() {
                    @Override
                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                        Log.d("TAG", response.code() + "");
                        Log.d("TAG2", response.raw() + "");
                        Log.d("TAG3", response.body() + "");
                        Log.d("TAG4", response.message() + "");
                        Log.d("TAG5", response.headers() + "");
                        Log.d("TAG6", getResources().getString(R.string.success_message));
                        if (response.isSuccessful()) {
                            account.setPassword(newPass.getText().toString());
                            passBlackScreen.setVisibility(View.INVISIBLE);
                            resumeClick();
                            changeReminder.setTextColor(Color.parseColor("#4ddbff"));
                            changeReminder.setText(getResources().getString(R.string.password_change_success));
                        } else {
                            passBlackScreen.setVisibility(View.INVISIBLE);
                            resumeClick();
                            changeReminder.setText(getResources().getString(R.string.password_change_fail));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("TAG", displayResponse);
                        Log.d("TAG", getResources().getString(R.string.fail_message));
                        passBlackScreen.setVisibility(View.INVISIBLE);
                        resumeClick();
                        changeReminder.setText(getResources().getString(R.string.connection_failed));
                    }
                });
            } else {
                passBlackScreen.setVisibility(View.INVISIBLE);
                resumeClick();
                if (newPass.getText().toString().isEmpty()) {
                    changeReminder.setText(getResources().getString(R.string.empty_field));
                } else {
                    changeReminder.setText(getResources().getString(R.string.confirm_password_mismatch));
                }
            }
        } else {
            passBlackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
            if (oldPass.getText().toString().isEmpty()) {
                changeReminder.setText(getResources().getString(R.string.empty_old_password));
            } else {
                changeReminder.setText(getResources().getString(R.string.wrong_old_password));
            }
        }
    }

    public void showDirection(View view) {
        if (currentLocation != null) {
            getDirection(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(selectedLot.getLatitude(), selectedLot.getLongitude()));
        }
        else{
            callForLocation();
        }
    }


    public void getDirection(LatLng from, LatLng to) {
        new clearPort(this).execute();
        RequestParams params = getParams(from, to);
        HttpUtils.getByUrl(getResources().getString(R.string.google_api_link), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<List<HashMap<String, String>>> routes = null;
                try {
                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    routes = parser.parse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("NETWORK: ", e.toString());
                }
                if (routes != null) {
                    ArrayList<LatLng> points;
                    PolylineOptions lineOptions = null;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    // Traversing through all the routes
                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);

                        // Fetching all the points in i-th route
                        for (HashMap<String, String> point : path) {
                            double lat = NumberUtil.tryParseDouble(point.get("lat"), 0);
                            double lng = NumberUtil.tryParseDouble(point.get("lng"), 0);
                            LatLng position = new LatLng(lat, lng);
                            builder.include(position);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(15);
                        lineOptions.color(Color.BLUE);
                    }

                    if (directionPolyline != null) {
                        directionPolyline.remove();
                    }

                    if (lineOptions == null) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.carpark_out_of_range), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    directionPolyline = map.addPolyline(lineOptions);

                    directionPolyline.setStartCap(new RoundCap());
                    directionPolyline.setEndCap(new RoundCap());
                    directionPolyline.setJointType(JointType.ROUND);
                    directionPolyline.setPattern(null);

                    LatLngBounds bounds = builder.build();
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                    if (selectedMarker != null) {
                        selectedMarker.hideInfoWindow();
                        selectedMarker = null;
                    }
                    btnShowDirection.hide();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(UserInterfaceActivity.this, "Connection's failed! Please check your connection!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addMarkers() {
        map.clear();
        LatLng marker;
        if (isLotsReady && isMapReady) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ParkingLot lot : parkingLots) {
                if(lot.isActive()) {
                    marker = new LatLng(lot.getLatitude(), lot.getLongitude());
                    MarkerOptions option = new MarkerOptions();
                    option.position(marker);

                    float color = BitmapDescriptorFactory.HUE_GREEN;
                    option.icon(BitmapDescriptorFactory.defaultMarker(color));
                    option.title(lot.getDisplayName());
                    option.snippet("Price: " + lot.getPrice() + "\n" + lot.getEmptySlot() + " Empty / " + lot.getTotalSlot() + " Slots");

                    map.addMarker(option);
                    builder.include(marker);
                }
            }
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }
//        lotsList.add(new LatLng(10.8671443, 106.6250343));
//        lotsList.add(new LatLng(10.8542203, 106.6119883));
//        lotsList.add(new LatLng(10.8473773, 106.6434973));
    }

    private RequestParams getParams(LatLng origin, LatLng dest) {
        RequestParams params = new RequestParams();
        params.add("origin", origin.latitude + "," + origin.longitude);
        params.add("destination", dest.latitude + "," + dest.longitude);
        params.add("lang", "en");
        params.add("key", getResources().getString(R.string.google_maps_key));
        params.add("sensor", "false");
        return params;
    }

    private void bindView() {
        btnShowDirection = findViewById(R.id.btnShowDirection);
        btnShowDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirection(v);
            }
        });

        btnRefreshMap = findViewById(R.id.btnRefreshMap);
        btnRefreshMap.setBackgroundColor(Color.WHITE);
        btnRefreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllParkingLots();
            }
        });

        vf = findViewById(R.id.vfu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4F515C"));
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);

        header = findViewById(R.id.toolbar_title);
        ownerText = findViewById(R.id.owner_text);
        carparkText = findViewById(R.id.carpark_text);
        addressText = findViewById(R.id.address_text);
        telText = findViewById(R.id.tel_text);
        slotButton = findViewById(R.id.slotButton);
        timeText = findViewById(R.id.time_text);
        phoneNumberText = findViewById(R.id.phone_number);
        plateNumberText = findViewById(R.id.plate_number);
        emailText = findViewById(R.id.email);
        firstNameText = findViewById(R.id.first_name);
        lastNameText = findViewById(R.id.last_name);
        changeButton = findViewById(R.id.save_info);
        reminder = findViewById(R.id.reminder);
        oldPass = findViewById(R.id.old_password);
        newPass = findViewById(R.id.new_password);
        confirmNewPass = findViewById(R.id.confirm_password);
        blackScreen = findViewById(R.id.loading_image);
        passBlackScreen = findViewById(R.id.change_pass_loading_image);
        currentAvatar = findViewById(R.id.current_user_avatar);
        changeReminder = findViewById(R.id.change_reminder);
        carparkImage = findViewById(R.id.carparkImage);
        priceText = findViewById(R.id.price_text);
        parkBlackScreen = findViewById(R.id.park_detail_loading_image);
        totalSlot = findViewById(R.id.total_slot);
        sidebarAvatar = navigationView.getHeaderView(0).findViewById(R.id.user_sidebar_avatar);
        sidebarEmail = navigationView.getHeaderView(0).findViewById(R.id.user_sidebar_email);
        sidebarName = navigationView.getHeaderView(0).findViewById(R.id.user_sidebar_name);

        //slot scroll view
        slotScrollView = findViewById(R.id.slotScrollView);

        //Entities
        account = new InformationAccount();

        //API
        apiClient = new APIClient();
        apiInterface = APIClient.getClient(getResources().getString(R.string.main_link)).create(APIInterface.class);
        parkingLots = new ArrayList<>();

        //Momo bound
        cashText = findViewById(R.id.account_money);

        fbDialogue = new Dialog(UserInterfaceActivity.this, android.R.style.Theme_Black_NoTitleBar);
        fbDialogue.setContentView(R.layout.momo_popup);
        fbDialogue.setCancelable(true);
        reviewBox = fbDialogue.findViewById(R.id.accept_checkbox);
        acceptButton = fbDialogue.findViewById(R.id.accept_btn);
        paymentReminder = fbDialogue.findViewById(R.id.payment_reminder);
        momoLoading = fbDialogue.findViewById(R.id.momo_loading);

        amountSpinner = fbDialogue.findViewById(R.id.amount_input);
        momoPassword = fbDialogue.findViewById(R.id.momo_password);


        //location manager
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        //Book info
        bookingMenu = navigationView.getMenu().findItem(R.id.booking_timer).getSubMenu();
        usingMenu = navigationView.getMenu().findItem(R.id.parking_timer).getSubMenu();
        historyScrollView = findViewById(R.id.historyScrollView);
    }

    public void infoNavigate(View view) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        header.setText(getResources().getString(R.string.home));
        vf.setDisplayedChild(getResources().getInteger(R.integer.MAP_SCREEN));
        showDirection(hView);
    }

    public void changeAvatar(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        changeButton.setEnabled(true);
        changeButton.setTextColor(Color.parseColor("#ffffff"));
        header.setText(getResources().getString(R.string.change_avatar_header_text));
        vf.setDisplayedChild(getResources().getInteger(R.integer.AVATAR_SCREEN));
    }

    public void preventClick() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void resumeClick() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void buildCamera(LatLng latLng, int zoomValue, int tiltValue){
        CameraPosition camPos = new CameraPosition.Builder()
                .target(latLng)
                .zoom(zoomValue)
                .tilt(tiltValue)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        map.animateCamera(camUpd3);
    }

    private static final int GALLERY_REQUEST_CODE = 2;
    Intent CropIntent;
    Uri uri;

    public void selectImageAction(View view) {
        Intent GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image from Gallery"), GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK)
            CropImage();
        else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data != null) {
                uri = data.getData();
                CropImage();
            }
        } else if (requestCode == 1) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = null;
                if (bundle != null) {
                    bitmap = bundle.getParcelable("data");
                }
                currentAvatar.setImageBitmap(apiClient.getRoundedShape(bitmap));
                changeButton.setEnabled(true);
                changeButton.setTextColor(Color.parseColor("#ffffff"));
            }
        }
    }

    private void CropImage() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 3);
            CropIntent.putExtra("aspectY", 3);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 1);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }

    }


    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        hView = navigationView.getHeaderView(0);

        //gpstracker = new GPSTracker(UserInterfaceActivity.this);
        //currentLocation = new Location(gpstracker.getLocation());

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                buildCamera(place.getLatLng(), 15, 0);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                buildCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15, 0);
                focusNearestCarPark();
                if (directionPolyline != null && currentLocation != null) {
                        List<LatLng> points = directionPolyline.getPoints();
                        LatLng to = points.get(points.size() - 1);
                        getDirection(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), to);
                    }
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        //QR action
        multiFormatWriter = new MultiFormatWriter();
        barcodeEncoder = new BarcodeEncoder();

        //Timer
        existTimer = new ArrayList<CountDownTimer>();

        //book
        bookList = new ArrayList<>();
        useList = new ArrayList<>();

    }

    private static class clearPort extends AsyncTask<Void, Void, String> {

        private WeakReference<UserInterfaceActivity> activityReference;

        // only retain a weak reference to the activity
        clearPort(UserInterfaceActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                Socket s = new Socket(InetAddress.getLocalHost().getHostAddress(), 443);
                s.setReuseAddress(true);
                AsyncHttpClient client = HttpUtils.getClient();
                client.setMaxConnections(100);
                HttpUtils.setClient(client);
                return "task finished";
            }
            catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            // get a reference to the activity if it is still there
            UserInterfaceActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
        }
    }

    private void startLocationUpdates() {
        permissionCheck();
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                });
    }

    private boolean permissionCheck(){
        boolean check = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ||  !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            callForLocation();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else{
            check = true;
        }
        return check;
    }

    private void callForLocation(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Carpark finding require GPS with high accuracy");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                UserInterfaceActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void requestSlotDetail(View view) {
        if(selectedLot != null) {
            withSlotRender = true;
            getSlotFromALot(selectedLot.getParkingLotId(), selectedLot.getBookingSlot());
            vf.setDisplayedChild(getResources().getInteger(R.integer.SLOT_DETAIL_SCREEN));
            header.setText("Slot detail");
        }
    }

    public void refreshCarSlot(View view){
        if(selectedLot != null) {
            withSlotRender = true;
            getSlotFromALot(selectedLot.getParkingLotId(), selectedLot.getBookingSlot());
        }
    }

    public void focusNearestCarPark(){
        if(currentLocation != null && parkingSlots != null) {
            if(!parkingSlots.isEmpty()) {
                Collections.sort(parkingLots, new Comparator<ParkingLot>() {

                    @Override
                    public int compare(ParkingLot o1, ParkingLot o2) {
                        Location locationA = new Location("point A");
                        locationA.setLatitude(o1.getLatitude());
                        locationA.setLongitude(o1.getLatitude());
                        Location locationB = new Location("point B");
                        locationB.setLatitude(o2.getLatitude());
                        locationB.setLongitude(o2.getLatitude());
                        float distanceOne = currentLocation.distanceTo(locationA);
                        float distanceTwo = currentLocation.distanceTo(locationB);
                        return Float.compare(distanceOne, distanceTwo);
                    }
                });
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                builder.include(new LatLng(parkingLots.get(parkingLots.size() - 1).getLatitude(), parkingLots.get(parkingLots.size() - 1).getLongitude()));
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                map.animateCamera(cu);
            }
        }
    }

    private void carSlotDetail(){
            List<CarSlotCounter> organizeSlots = new ArrayList<>();
            int callingLane = -1;
            for(int i = 0; i < parkingSlots.size(); i++){
                existCheck = false;
                for(int j = 0; j < organizeSlots.size(); j++){
                    if(organizeSlots.get(j)!= null) {
                        if (parkingSlots.get(i).getLane().equals(organizeSlots.get(j).getLaneCharacter())) {
                            existCheck = true;
                            callingLane = j;
                        }
                    }
                }
                if(existCheck == false){
                    organizeSlots.add(new CarSlotCounter(parkingSlots.get(i).getLane(), new ArrayList<ParkingSlot>()));
                    callingLane = organizeSlots.size() - 1;
                }
                        organizeSlots.get(callingLane).getSlotInLane().add(parkingSlots.get(i));
                }

            Collections.sort(organizeSlots, new CarSlotCounter());
            slotScrollView.removeAllViews();
            TableLayout slotTable = new TableLayout(this);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            slotTable.setLayoutParams(new TableLayout.LayoutParams(params));
            LinearLayout outerLinear = new LinearLayout(this);
            outerLinear.setLayoutParams(params);
            for(int k = 0; k < organizeSlots.size() ; k++){
                HorizontalScrollView currentScrollView = new HorizontalScrollView(this);
                LinearLayout currentLinear = new LinearLayout(this);
                for(int l = 0; l < organizeSlots.get(k).getSlotInLane().size(); l++){
                    CardView currentCard = new CardView(this,null, R.drawable.button_color);
                    TextView currentText = new TextView(this);
                    currentText.setLayoutParams(params);
                    currentText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    currentText.setText(organizeSlots.get(k).getLaneCharacter() + " - " + organizeSlots.get(k).getSlotInLane().get(l).getRow());
                    currentCard.addView(currentText);
                    currentCard.setLayoutParams(new LinearLayout.LayoutParams(130, 150));
                    currentCard.setRadius(5);
                    currentCard.setElevation(10);
                    ViewGroup.MarginLayoutParams layoutParams =
                            (ViewGroup.MarginLayoutParams) currentCard.getLayoutParams();
                    layoutParams.setMargins(5, 5, 5, 5);
                    currentCard.requestLayout();
                    if(organizeSlots.get(k).getSlotInLane().get(l).getStatus().equals("empty")){
                        currentCard.setCardBackgroundColor(0xcfa3ff75);
                    }
                    else if (organizeSlots.get(k).getSlotInLane().get(l).getStatus().equals("occupied")){
                        currentCard.setCardBackgroundColor(0xcfff8c9b);
                    }
                    else{
                        currentCard.setCardBackgroundColor(0xcffcff82);
                    }
                    currentText.setText(currentText.getText() + "\n" + organizeSlots.get(k).getSlotInLane().get(l).getStatus());
                    currentText.setTypeface(null, Typeface.BOLD);
                    currentLinear.addView(currentCard);
                }
                currentLinear.setLayoutParams(params);
                currentScrollView.addView(currentLinear);
                slotTable.addView(currentScrollView);
            }

            slotScrollView.addView(slotTable);
        }

    private Bitmap stringToQRImage(String code, int width, int height){
        Bitmap bitmap = null;
        try {
            bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE,width,height);
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            Log.d("QR", "Invalid QR code: " + e.toString());
        }
        return bitmap;
    }

    public void topUpAction(View view) {
        if(account != null) {
            withSlotRender = true;
            vf.setDisplayedChild(getResources().getInteger(R.integer.PAYMENT_SCREEN));
            header.setText("Top up");
        }
    }

    public void momoAction(View view) {
        reviewBox.setChecked(false);
        acceptButton.setEnabled(false);
        paymentReminder.setText("");
        reviewBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviewBox.isChecked() == false) {
                    acceptButton.setEnabled(false);
                }
                else{
                    acceptButton.setEnabled(true);
                }
            }
        });
        fbDialogue.show();
    }

    public void closePopup(View view) {
        fbDialogue.cancel();
    }

    public void paymentAction(final View view) {
        if(!momoPassword.getText().toString().isEmpty()) {
            momoLoading.setVisibility(View.VISIBLE);
            preventClick();
            try {
                topupAmount = Integer.valueOf(amountSpinner.getSelectedItem().toString().trim().replaceAll("\\s+", ""));
                Log.d("MONEY", "Test money: " + topupAmount);
                apiInterface.doAddMoreMoney(account.getAccountId(), topupAmount).enqueue(new Callback<ResponseTemplate>() {
                    @Override
                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                        Log.d("MOMO", response.code() + "");
                        Log.d("MOMO", response.raw() + "");
                        Log.d("MOMO", response.body() + "");
                        Log.d("MOMO", getResources().getString(R.string.success_message));
                        if (response.body().getObjectResponse() == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserInterfaceActivity.this);
                            builder.setMessage("Look at this dialog!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            paymentReminder.setText("GOOD");
                            account = (InformationAccount) apiClient.ObjectConverter(response.body().getObjectResponse(), new InformationAccount());
                        }
                        momoLoading.setVisibility(View.INVISIBLE);
                        resumeClick();
                        viewInfo(view);
                        fbDialogue.cancel();
                        Toast.makeText(UserInterfaceActivity.this, "Money top up successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("MOMO", displayResponse);
                        paymentReminder.setText(getResources().getString(R.string.connection_failed));
                        momoLoading.setVisibility(View.INVISIBLE);
                        resumeClick();
                    }
                });
            } catch (Exception e) {
                paymentReminder.setText(getResources().getString(R.string.update_fail));
                Log.d("MOMO", e.toString());
                momoLoading.setVisibility(View.INVISIBLE);
                resumeClick();
            }
        }
        else{
            paymentReminder.setText("Invalid password");
        }
    }

    public void bookASlot(final View view) {
        if (permissionCheck() == true) {
            if (selectedLot != null) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle("Car park booking");

                // Setting Dialog Message
                alertDialog.setMessage("Price: " + String.format("%.0f", selectedLot.getPrice()) + "\n" + "Are you sure you want to book this car park");

                // On pressing Settings button
                alertDialog.setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        if(bookList.isEmpty() && useList.isEmpty()) {
                            parkBlackScreen.setVisibility(View.VISIBLE);
                            preventClick();
                            Date date = new Date();
                            apiInterface.doBookSlot(account.getAccountId(), selectedLot.getParkingLotId(), new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss").format(new Date())).enqueue(new Callback<ResponseTemplate>() {
                                @Override
                                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                    if (response.body().getObjectResponse() == null) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(UserInterfaceActivity.this);
                                        builder.setMessage("The car park is full or out of order")
                                                .setTitle("Unable to book this car park")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        getBooking();
                                                        getParking();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                        parkBlackScreen.setVisibility(View.INVISIBLE);
                                        resumeClick();
                                    } else {
                                        booking = (Booking) apiClient.ObjectConverter(response.body().getObjectResponse(), new Booking());
                                        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(UserInterfaceActivity.this);
                                        showImage = new ImageView(UserInterfaceActivity.this);
                                        showImage.setImageBitmap(stringToQRImage(booking.getUrlApiCheckIn(), 500, 500));
                                        ImageDialog.setView(showImage);
                                        ImageDialog.setTitle("Booking successful");
                                        ImageDialog.setMessage("You have chosen to book: " + selectedLot.getDisplayName() + "\n" +
                                                "The price is: " + String.format("%.0f", selectedLot.getPrice()) + " VND/h" + "\n" +
                                                "Use this QR code as a check for the car park" + "\n" +
                                                "This QR code will expire in 30 minutes");
                                        ImageDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                            }
                                        });
                                        ImageDialog.show();
                                        backButton(view);
                                        showDirection(view);
                                        getBooking();
                                        getParking();
                                        parkBlackScreen.setVisibility(View.INVISIBLE);
                                        resumeClick();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                    String displayResponse = t.toString();
                                    Log.d("MOMO", displayResponse);
                                    paymentReminder.setText(getResources().getString(R.string.connection_failed));
                                    parkBlackScreen.setVisibility(View.INVISIBLE);
                                    resumeClick();
                                }
                            });
                        }
                        else{
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserInterfaceActivity.this);
                            alertDialog.setTitle("Cannot book when a previous booking is still exist");
                            alertDialog.setMessage("Cancel your previous order if you have one \n" +
                                    "You cannot book a car park if your car is already parked");
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                            backButton(view);
                            getBooking();
                            getParking();
                            parkBlackScreen.setVisibility(View.INVISIBLE);
                            resumeClick();
                        }

                    }
                });

                // on pressing cancel button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        }
        else{
            Toast.makeText(UserInterfaceActivity.this, "Cannot book without GPS connection", Toast.LENGTH_LONG).show();
        }
    }

    public int JsonToSeconds(String JSON){
        int seconds = 0;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map = mapper.readValue(JSON, new TypeReference(){});
            System.out.println(map.get("duration_in_traffic").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }

    public Long getDurationForRoute(String origin, String destination) throws InterruptedException, ApiException, IOException {
    // - We need a context to access the API
    GeoApiContext geoApiContext = new GeoApiContext.Builder()
            .apiKey(getResources().getString(R.string.google_maps_key))
            .build();

    // - Perform the actual request
    DirectionsResult directionsResult = DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(origin)
            .destination(destination)
            .await();

    // - Parse the result
    DirectionsRoute route = directionsResult.routes[0];
    DirectionsLeg leg = route.legs[0];
    Duration duration = leg.duration;
    return duration.inSeconds;
}

    public long getRemainTimeInMilliSeconds(Date startDate){
        long result = (startDate.getTime() - new Date().getTime()) + 1800000;
        if(result < 0){
            result = 0;
        }
        return result;
    }

    private void getBooking() {
        bookingMenu.clear();
        bookList.clear();
        apiInterface.doGetBookByStatus(account.getAccountId(), getResources().getString(R.string.status_booking), getResources().getInteger(R.integer.book_limit)).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                bookingMenu.clear();
                try {
                    sizeOfABookList = ((List) response.body().getObjectResponse()).size();
                }
                catch(Exception e){
                    sizeOfABookList = 0;
                }
                clearExistTimer();
                if (sizeOfABookList == 0) {
                    bookingMenu.add("---Empty---");
                } else {
                    try {
                        for (int i = 0; i < sizeOfABookList; i++) {
                            positionInABookList = i;
                            booking = (Booking) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new Booking());
                            bookList.add(booking);
                            remainTime = getRemainTimeInMilliSeconds(booking.getBookingTime());
                            if (remainTime > 0) {
                                final TextView timerText = new TextView(UserInterfaceActivity.this);
                                timerText.setTextSize(15);
                                timerText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                                timerText.setGravity(Gravity.CENTER);
                                timerText.setTextColor(getColor(R.color.foregroundText));
                                CountDownTimer timer = new CountDownTimer(getRemainTimeInMilliSeconds(booking.getBookingTime()), 1000) {

                                    @Override
                                    public void onFinish() {
                                        //TODO Whatever's meant to happen when it finishes
                                        counter = "Expired";
                                        timerText.setText(counter);
                                        apiInterface.doCancelOrder(booking.getBookingId()).enqueue(new Callback<ResponseTemplate>() {
                                            @Override
                                            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                                Log.d("TAG", response.code() + "");
                                                Log.d("TAG", response.raw() + "");
                                                Log.d("TAG", response.body() + "");
                                                getBooking();
                                                getParking();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                                String displayResponse = t.toString();
                                                Log.d("TAG", displayResponse);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onTick(long millisecondsLeft) {
                                        int timeLeft = (int) Math.round((millisecondsLeft / (double) 1000));
                                        long Hours = timeLeft / (60 * 60) % 24;
                                        long Minutes = timeLeft / 60 % 60;
                                        long Seconds = timeLeft % 60;
                                        counter = //String.format("%02d", Hours) + " Hours " +
                                                String.format("%02d", Minutes) + " m " +
                                                        String.format("%02d", Seconds) + " s ";
                                        invalidateOptionsMenu();
                                        //onPrepareOptionsMenu(menu);
                                        timerText.setText(counter);
                                        //timerText.setText(counter);
                                        Log.d("TAG", "Time is running more!");
                                    }
                                };
                                existTimer.add(timer);
                                bookingMenu.add(booking.getParkingLotName())
                                        //.setIcon(getDrawable(R.drawable.logout))
                                        .setActionView(timerText).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(UserInterfaceActivity.this);
                                        showImage = new ImageView(UserInterfaceActivity.this);
                                        showImage.setImageBitmap(stringToQRImage(booking.getUrlApiCheckIn(), 500, 500));
                                        ImageDialog.setView(showImage);
                                        ImageDialog.setTitle("Check in QR code");
                                        ImageDialog.setMessage("You have chosen to book: " + new String(booking.getParkingLotName()) + "\n" +
                                                "The car plate number is: " + new String(booking.getPlateNumber()) + "\n" +
                                                "The price is: " + String.format("%.0f", new Float(booking.getPrice())) + " VND/h" + "\n" +
                                                "Scan this QR code as a check for the car park" + "\n" +
                                                "If this QR code is expired, it cannot be used");
                                        ImageDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                getBooking();
                                                getParking();
                                            }
                                        });
                                        ImageDialog.setPositiveButton("Cancel order", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                apiInterface.doCancelOrder(new Integer(booking.getBookingId())).enqueue(new Callback<ResponseTemplate>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                                        Toast.makeText(UserInterfaceActivity.this, "Order canceled", Toast.LENGTH_SHORT).show();
                                                            getBooking();
                                                            getParking();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                                        String displayResponse = t.toString();
                                                        Log.d("TAG", displayResponse);
                                                    }
                                                });
                                            }
                                        });
                                        ImageDialog.show();
                                        return false;
                                    }
                                });
                                timer.start();
                            } else {
                                apiInterface.doCancelOrder(booking.getBookingId()).enqueue(new Callback<ResponseTemplate>() {
                                    @Override
                                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                        if (positionInABookList == sizeOfABookList - 1) {
                                            getBooking();
                                            getParking();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                        String displayResponse = t.toString();
                                        Log.d("TAG", displayResponse);
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        Log.d("TAG", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
            }
        });
    }

    private void getParking() {
        usingMenu.clear();
        useList.clear();
        apiInterface.doGetBookByStatus(account.getAccountId(), getResources().getString(R.string.status_in_use), getResources().getInteger(R.integer.parking_limit)).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                usingMenu.clear();
                try {
                    sizeOfAParkList = ((List) response.body().getObjectResponse()).size();
                }
                catch (Exception e){
                    sizeOfABookList = 0;
                }
                if (sizeOfAParkList == 0) {
                    usingMenu.add("---Empty---");
                } else {
                    try {
                        for (int i = 0; i < sizeOfAParkList; i++) {
                            positionInAParkList = i;
                            booking = (Booking) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new Booking());
                            useList.add(booking);
                            Chronometer timeElapsed  = new Chronometer(UserInterfaceActivity.this);
                            timeElapsed.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                                @Override
                                public void onChronometerTick(Chronometer cArg) {
                                    long time = SystemClock.elapsedRealtime() - cArg.getBase();
                                    int h   = (int)(time /3600000);
                                    int m = (int)(time - h*3600000)/60000;
                                    int s= (int)(time - h*3600000- m*60000)/1000 ;
                                    String hh = h < 10 ? "0"+h: h+"";
                                    String mm = m < 10 ? "0"+m: m+"";
                                    String ss = s < 10 ? "0"+s: s+"";
                                    cArg.setText(hh+" h "+mm+" m "+ss + " s ");
                                }
                            });
                            timeElapsed.setBase(SystemClock.elapsedRealtime() - (new Date().getTime() - booking.getTimeStart().getTime()));
                            timeElapsed.setTextSize(15);
                            timeElapsed.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            timeElapsed.setGravity(Gravity.CENTER);
                            timeElapsed.setTextColor(getColor(R.color.foregroundText));
                                usingMenu.add(booking.getParkingLotName())
                                        //.setIcon(getDrawable(R.drawable.logout))
                                        .setActionView(timeElapsed).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        apiInterface.doGetACarPark(new Integer(booking.getParkingLotId())).enqueue(new Callback<ResponseTemplate>() {
                                            @Override
                                            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                                Log.d("TAG", response.code() + "");
                                                Log.d("TAG", response.raw() + "");
                                                Log.d("TAG", response.body() + "");
                                                try {
                                                    selectedLot = (ParkingLot)apiClient.ObjectConverter((response.body().getObjectResponse()), new ParkingLot());
                                                    AlertDialog.Builder ImageDialog = new AlertDialog.Builder(UserInterfaceActivity.this);
                                                    showImage = new ImageView(UserInterfaceActivity.this);
                                                    showImage.setImageBitmap(stringToQRImage(booking.getUrlApiCheckOut(), 500, 500));
                                                    ImageDialog.setView(showImage);
                                                    ImageDialog.setTitle("Checkout QR code");
                                                    ImageDialog.setMessage("You have chosen to book: " + new String(booking.getParkingLotName()) + "\n" +
                                                            "The car plate number is: " + new String(booking.getPlateNumber()) + "\n" +
                                                            "The price is: " + String.format("%.0f", new Float(booking.getPrice())) + " VND/h" + "\n" +
                                                            "Scan this QR code as a check out for the car park" + "\n" +
                                                            "Payment will be automatically called.");
                                                    ImageDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0, int arg1) {
                                                            getBooking();
                                                            getParking();
                                                        }
                                                    });
                                                    showDirection(hView);
                                                    ImageDialog.show();
                                                } catch (Exception e) {
                                                    Log.d("TAG", e.toString());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                                String displayResponse = t.toString();
                                                Log.d("TAG", displayResponse);
                                            }
                                        });
                                        return false;
                                    }
                                });
                                timeElapsed.start();
                        }
                    } catch (Exception e) {
                        Log.d("TAG", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
            }
        });
    }

    public void clearExistTimer(){
        if(existTimer != null) {
            if (!existTimer.isEmpty()) {
                for (int i = 0; i < existTimer.size(); i++) {
                    existTimer.get(i).cancel();
                }
                existTimer.clear();
            }
        }
    }

    public void makeHistory(View view){
        header.setText(getResources().getString(R.string.history));
        vf.setDisplayedChild(getResources().getInteger(R.integer.HISTORY_SCREEN));
        apiInterface.doGetBookByStatus(account.getAccountId(), getResources().getString(R.string.status_finish), getResources().getInteger(R.integer.history_limit)).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                try {
                    finishList = new ArrayList<Booking>();
                    if (response.body().getObjectResponse() != null){
                        for (int i = 0; i < ((List) response.body().getObjectResponse()).size(); i++) {
                            finishList.add((Booking) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new Booking()));
                        }
                    }
                    historyScrollView.removeAllViews();
                    TableLayout historyTable = new TableLayout(UserInterfaceActivity.this);
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 20, 0, 30);
                    historyTable.setLayoutParams(params);
                    LinearLayout outerLinear = new LinearLayout(UserInterfaceActivity.this);
                    outerLinear.setLayoutParams(params);
                    LinearLayout.LayoutParams iconParams=new LinearLayout.LayoutParams(150, 160);
                    iconParams.gravity=Gravity.LEFT;
                    ShapeDrawable sd = new ShapeDrawable();
                    sd.setShape(new RectShape());
                    sd.getPaint().setColor(getColor(R.color.foregroundText));
                    sd.getPaint().setStrokeWidth(1f);
                    sd.getPaint().setStyle(Paint.Style.STROKE);

                    for(int i = 0; i < finishList.size() ; i++){
                        TextView currentText = new TextView(UserInterfaceActivity.this);
                        TextView dateText = new TextView(UserInterfaceActivity.this);
                        dateText.setGravity(Gravity.RIGHT | Gravity.TOP);
                        dateText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        dateText.setTextColor(getColor(R.color.foregroundText));
                        ImageView navigationImage = new ImageView(UserInterfaceActivity.this);
                        currentText.setGravity(Gravity.LEFT);
                        currentText.setTypeface(null, Typeface.BOLD);
                        navigationImage.setImageDrawable(getDrawable(R.drawable.finder_icon));
                        navigationImage.setLayoutParams(iconParams);
                        //currentText.setLayoutParams(params);
                        currentText.setTextAppearance(R.drawable.button_color);
                        currentText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        currentText.setTextColor(getColor(R.color.foregroundText));
                        currentText.setWidth(400);
                        dateText.setText(new SimpleDateFormat("MM/dd/yyyy").format(finishList.get(i).getBookingTime()));
                        currentText.setText(
                                    finishList.get(i).getParkingLotName() +
                                            "\n" + (finishList.get(i).getPlateNumber() == null? "N/A": finishList.get(i).getPlateNumber())  +
                                    "\n\nPrice: " + String.format("%.0f", finishList.get(i).getPrice())
                                //"\n Used from: " + (finishList.get(i).getTimeStart() != null ?
                                //new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(finishList.get(i).getTimeStart()) : "N/A") +
                                //"\n Used To: " + (finishList.get(i).getTimeEnd() != null ?
                                //new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(finishList.get(i).getTimeEnd()) : "N/A")
                        );
                        final String extraMessage = finishList.get(i).getParkingLotName() +
                                "\n\nCar number: "+ finishList.get(i).getPlateNumber() +
                                "\nBooked date: " + (finishList.get(i).getBookingTime() != null ?
                                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(finishList.get(i).getBookingTime()) : "N/A") +
                                "\nPrice: " + String.format("%.0f", finishList.get(i).getPrice()) +
                        "\nUsed from: " + (finishList.get(i).getTimeStart() != null ?
                        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(finishList.get(i).getTimeStart()) : "N/A") +
                        "\nUsed To: " + (finishList.get(i).getTimeEnd() != null ?
                        new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(finishList.get(i).getTimeEnd()) : "N/A");


                        navigationImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                apiInterface.doGetACarPark(new Integer(booking.getParkingLotId())).enqueue(new Callback<ResponseTemplate>() {
                                    @Override
                                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                                        try {
                                            selectedLot = (ParkingLot)apiClient.ObjectConverter((response.body().getObjectResponse()), new ParkingLot());
                                            if(selectedLot != null) {
                                                LatLng position = new LatLng(selectedLot.getLatitude(), selectedLot.getLongitude());
                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                builder.include(position);
                                                LatLngBounds bounds = builder.build();
                                                backButton(hView);
                                                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 8));
                                            }
                                        } catch (Exception e) {
                                            Log.d("TAG", e.toString());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                                        String displayResponse = t.toString();
                                        Log.d("TAG", displayResponse);
                                    }
                                });
                            }
                        });

                        currentText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserInterfaceActivity.this);
                                alertDialog.setTitle("Order detail");
                                alertDialog.setMessage(extraMessage);
                                alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                            }
                        });
                        LinearLayout inTable = new LinearLayout(UserInterfaceActivity.this);
                        inTable.setLayoutParams(layoutParams);
                        inTable.addView(navigationImage);
                        inTable.addView(currentText);
                        inTable.addView(dateText);
                        inTable.setBackground(sd);
                        historyTable.addView(inTable);
                    }
                    historyScrollView.addView(historyTable);

                } catch (Exception e) {
                    Log.d("TAG", e.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
                Log.d("TAG", getResources().getString(R.string.fail_message));
            }
        });
    }

}

