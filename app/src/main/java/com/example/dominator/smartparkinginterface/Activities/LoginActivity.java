package com.example.dominator.smartparkinginterface.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.dominator.smartparkinginterface.Entities.Account;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.Entities.UserLogin;
import com.example.dominator.smartparkinginterface.R;
import com.example.dominator.smartparkinginterface.Retrofit.APIClient;
import com.example.dominator.smartparkinginterface.Retrofit.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    //API
    private APIInterface apiInterface;
    private APIClient apiClient;

    //Entities
    private InformationAccount accountInfo;
    private Account account;

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
    private ImageView newUserAvatar;
    private ImageView choosingAvatarImage;
    private TextView checkEmail;
    private TextView checkFirstName;
    private TextView checkLastName;
    private TextView checkPhoneNumber;
    private TextView remindText;
    private ImageView checkUserAvatar;

    //Animation
    private Animation animateLogo;

    //int
    public static final int GET_FROM_GALLERY = 69;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int GALLERY_REQUEST_CODE = 2;

    Intent CropIntent;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            Toast.makeText(getApplicationContext(), R.string.Request_location_permission, Toast.LENGTH_SHORT).show();
        }

        //bind views
        bindView();

        // start the animation
        loadingImage.startAnimation(animateLogo);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                vf.setDisplayedChild(getResources().getInteger(R.integer.LOGIN_SCREEN));
            }
        }, 5000);   //5 seconds
    }

    public void loginInput(View view) {
        reminder.setText("");
        blackScreen.setVisibility(View.VISIBLE);
        preventClick();
//        nextActivity();
        if (!emailText.getText().toString().isEmpty() && !passwordText.getText().toString().isEmpty()) {
            apiInterface.doCheckLogin(new UserLogin(null, emailText.getText().toString(), passwordText.getText().toString())).enqueue(new Callback<ResponseTemplate>() {
                @Override
                public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                    Log.d("TAG", response.code() + "");
                    Log.d("TAG", response.raw() + "");
                    Log.d("TAG", response.body() + "");
                    Log.d("TAG", getResources().getString(R.string.success_message));
                    blackScreen.setVisibility(View.INVISIBLE);
                    resumeClick();
                    if (response.body().getObjectResponse() == null) {
                        reminder.setText(getResources().getString(R.string.invalid_email_and_password));
                        if (response.body().isStatus()) {
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
                    resumeClick();
                }
            });
        } else {
            reminder.setText(getResources().getString(R.string.empty_email_and_password));
            blackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        }
    }

    public void nextActivity() {
        if (accountInfo.getAvatar() == null) {
            accountInfo.setAvatar(apiClient.bitmapToByte(((BitmapDrawable) getResources().getDrawable(R.drawable.default_avatar)).getBitmap()));
        }
        Intent intent = new Intent(this, UserInterfaceActivity.class)
                .putExtra("ACCOUNT_INFO", accountInfo);
        this.startActivity(intent);
    }

    public void forgetPassword(View view) {
        forgetReminder.setText("");
        vf.setDisplayedChild(getResources().getInteger(R.integer.FORGET_PASS_SCREEN));
    }

    public void newAccount(View view) {
        newReminder.setText("");
        vf.setDisplayedChild(getResources().getInteger(R.integer.NEW_ACCOUNT_SCREEN));
    }

    public void returnLogin(View view) {
        reminder.setText("");
        vf.setDisplayedChild(getResources().getInteger(R.integer.LOGIN_SCREEN));
    }

    public void createAccount(View view) {
        newReminder.setText("");
        newBlackScreen.setVisibility(View.VISIBLE);
        preventClick();

        if (email.getText().toString().isEmpty() ||
                firstName.getText().toString().isEmpty() ||
                lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                confirmPassword.getText().toString().isEmpty()) {
            newReminder.setText(getResources().getString(R.string.empty_field));
            newBlackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            newReminder.setText(getResources().getString(R.string.invalid_email));
            newBlackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        } else if (!Patterns.PHONE.matcher(phoneNumber.getText().toString()).matches()) {
            newReminder.setText(getResources().getString(R.string.invalid_phone));
            newBlackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        } else if (password.getText().toString().equals(confirmPassword.getText().toString())) {
            account = new Account();
            account.setEmail(email.getText().toString());
            account.setFirstName(firstName.getText().toString());
            account.setLastName(lastName.getText().toString());
            account.setPhoneNumber(phoneNumber.getText().toString());
            account.setPassword(password.getText().toString());
            account.setAvatar(apiClient.bitmapToByte(((BitmapDrawable) (newUserAvatar.getDrawable())).getBitmap()));

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
                    resumeClick();
                    if (response.body().isStatus()) {
                        resumeClick();
                        checkEmail.setText(account.getEmail());
                        checkFirstName.setText(account.getFirstName());
                        checkLastName.setText(account.getLastName());
                        checkPhoneNumber.setText(account.getPhoneNumber());
                        checkUserAvatar.setTag(account.getAvatar());
                        checkUserAvatar.setImageBitmap(apiClient.byteToBitmap(account.getAvatar()));
                        vf.setDisplayedChild(getResources().getInteger(R.integer.CONFIRM_ACCOUNT_SCREEN));
                    } else {
                        newReminder.setText(getResources().getString(R.string.account_exist));
                        resumeClick();
                    }
                }

                @Override
                public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                    String displayResponse = t.toString();
                    Log.d("TAG", displayResponse);
                    Log.d("TAG", getResources().getString(R.string.fail_message));
                    newReminder.setText(getResources().getString(R.string.connection_failed));
                    newBlackScreen.setVisibility(View.INVISIBLE);
                    resumeClick();
                }
            });
        } else {
            newBlackScreen.setVisibility(View.INVISIBLE);
            newReminder.setText(getResources().getString(R.string.confirm_password_mismatch));
            resumeClick();
        }
    }

    public void confirmForgetPassword(View view) {
        forgetReminder.setText("");
        remindText.setVisibility(View.INVISIBLE);
        changeBlackScreen.setVisibility(View.VISIBLE);
        preventClick();
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
                                forgetReminder.setText("");
                                remindText.setVisibility(View.VISIBLE);
                            } else {
                                forgetReminder.setText(getResources().getString(R.string.invalid_email));
                            }
                            changeBlackScreen.setVisibility(View.INVISIBLE);
                            resumeClick();
                        }

                        @Override
                        public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                            String displayResponse = t.toString();
                            Log.d("TAG", displayResponse);
                            Log.d("TAG", getResources().getString(R.string.fail_message));
                            forgetReminder.setText(getResources().getString(R.string.connection_failed));
                            changeBlackScreen.setVisibility(View.INVISIBLE);
                            resumeClick();
                        }
                    }
            );
        } else {
            forgetReminder.setText(getResources().getString(R.string.invalid_email));
            changeBlackScreen.setVisibility(View.INVISIBLE);
            resumeClick();
        }
    }

    public void changeAvatar(View view) {
        vf.setDisplayedChild(getResources().getInteger(R.integer.AVATAR_SCREEN));
    }

    public void choosingImage(View view) {
        String pageImage = view.getTag().toString();
        choosingAvatarImage.setImageResource(APIClient.getResId(pageImage, R.drawable.class));
        choosingAvatarImage.setTag(pageImage);
    }

    public void saveImage(View view) {
        newUserAvatar.setImageResource(APIClient.getResId(choosingAvatarImage.getTag().toString(), R.drawable.class));
        newUserAvatar.setTag(choosingAvatarImage.getTag().toString());
        vf.setDisplayedChild(getResources().getInteger(R.integer.NEW_ACCOUNT_SCREEN));
    }

    public void backButton(View view) {
        vf.setDisplayedChild(getResources().getInteger(R.integer.NEW_ACCOUNT_SCREEN));
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
        email = findViewById(R.id.new_email);
        firstName = findViewById(R.id.new_firstname);
        lastName = findViewById(R.id.new_lastname);
        phoneNumber = findViewById(R.id.new_phone_number);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        submitEmail = findViewById(R.id.forget_email_text);
        newUserAvatar = findViewById(R.id.new_user_avatar);
        choosingAvatarImage = findViewById(R.id.choosing_user_avatar);
        checkEmail = findViewById(R.id.check_email);
        checkFirstName = findViewById(R.id.check_firstname);
        checkLastName = findViewById(R.id.check_lastname);
        checkPhoneNumber = findViewById(R.id.check_phone_number);
        remindText = findViewById(R.id.remind_text);
        checkUserAvatar = findViewById(R.id.check_user_avatar);

        //API
        apiInterface = APIClient.getClient(getResources().getString(R.string.main_link)).create(APIInterface.class);
        apiClient = new APIClient();

        //Entities
        accountInfo = new InformationAccount();

        //Bind animation
        animateLogo = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.loadinground);

    }

    public void preventClick() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void resumeClick() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }

        }
    }

    private boolean checkPermissions() {
        int networkLocationState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int gpsLocationState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return networkLocationState == PackageManager.PERMISSION_GRANTED || gpsLocationState == PackageManager.PERMISSION_GRANTED;
    }

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
                newUserAvatar.setImageBitmap(apiClient.getRoundedShape(bitmap));
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

}
