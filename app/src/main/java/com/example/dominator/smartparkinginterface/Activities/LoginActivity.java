package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.dominator.smartparkinginterface.Entities.Account;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.Entities.UserLogin;
import com.example.dominator.smartparkinginterface.R;
import com.example.dominator.smartparkinginterface.Retrofit.APIClient;
import com.example.dominator.smartparkinginterface.Retrofit.APIInterface;

import java.io.IOException;
import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {


    //API
    private APIInterface apiInterface;
    private APIClient apiClient;

    //Entities
    private InformationAccount accountInfo;

    //View
    private ViewFlipper vf;
    private ImageView blackScreen;
    private ImageView newBlackScreen;
    private ImageView changeBlackScreen;
    private ImageView loadingImage;
    private TextView emailText;
    private TextView passwordText;
    private TextView forgetReminder;
    private TextView newReminder;
    private TextView reminder;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;
    private EditText submitEmail;

    //Animation
    private Animation animateLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLocationPermission();

        //bind views
        bindView();

        // start the animation
        loadingImage.startAnimation(animateLogo);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                vf.setDisplayedChild(1);
            }
        }, 5000);   //5 seconds
    }

    public void loginInput(View view) {
        blackScreen.setVisibility(View.VISIBLE);
        //nextActivity();
        if(!emailText.getText().toString().isEmpty() || !passwordText.getText().toString().isEmpty()) {
            apiInterface.doCheckLogin(new UserLogin(null, emailText.getText().toString(), passwordText.getText().toString())).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                    Log.d("TAG", response.code() + "");
                    Log.d("TAG", response.raw() + "");
                    Log.d("TAG", response.body() + "");
                    Log.d("TAG", getResources().getString(R.string.success_message));
                    blackScreen.setVisibility(View.INVISIBLE);
                    if (response.body().getObjectResponse() == null) {
                        reminder.setText(getResources().getString(R.string.invalid_email_and_password));
                        if (response.body().isStatus() == true) {
                            reminder.setText(getResources().getString(R.string.not_activated));
                        }
                    } else {
                        accountInfo = (InformationAccount) apiClient.ObjectConverter(response.body().getObjectResponse(), new InformationAccount());
                        accountInfo.setPassword(passwordText.getText().toString());
                        nextActivity();
                    }
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    reminder.setText(getResources().getString(R.string.connection_failed));
                    blackScreen.setVisibility(View.INVISIBLE);
                }
            });
        }
        else{
            reminder.setText(getResources().getString(R.string.invalid_email_and_password));
            blackScreen.setVisibility(View.INVISIBLE);
        }
    }

    public void nextActivity() {
        Intent intent = new Intent(this, UserInterfaceActivity.class)
                .putExtra("ACCOUNT_INFO", (Serializable) accountInfo);
        this.startActivity(intent);
    }

    public void forgetPassword(View view) {
        vf.setDisplayedChild(3);
    }

    public void newAccount(View view) {
        vf.setDisplayedChild(2);
    }

    public void returnLogin(View view) {
        vf.setDisplayedChild(1);
    }

    public void createAccount(View view) {
        newBlackScreen.setVisibility(View.VISIBLE);

        if (email.getText().toString().isEmpty() ||
                firstName.getText().toString().isEmpty() ||
                lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                confirmPassword.getText().toString().isEmpty()) {
            newReminder.setText(getResources().getString(R.string.empty_field));
            newBlackScreen.setVisibility(View.INVISIBLE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            newReminder.setText(getResources().getString(R.string.invalid_email));
            newBlackScreen.setVisibility(View.INVISIBLE);
        } else if (!Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches()) {
            newReminder.setText(getResources().getString(R.string.invalid_phone));
            newBlackScreen.setVisibility(View.INVISIBLE);
        } else if (password.getText().toString().equals(confirmPassword.getText().toString())) {
            Account account = new Account();
            account.setEmail(email.getText().toString());
            account.setFirstName(firstName.getText().toString());
            account.setLastName(lastName.getText().toString());
            account.setPhoneNumber(phoneNumber.getText().toString());
            account.setPassword(password.getText().toString());

            apiInterface.doSubmitUser(account).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, retrofit2.Response<ResponseTemplate> response) {
                    Log.d("TAG", response.code() + "");
                    Log.d("TAG", response.raw() + "");
                    Log.d("TAG", response.body() + "");
                    Log.d("TAG", response.message() + "");
                    Log.d("TAG", response.headers() + "");
                    Log.d("TAG", getResources().getString(R.string.success_message));
                    newBlackScreen.setVisibility(View.INVISIBLE);
                    if(response.body().isStatus()){
                        newReminder.setText(getResources().getString(R.string.new_account_success));
                    }
                    else{
                        newReminder.setText(getResources().getString(R.string.account_exist));
                    }
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    newBlackScreen.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            newReminder.setText(getResources().getString(R.string.confirm_password_mismatch));
        }
    }

    public void confirmForgetPassword(View view) {
        changeBlackScreen.setVisibility(View.VISIBLE);
        if (!submitEmail.getText().toString().isEmpty()) {
            apiInterface.doForgetPassword(submitEmail.getText().toString()).enqueue(
                    new Callback<ResponseTemplate>() {
                        @Override
                        public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                            Log.d("TAG", response.code() + "");
                            Log.d("TAG", response.raw() + "");
                            Log.d("TAG", response.body() + "");
                            Log.d("TAG", getResources().getString(R.string.success_message));
                            if (response.body().isStatus()) {
                                forgetReminder.setText(getResources().getString(R.string.email_sent));
                            } else {
                                forgetReminder.setText(getResources().getString(R.string.invalid_email));
                            }
                            changeBlackScreen.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                            String displayResponse = t.toString();
                            Log.d("TAG", displayResponse);
                            Log.d("TAG", getResources().getString(R.string.fail_message));
                            forgetReminder.setText(getResources().getString(R.string.connection_failed));
                            changeBlackScreen.setVisibility(View.INVISIBLE);
                        }
                    }
            );
        } else {
            forgetReminder.setText(getResources().getString(R.string.invalid_email));
            changeBlackScreen.setVisibility(View.INVISIBLE);
        }
    }


    private void bindView() {

        //Bind view
        vf = findViewById(R.id.vf);
        loadingImage = findViewById(R.id.loadingLogo);
        blackScreen = findViewById(R.id.loading_image);
        newBlackScreen = findViewById(R.id.new_loading_image);
        changeBlackScreen = findViewById(R.id.forget_loading_image);
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        reminder = findViewById(R.id.reminder);
        forgetReminder = findViewById(R.id.forget_reminder);
        newReminder = findViewById(R.id.new_reminder);
        email = findViewById(R.id.email);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        submitEmail = findViewById(R.id.forget_email_text);

        //API
        apiInterface = APIClient.getClient(getResources().getString(R.string.main_link)).create(APIInterface.class);
        apiClient = new APIClient();

        //Entities
        accountInfo = new InformationAccount();

        //Bind animation
        animateLogo = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.loadinground);

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("This is a title")
                        .setMessage("This is a message")
                        .setPositiveButton("OKIE string", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}
