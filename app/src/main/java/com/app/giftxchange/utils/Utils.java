package com.app.giftxchange.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.app.giftxchange.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class Utils {

    public static ProgressDialog progressDialog;
    public static final String PACKAGE_NAME = "com.example.currentaddress";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RECEVIER = PACKAGE_NAME + ".RECEVIER";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String ADDRESS = PACKAGE_NAME + ".ADDRESS";
    public static final String LOCAITY = PACKAGE_NAME + ".LOCAITY";
    public static final String COUNTRY = PACKAGE_NAME + ".COUNTRY";
    public static final String DISTRICT = PACKAGE_NAME + ".DISTRICT";
    public static final String POST_CODE = PACKAGE_NAME + ".POST_CODE";
    public static final String STATE = PACKAGE_NAME + ".STATE";

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static final int SUCCESS_RESULT = 1;
    public static final int FAILURE_RESULT = 0;

    public static int[] imageResources = {
            R.drawable.g1,
            R.drawable.g2,
            R.drawable.g3,
            R.drawable.g4,
            R.drawable.g5,
            R.drawable.g6,
            R.drawable.g7
    };

    public static void showProgressDialog(Activity activity, String title) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(title);
        if (!progressDialog.isShowing() && !activity.isFinishing()) {
            progressDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences;
    }

    public static void saveSharedData(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedData(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    @SuppressLint("CommitPrefEdits")
    public static void clearSession() {
        sharedPreferences.edit().clear();
        sharedPreferences.edit().apply();
    }

    public static void setTitleWithColor(ActionBar actionBar, String title, int color) {
        SpannableString spannableTitle = new SpannableString(title);
        spannableTitle.setSpan(new ForegroundColorSpan(color), 0, spannableTitle.length(), 0);
        actionBar.setTitle(spannableTitle);
    }
}
