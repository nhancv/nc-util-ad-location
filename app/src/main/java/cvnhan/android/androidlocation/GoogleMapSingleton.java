package cvnhan.android.androidlocation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

//import android.location.Location;

/**
 * Created by Administrator on 01-Apr-15.
 */
public class GoogleMapSingleton {
    private static final String TAG = GoogleMapSingleton.class.getSimpleName();
    private static GoogleMapSingleton instance;
    private GoogleMap googleMap;
    private Context context;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private GoogleMapSingleton(Context _context) {
        this.context = _context;
    }

    public static GoogleMapSingleton getInstance(Context _context) {
        if (instance == null) {
            instance = new GoogleMapSingleton(_context);
        }
        return instance;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public boolean getRequestUpdateLoc() {
        return mRequestingLocationUpdates;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    public void initilizeMap(Fragment fragment) {
        if (googleMap == null) {
            googleMap = ((MapFragment) fragment).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(context,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            } else {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);

            }
        }
    }
    //////////////////////////////////////////////////////////////////////////

    /**
     * Method to display the location on UI
     */
    public void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.e(TAG, latitude + ", " + longitude);

        } else {
            Log.e(TAG, "(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(onConnectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(context,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                    + connectionResult.getErrorCode());
        }
    };
    GoogleApiClient.ConnectionCallbacks onConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            // Once connected with google api, get the location
            displayLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    };

    //////////////////////////////////////////////////////////////////////////
    //Location update
    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, locationListener);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // Assign the new location
            mLastLocation = location;
            Toast.makeText(context, "Location changed!",
                    Toast.LENGTH_SHORT).show();
            // Displaying the new location on UI
            displayLocation();
            updateLocation(location);
        }
    };

    private void updateLocation(Location location) {
        LatLng latlong = new LatLng(location.getLatitude(),
                location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                latlong, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    public void togglePeriodicLocationUpdates(Button UpdateLocBtn) {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            UpdateLocBtn.setText("Stop Update Loc");
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");
        } else {
            // Changing the button text
            UpdateLocBtn.setText("Start Update Loc");
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    //////////////////////////////////////////////////////////////
    //Main Activity
    /**
     * call in onStart func of MainActivity
     */
    public static void onCreate(Context context, Activity activity, Fragment fragment) {
        GoogleMapSingleton instance = GoogleMapSingleton.getInstance(context);
        // First we need to check availability of play services
        if (instance.checkPlayServices(activity)) {
            instance.initilizeMap(fragment);
            // Building the GoogleApi client
            instance.buildGoogleApiClient();
            instance.createLocationRequest();
        }
    }

    public static void onStart(Context context) {
        if (GoogleMapSingleton.getInstance(context).getGoogleApiClient() != null) {
            GoogleMapSingleton.getInstance(context).getGoogleApiClient().connect();
        }
    }

    public static void onResume(Context context, Activity activity) {
        GoogleMapSingleton instance = GoogleMapSingleton.getInstance(context);
        instance.checkPlayServices(activity);
        // Resuming the periodic location updates
        if (instance.getGoogleApiClient().isConnected() && instance.getRequestUpdateLoc()) {
            instance.startLocationUpdates();
        }
    }

    public static void onPause(Context context) {
        GoogleMapSingleton.getInstance(context).stopLocationUpdates();
    }

    public static void onStop(Context context) {
        GoogleApiClient instance = GoogleMapSingleton.getInstance(context).getGoogleApiClient();
        if (instance.isConnected()) {
            instance.disconnect();
        }
    }


}
