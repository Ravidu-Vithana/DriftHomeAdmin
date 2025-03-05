package com.ryvk.drifthomeadmin;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class Admin {
    private String name;
    private String email;
    private String otp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public static Admin getSPAdmin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.ryvk.drifthomeadmin.data", Context.MODE_PRIVATE);
        String adminJSON = sharedPreferences.getString("user",null);
        Gson gson = new Gson();

        return gson.fromJson(adminJSON, Admin.class);
    }

    public void updateSPAdmin (Context context,Admin admin){
        Gson gson = new Gson();
        String adminJSON = gson.toJson(admin);

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.ryvk.drifthomeadmin.data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", adminJSON);
        editor.apply();
        editor.commit();
    }

    public void removeSPAdmin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.ryvk.drifthomeadmin.data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
        editor.apply();
    }
}
