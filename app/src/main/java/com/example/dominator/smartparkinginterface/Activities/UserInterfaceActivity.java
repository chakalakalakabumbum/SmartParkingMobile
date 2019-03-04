package com.example.dominator.smartparkinginterface.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.dominator.smartparkinginterface.Cons.AppValue;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.PasswordChanger;
import com.example.dominator.smartparkinginterface.Entities.ParkingLot;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.R;
import com.example.dominator.smartparkinginterface.Retrofit.APIClient;
import com.example.dominator.smartparkinginterface.Retrofit.APIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInterfaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewFlipper vf;
    private AppValue appValue;
    private String currentUserId = appValue.getTestUser();
    private APIClient apiClient = new APIClient();
    private APIInterface apiInterface = apiClient.getClient(appValue.getMainLink()).create(APIInterface.class);;
    private InformationAccount account = new InformationAccount();
    List<ParkingLot> parkingLots = new ArrayList<>();
    private int currentCarParkId = 1;
    private boolean failsafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
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

        apiInterface.doGetUser(currentUserId).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG",response.code()+"");
                Log.d("TAG",response.raw()+"");
                Log.d("TAG",response.body()+"");
                Log.d("TAG",appValue.getSuccessMessage());
                try {
                    account = (InformationAccount) apiClient.ObjectConverter(response.body().getObjectResponse(), new InformationAccount());
                    failsafe = true;
                }
                catch(Exception e){
                    Log.d("TAG",appValue.getFailMessage());
                    Log.d("TAG",e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG",displayResponse);
                Log.d("TAG",appValue.getFailMessage());
            }
        });

        apiInterface.doGetAllParkingLot().enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG",response.code()+"");
                Log.d("TAG",response.raw()+"");
                Log.d("TAG",response.body()+"");
                Log.d("TAG",appValue.getSuccessMessage());
                ResponseTemplate responseTemplate = response.body();
                try {
                    for(int i = 0; i < ((List)response.body().getObjectResponse()).size(); i++) {
                        parkingLots.add((ParkingLot)apiClient.ObjectConverter(((List)response.body().getObjectResponse()).get(i), new ParkingLot()));
                        //parkingLots.add((ParkingLot)gson.fromJson(gson.toJson((LinkedTreeMap)(((List)response.body().getObjectResponse()).get(i))), parkingLots.getClass()));
                    }
                    failsafe = true;
                }
                catch(Exception e){
                    Log.d("TAG",appValue.getFailMessage());
                    Log.d("TAG",e.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG",displayResponse);
                Log.d("TAG",appValue.getFailMessage());
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_images) {
            // Handle the camera action
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this,LoginActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_news) {

        } else if (id == R.id.nav_profile) {
            if(failsafe == true || failsafe == false)
            {
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
        TextView header = (TextView)findViewById(R.id.toolbar_title);
        header.setText("Home");
    }
    public void changePassword(View view) {
        TextView header = (TextView)findViewById(R.id.toolbar_title);
        header.setText("Change password");
        vf.setDisplayedChild(3);
    }
    public void viewCarpark(View view) {
        TextView header = (TextView)findViewById(R.id.toolbar_title);
        header.setText("Car park info");
        vf.setDisplayedChild(2);
        TextView ownerText = (TextView) findViewById(R.id.owner_text);
        TextView addressText = (TextView) findViewById(R.id.address_text);
        TextView telText = (TextView) findViewById(R.id.tel_text);
        TextView slotText = (TextView) findViewById(R.id.slot_text);
        TextView timeText = (TextView) findViewById(R.id.time_text);
        if(!parkingLots.isEmpty()) {
            ownerText.setText(parkingLots.get(currentCarParkId).getOwner().getFirstName()
                    + " " + parkingLots.get(currentCarParkId).getOwner().getLastName());
            addressText.setText(parkingLots.get(currentCarParkId).getAddress());
            telText.setText(parkingLots.get(currentCarParkId).getPhoneNumber());
            slotText.setText(parkingLots.get(currentCarParkId).getTotalSlot().toString());
            timeText.setText(parkingLots.get(currentCarParkId).getTimeOfOperation());
        }

    }
    public void viewInfo(View view) {
        TextView header = (TextView)findViewById(R.id.toolbar_title);
        header.setText("User info");
        vf.setDisplayedChild(1);
        EditText phoneNumberText = (EditText)findViewById(R.id.phone_number);
        TextView emailText = (TextView) findViewById(R.id.email);
        EditText firstNameText = (EditText)findViewById(R.id.first_name);
        EditText lastNameText = (EditText)findViewById(R.id.last_name);
        Button changeButton = (Button)findViewById(R.id.save_info);

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
                Button changeButton = (Button)findViewById(R.id.save_info);
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
        Button changeButton = (Button)findViewById(R.id.save_info);
        EditText firstNameText = (EditText)findViewById(R.id.first_name);
        EditText lastNameText = (EditText)findViewById(R.id.last_name);
        EditText phoneNumberText = (EditText)findViewById(R.id.phone_number);
        TextView reminder = (TextView)findViewById(R.id.reminder);

        if(firstNameText.getText().toString().isEmpty() || lastNameText.getText().toString().isEmpty()
                || phoneNumberText.getText().toString().isEmpty()){
            reminder.setText("Some required field is empty");
        }
        else if(Patterns.PHONE.matcher(phoneNumberText.getText().toString()).matches() == false){
            reminder.setText("Phone number is invalid");
        }
        else if(changeButton.isEnabled() == true){

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
                    Log.d("TAG6", appValue.getSuccessMessage());
                    failsafe = true;
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", appValue.getFailMessage());
                }
            });
            if(failsafe = true) {
                account.setFirstName(firstNameText.getText().toString());
                account.setLastName(lastNameText.getText().toString());
                account.setPhoneNumber(phoneNumberText.getText().toString());
            }
        }


    }

    public void confirmChangePassword(View view) {
        EditText oldPass = (EditText)findViewById(R.id.old_password);
        EditText newPass = (EditText)findViewById(R.id.new_password);
        EditText confirmNewPass = (EditText)findViewById(R.id.confirm_password);
        TextView reminder = (TextView)findViewById(R.id.reminder);

        Log.d("TAG", "Old PASS: " + account.getPassword());

        if(oldPass.getText().toString().equals(account.getPassword()) && !oldPass.getText().toString().isEmpty()){
            if(newPass.getText().toString().equals(confirmNewPass.getText().toString()) && !newPass.getText().toString().isEmpty()){
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
                        Log.d("TAG6", appValue.getSuccessMessage());
                        failsafe = true;
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("TAG", displayResponse);
                        Log.d("TAG", appValue.getFailMessage());
                    }
                });
                if(failsafe = true) {
                    account.setPassword(newPass.getText().toString());
                    vf.setDisplayedChild(0);
                    TextView header = (TextView) findViewById(R.id.toolbar_title);
                    header.setText("Home");
                }
            }
            else{
                reminder.setText("New password and confirm password mismatch or empty");
            }
        }
        else{
            reminder.setText("Old password is empty or not correct");
        }
    }
}
