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
import retrofit2.http.Query;

public interface APIInterface {

    @HTTP(method = "GET", path = "/driver/{id}")
    Call<ResponseTemplate> doGetDriver(@Path("id") int id);

    @HTTP(method = "GET", path = "/driver/get_parking_lot/{id}")
    Call<ResponseTemplate> doGetACarPark(@Path("id") int id);

    @HTTP(method = "GET", path = "/parking_lot/list_parking_slot")
    Call<ResponseTemplate> doGetCarparkSlots(@Query("parkingLotId") int parkingLotId);

    @HTTP(method = "PUT", path = "/driver/add_cash", hasBody = true)
    Call<ResponseTemplate> doAddMoreMoney(@Query("accountId") int accountID, @Query("amountOfCash") int amountOfCash);

    @HTTP(method = "GET", path = "/driver/all_parking_lot")
    Call<ResponseTemplate> doGetAllParkingLot();

    @HTTP(method = "POST", path = "/driver/create_driver", hasBody = true)
    Call<ResponseTemplate> doSubmitUser(@Body Account account);

    @HTTP(method = "POST", path = "/account/login", hasBody = true)
    Call<ResponseTemplate> doCheckLogin(@Body UserLogin userLogin);

    @HTTP(method = "POST", path = "/driver/forget_password", hasBody = true)
    Call<ResponseTemplate> doForgetPassword(@Query("email") String email);

    @HTTP(method = "PUT", path = "/account/update/{id}", hasBody = true)
    Call<ResponseTemplate> doUpdateUser(@Path("id") Integer id, @Body InformationAccount account);

    @HTTP(method = "PUT", path = "/account/change_password", hasBody = true)
    Call<ResponseTemplate> doChangePassword(@Body PasswordChanger changePassword);

    @HTTP(method = "POST", path = "/driver/booking_slot", hasBody = true)
    Call<ResponseTemplate> doBookSlot(@Query("accountId") int accountID, @Query("parkingLotId") int parkingLotId, @Query("bookingTime") String bookingTime);

    @HTTP(method = "GET", path = "/driver/list_booking/{id}")
    Call<ResponseTemplate> doGetBookByStatus(@Path("id") int id, @Query("statusName") String statusName, @Query("quantity") int quantity);

    @HTTP(method = "PUT", path = "/driver/booking_cancel", hasBody = true)
    Call<ResponseTemplate> doCancelOrder(@Query("bookingId") int bookingId);
}
