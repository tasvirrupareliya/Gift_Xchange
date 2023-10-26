package com.app.giftxchange.utils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.app.giftxchange.utils.Utils;

import java.util.List;
import java.util.Locale;


public class FetchAddressIntentServices extends IntentService {
    ResultReceiver resultReceiver;

    public FetchAddressIntentServices() {
        super("FetchAddressIntentServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String errormessgae = "";
            resultReceiver = intent.getParcelableExtra(Utils.RECEVIER);
            Location location = intent.getParcelableExtra(Utils.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            } catch (Exception ioException) {
                Log.e("", "Error in getting address for the location");
            }

            if (addresses == null || addresses.size() == 0) {
                errormessgae = "No address found for the location";
                Toast.makeText(this, "" + errormessgae, Toast.LENGTH_SHORT).show();
            } else {
                Address address = addresses.get(0);
                String str_postcode = address.getPostalCode();
                String str_Country = address.getCountryName();
                String str_state = address.getAdminArea();
                String str_district = address.getSubAdminArea();
                String str_locality = address.getLocality();
                String str_address = address.getFeatureName();
                devliverResultToRecevier(Utils.SUCCESS_RESULT, str_address, str_locality, str_district, str_state, str_Country, str_postcode);
            }
        }

    }

    private void devliverResultToRecevier(int resultcode, String address, String locality, String district, String state, String country, String postcode) {
        Bundle bundle = new Bundle();
        bundle.putString(Utils.ADDRESS, address);
        bundle.putString(Utils.LOCAITY, locality);
        bundle.putString(Utils.DISTRICT, district);
        bundle.putString(Utils.STATE, state);
        bundle.putString(Utils.COUNTRY, country);
        bundle.putString(Utils.POST_CODE, postcode);
        resultReceiver.send(resultcode, bundle);
    }

}