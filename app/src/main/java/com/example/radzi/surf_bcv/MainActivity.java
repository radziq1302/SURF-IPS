package com.example.radzi.surf_bcv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
// --------- MainActivity wyświetla dwa przyciski. Jeden z nich pozwala na otworzenie modułu przetwarzania w czasie rzeczywistym, drugi pokazuje wzorzec i naniesione na nim okręgi wokół wykrytych punktow charakterystycznych
public class MainActivity extends AppCompatActivity {
    Button btn;
    Button btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button_surf);
        btn2 = (Button) findViewById(R.id.button_wzorzec);

        // otworzenie aktywności kamery
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(getApplicationContext(),surf.class);
                startActivity(logIntent);
            }
        });

        // otworzenie aktywności wyświetlania wzorca z naniesionymi znacznikami
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(getApplicationContext(),wynik.class);
                startActivity(logIntent);
            }
        });
        /*Intent intent = new Intent(this, surf.class);
        startActivity(intent);*/
    }
}
