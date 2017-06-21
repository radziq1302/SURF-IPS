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


//import org.boofcv.android.DemoVideoDisplayActivity;

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

public class pokazWzor extends DemoVideoDisplayActivity {

    ProgressBar progressBar;

    private Handler handler;
    Bitmap afterProcess;
    ImageView wzor;
    ImageView imageAfter;
    Bitmap wzorcowy;
    EditText abc;
    List<BrightFeature> brajt;
    List<ScalePoint> scp;



    ImageReader mImageReader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokaz_wzor);
        wzor=(ImageView) findViewById(R.id.wzorzec);
        wzorcowy = BitmapFactory.decodeResource(getResources(),R.drawable.stones);
        GrayF32 do_zmian = ConvertBitmap.bitmapToGray(wzorcowy, (GrayF32)null,null);
        abc = (EditText) findViewById(R.id.textView);
        imageAfter = (ImageView)findViewById(R.id.imageView4);
        handler = new Handler();
        //StratBackgroundProcess();

        GrayF32 wzor1 = new GrayF32(512,512);
        //ConvertImage.convert();
        //ImageView wzor2 = ImageType.single(GrayF32.class);
    }

    public Bitmap processingBitmap(Bitmap src){
        GrayF32 img = ConvertBitmap.bitmapToGray(src, (GrayF32)null,null);
        Bitmap dest = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(), src.getConfig());
        DetectDescribePoint<GrayF32, BrightFeature> surf = FactoryDetectDescribe.surfStable(new ConfigFastHessian(0, 2, 20, 2, 9, 4,4), null, null, GrayF32.class);
        surf.detect(img);
        //zamiast this.surfAlg(img);

        dest = ConvertBitmap.grayToBitmap(img, Bitmap.Config.ARGB_8888);
        int bmWidth = src.getWidth();
        int bmHeight = src.getHeight();

        return dest;
    }


    public <II extends ImageGray> void surfAlg(GrayF32 src) {
        //GrayF32 img = ConvertBitmap.bitmapToGray(src, (GrayF32)null,null);
        GrayF32 img = src;

        //deklaracja typu
        Class<II> integralType = GIntegralImageOps.getIntegralType(GrayF32.class);
        NonMaxSuppression extractor =
                FactoryFeatureExtractor.nonmax(new ConfigExtract(2, 0, 5, true));
        FastHessianFeatureDetector<II> detector =
                new FastHessianFeatureDetector<>(extractor, 40, 2, 9, 3, 2, 6); //200, 2, 9, 4, 4, 6);
        // estimate orientation
        OrientationIntegral<II> orientation =
                FactoryOrientationAlgs.sliding_ii(null, integralType);
        DescribePointSurf<II> descriptor = FactoryDescribePointAlgs.<II>surfStability(null,integralType);

        II integral = GeneralizedImageOps.createSingleBand(integralType,img.width,img.height);
        GIntegralImageOps.transform(img, integral);
        // wykrywanie znacznikow za pomcoa hesianu
        detector.detect(integral);
        // podanie obrazu do operacji
        orientation.setImage(integral);
        descriptor.setImage(integral);
        List<ScalePoint> points = detector.getFoundPoints();

        List<BrightFeature> descriptions = new ArrayList<>();

        for( ScalePoint p : points ) {
            // szacowanie orientacji
            orientation.setObjectRadius( p.scale* BoofDefaults.SURF_SCALE_TO_RADIUS);
            double angle = orientation.compute(p.x,p.y);

            // wydobycie znacnzikow SURF
            BrightFeature desc = descriptor.createDescription();
            descriptor.describe(p.x,p.y,angle,p.scale,desc);

            // zapis
            descriptions.add(desc);

            Log.d("liczba_pkt", "value" + desc.toString());

        }
        this.brajt = descriptions;
        this.scp = points;
        //abc.setText("dsdada");//+points.size());
        Log.v("liczba_pkt", "value xd" + points.size());


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




