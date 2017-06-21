package com.example.radzi.surf_bcv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import boofcv.struct.feature.ScalePoint;
import boofcv.struct.image.GrayF32;

/**
 * Created by radzi on 2017-06-13.
 */
// klasa wyświetla wzorzec ze znacnzikami

public class wynik extends AppCompatActivity {

    //deklaracje zmiennych
    pokazWzor wzorzec= new pokazWzor ();
    ImageView imgView;
    public static List <ScalePoint> listka = new ArrayList<>();


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wynik);
        imgView = (ImageView) findViewById(R.id.imageView);
        // wczytywanie obrazu jako bitmapy. Na poczatku ustawienie opcji inMutable, bo inaczej zmienionej bitmapy nie będzie można potem ustawić na powrot do ImageView
        BitmapFactory.Options opcje = new BitmapFactory.Options();
        opcje.inMutable = true;
        //wczytanie bitmapy
        Bitmap wzorcowy = BitmapFactory.decodeResource(getResources(),R.drawable.stonesmin, opcje);
        //konwersja do GrayF32, czyli obrazu typu integral operującego na zmiennych float 32 bitowa
        GrayF32 img = boofcv.android.ConvertBitmap.bitmapToGray(wzorcowy, (GrayF32)null,null);
        // wykonanie algorytmy SURF na obrazie img
        this.wzorzec.surfAlg(img);
        // wyswietlenie
        imgView.setImageBitmap(wzorcowy);
        //utworzenie obiektu canvas, umozliwia rysowanie
        Canvas canvas = new Canvas( wzorcowy);
        // utworzenie obiektu paint, ktory zawiera konfiguracje stylu rysowania (w tym przypadku color i wypełnienie)
        Paint paint2 = new Paint();
        paint2.setColor(Color.YELLOW);
        paint2.setStyle(Paint.Style.FILL);
        //wydobycie z algorytmu danych dotyczących ScalePointow - lista ScalePointow zawiera informacje o wspolrzednych i skali, czyli istotnosci dla pozniejszej analizy
        listka = wzorzec.scp;
        // standardowa petla for, dla kazdego ScalePointa narysuje okrag o promieniu 5 zgodnie z wzorcem paint2
        for (ScalePoint i: listka) {

            canvas.drawCircle((float)i.getX(), (float)i.getY(),5 ,paint2);
        }


    }


}
