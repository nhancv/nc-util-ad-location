package cvnhan.android.androidlocation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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

    private Marker marker;

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
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.setMyLocationEnabled(false);
                googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener());
            }
        }
    }
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String name=marker.getTitle();
                String detail="";

                if(name.equals("Shop1")){
                    detail=Model.getListSamples().get(0).details;
                }else if(name.equals("Shop2")){
                    detail=Model.getListSamples().get(1).details;
                }else if(name.equals("Shop3")){
                    detail=Model.getListSamples().get(2).details;
                }

                Intent nextScreen = new Intent(context,
                        DetailModel.class);
                nextScreen.putExtra("name", name);
                nextScreen.putExtra("detail", detail);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(nextScreen);
            }

        };

    }
    //////////////////////////////////////////////////////////////////////////

    /**
     * Method to display the location on UI
     */
    public void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            updateCameraLocation(mLastLocation);
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
            updateCameraLocation(location);
        }
    };

    private void updateCameraLocation(Location location) {
        LatLng latlong = new LatLng(location.getLatitude(),
                location.getLongitude());
        // Flat markers will rotate when the map is rotated,
        // and change perspective when the map is tilted.
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.direction_arrow))
                    .position(latlong)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .rotation(245));
        }
        animateMarker(marker, location);
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latlong)
                .zoom(13)
                .bearing(90)
                .build();
        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);

                double lng = t * location.getLongitude() + (1 - t)
                        * startLatLng.longitude;
                double lat = t * location.getLatitude() + (1 - t)
                        * startLatLng.latitude;

                float rotation = (float) (t * location.getBearing() + (1 - t)
                        * startRotation);

                marker.setPosition(new LatLng(lat, lng));
                marker.setRotation(rotation);

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
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

    public void addMarkerModel(Context context, ArrayList<Model> models) {
        int density = (int)context.getResources().getDisplayMetrics().density;
        for (int i = 0; i < models.size(); i++) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(models.get(i).location).
                    title("Shop" + (i + 1)).snippet(models.get(i).location.toString()));
            Bitmap icon=null;
            switch (i){
                case 0:
                    icon=Model.getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shop1),density*50, density*50);
                    break;
                case 1:
                    icon=Model.getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shop2),density*50, density*50);
                    break;
                default:
                    icon=Model.getResizedBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.shop3),density*50, density*50);
                    break;
            }
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(Model.CircleBitmap(icon)));
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
            instance.addMarkerModel(context,Model.getListSamples());
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
