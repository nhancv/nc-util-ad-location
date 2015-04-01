package cvnhan.android.androidlocation;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleMapSingleton.getInstance(getApplicationContext()).initilizeMap(getFragmentManager().findFragmentById(R.id.map));
    }
}
