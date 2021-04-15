package com.library1.example.perkins.location23;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView myView;
    TextView myView2;

    private boolean canAccessLocation = false;
    static final int PERMS_REQ_CODE = 200;
    String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

    private LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myView = (TextView) findViewById(R.id.textViewAddress);
        myView2 = (TextView) findViewById(R.id.textViewCoords);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();

        stopListeningIfAllowed();
    }


    //only ask if dev is running 23 or higher
    private boolean startListeningIfAllowed(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
            if   ((checkSelfPermission(perms[0])== PackageManager.PERMISSION_GRANTED )&& (checkSelfPermission(perms[0])==PackageManager.PERMISSION_GRANTED))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        return true;
    }
    //only ask if dev is running 23 or higher
    private boolean stopListeningIfAllowed(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1){
            if   ((checkSelfPermission(perms[0])== PackageManager.PERMISSION_GRANTED )&& (checkSelfPermission(perms[0])==PackageManager.PERMISSION_GRANTED))
                if (locationManager != null && locationListener!= null)
                    locationManager.removeUpdates(locationListener);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        boolean loc_Fine_Accepted = false;
        boolean loc_Coarse_Accepted = false;

        switch (permsRequestCode) {
            case PERMS_REQ_CODE:
                loc_Fine_Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                loc_Coarse_Accepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (loc_Fine_Accepted==true && loc_Coarse_Accepted==true )
            //got em, setup for listen
            setupListener();
        else
            Toast.makeText(this,"YOU DONT HAVE PROPER PERMISSIONS",Toast.LENGTH_SHORT).show();
    }

     public void setupListener(){
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                location.getLatitude();
                location.getLongitude();

                String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
                MainActivity.this.myView.setText(myLocation);

                List<Address> geocodeMatches = MainActivity.this.getAddressFromCoordinates(location);
                if (geocodeMatches != null && !geocodeMatches.isEmpty())
                {
                    String Address1 = geocodeMatches.get(0).getAddressLine(0);
                    String Address2 = geocodeMatches.get(0).getAddressLine(1);
                    String State    = geocodeMatches.get(0).getAdminArea();
                    String Zipcode  = geocodeMatches.get(0).getPostalCode();
                    String Country  = geocodeMatches.get(0).getCountryName();

                    MainActivity.this.myView2.setText("MY CURRENT address " + Address1 +"\n" +Address2 + "\n");
                    //I make a log to see the results
                    Log.e("MY CURRENT address", Address1 +"\n" +Address2 + "\n");

                    //how to go the other way
                    getLocationCoordinatesFromAddress(Address1 + " " + Address2 + " ," + State + " " + Zipcode );
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        startListeningIfAllowed();
    }

    private  List<Address> getAddressFromCoordinates(Location location){

        List<Address> geocodeMatches = null;
        String Address1;
        String Address2;
        String State;
        String Zipcode;
        String Country;

        try {
            geocodeMatches =
                    new Geocoder(this).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!geocodeMatches.isEmpty())
        {
            Address1 = geocodeMatches.get(0).getAddressLine(0);
            Address2 = geocodeMatches.get(0).getAddressLine(1);
            State = geocodeMatches.get(0).getAdminArea();
            Zipcode = geocodeMatches.get(0).getPostalCode();
            Country = geocodeMatches.get(0).getCountryName();

            //I make a log to see the results
            Log.e("MY CURRENT address", Address1 +"\n" +Address2 + "\n");
        }
        return geocodeMatches;
    }

    private String getLocationCoordinatesFromAddress(String address){
        double latitude = 0.0;
        double longitude = 0.0;

        List<Address> geocodeMatches = null;

        try {
            geocodeMatches =
                    new Geocoder(this).getFromLocationName(
                            address, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!geocodeMatches.isEmpty())
        {
            latitude = geocodeMatches.get(0).getLatitude();
            longitude = geocodeMatches.get(0).getLongitude();
        }

        String myString = "Latitude = " + Double.toString(latitude) + " longitude = " +  Double.toString(longitude);
        return myString;
    }

    public void doCoordinatesFromAddress(View view) {

        requestPermissions(perms, PERMS_REQ_CODE);

        String address = myView.getText().toString();

        myView2.setText(getLocationCoordinatesFromAddress(address));
    }
}
