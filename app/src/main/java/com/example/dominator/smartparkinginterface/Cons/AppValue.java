package com.example.dominator.smartparkinginterface.Cons;

public class AppValue {
    public static final String MAIN_LINK = "http://192.168.1.56:8080/";
    public static final String FAIL_MESSAGE = "FAILED";
    public static final String SUCCESS_MESSAGE = "DONE";
    public static final String TEST_USER = "6";

    public static String getMainLink() {
        return MAIN_LINK;
    }

    public static String getFailMessage() {
        return FAIL_MESSAGE;
    }

    public static String getSuccessMessage() {
        return SUCCESS_MESSAGE;
    }

    public static String getTestUser() {
        return TEST_USER;
    }
}
