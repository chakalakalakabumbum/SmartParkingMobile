package com.example.dominator.smartparkinginterface.Retrofit;

import com.example.dominator.smartparkinginterface.Entities.Account;
import com.example.dominator.smartparkinginterface.Entities.InformationAccount;
import com.example.dominator.smartparkinginterface.Entities.PasswordChanger;
import com.example.dominator.smartparkinginterface.Entities.ResponseTemplate;
import com.example.dominator.smartparkinginterface.Entities.UserLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Path;

public interface APIInterface {

    @HTTP(method = "GET", path = "/account/{id}")
    Call<ResponseTemplate> doGetUser(@Path("id") String id);

    @HTTP(method = "GET", path = "parking_lot/all_parking_lot")
    Call<ResponseTemplate> doGetAllParkingLot();

    @HTTP(method = "POST", path = "/driver_account/create", hasBody = true)
    Call<ResponseTemplate> doSubmitUser(@Body Account account);

    @HTTP(method = "POST", path = "/account/login", hasBody = true)
    Call<ResponseTemplate> doCheckLogin(@Body UserLogin userLogin);

    @HTTP(method = "PUT", path = "/account/update/{id}", hasBody = true)
    Call<ResponseTemplate> doUpdateUser(@Path("id") String id, @Body InformationAccount account);

    @HTTP(method = "PUT", path = "/account/change_password", hasBody = true)
    Call<ResponseTemplate> doChangePassword(@Body PasswordChanger changePassword);
}
