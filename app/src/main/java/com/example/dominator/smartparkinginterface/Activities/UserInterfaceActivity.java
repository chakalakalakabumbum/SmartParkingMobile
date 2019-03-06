package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.dominator.smartparkinginterface.Constant.AppValue;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.Owner;
import com.example.dominator.smartparkinginterface.Entities.PasswordChanger;
import com.example.dominator.smartparkinginterface.Entities.ParkingLot;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.R;
import com.example.dominator.smartparkinginterface.Retrofit.APIClient;
import com.example.dominator.smartparkinginterface.Retrofit.APIInterface;
import com.example.dominator.smartparkinginterface.utils.DirectionsJSONParser;
import com.example.dominator.smartparkinginterface.utils.HttpUtils;
import com.example.dominator.smartparkinginterface.utils.LocationResult;
import com.example.dominator.smartparkinginterface.utils.MyLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
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
//    private FloatingActionButton btnShowDetail;
    private TextView header;
    private TextView ownerText;
    private TextView addressText;
    private TextView telText;
    private TextView slotText;
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

    //API
    private String currentUserId = AppValue.getTestUser();
    private APIClient apiClient = new APIClient();
    private APIInterface apiInterface = APIClient.getClient(AppValue.getMainLink()).create(APIInterface.class);
    private InformationAccount account = new InformationAccount();

    //Data
    private List<ParkingLot> parkingLots = new ArrayList<>();
    private static LatLng currentLocation = new LatLng(10.852711, 106.626786);

    //Google Map
    private Polyline directionPolyline;
    private GoogleMap map;

    //Flags
    private Marker selectedMarker = null;
    private ParkingLot selectedLot = null;
    private boolean isLotsReady = false;
    private boolean isMapReady = false;

    //Location Result
    private LocationResult locationResult = new LocationResult() {
        @Override
        public void gotLocation(Location location) {
            try {
                SharedPreferences locationPref = getApplication().getSharedPreferences("location", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = locationPref.edit();
                prefsEditor.putString("Longitude", location.getLongitude() + "");
                prefsEditor.putString("Latitude", location.getLatitude() + "");
                prefsEditor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        bindView();

        account = (InformationAccount) getIntent().getSerializableExtra("ACCOUNT_INFO");

        apiInterface.doGetAllParkingLot().enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG", response.code() + "");
                Log.d("TAG", response.raw() + "");
                Log.d("TAG", response.body() + "");
                Log.d("TAG", AppValue.getSuccessMessage());
                try {
                    for (int i = 0; i < ((List) response.body().getObjectResponse()).size(); i++) {
                        parkingLots.add((ParkingLot) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new ParkingLot()));
                    }
                    isLotsReady = true;
                    addMarkers();
                } catch (Exception e) {
                    Log.d("TAG", AppValue.getFailMessage());
                    Log.d("TAG", e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG", displayResponse);
                Log.d("TAG", AppValue.getFailMessage());
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerPosition = marker.getPosition();
        for (ParkingLot lot : parkingLots) {
            if (lot.getLatitude() == markerPosition.latitude && lot.getLongitude() == markerPosition.longitude) {
                selectedLot = lot;
            }
        }
        selectedMarker = marker;
        marker.showInfoWindow();
        btnShowDirection.show();
//        btnShowDetail.show();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map != null) {
            map.setContentDescription(getString(R.string.map_description));

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please accept location permission!", Toast.LENGTH_SHORT).show();
                return;
            }
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    //DO NOTHING
                }

                @Override
                public void onProviderEnabled(String s) {
                    //DO NOTHING
                }

                @Override
                public void onProviderDisabled(String s) {
                    //DO NOTHING
                }
            };
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);

            MyLocation myLocation = new MyLocation();
            if (myLocation.getLocation(getApplicationContext(), locationResult)) {
                SharedPreferences locationPref = getApplication().getSharedPreferences("location", MODE_PRIVATE);
                currentLocation = new LatLng(Double.parseDouble(locationPref.getString("Latitude", "10.852711")),
                        Double.parseDouble(locationPref.getString("Longitude", "106.626786")));
            }

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

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_images) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_news) {

        } else if (id == R.id.nav_profile) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                TextView header = findViewById(R.id.toolbar_title);
                header.setText("Profile");
                View hView = navigationView.getHeaderView(0);
                viewInfo(hView);
        } else if (id == R.id.nav_rate_us) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backButton(View view) {
        vf.setDisplayedChild(0);
        header.setText("Home");
    }

    public void changePassword(View view) {
        header.setText("Change password");
        vf.setDisplayedChild(3);
        changeReminder.setText("");
    }

    @SuppressLint("DefaultLocale")
    public void viewParkingLot(ParkingLot parkingLot) {
        header.setText(parkingLot.getDisplayName());
        vf.setDisplayedChild(2);
        Owner owner = parkingLot.getOwner();
        ownerText.setText(owner.getFullName());
        addressText.setText(parkingLot.getAddress());
        telText.setText(parkingLot.getPhoneNumber());
        slotText.setText(String.format("%d", parkingLot.getTotalSlot()));
        timeText.setText(parkingLot.getTimeOfOperation());
    }

    public void viewInfo(View view) {
        header.setText("User info");
        vf.setDisplayedChild(1);
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
        if (firstNameText.getText().toString().isEmpty() || lastNameText.getText().toString().isEmpty()
                || phoneNumberText.getText().toString().isEmpty()) {
            reminder.setText("Some required field is empty");
            blackScreen.setVisibility(View.INVISIBLE);
        } else if (!Patterns.PHONE.matcher(phoneNumberText.getText().toString()).matches()) {
            reminder.setText("Phone number is invalid");
            blackScreen.setVisibility(View.INVISIBLE);
        } else if (changeButton.isEnabled()) {

            InformationAccount user = new InformationAccount();
            user.setLastName(lastNameText.getText().toString());
            user.setFirstName(firstNameText.getText().toString());
            user.setPhoneNumber(phoneNumberText.getText().toString());
            apiInterface.doUpdateUser(currentUserId, user).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                    Log.d("TAG1", response.code() + "");
                    Log.d("TAG2", response.raw() + "");
                    Log.d("TAG3", response.body() + "");
                    Log.d("TAG4", response.message() + "");
                    Log.d("TAG5", response.headers() + "");
                    Log.d("TAG6", AppValue.getSuccessMessage());
                    if(response.isSuccessful() == true) {
                        account.setFirstName(firstNameText.getText().toString());
                        account.setLastName(lastNameText.getText().toString());
                        account.setPhoneNumber(phoneNumberText.getText().toString());
                        reminder.setText("Account update sucessfull");
                        changeButton.setEnabled(false);
                        changeButton.setTextColor(Color.parseColor("#999999"));
                        blackScreen.setVisibility(View.INVISIBLE);
                    }
                    else{
                        reminder.setText("Unable to save this new infromation");
                        blackScreen.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", AppValue.getFailMessage());
                    reminder.setText("Connection failed, please check your connection");
                    blackScreen.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public void confirmChangePassword(View view) {
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
                        Log.d("TAG6", AppValue.getSuccessMessage());
                        if(response.isSuccessful()) {
                            account.setPassword(newPass.getText().toString());
                            changeReminder.setText("Password has been changed");
                        }
                        else{
                            changeReminder.setText("Unable to change password");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("TAG", displayResponse);
                        Log.d("TAG", AppValue.getFailMessage());
                        changeReminder.setText("Connection failed, please check your network");
                    }
                });
            } else {
                changeReminder.setText("New password and confirm password mismatch or empty");
            }
        } else {
            changeReminder.setText("Old password is empty or not correct");
        }
    }

    public void showDirection(View view) {
        RequestParams params = getParams(currentLocation, selectedMarker.getPosition());

        HttpUtils.getByUrl("https://maps.googleapis.com/maps/api/directions/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                List<List<HashMap<String, String>>> routes = null;
                try {
                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    routes = parser.parse(response);
                } catch (Exception e) {
                    e.printStackTrace();
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
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
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

                    // Drawing polyline in the Google Map for the i-th route
                    directionPolyline = map.addPolyline(lineOptions);

                    directionPolyline.setStartCap(new RoundCap());
                    directionPolyline.setEndCap(new RoundCap());
                    directionPolyline.setJointType(JointType.ROUND);
                    directionPolyline.setPattern(null);

                    LatLngBounds bounds = builder.build();
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                    selectedMarker.hideInfoWindow();
                    selectedMarker = null;
                    btnShowDirection.hide();
                }
            }
        });
    }

