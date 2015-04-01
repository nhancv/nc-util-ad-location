package cvnhan.android.androidlocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cvnhan on 01-Apr-15.
 */
public class DetailModel extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmodel);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String detail = intent.getStringExtra("detail");

        ((TextView) findViewById(R.id.nameTxt)).setText(name);
        ((TextView) findViewById(R.id.detailTxt)).setText(detail);
        if (name.equals("Shop1")) {
            ((ImageView) findViewById(R.id.img)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shop1));
        } else if (name.equals("Shop2")) {
            ((ImageView) findViewById(R.id.img)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shop2));
        } else {
            ((ImageView) findViewById(R.id.img)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.shop3));
        }
    }
}
