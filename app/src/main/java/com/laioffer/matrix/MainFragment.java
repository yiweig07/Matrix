package com.laioffer.matrix;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements OnMapReadyCallback, ReportDialog.DialogCallBack {
    private MapView mapView;
    private View view;
    private GoogleMap googleMap;
    private LocationTracker locationTracker;
    private FloatingActionButton fabReport;
    private FloatingActionButton fabFocus;
    private ReportDialog dialog;
    private DatabaseReference database;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.event_map_view);
        fabReport = view.findViewById(R.id.fab);
        fabReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(null, null);
            }
        });

        fabFocus = view.findViewById(R.id.fab_focus);
        fabFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMapAsync(MainFragment.this);
            }
        });

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());

        this.googleMap = googleMap;
        this.googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));

//        double latitude = 30.5;
//        double longitude = 114.3;
//        //Create marker on google map
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("武汉加油");
//
//        //Change marker Icon on google map
//        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        //Add marker to google map
//        googleMap.addMarker(marker);
//
//        //Set up camera configuration, set camera to lat and lng, and set Zoom to 12
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude,longitude))
//                .zoom(12)
//                .build();
//        //Animate the zoom process
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        locationTracker = new LocationTracker(getActivity());
        locationTracker.getLocation();

        LatLng latLng = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng) // Sets the center of the map
                .zoom(16)
                .bearing(90) //Sets the orientation of the camera to east
                .tilt(30)  // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        MarkerOptions marker = new MarkerOptions().position(latLng).title("You");
        //Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.boy));
        //adding marker
        googleMap.addMarker(marker);

    }

    //    private void showDialog(String label, String prefillText) {
//        dialog = new ReportDialog(getContext());
//        dialog.show();
//    }
    private void showDialog(String label, String prefillText) {
        int cx = (int) (fabReport.getX() + (fabReport.getWidth() / 2)); // center of report floating action button
        int cy = (int) (fabReport.getY()) + fabReport.getHeight() + 56;
        dialog = ReportDialog.newInstance(getContext(), cx, cy);
        dialog.setDialogCallBack(this);
        dialog.show();
    }

    private String uploadEvent(String user_id, String editString, String event_type) {
        TrafficEvent event = new TrafficEvent();
        event.setEvent_type(event_type);
        event.setEvent_description(editString);
        event.setEvent_reporter_id(user_id);
        event.setEvent_timestamp(System.currentTimeMillis());
        event.setEvent_latitude(locationTracker.getLatitude());
        event.setEvent_longitude(locationTracker.getLongitude());
        event.setEvent_like_number(0);
        event.setEvent_comment_number(0);

        String key = database.child("events").push().getKey();
        event.setId(key);
        database.child("events").child(key).setValue(event, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast toast = Toast.makeText(getContext(),
                            "The event is failed, please check your network status", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                } else {
                    Toast toast = Toast.makeText(getContext(),
                            "The event is reported", Toast.LENGTH_SHORT);
                    toast.show();
                    //TODO:update map fragment
                }
            }
        });
        return key;
    }

    @Override
    public void onSubmit(String editString, String event_type) {
        String key = uploadEvent(Config.username, editString, event_type);
    }

    @Override
    public void startCamera() {

    }
}
