package com.example.dominator.smartparkinginterface.Activities;

import android.content.Intent;
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

import com.example.dominator.smartparkinginterface.Constant.AppValue;
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

    private View mainActivity;
    private ViewFlipper vf;
    private AppValue appValue;
    private APIInterface apiInterface = APIClient.getClient(appValue.getMainLink()).create(APIInterface.class);;
    private InformationAccount accountInfo = new InformationAccount();
    private APIClient apiClient = new APIClient();
    private Boolean failsafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView logoImage;
        ImageView loadingImage;
        logoImage = findViewById(R.id.logo);
        loadingImage = findViewById(R.id.loadingLogo);
        mainActivity = findViewById(R.id.my_layout);
        vf = findViewById(R.id.vf);


        // Animation
        Animation animUpDown;
        Animation loadingLogo;

        // load the animation
        loadingLogo = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.loadinground);

        // start the animation
        loadingImage.startAnimation(loadingLogo);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                vf.setDisplayedChild(1);
            }
        }, 5000);   //5 seconds
    }
    public void loginInput(View view) {
        TextView emailText = (TextView) findViewById(R.id.email_text);
        final TextView passwordText = (TextView) findViewById(R.id.password_text);
        final ImageView blackScreen = (ImageView)findViewById(R.id.loading_image);
        final ImageView loadingLogo = (ImageView)findViewById(R.id.loadingLogo);


        final TextView reminder = (TextView) findViewById(R.id.reminder);
        failsafe = false;
        blackScreen.setVisibility(View.VISIBLE);
        loadingLogo.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.loadinground);
        loadingLogo.startAnimation(loadAnimation);
        apiInterface.doCheckLogin(new UserLogin(null, emailText.getText().toString(), passwordText.getText().toString())).enqueue(new Callback<ResponseTemplate>() {
            @Override
            public void onResponse(Call<ResponseTemplate> call, Response<ResponseTemplate> response) {
                Log.d("TAG",response.code()+"");
                Log.d("TAG",response.raw()+"");
                Log.d("TAG",response.body()+"");
                Log.d("TAG",appValue.getSuccessMessage());
                blackScreen.setVisibility(View.INVISIBLE);
                loadingLogo.setVisibility(View.INVISIBLE);
                if(response.body().getObjectResponse() == null){
                    reminder.setText("Invalid email or password");
                }else{
                    accountInfo = (InformationAccount) apiClient.ObjectConverter(response.body().getObjectResponse(), new InformationAccount());
                    accountInfo.setPassword(passwordText.getText().toString());
                    nextActivity();
                }
            }

            @Override
            public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                String displayResponse = t.toString();
                Log.d("TAG",displayResponse);
                Log.d("TAG",appValue.getFailMessage());
                reminder.setText("Unable to connect to server");
                blackScreen.setVisibility(View.INVISIBLE);
                loadingLogo.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void nextActivity(){
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
        TextView reminder = findViewById(R.id.reminder);
        EditText email = findViewById(R.id.email);
        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        EditText phoneNumber = findViewById(R.id.phone_number);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirm_password);

        if(email.getText().toString().isEmpty() ||
                firstName.getText().toString().isEmpty() ||
                lastName.getText().toString().isEmpty() ||
                phoneNumber.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                confirmPassword.getText().toString().isEmpty()) {
            reminder.setText("Some required field is empty");
        }
        else if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() == false){
            reminder.setText("Email pattern is invalid");
        }
        else if(Patterns.PHONE.matcher(email.getText().toString()).matches() == false){
            reminder.setText("Phone number is invalid");
        }

        else if (password.getText().toString().equals(confirmPassword.getText().toString())) {
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
                        Log.d("TAG", appValue.getSuccessMessage());
                    }

                    @Override
                    public void onFailure(Call<ResponseTemplate> call, Throwable t) {
                        String displayResponse = t.toString();
                        Log.d("TAG", displayResponse);
                        Log.d("TAG", appValue.getFailMessage());
                    }
                });
         } else {
                reminder.setText("Password and confirm password mismatch");
         }

    }
}
