package cvnhan.android.androidlocation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleMapSingleton.onCreate(getApplicationContext(), this, getFragmentManager().findFragmentById(R.id.map));
        ((Button)findViewById(R.id.myLocBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleMapSingleton.getInstance(getApplicationContext()).displayLocation();
            }
        });

        ((Button)findViewById(R.id.updateLocBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleMapSingleton.getInstance(getApplicationContext()).togglePeriodicLocationUpdates((Button)findViewById(R.id.updateLocBtn));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleMapSingleton.onStart(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleMapSingleton.onResume(getApplicationContext(),this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GoogleMapSingleton.onPause(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleMapSingleton.onStop(getApplicationContext());
    }
}
