package com.example.mwcproject.fragments.Map;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mwcproject.R;
import com.example.mwcproject.fragments.LocationPopup.LocationPopupFragment;
import com.example.mwcproject.requests.RequestsHandler;
import com.example.mwcproject.services.Localisation.LocationService;
import com.example.mwcproject.utils.LocationMarker;
import com.example.mwcproject.utils.LocationUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapMarkers  {
    private static final int START_ZOOM = 15;
    public static MapMarkers instance;
    private final Context context;
    private final GoogleMap mMap;
    private final FragmentManager fragmentManager;
    boolean isBound;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            isBound = true;
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            LocationService locationService = binder.getService();
            locationService.setLocalisationUpdateListener(new LocationService.LocationUpdateListener() {
                @Override
                public void onLocationChanged(Location location) {
                    onLocationChange(location);
                }
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) { isBound = false; }
    };


    private boolean isRunnableRunning = false;
    public MapMarkers(GoogleMap map, Context context, FragmentManager fragmentManager) {
        this.mMap = map;
        this.context = context;
        this.fragmentManager = fragmentManager;
        Intent intent = new Intent(context, LocationService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        instance = this;
    }
    LatLng userLocation;
    public void onLocationChange(Location location) {
        userLocation = LocationUtils.locationToLatLng(location);


        if (!isRunnableRunning) {
            isRunnableRunning = true;
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    updateMarkers();
                    // Repeat this runnable code block again every 30 seconds
                    handler.postDelayed(this, 5000);
                }
            };
            handler.post(runnableCode);
        }
    }

    private final Handler handler = new Handler();
    public static void updateMarkers() { instance.getLocations(instance.userLocation);}

    private void fetchLocations(LatLng userPosition) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                final JSONObject result = RequestsHandler.getLocationList(userPosition, LocationUtils.GetRange(), context);
                handler.post(() -> {
                    try {
                        setPointsOnMap(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> System.out.println("Network request failed: " + e.getMessage()));
            }
        });
    }

    private void getLocations(LatLng userPosition) {
        if (userPosition != null) {
            fetchLocations(userPosition);
        } else {
            System.out.println("Null position");
        }
    }

    private void setPointsOnMap(JSONObject res) throws JSONException {
        mMap.clear();
        JSONArray data = res.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject postData = data.getJSONObject(i);
            JSONObject location = postData.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            double latitude = coordinates.getDouble(0);
            double longitude = coordinates.getDouble(1);
            LocationMarker locationMarker = new LocationMarker();
            locationMarker.position(new LatLng(longitude, latitude));
            Marker m = mMap.addMarker(locationMarker);
            assert m != null;
        }

        mMap.setOnMarkerClickListener(marker -> {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getMakerLocationsWithOffset(marker.getPosition()), START_ZOOM));

            Fragment existingFragment = fragmentManager.findFragmentById(R.id.fragment_location_popup);
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out);

            if (existingFragment != null) {
                transaction.remove(existingFragment);
            }

            Bundle args = new Bundle();
            args.putDouble("Lng", marker.getPosition().longitude);
            args.putDouble("Lat", marker.getPosition().latitude);

            transaction.replace(R.id.fragment_location_popup, LocationPopupFragment.class, args);

            transaction.addToBackStack("Position photo");

            transaction.commit();

            return true;
        });
    }


    private static final float Y_OFFSET_MARKER = 0.006f;

    private LatLng getMakerLocationsWithOffset(LatLng markerLocation) {
        return  new LatLng(markerLocation.latitude - Y_OFFSET_MARKER, markerLocation.longitude);
    }


}
