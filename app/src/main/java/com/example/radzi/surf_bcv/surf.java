<<<<<<< HEAD
package com.example.radzi.surf_bcv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import boofcv.android.ConvertBitmap;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;

/**
 * Created by radzi on 2017-06-06.
 */
// klasa która odpowiada za obsługę widoku z kamery i wywoływanie funkcji przetwarzania
public class surf extends VideoDisplayActivity
{
    //deklaracja zmiennych, dwóch par list - jedna dla wzorca, druga dla obrazu rzeczywistego
    List<BrightFeature> lista_live;
    showSurf surfik;
    List<BrightFeature> lista_wzorca;
    static List<ScalePoint> lista_wzorca_scale;
    public Bitmap wzorcowy;
    pokazWzor wzorzec= new pokazWzor ();
    GrayF32 img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // wczytanie obrazka jako bitmapy i konwersja do GrayF32, by uczynić go możliwym do przetwarzania przez algorytm SURF
        wzorcowy = BitmapFactory.decodeResource(getResources(), R.drawable.stonesmin);
        img = ConvertBitmap.bitmapToGray(wzorcowy, (GrayF32)null,null);
        /*Log.v("kamienieh",""+img.getHeight());
        Log.v("kamieniew",""+img.getWidth());
        nieistotne Logi*/
        //wykonanie algorytmu Surf na obrazie img
        this.wzorzec.surfAlg(img);
        //wypełnienie list BrightFeature i ScalePoint danymi, które dostarcza algorytm - dostęp poprzez odwołąnie się do pola "brajt' i "scp" zawierających wlasnie odpowiednie listy
        lista_wzorca = new ArrayList<>();
        lista_wzorca = this.wzorzec.brajt;
        lista_wzorca_scale = new ArrayList<>();
        lista_wzorca_scale = this.wzorzec.scp;
        lista_live = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // wywołanie metody przetwarzania dla obiektu klasy showSurf
        setProcessing(surfik = new showSurf());
        //dla obiektu tejże klasy wywołanie metody abc, ktora pozwoli na dalsze przetwarzanie danych z listy wzroca.
        surfik.abc(lista_wzorca, lista_wzorca_scale, false);
        // wykorzystanie licznika FPSów dostępnego w bibliotece Boof
		setShowFPS(true);
    }
/////////////////////////////////
    ////////////////////////////
    // dalsza część odpowiada za obsługę kamery i jest szkieletem / programem zaczerpniętym z dokumentacji biblioteki Boof
    @Override
    protected Camera openConfigureCamera(Camera.CameraInfo cameraInfo )
    {
        Camera mCamera = selectAndOpenCamera(cameraInfo);
        Camera.Parameters param = mCamera.getParameters();

        // Select the preview size closest to 320x240
        // Smaller images are recommended because some computer vision operations are very expensive
        List<Camera.Size> sizes = param.getSupportedPreviewSizes();
        Camera.Size s = sizes.get(closest(sizes,240,160));
        param.setPreviewSize(s.width,s.height);
        mCamera.setParameters(param);

        return mCamera;
    }

    /**
     * Step through the camera list and select a camera.  It is also possible that there is no camera.
     * The camera hardware requirement in AndroidManifest.xml was turned off so that devices with just
     * a front facing camera can be found.  Newer SDK's handle this in a more sane way, but with older devices
     * you need this work around.
     */
    private Camera selectAndOpenCamera(Camera.CameraInfo info) {
        int numberOfCameras = Camera.getNumberOfCameras();

        int selected = -1;

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, info);

            if( info.facing == Camera.CameraInfo.CAMERA_FACING_BACK ) {
                selected = i;
                break;
            } else {
                // default to a front facing camera if a back facing one can't be found
                selected = i;
            }
        }

        if( selected == -1 ) {
            dialogNoCamera();
            return null; // won't ever be called
        } else {
            return Camera.open(selected);
        }
    }

    /**
     * Gracefully handle the situation where a camera could not be found
     */
    private void dialogNoCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device has no cameras!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Goes through the size list and selects the one which is the closest specified size
     */
    public static int closest( List<Camera.Size> sizes , int width , int height ) {
        int best = -1;
        int bestScore = Integer.MAX_VALUE;

        for( int i = 0; i < sizes.size(); i++ ) {
            Camera.Size s = sizes.get(i);

            int dx = s.width-width;
            int dy = s.height-height;

            int score = dx*dx + dy*dy;
            if( score < bestScore ) {
                best = i;
                bestScore = score;
            }
        }

        return best;
    }

=======
package com.example.radzi.surf_bcv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import boofcv.android.ConvertBitmap;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;

/**
 * Created by radzi on 2017-06-06.
 */
