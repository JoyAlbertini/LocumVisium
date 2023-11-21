package com.example.mwcproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.mwcproject.databinding.ActivityMainBinding;
import com.example.mwcproject.fragments.maps.DownButtons.DownButtonFragment;
import com.example.mwcproject.fragments.maps.maps.MapsFragment;
import com.example.mwcproject.services.LocationService;

public class MainActivity extends AppCompatActivity {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;


    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestLocationPermissions();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_map, MapsFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null) // if you want to add it to the back stack
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_buttons, DownButtonFragment.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null) // if you want to add it to the back stack
                    .commit();
        }
    }



    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                //Todo: Handle the case where the user denies the permission
            }
        }
    }

    private void startLocationService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }

}