package cvnhan.android.androidlocation;

import android.app.Fragment;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

/**
 * Created by Administrator on 01-Apr-15.
 */
public class GoogleMapSingleton {
    private static GoogleMapSingleton instance;
    // Google Map
    private GoogleMap googleMap;
    private Context context;

    private GoogleMapSingleton(Context _context){
        this.context=_context;
    }
    public static GoogleMapSingleton getInstance(Context _context){
        if(instance==null){
            instance=new GoogleMapSingleton(_context);
        }
        return instance;
    }
    public GoogleMap getGoogleMap(){
        return googleMap;
    }
    /*
     * function to load map. If map is not created it will create it for you
     * */
    public void initilizeMap(Fragment fragment) {
        if (googleMap == null) {
            googleMap = ((MapFragment) fragment).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(context,
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }else{
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(false);
            }
        }
    }




}
