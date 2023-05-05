package com.example.tharinduranaweera.infinitecharge;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/*
* MapsActivity which shows the user a map with Google Maps API. the travelled path is acquired from the firebase which
* saved earlier itself. All the latitudes and longitudes are matched perfectly and showed every place visited using a map.
* So the data is much more meaningful when the user is able to see the places. (HCI)
*
* Security: All the longitudes and latitudes of the locations are stored in the firebase realtime database which
* is already secured from Google's content security policy.
* */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Declaring

    private GoogleMap mMap;

    List<Double> lats, lans;
    List<LatLng> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        lats = StatisticsActivity.lats;
        lans = StatisticsActivity.lngs;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Making of markers

        for (int i = 0; i < lans.size(); i++ ){
            locations.add(new LatLng(lats.get(i), lans.get(i)));
            mMap.addMarker(new MarkerOptions().position(locations.get(i)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locations.get(i)));
        }

        Location first = new Location("");
        first.setLatitude(locations.get(0).latitude);
        first.setLongitude(locations.get(0).longitude);

        Location last = new Location("");
        last.setLatitude(locations.get(locations.size() - 1).latitude);
        last.setLongitude(locations.get(locations.size() - 1).longitude);

        //Calculating the distace between first location and last

        System.out.println("Distance: " + first.distanceTo(last));
    }
}
