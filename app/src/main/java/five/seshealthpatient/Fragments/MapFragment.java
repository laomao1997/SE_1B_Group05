package five.seshealthpatient.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import five.seshealthpatient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    /**
     * Hospitals in Sydney City
     */
    private static final LatLng ST_VINCENT = new LatLng(-33.8809265258, 151.2199616432);
    private static final LatLng PRINCE_ALFRED = new LatLng(-33.8891562498, 151.1829471588);
    private static final LatLng EAST_SYDNEY = new LatLng(-33.8737404548, 151.2159517407);
    private static final LatLng DAY_SURGERY = new LatLng(-33.8916744401, 151.1831671000);
    private static final LatLng BALMAIN = new LatLng(-33.8591682652,151.1818313599);
    private static final LatLng SYDNEY_DAY = new LatLng(-33.8669928858,151.2121128969);
    private static final LatLng EYE = new LatLng(-33.8684880273,151.2124809623);
    private static final LatLng SKIN = new LatLng(-33.8754229003,151.2158444524);

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    String GPS;
    private LocationManager lm;// Location management
    private FusedLocationProviderClient mFusedLocationClient;



    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Facilities Map");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this, mView);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }



    private void initMap() {
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        getLocationPermission();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // googleMap.addMarker(new MarkerOptions().position(new LatLng(-33.8839197, 151.1991928)));
        moveCamera(new LatLng(-33.8839197, 151.1991928), DEFAULT_ZOOM);

        initGPS();
    }

    @OnClick(R.id.button_GPS)
    public void getFacilities() {
        String searchString = "clinic";

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        for(Address address : list) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(address.getLatitude(), address.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        }
        mGoogleMap.addMarker(new MarkerOptions()
                .position(ST_VINCENT)
                .title("St Vincent's Hospital Sydney")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(PRINCE_ALFRED)
                .title("Royal Prince Alfred Hospital")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(EAST_SYDNEY)
                .title("East Sydney ")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(DAY_SURGERY)
                .title("Sydney Day Surgery Prince Alfred")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(BALMAIN)
                .title("Baimain Hospital")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(SYDNEY_DAY)
                .title("Sydney Day Hospital")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(EYE)
                .title("Sydney Hospital and Sydney Eye Hospital")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(SKIN)
                .title("The Skin Hospital")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_facilities)));
    }



    private void moveCamera(LatLng latLng, float zoom){
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Request to get the location permission
     */
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(this.getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this.getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    public void initGPS() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        lm = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ok) // GPS service is OK
        {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // No permission
                Toast.makeText(getActivity(), "No GPS Permission", Toast.LENGTH_SHORT).show();

            } else {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    GPS = "["+location.getLatitude()+","+location.getLongitude()+"]";
                                    // currentLocation.setText("Current Location: " + GPS);
                                    double lat = location.getLatitude();
                                    double longi = location.getLongitude();
                                    moveCamera(new LatLng(lat, longi), DEFAULT_ZOOM);
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(getActivity(), "Please open the GPS service", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }

        mGoogleMap.setMyLocationEnabled(true);

        //getFacilities();
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }
}