//    public void showDetail(View view) {
//        if (selectedLot != null) {
//            viewParkingLot(selectedLot);
//        }
//        selectedLot = null;
//    }

    private void addMarkers() {
        if (isLotsReady && isMapReady) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ParkingLot lot : parkingLots) {
                LatLng marker = new LatLng(lot.getLatitude(), lot.getLongitude());
                MarkerOptions option = new MarkerOptions();
                option.position(marker);

                float color = lot.isActive() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;
                option.icon(BitmapDescriptorFactory.defaultMarker(color));

                option.title(lot.getDisplayName());
                option.snippet("Total slots: " + lot.getTotalSlot());

                map.addMarker(option);
                builder.include(marker);
            }
            builder.include(currentLocation);
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
//        btnShowDetail = findViewById(R.id.btnShowDetail);

        vf = findViewById(R.id.vfu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4F515C"));
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = findViewById(R.id.toolbar_title);
        ownerText = findViewById(R.id.owner_text);
        addressText = findViewById(R.id.address_text);
        telText = findViewById(R.id.tel_text);
        slotText = findViewById(R.id.slot_text);
        timeText = findViewById(R.id.time_text);
        phoneNumberText = findViewById(R.id.phone_number);
        emailText = findViewById(R.id.email);
        firstNameText =  findViewById(R.id.first_name);
        lastNameText = findViewById(R.id.last_name);
        changeButton = findViewById(R.id.save_info);
        reminder = findViewById(R.id.reminder);
        oldPass = findViewById(R.id.old_password);
        newPass = findViewById(R.id.new_password);
        confirmNewPass = findViewById(R.id.confirm_password);
        blackScreen = findViewById(R.id.loading_image);
    }

}
