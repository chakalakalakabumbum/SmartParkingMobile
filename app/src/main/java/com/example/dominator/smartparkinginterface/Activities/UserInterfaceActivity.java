package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
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

    private ViewFlipper vf;
    private String currentUserId = AppValue.getTestUser();
    private APIClient apiClient = new APIClient();
    private APIInterface apiInterface = apiClient.getClient(AppValue.getMainLink()).create(APIInterface.class);
    private InformationAccount account = new InformationAccount();
    List<ParkingLot> parkingLots = new ArrayList<>();
    private int currentCarParkId = 1;
    private boolean failsafe;

    private static LatLng currentLocation = new LatLng(10.852711, 106.626786);

    private static final List<LatLng> lotsList = new ArrayList<>();

    private Polyline directionPolyline;

    private GoogleMap map;
    private Marker selectedMarker = null;

    private FloatingActionButton btnShowDirection;

    public LocationResult locationResult = new LocationResult() {

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

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnShowDirection = findViewById(R.id.btnShowDirection);

        addMarkers();

        vf = findViewById(R.id.vfu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#4F515C"));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        account = (InformationAccount) getIntent().getSerializableExtra("ACCOUNT_INFO");

//        final ImageView blackScreen = (ImageView) findViewById(R.id.loading_image);
//        final ImageView loadingLogo = (ImageView) findViewById(R.id.loadingLogo);

        final TextView reminder = (TextView) findViewById(R.id.reminder);
//        blackScreen.setVisibility(View.VISIBLE);
//        loadingLogo.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.loadinground);
//        loadingLogo.startAnimation(loadAnimation);

        apiInterface.doGetAllParkingLot().enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG", response.code() + "");
                Log.d("TAG", response.raw() + "");
                Log.d("TAG", response.body() + "");
                Log.d("TAG", AppValue.getSuccessMessage());
                ResponseTemplate responseTemplate = response.body();
                try {
                    for (int i = 0; i < ((List) response.body().getObjectResponse()).size(); i++) {
                        parkingLots.add((ParkingLot) apiClient.ObjectConverter(((List) response.body().getObjectResponse()).get(i), new ParkingLot()));
                        //parkingLots.add((ParkingLot)gson.fromJson(gson.toJson((LinkedTreeMap)(((List)response.body().getObjectResponse()).get(i))), parkingLots.getClass()));
                    }
//                    blackScreen.setVisibility(View.INVISIBLE);
//                    loadingLogo.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    Log.d("TAG", AppValue.getFailMessage());
                    Log.d("TAG", e.toString());
//                    blackScreen.setVisibility(View.INVISIBLE);
//                    loadingLogo.setVisibility(View.INVISIBLE);
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
            if (failsafe == true || failsafe == false) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                TextView header = (TextView) findViewById(R.id.toolbar_title);
                header.setText("Profile");
                View hView = navigationView.getHeaderView(0);
                viewInfo(hView);
            }
        } else if (id == R.id.nav_rate_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void backButton(View view) {
        vf.setDisplayedChild(0);
        TextView header = (TextView) findViewById(R.id.toolbar_title);
        header.setText("Home");
    }

    public void changePassword(View view) {
        TextView header = (TextView) findViewById(R.id.toolbar_title);
        header.setText("Change password");
        vf.setDisplayedChild(3);
    }

    public void viewCarpark(View view) {
        TextView header = (TextView) findViewById(R.id.toolbar_title);
        header.setText("Car park info");
        vf.setDisplayedChild(2);
        TextView ownerText = (TextView) findViewById(R.id.owner_text);
        TextView addressText = (TextView) findViewById(R.id.address_text);
        TextView telText = (TextView) findViewById(R.id.tel_text);
        TextView slotText = (TextView) findViewById(R.id.slot_text);
        TextView timeText = (TextView) findViewById(R.id.time_text);
        if (!parkingLots.isEmpty()) {
            Owner owner = parkingLots.get(currentCarParkId).getOwner();

            ownerText.setText(owner.getFullName());
            addressText.setText(parkingLots.get(currentCarParkId).getAddress());
            telText.setText(parkingLots.get(currentCarParkId).getPhoneNumber());
            slotText.setText(parkingLots.get(currentCarParkId).getTotalSlot());
            timeText.setText(parkingLots.get(currentCarParkId).getTimeOfOperation());
        }

    }

    public void viewInfo(View view) {
        TextView header = (TextView) findViewById(R.id.toolbar_title);
        header.setText("User info");
        vf.setDisplayedChild(1);
        EditText phoneNumberText = (EditText) findViewById(R.id.phone_number);
        TextView emailText = (TextView) findViewById(R.id.email);
        EditText firstNameText = (EditText) findViewById(R.id.first_name);
        EditText lastNameText = (EditText) findViewById(R.id.last_name);
        Button changeButton = (Button) findViewById(R.id.save_info);

        //get info from outer resource
        phoneNumberText.setText(account.getPhoneNumber());
        emailText.setText(account.getEmail());
        firstNameText.setText(account.getFirstName());
        lastNameText.setText(account.getLastName());

        changeButton.setEnabled(false);
        changeButton.setTextColor(Color.parseColor("#999999"));
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button changeButton = (Button) findViewById(R.id.save_info);
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
        Button changeButton = (Button) findViewById(R.id.save_info);
        EditText firstNameText = (EditText) findViewById(R.id.first_name);
        EditText lastNameText = (EditText) findViewById(R.id.last_name);
        EditText phoneNumberText = (EditText) findViewById(R.id.phone_number);
        TextView reminder = (TextView) findViewById(R.id.reminder);

        if (firstNameText.getText().toString().isEmpty() || lastNameText.getText().toString().isEmpty()
                || phoneNumberText.getText().toString().isEmpty()) {
            reminder.setText("Some required field is empty");
        } else if (!Patterns.PHONE.matcher(phoneNumberText.getText().toString()).matches()) {
            reminder.setText("Phone number is invalid");
        } else if (changeButton.isEnabled()) {

            InformationAccount user = new InformationAccount();
            user.setLastName(lastNameText.getText().toString());
            user.setFirstName(firstNameText.getText().toString());
            user.setPhoneNumber(phoneNumberText.getText().toString());
            failsafe = false;
            apiInterface.doUpdateUser(currentUserId, user).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                    Log.d("TAG1", response.code() + "");
                    Log.d("TAG2", response.raw() + "");
                    Log.d("TAG3", response.body() + "");
                    Log.d("TAG4", response.message() + "");
                    Log.d("TAG5", response.headers() + "");
                    Log.d("TAG6", AppValue.getSuccessMessage());
                    failsafe = true;
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", AppValue.getFailMessage());
                }
            });
            if (failsafe) {
                account.setFirstName(firstNameText.getText().toString());
                account.setLastName(lastNameText.getText().toString());
                account.setPhoneNumber(phoneNumberText.getText().toString());
            }
        }
    }

    public void confirmChangePassword(View view) {
        EditText oldPass = (EditText) findViewById(R.id.old_password);
        EditText newPass = (EditText) findViewById(R.id.new_password);
        EditText confirmNewPass = (EditText) findViewById(R.id.confirm_password);
        TextView reminder = (TextView) findViewById(R.id.reminder);

        Log.d("TAG", "Old PASS: " + account.getPassword());

        if (oldPass.getText().toString().equals(account.getPassword()) && !oldPass.getText().toString().isEmpty()) {
            if (newPass.getText().toString().equals(confirmNewPass.getText().toString()) && !newPass.getText().toString().isEmpty()) {
                PasswordChanger passwordChanger = new PasswordChanger(
                        account.getAccountId(),
                        oldPass.getText().toString(),
                        newPass.getText().toString(),
                        confirmNewPass.getText().toString()
                );
                failsafe = false;
                apiInterface.doChangePassword(passwordChanger).enqueue(new Callback<ResponseTemplate>() {
                    @Override
                    public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                        Log.d("TAG", response.code() + "");
                        Log.d("TAG2", response.raw() + "");
                        Log.d("TAG3", response.body() + "");
                        Log.d("TAG4", response.message() + "");
                        Log.d("TAG5", response.headers() + "");
                        Log.d("TAG6", AppValue.getSuccessMessage());
                        failsafe = true;
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("TAG", displayResponse);
                        Log.d("TAG", AppValue.getFailMessage());
                    }
                });
                if (failsafe) {
                    account.setPassword(newPass.getText().toString());
                    vf.setDisplayedChild(0);
                    TextView header = (TextView) findViewById(R.id.toolbar_title);
                    header.setText("Home");
                }
            } else {
                reminder.setText("New password and confirm password mismatch or empty");
            }
        } else {
            reminder.setText("Old password is empty or not correct");
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

    @Override
    public boolean onMarkerClick(Marker marker) {
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
            myLocation.getLocation(getApplicationContext(), locationResult);
            boolean r = myLocation.getLocation(getApplicationContext(),
                    locationResult);
            if (r) {
                SharedPreferences locationpref = getApplication().getSharedPreferences("location", MODE_PRIVATE);
                currentLocation = new LatLng(Double.parseDouble(locationpref.getString("Latitude", "10.852711")),
                        Double.parseDouble(locationpref.getString("Longitude", "106.626786")));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            int i = 0;
            for (LatLng lot : lotsList) {
                MarkerOptions option = new MarkerOptions();
                option.position(lot);
                //TODO: RED if busy, GREEN if available
                option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                //TODO: Change number of slots and name
                option.snippet("Free/Total: 123/512");
                option.title("LOT #" + (i + 1));
                i++;
                map.addMarker(option);
                builder.include(lot);
            }

            builder.include(currentLocation);
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            map.setOnMarkerClickListener(this);
        }
    }

    private void addMarkers() {
        lotsList.add(new LatLng(10.8671443, 106.6250343));
        lotsList.add(new LatLng(10.8542203, 106.6119883));
        lotsList.add(new LatLng(10.8473773, 106.6434973));
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

}
