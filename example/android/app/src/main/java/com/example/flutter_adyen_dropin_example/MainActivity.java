package com.example.flutter_adyen_dropin_example;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.flutter_adyen_dropin.AdyenSetup;

import io.flutter.embedding.android.FlutterFragmentActivity;

public class MainActivity extends FlutterFragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdyenSetup.setActivity(this);
        AdyenSetup.setLauncherActivity(this);
    }
}
