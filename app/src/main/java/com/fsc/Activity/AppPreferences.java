package com.fsc.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONArray;

public class AppPreferences {
    private static final String APP_SHARED_PREFS = "DFSC";
    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;

    public AppPreferences(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public void RemoveAllSharedPreference() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public String getURL() {
        return appSharedPrefs.getString("url", " ");
    }

    public void setURL(String text) {
        prefsEditor.putString("url", text);
        prefsEditor.commit();
    }

    public int getMobileVald() {
        return appSharedPrefs.getInt("mobile_validation", 0);
    }

    public void setMobileVad(int text) {
        prefsEditor.putInt("mobile_validation", text);
        prefsEditor.commit();
    }

    public String getShouldLogin() {
        return appSharedPrefs.getString("LOGIN", " ");
    }

    public void setShouldLogin(String text) {
        prefsEditor.putString("LOGIN", text);
        prefsEditor.commit();
    }

    public String getLanguageSelected() {
        return appSharedPrefs.getString("lang_selected", " ");
    }

    public void setLanguageSelected(String text) {
        prefsEditor.putString("lang_selected", text);
        prefsEditor.commit();
    }

    public String getEmail() {
        return appSharedPrefs.getString("email", " ");
    }

    public void setEmail(String text) {
        prefsEditor.putString("email", text);
        prefsEditor.commit();
    }

    public String getGroup() {
        return appSharedPrefs.getString("group", " ");
    }

    public void setGroup(String text) {
        prefsEditor.putString("group", text);
        prefsEditor.commit();
    }

    public String getMobile() {
        return appSharedPrefs.getString("mobile", " ");
    }

    public void setMobile(String text) {
        prefsEditor.putString("mobile", text);
        prefsEditor.commit();
    }

    public String getUsername() {
        return appSharedPrefs.getString("username", " ");
    }

    public void setUsername(String text) {
        prefsEditor.putString("username", text);
        prefsEditor.commit();
    }

    public String getUserId() {
        return appSharedPrefs.getString("user_id", "");
    }

    public void setUserId(String text) {
        prefsEditor.putString("user_id", text);
        prefsEditor.commit();
    }

    public String getFlag() {
        return appSharedPrefs.getString("flag", "");
    }

    public void setFlag(String text) {
        prefsEditor.putString("flag", text);
        prefsEditor.commit();
    }

    public String getCountry() {
        return appSharedPrefs.getString("country", "");
    }

    public void setCountry(String text) {
        prefsEditor.putString("country", text);
        prefsEditor.commit();
    }

    public String getCode() {
        return appSharedPrefs.getString("code", "");
    }

    public void setCode(String text) {
        prefsEditor.putString("code", text);
        prefsEditor.commit();
    }

    public void setSideMenu(JSONArray userObject){
        prefsEditor.putString("side_menu", String.valueOf(userObject));
        prefsEditor.commit();
    }

    public String getLanguage(){
        return  appSharedPrefs.getString("languages", null);
    }

    public void setLanguage(JSONArray userObject){
        prefsEditor.putString("languages", String.valueOf(userObject));
        prefsEditor.commit();
    }

    public String getSideMenu(){
        return  appSharedPrefs.getString("side_menu", null);
    }

    public void setDashboard(JSONArray userObject){
        prefsEditor.putString("dashboard", String.valueOf(userObject));
        prefsEditor.commit();
    }

    public String getDashboard(){
        return  appSharedPrefs.getString("dashboard", null);
    }
}