package com.example.radzi.surf_bcv;

/**
 * Created by radzi on 2017-06-07.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import boofcv.android.VisualizeImageData;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;

import static java.lang.Math.sqrt;


////
//////


//public class showSurf extends VideoImageProcessing<GrayF32>
public class showSurf extends VideoImageProcessing<GrayF32>
{
    pokazWzor xdd = new pokazWzor ();
    List<BrightFeature> punkciki;
    public static List<BrightFeature> lista_wzorca;
    public static List<BrightFeature> lista_wzorca_mini;
    public static List<ScalePoint> lista_wzorca_scale;
    List<ScalePoint> scalepointy = new ArrayList<>();

    doObliczen odleglosci = new doObliczen();
    boolean stopuj = false;
    double funkcja_dystans = 0;

    public static boolean x = false;


    protected showSurf()
    {
        super(ImageType.single(GrayF32.class));

    }

    public void abc(List<BrightFeature> lista_wzorca,List<ScalePoint> lista_wzorca_scale, boolean x2){
        this.lista_wzorca = new ArrayList<>();
        this.lista_wzorca_mini = new ArrayList<>();
        this.lista_wzorca=lista_wzorca;
        this.lista_wzorca_mini.add(lista_wzorca.get(1));
        this.lista_wzorca_mini.add(lista_wzorca.get(2));
        this.lista_wzorca_scale = new ArrayList<>();
        this.lista_wzorca_scale=lista_wzorca_scale;
        x  = x2;
    }


    @Override
    protected void declareImages( int width , int height ) {
        // You must call the super or else it will crash horribly
        super.declareImages(width,height);
    }


    @Override
    protected void process(GrayF32 gray, Bitmap output, byte[] storage)
    {
        xdd.surfAlg(gray);
        punkciki = xdd.brajt;
         scalepointy = xdd.scp;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas( output);
        Paint paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setStyle(Paint.Style.STROKE);
        Paint paint3 = new Paint();
        paint3.setColor(Color.BLUE);
        paint3.setStyle(Paint.Style.STROKE);
        Paint paint4 = new Paint();
        paint4.setColor(Color.MAGENTA);
        paint4.setStyle(Paint.Style.STROKE);
        Paint paint5 = new Paint();
        paint5.setColor(Color.MAGENTA);
        paint5.setStyle(Paint.Style.STROKE);


        //Point luc = new Point(50,50);
        //canvas.drawPoint(50, 50, paint);



        VisualizeImageData.grayMagnitude(gray,255,output, storage);

        //rozmiar obrazka integral jest 2x wiekszy niz wczytanych kamieni;
        metoda();
        //scalepointy.get(odleglosci.num_obiekt.get(1)).getX();
        /*ScalePoint LU = wybierzPktLU(lista_wzorca_scale);
        ScalePoint LD = wybierzPktLD(lista_wzorca_scale);
        ScalePoint RU = wybierzPktRU(lista_wzorca_scale);
        ScalePoint RD = wybierzPktRD(lista_wzorca_scale);*/

        ScalePoint LU = wybierzPktLU(scalepointy);
        ScalePoint LD = wybierzPktLD(scalepointy);
        ScalePoint RU = wybierzPktRU(scalepointy);
        ScalePoint RD = wybierzPktRD(scalepointy);

        for (ScalePoint i: scalepointy) {

            canvas.drawCircle((float)i.getX(), (float)i.getY(),5 ,paint1);
        }
        if (scalepointy.size()>35)
            //canvas.drawRect(150,50,70,50, paint5);
            canvas.drawRect((float)LU.getX()+20,(float)LU.getY()+20,120,120,paint5);


        /*canvas.drawCircle((float)LU.getX()/2, (float)LU.getY()/2,20 ,paint1);
        canvas.drawCircle((float)LD.getX()/2, (float)LD.getY()/2,25 , paint2);
        canvas.drawCircle((float)RU.getX()/2, (float)RU.getY()/2,20 , paint3);
        canvas.drawCircle((float)RD.getX()/2, (float)RD.getY()/2,20 , paint4);*/
        //Log.v ("wspolrzedne zolty",""+(float)LD.getX()/2 + "i" + (float)LD.getY()/2);


        //odleglosci.num_wzor.clear();
    }
    public void metoda() {

//            lista_wzorca = pokazWzor.surfAlg();
            if (stopuj==false&&!(lista_wzorca.isEmpty() || punkciki.isEmpty() || lista_wzorca_scale.isEmpty())) {
                odleglosci = tePunkty(lista_wzorca, punkciki, odleglosci);
                //FancyInterestPointRender render = new FancyInterestPointRender();
                //render.addCircle(200,200,40);


                Log.v("tuna", "kaszanka");
            }
        Log.v("tuna", ""+x);
    }

    public doObliczen tePunkty (List<BrightFeature> a, List<BrightFeature> b, doObliczen dxd) {
        int licznik = 0;
        int wsk=0;

        for( BrightFeature p : a ) {
            int licznik1 = 0;
            double dst = 100;
            double dst_roboczy;
            dxd.num_wzor.add(licznik, licznik);

            for( BrightFeature t : b ) {
                dst_roboczy = odleglosc(p,t, funkcja_dystans);
                if (dst_roboczy<dst && dst_roboczy < 0.4) {
                    dst = dst_roboczy;
                    wsk = licznik1;
                }
                licznik1++;
            }
            dxd.num_obiekt.add(licznik, wsk);
            licznik1=0;
            //dxd.num_obiekt.add(wsk);
            dxd.distance.add(dst);
            licznik++;
        }
    return dxd;
    }
    public double odleglosc (BrightFeature a,BrightFeature b, double abc) {
        double squaresum=0;

        for (int i=0; i<64; i++) {
            squaresum = squaresum + (a.value[i]-b.value[i])*(a.value[i]-b.value[i]);
//            a.value[1] = 0;
        }
        abc = sqrt(squaresum);
        return abc;
    }
    public ScalePoint wybierzPktLU (List<ScalePoint> lista) {
        double lc = 10000;
        double uc = 10000;
        ScalePoint punkcik = lista.get(0);
        for (ScalePoint i : lista) {

            if (i.x < lc && i.y < uc) {
                lc = i.x;
                uc = i.y;
                punkcik = i;
            }
        }
        return punkcik;
    }
    public ScalePoint wybierzPktLD (List<ScalePoint> lista) {
        double lc = 10000;
        double uc = 0;
        ScalePoint punkcik = lista.get(0);
        for (ScalePoint i : lista) {

            if (i.x < lc && i.y > uc) {
                lc = i.x;
                uc = i.y;
                punkcik = i;
            }
        }
        return punkcik;
    }
    public ScalePoint wybierzPktRU (List<ScalePoint> lista) {
        double rc = 0;
        double uc = 10000;
        ScalePoint punkcik = lista.get(0);
        for (ScalePoint i : lista) {

            if (i.x > rc && i.y < uc) {
                rc = i.x;
                uc = i.y;
                punkcik = i;
            }
        }
        return punkcik;
    }
    public ScalePoint wybierzPktRD (List<ScalePoint> lista) {
        double rc = 0;
        double uc = 0;
        ScalePoint punkcik = lista.get(0);
        for (ScalePoint i : lista) {

            if (i.x > rc && i.y > uc) {
                rc = i.x;
                uc = i.y;
                punkcik = i;
            }
        }
        return punkcik;
    }
}