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


//// klasa odpowiada za operacje na listach punktow ScalePoint i BrightFeature oraz rysowanie obiektów


public class showSurf extends VideoImageProcessing<GrayF32>
{
    //deklaracja zmiennych
    pokazWzor xdd = new pokazWzor ();
    List<BrightFeature> punkciki;
    public static List<BrightFeature> lista_wzorca;
    public static List<ScalePoint> lista_wzorca_scale;
    List<ScalePoint> scalepointy = new ArrayList<>();
    List<ScalePoint> scalepointy1 = new ArrayList<>();

    doObliczen odleglosci = new doObliczen();
    double funkcja_dystans = 0;

    public static boolean x = false;


    protected showSurf()
    {
        super(ImageType.single(GrayF32.class));

    }

    // metoda "abc" przepisuje listy punktów wzorca do późniejszej obrobki

    public void abc(List<BrightFeature> lista_wzorca,List<ScalePoint> lista_wzorca_scale, boolean x2){
        this.lista_wzorca = new ArrayList<>();
        this.lista_wzorca=lista_wzorca;
        this.lista_wzorca_scale = new ArrayList<>();
        this.lista_wzorca_scale=lista_wzorca_scale;
        x  = x2;
    }

    // metoda, która odpowiada za konieczne zadklarowanie rozmiarow obrazka, bez tego program nie zadziala
    @Override
    protected void declareImages( int width , int height ) {

        super.declareImages(width,height);
    }

    // metoda przetwarzajaca obraz z kamery
    @Override
    protected void process(GrayF32 gray, Bitmap output, byte[] storage)
    {
        // wykonanie algorytmu SURF dla obrazu w czasie rzeczywistym
        xdd.surfAlg(gray);
        //zapisanie listy BrightPoint do zmiennej listowej "punkciki"
        punkciki = xdd.brajt;
        // analogicznie dla listy ScalePoint
         scalepointy = xdd.scp;

        // //utworzenie obiektu canvas, umozliwia on rysowanie kształtów
        Canvas canvas = new Canvas( output);
        // blok inicjujący różne style rysowania - kolor zielony albo magenta

        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setStyle(Paint.Style.STROKE);

        Paint paint5 = new Paint();
        paint5.setColor(Color.MAGENTA);
        paint5.setStyle(Paint.Style.STROKE);

        // pokazuje obraz kamery na ekranie, w skali szarości, dla wartości pixeli od 0 - 255 (wszystkich)
        VisualizeImageData.grayMagnitude(gray,255,output, storage);

        //metoda() wykonuje operacje obliczania "Dystansu" między punktami BrightFeauture
        metoda();

        // metoda wybierzPktLU wybiera z listy punktów, które zwrocić algorytm surf, ten punkt, którego współrzedne x i y są najbliżej lewego górnego roku ekranu
        ScalePoint LU = wybierzPktLU(scalepointy);

        // petla for rysuje okregi wokól wykrytych punktów
        for (ScalePoint i: scalepointy) {

            canvas.drawCircle((float)i.getX(), (float)i.getY(),5 ,paint1);
        }

        // jeżeli na liście znaczników jest odpowiednio dużo elementow - następuje wykrycie obiektu i rysowany jest prostokąt
        if (scalepointy.size()>35) {
            canvas.drawRect((float) LU.getX() + 20, (float) LU.getY() + 20, 120, 120, paint5);
        }
    }
    // wyliczanie "dystansu" poprzez wywołanie odpowiedniej metody - tePunkty
    public void metoda() {
            // jeżeli listy nie są puste można działać dalej
            if (!(lista_wzorca.isEmpty() || punkciki.isEmpty() || lista_wzorca_scale.isEmpty())) {
                odleglosci = tePunkty(lista_wzorca, punkciki, odleglosci);

            }
    }

    // klasa bierze listy BrighFeature i za pomocą kolejnych iteracji pętli for bierze każdy z 64 detektorów. Porównuje między nimi "dystans" i zwraca listę tych, które mają mały dystans;
    // efektem koncowym jest struktura danych charakterystyczna dla klasy "doObliczen", ktora zawiera nr punktu wzorca, korespondujacy z nim nr punktu obrazu zczytywanego i dystans miedzy nimi
    public doObliczen tePunkty (List<BrightFeature> a, List<BrightFeature> b, doObliczen dxd) {
        int licznik = 0;
        int wsk=0;

        for( BrightFeature p : a ) {
            //dystans na poczatku jest duzy, zeby nawet odlegly punkt mogl dostac sie na listę. Dystans zawiera się w przedziale <0,1> zatem dst = 100 nie wpłynie na dzialanie programu
            int licznik1 = 0;
            double dst = 100;
            double dst_roboczy;
            // do obiektu typu doObliczen zostanie dodany do pola num_wzor, czyli numer punktu z wzorca numer porzadkowy zgodny z licznikiem petli for)
            dxd.num_wzor.add(licznik, licznik);

            for( BrightFeature t : b ) {
                // funkcja odległość obliczy dystans między (p,t)
                dst_roboczy = odleglosc(p,t, funkcja_dystans);
                // jeżeli dystans jest dostatecznie mały i najmniejszy ze wszystkich dystansów między danym punktem z listy a, a punktami z listy b następuje wpisanie go do listy
                if (dst_roboczy<dst && dst_roboczy < 0.4) {
                    dst = dst_roboczy;
                    wsk = licznik1;
                }
                licznik1++;
            }
            dxd.num_obiekt.add(licznik, wsk);
            scalepointy1.add(scalepointy.get(wsk));
            licznik1=0;
            //dxd.num_obiekt.add(wsk);
            dxd.distance.add(dst);
            licznik++;
        }
    return dxd;
    }
    //pomiar odleglosci miedzy punktami
    public double odleglosc (BrightFeature a,BrightFeature b, double abc) {
        double squaresum=0;
        // dla każdego z deskryptorów obliczany jest jako odległość kartezjanska tzn. pierwiastek ((a1-b1)^2+...(a64-b64)^2)
        for (int i=0; i<64; i++) {
            squaresum = squaresum + (a.value[i]-b.value[i])*(a.value[i]-b.value[i]);
//            a.value[1] = 0;
        }
        abc = sqrt(squaresum);
        return abc;
    }
    // wyznaczenie punktu najbliższego lewemu górnemu narożnikowi
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


    // nieużyte w koncowej wersji metody, ktore wybieraly punkty najbliżej innych narożników
   /* public ScalePoint wybierzPktLD (List<ScalePoint> lista) {
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
    }*/
}