package com.example.wub_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.internal.ui.AutocompleteImplFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private SupportMapFragment mapFragment;
    private Button logout,callmove,receipt,call_phone;
    private LatLng worklocation;
    private boolean requestBol=false;
    private Marker workmarker;
    private LinearLayout mDriverInfo;
    private ImageView mDriverProfileImage;
    private RadioGroup mRadioGroup;
    private String requestServices;
    private TextView mDriverName,mDriverPhone,mDriverTypeOfWork,mCost,mcostWithoutDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        else{
            mapFragment.getMapAsync(this);
        }

        logout=findViewById(R.id.logout);
        callmove=findViewById(R.id.callmove);
        call_phone=findViewById(R.id.call_btn);
        receipt=findViewById(R.id.receipt_btn);

        mRadioGroup = findViewById(R.id.radiogroup);
        mRadioGroup.check(R.id.homeshift);

        mDriverInfo=findViewById(R.id.driverinfo);
        mDriverProfileImage=findViewById(R.id.driverProfileImage);
        mDriverName=findViewById(R.id.driverName);
        mDriverPhone=findViewById(R.id.driverPhone);
        mDriverTypeOfWork=findViewById(R.id.driverTypeofwork);
        mCost=findViewById(R.id.temp_cost);
        mcostWithoutDis=findViewById(R.id.temp_costWithDis);

        final String cost=getIntent().getStringExtra("keycost");
        final String costWithoutDis=getIntent().getStringExtra("keytotal");

        mCost.setText(cost);
        mcostWithoutDis.setText(costWithoutDis);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(CustomerMapActivity.this,ChosseActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        callmove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestBol){
                    //when request is cancel
                    //remove request from database
                    requestBol=false;
                    geoQuery.removeAllListeners();
                    driverLocationRef.removeEventListener(driverLocationRefListener);

                    if(driverFoundId !=null){
                        //for remove the customer request
                        DatabaseReference driverRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId).child("CustomerRequestId");
                        driverRef.removeValue();
                        driverFoundId=null;
                    }
                    driverFound=false;
                    radius=1;

                    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("CustomerRequest");
                    GeoFire geoFire=new GeoFire(ref);
                    geoFire.removeLocation(userId);

                    //remove work marker
                    if(workmarker!=null){
                        workmarker.remove();
                    }
                    callmove.setText("Call Move");

                    mDriverInfo.setVisibility(View.GONE);
                    mDriverName.setText("");
                    mDriverPhone.setText("");
                    mDriverTypeOfWork.setText("");
                    mDriverProfileImage.setImageResource(R.drawable.common_full_open_on_phone);
                }
                else{
                    int selectId=mRadioGroup.getCheckedRadioButtonId();

                    final RadioButton radioButton=findViewById(selectId);

                    if(radioButton.getText()==null){
                        return;
                    }

                    requestServices=radioButton.getText().toString();
                    requestBol=true;
                    String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("CustomerRequest");
                    GeoFire geoFire=new GeoFire(ref);
                    geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                    //create mark
                    worklocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    workmarker=mMap.addMarker(new MarkerOptions().position(worklocation).title("Work is here"));

                    callmove.setText("Getting your driver...");

                    getClosestDriver();
                }
            }
        });

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String driverNumber=mDriverPhone.getText().toString();
                String customerCost=mCost.getText().toString();
                String customerCostWithoutDis=mcostWithoutDis.getText().toString();

                Intent intent=new Intent(CustomerMapActivity.this,ReceiptActivity.class);
                intent.putExtra("keydrivernumber",driverNumber);
                intent.putExtra("keycustomercost",customerCost);
                intent.putExtra("keycustomercostwithoutdis",customerCostWithoutDis);
                startActivity(intent);
                finish();
            }
        });

        call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ mDriverPhone.getText().toString()));
                startActivity(intent);
            }
        });

    }

    private  int radius=1;
    private Boolean driverFound=false;
    private String driverFoundId;
    GeoQuery geoQuery;

    private void getClosestDriver() {
        DatabaseReference driverloaction=FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        GeoFire geoFire=new GeoFire(driverloaction);
        //center of radius
        geoQuery=geoFire.queryAtLocation(new GeoLocation(worklocation.latitude,worklocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //this function will called when a driver come in a radius
                if (!driverFound && requestBol) {
                    DatabaseReference mCustomerDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);

                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                    Map<String, Object> driverMap=(Map<String, Object>) dataSnapshot.getValue();
                                    if(driverFound){
                                        return;
                                    }

                                    if(driverMap.get("Service").equals(requestServices)){
                                        driverFound=true;
                                        driverFoundId=dataSnapshot.getKey();

                                        //making a child in users
                                        DatabaseReference driverRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId);
                                        String customerId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        HashMap map=new HashMap();
                                        map.put("CustomerRequestId",customerId);
                                        driverRef.updateChildren(map);

                                        getDriverLocation();
                                        //getting driver information
                                        getDriverInfo();
                                        callmove.setText("Looking for driver location");
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //find a driver by this function
                if(!driverFound){
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getDriverInfo() {
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundId);
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map=(Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!=null){
                       mDriverName.setText(map.get("Name").toString());
                    }

                    if(map.get("Phone")!=null){
                        mDriverPhone.setText(map.get("Phone").toString());
                    }

                    if(map.get("TypeOfWork")!=null){
                        mDriverTypeOfWork.setText(map.get("TypeOfWork").toString());
                    }

                    if(map.get("profileImageUrl")!=null){
                        Picasso.get().load(map.get("profileImageUrl").toString()).into(mDriverProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation() {
        //current updated location turn into driver working
        driverLocationRef=FirebaseDatabase.getInstance().getReference().child("DriverWorking").child(driverFoundId).child("l");
        driverLocationRefListener=driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map=(List<Object>)dataSnapshot.getValue();
                    double locationLat=0;
                    double locationLong=0;
                    callmove.setText("Driver Found");

                    if(map.get(0) !=null){
                        locationLat=Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) !=null){
                        locationLong=Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng=new LatLng(locationLat,locationLong);

                    //creating marker
                    if(mDriverMarker!=null){
                        mDriverMarker.remove();
                    }
                    Location loc1=new Location("");
                    loc1.setLatitude(worklocation.latitude);
                    loc1.setLongitude(worklocation.longitude);

                    Location loc2=new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance=loc1.distanceTo(loc2);

                    //notify when driver is near
                    if(distance<100){
                        callmove.setText("Driver is near:" + String.valueOf(distance));
                    }
                    else{
                        callmove.setText("Driver Found:" + String.valueOf(distance));
                    }
                    //notify when driver is near

                    mDriverMarker=mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();
        //adding my location
        mMap.setMyLocationEnabled(true);

    }

    protected synchronized void buildGoogleApiClient(){
        //set the google api client value
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //getting Updated location
        if(getApplicationContext()!=null){
            mLastLocation=location;

            LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

            //change camera view

        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));*/
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    final int LOCATION_REQUEST_CODE=1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                }
                else{
                    Toast.makeText(getApplicationContext(),"plz provide the permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
