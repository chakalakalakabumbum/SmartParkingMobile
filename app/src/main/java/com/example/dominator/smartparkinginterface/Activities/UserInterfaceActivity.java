package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
    private ImageView choosingAvatar;
    private ImageView currentAvatar;
    private ImageView sidebarAvatar;
    private TextView sidebarName;
    private TextView sidebarEmail;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView carparkImage;

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

        getAllParkingLots();

        currentAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        choosingAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        sidebarAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        sidebarEmail.setText(account.getEmail());
        sidebarName.setText(account.getFullName());
        init();
        startLocationUpdates();
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
            View hView = navigationView.getHeaderView(0);
            viewInfo(hView);
        }
        /*
        else if (id == R.id.nav_images) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_news) {

        } else if (id == R.id.nav_rate_us) {

        }
        */
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backButton(View view) {
        currentAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
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
        getSlotFromALot(parkingLot.getParkingLotId());
        header.setText(parkingLot.getDisplayName());
        vf.setDisplayedChild(getResources().getInteger(R.integer.CARPARK_SCREEN));
        Owner owner = parkingLot.getOwner();
        ownerText.setText(owner.getFullName());
        carparkText.setText(parkingLot.getDisplayName());
        addressText.setText(parkingLot.getAddress());
        telText.setText(parkingLot.getPhoneNumber());
        slotButton.setText("Calculating...");
        timeText.setText(parkingLot.getTimeOfOperation());
        if (parkingLot.getParklotImage() != null) {
            carparkImage.setImageBitmap(apiClient.byteToBitmap(parkingLot.getParklotImage()));
        } else {
            carparkImage.setImageResource(R.drawable.persuo_carpark);
        }

    }

    public void getSlotFromALot(int id){
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
                    slotButton.setText("  " + freeSlots + " Empty / " + parkingSlots.size() + " Slots");
                    slotButton.setClickable(true);
                    if(withSlotRender == true){
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
        currentAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        choosingAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        sidebarAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
        drawer.closeDrawer(GravityCompat.START);
        header.setText(getResources().getString(R.string.user_info));
        vf.setDisplayedChild(getResources().getInteger(R.integer.USER_INFO_SCREEN));
        //get info from outer resource
        phoneNumberText.setText(account.getPhoneNumber());
        emailText.setText(account.getEmail());
        firstNameText.setText(account.getFirstName());
        lastNameText.setText(account.getLastName());

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
    }

    public void saveInfo(View view) {
        blackScreen.setVisibility(View.VISIBLE);
        reminder.setTextColor(Color.parseColor("#D81B60"));
        reminder.setText("");
        preventClick();
        if (firstNameText.getText().toString().isEmpty() || lastNameText.getText().toString().isEmpty()
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
            getDirection(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), selectedMarker.getPosition());
        }
        else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            callForLocation();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        }
    }

    public void getDirection(LatLng from, LatLng to) {
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
                Toast.makeText(getApplicationContext(), "Connection's failed! Please check your connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkers() {
        map.clear();
        LatLng marker;
        if (isLotsReady && isMapReady) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ParkingLot lot : parkingLots) {
                marker = new LatLng(lot.getLatitude(), lot.getLongitude());
                MarkerOptions option = new MarkerOptions();
                option.position(marker);

                float color = lot.isActive() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
                option.icon(BitmapDescriptorFactory.defaultMarker(color));
                option.title(lot.getDisplayName());
                option.snippet(lot.getEmptySlot() + " Empty / " + lot.getTotalSlot() + " Slots");

                map.addMarker(option);
                builder.include(marker);
            }
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
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
        choosingAvatar = findViewById(R.id.choosing_user_avatar);
        currentAvatar = findViewById(R.id.current_user_avatar);
        changeReminder = findViewById(R.id.change_reminder);
        carparkImage = findViewById(R.id.carparkImage);
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

    }

    public void infoNavigate(View view) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
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

    public void choosingImage(View view) {
        String pageImage = view.getTag().toString();
        choosingAvatar.setImageResource(APIClient.getResId(pageImage, R.drawable.class));
        choosingAvatar.setTag(pageImage);
    }

    public void saveImage(View view) {
        currentAvatar.setImageResource(APIClient.getResId(choosingAvatar.getTag().toString(), R.drawable.class));
        vf.setDisplayedChild(getResources().getInteger(R.integer.USER_INFO_SCREEN));
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
    }

    private void startLocationUpdates() {
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
            getSlotFromALot(selectedLot.getParkingLotId());
            vf.setDisplayedChild(5);
            header.setText("Slot detail");
        }
    }

    public void refreshCarSlot(View view){
        if(selectedLot != null) {
            withSlotRender = true;
            getSlotFromALot(selectedLot.getParkingLotId());
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

}

