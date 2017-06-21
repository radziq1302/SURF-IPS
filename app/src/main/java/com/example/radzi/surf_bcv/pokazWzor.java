package com.example.radzi.surf_bcv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.radzi.surf_bcv.boofcv_klasy.DemoVideoDisplayActivity;

import java.util.ArrayList;
import java.util.List;

import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.orientation.OrientationIntegral;
import boofcv.alg.feature.describe.DescribePointSurf;
import boofcv.alg.feature.detect.interest.FastHessianFeatureDetector;
import boofcv.alg.transform.ii.GIntegralImageOps;
import boofcv.android.ConvertBitmap;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.factory.feature.describe.FactoryDescribePointAlgs;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.orientation.FactoryOrientationAlgs;
import boofcv.struct.BoofDefaults;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;



/**
 * Created by radzi on 2017-06-07.
 */
// metoda wykonuje algorytm SURF

public class pokazWzor extends DemoVideoDisplayActivity {


    private Handler handler;
    ImageView wzor;
    ImageView imageAfter;
    Bitmap wzorcowy;
    EditText abc;
    List<BrightFeature> brajt;
    List<ScalePoint> scp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokaz_wzor);
        wzor=(ImageView) findViewById(R.id.wzorzec);
        wzorcowy = BitmapFactory.decodeResource(getResources(),R.drawable.stones);
        imageAfter = (ImageView)findViewById(R.id.imageView4);
        handler = new Handler();
    }


    public <II extends ImageGray> void surfAlg(GrayF32 src) {
        GrayF32 img = src;
        //deklaracja typu
        Class<II> integralType = GIntegralImageOps.getIntegralType(GrayF32.class);

        // ustawienie extractora nonMaxSuppression, ktory wykrywa lokalne minima i maksima intensywności dla kwadratowych subregionów.
        NonMaxSuppression extractor =
                FactoryFeatureExtractor.nonmax(new ConfigExtract(2, 0, 5, true));

        //ustawienie parametru dla hesianu
        FastHessianFeatureDetector<II> detector =
                new FastHessianFeatureDetector<>(extractor, 40, 2, 9, 3, 2, 6); //200, 2, 9, 4, 4, 6);
        // wykrycie orientacji
        OrientationIntegral<II> orientation =
                FactoryOrientationAlgs.sliding_ii(null, integralType);
        DescribePointSurf<II> descriptor = FactoryDescribePointAlgs.<II>surfStability(null,integralType);

        II integral = GeneralizedImageOps.createSingleBand(integralType,img.width,img.height);
        GIntegralImageOps.transform(img, integral);
        // wykrywanie znacznikow za pomoca hesianu
        detector.detect(integral);
        // podanie obrazu do operacji
        orientation.setImage(integral);
        descriptor.setImage(integral);
        //utworzenie list ScalePoint i BrightFeature
        List<ScalePoint> points = detector.getFoundPoints();

        List<BrightFeature> descriptions = new ArrayList<>();

        for( ScalePoint p : points ) {
            // szacowanie orientacji
            orientation.setObjectRadius( p.scale* BoofDefaults.SURF_SCALE_TO_RADIUS);
            double angle = orientation.compute(p.x,p.y);

            // wydobycie znacznikow SURF
            BrightFeature desc = descriptor.createDescription();
            descriptor.describe(p.x,p.y,angle,p.scale,desc);

            // zapis
            descriptions.add(desc);

            //Log.d("liczba_pkt", "value" + desc.toString());

        }
        // zapis wynikow
        this.brajt = descriptions;
        this.scp = points;
        //Log.v("liczba_pkt", "value xd" + points.size());


    }
/*    public void writeToFile(Context context) {
        ;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("dupson.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(this.brajt.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }*/
    }




