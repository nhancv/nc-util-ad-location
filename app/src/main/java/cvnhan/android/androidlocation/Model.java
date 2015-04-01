package cvnhan.android.androidlocation;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by cvnhan on 01-Apr-15.
 */
public class Model {
    LatLng location;
    String details;
    public Model(LatLng location, String details){
        this.location=location;
        this.details=details;
    }
    public static ArrayList<Model> getListSamples() {
        ArrayList<Model> arr = new ArrayList<>();
        arr.add(new Model(new LatLng(10.806266, 106.676378),"shop 1 is very good"));
        arr.add(new Model(new LatLng(10.801956, 106.676217), "Shop 1 is not bad"));
        arr.add(new Model(new LatLng(10.804496, 106.666904), "Shop 3 is so beautiful"));
        return arr;
    }
    public static Bitmap CircleBitmap( Bitmap bitmap)
    {
        Paint paint = new Paint();
        Bitmap mutableBitmap = Bitmap.createBitmap(bitmap);
        float w,h;
        w=mutableBitmap.getWidth();
        h=mutableBitmap.getHeight();
        Bitmap bitmapview = Bitmap.createBitmap((int)w, (int)h+30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapview);


        BitmapShader shader;
        shader = new BitmapShader(mutableBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        // init paint
        paint.setAntiAlias(true);
        paint.setShader(shader);
        int circleCenter = (int) (w / 2);
        canvas.drawCircle(circleCenter, circleCenter, w-20, paint);

        RectF rectF = new RectF(0, canvas.getHeight()/2, canvas.getWidth(),canvas.getHeight());
        canvas.drawArc (rectF, -90, 45, true, paint);
        return bitmapview;
    }
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;

    }
}
