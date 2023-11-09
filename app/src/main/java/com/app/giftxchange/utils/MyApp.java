package com.app.giftxchange.utils;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PaymentConfiguration.init(getApplicationContext(), "pk_test_51O70fMKlHXuDjdRG8t2AOZKFqd8iaQ5Fkm9iTF1pylHHCmdOTGNXWBFyYkcFqO5wHGMSLCXna6dSCVPeHV6Bqj5w00jhH9c4ex");
    }
}