// klasa która odpowiada za obsługę widoku z kamery i wywoływanie funkcji przetwarzania
public class surf extends VideoDisplayActivity
{
    //deklaracja zmiennych, dwóch par list - jedna dla wzorca, druga dla obrazu rzeczywistego
    List<BrightFeature> lista_live;
    showSurf surfik;
    List<BrightFeature> lista_wzorca;
    static List<ScalePoint> lista_wzorca_scale;
    public Bitmap wzorcowy;
    pokazWzor wzorzec= new pokazWzor ();
    GrayF32 img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // wczytanie obrazka jako bitmapy i konwersja do GrayF32, by uczynić go możliwym do przetwarzania przez algorytm SURF
        wzorcowy = BitmapFactory.decodeResource(getResources(), R.drawable.stonesmin);
        img = ConvertBitmap.bitmapToGray(wzorcowy, (GrayF32)null,null);
        /*Log.v("kamienieh",""+img.getHeight());
        Log.v("kamieniew",""+img.getWidth());
        nieistotne Logi*/
        //wykonanie algorytmu Surf na obrazie img
        this.wzorzec.surfAlg(img);
        //wypełnienie list BrightFeature i ScalePoint danymi, które dostarcza algorytm - dostęp poprzez odwołąnie się do pola "brajt' i "scp" zawierających wlasnie odpowiednie listy
        lista_wzorca = new ArrayList<>();
        lista_wzorca = this.wzorzec.brajt;
        lista_wzorca_scale = new ArrayList<>();
        lista_wzorca_scale = this.wzorzec.scp;
        lista_live = new ArrayList<>();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // wywołanie metody przetwarzania dla obiektu klasy showSurf
        setProcessing(surfik = new showSurf());
        //dla obiektu tejże klasy wywołanie metody abc, ktora pozwoli na dalsze przetwarzanie danych z listy wzroca.
        surfik.abc(lista_wzorca, lista_wzorca_scale, false);
        // wykorzystanie licznika FPSów dostępnego w bibliotece Boof
		setShowFPS(true);
    }
/////////////////////////////////
    // dalsza część odpowiada za obsługę kamery i jest szkieletem / programem zaczerpniętym z dokumentacji biblioteki Boof
    @Override
    protected Camera openConfigureCamera(Camera.CameraInfo cameraInfo )
    {
        Camera mCamera = selectAndOpenCamera(cameraInfo);
        Camera.Parameters param = mCamera.getParameters();

        // Select the preview size closest to 320x240
        // Smaller images are recommended because some computer vision operations are very expensive
        List<Camera.Size> sizes = param.getSupportedPreviewSizes();
        Camera.Size s = sizes.get(closest(sizes,240,160));
        param.setPreviewSize(s.width,s.height);
        mCamera.setParameters(param);

        return mCamera;
    }

    /**
     * Step through the camera list and select a camera.  It is also possible that there is no camera.
     * The camera hardware requirement in AndroidManifest.xml was turned off so that devices with just
     * a front facing camera can be found.  Newer SDK's handle this in a more sane way, but with older devices
     * you need this work around.
     */
    private Camera selectAndOpenCamera(Camera.CameraInfo info) {
        int numberOfCameras = Camera.getNumberOfCameras();

        int selected = -1;

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, info);

            if( info.facing == Camera.CameraInfo.CAMERA_FACING_BACK ) {
                selected = i;
                break;
            } else {
                // default to a front facing camera if a back facing one can't be found
                selected = i;
            }
        }

        if( selected == -1 ) {
            dialogNoCamera();
            return null; // won't ever be called
        } else {
            return Camera.open(selected);
        }
    }

    /**
     * Gracefully handle the situation where a camera could not be found
     */
    private void dialogNoCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device has no cameras!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Goes through the size list and selects the one which is the closest specified size
     */
    public static int closest( List<Camera.Size> sizes , int width , int height ) {
        int best = -1;
        int bestScore = Integer.MAX_VALUE;

        for( int i = 0; i < sizes.size(); i++ ) {
            Camera.Size s = sizes.get(i);

            int dx = s.width-width;
            int dy = s.height-height;

            int score = dx*dx + dy*dy;
            if( score < bestScore ) {
                best = i;
                bestScore = score;
            }
        }

        return best;
    }
/*    public List<BrightFeature> tePunkty (List<BrightFeature> a, List<BrightFeature> b) {
        List<BrightFeature> c = new ArrayList<>();
        for( BrightFeature p : a ) {
            for( BrightFeature t : b ) {
                if (a==b) {
                    c.add(t);
                }

            }
        }
        return c;
    }*/
>>>>>>> origin/master
}