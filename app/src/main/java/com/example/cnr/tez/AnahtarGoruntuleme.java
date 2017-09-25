package com.example.cnr.tez;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class AnahtarGoruntuleme extends Activity implements View.OnClickListener {

    EditText anahtaradi;
    TextView cevapgrup;
    Button kaydet;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anahtar_goruntuleme);

        String aTablo = getIntent().getExtras().getString("aTablo");
        String bTablo = getIntent().getExtras().getString("bTablo");
        String grup = getIntent().getExtras().getString("grup");

        degerAta();
        kaydet.setOnClickListener(this);
        cevapgrup.setText("Grup -> " + grup + "\n" + " Sonuçlar -> "
                + "1 - " + aTablo.charAt(12) + "\t"
                + "2 - " + aTablo.charAt(11)+ "\t"
                + "3 - " + aTablo.charAt(10)+ "\t"
                + "4 - " + aTablo.charAt(9)+ "\t"
                + "5 - " + aTablo.charAt(8)+ "\t"
                + "6 - " + aTablo.charAt(7)+ "\t"
                + "7 - " + aTablo.charAt(6)+ "\t"
                + "8 - " + aTablo.charAt(5)+ "\t"
                + "9 - " + aTablo.charAt(4)+ "\t"
                + "10 - " + aTablo.charAt(3)+ "\t"
                + "11 - " + aTablo.charAt(2)+ "\t"
                + "12 - " + aTablo.charAt(1)+ "\t"
                + "13 - " + aTablo.charAt(0)+ "\n"

                + "14 - " + bTablo.charAt(12) + "\t"
                + "15 - " + bTablo.charAt(11)+ "\t"
                + "16 - " + bTablo.charAt(10)+ "\t"
                + "17 - " + bTablo.charAt(9)+ "\t"
                + "18 - " + bTablo.charAt(8)+ "\t"
                + "19 - " + bTablo.charAt(7)+ "\t"
                + "20 - " + bTablo.charAt(6)+ "\t"
                + "21 - " + bTablo.charAt(5)+ "\t"
                + "22 - " + bTablo.charAt(4)+ "\t"
                + "23 - " + bTablo.charAt(3)+ "\t"
                + "24 - " + bTablo.charAt(2)+ "\t"
                + "25 - " + bTablo.charAt(1)+ "\t"
                + "26 - " + bTablo.charAt(0)+ "\t"
        );

    }
    public void degerAta(){

      anahtaradi = (EditText) findViewById(R.id.anahtaradigir);
      cevapgrup = (TextView) findViewById(R.id.cevapgrupgoster);
        kaydet = (Button) findViewById(R.id.anahtar_kaydet);

    }

    @Override
    public void onClick(View v) {


        String aTablo = getIntent().getExtras().getString("aTablo");
        String bTablo = getIntent().getExtras().getString("bTablo");
        String grup = getIntent().getExtras().getString("grup");

        try{
        String anahtar_ismi = anahtaradi.getText().toString();
        DBController db = new DBController(getApplicationContext());

        db.AnahtarKaydet(anahtar_ismi,aTablo+bTablo,grup);

        Toast.makeText(this,"Anahtar Kaydedildi",Toast.LENGTH_SHORT).show();
            Intent baslat = new Intent(AnahtarGoruntuleme.this,MainActivity.class);
            startActivity(baslat);
        }catch (Exception ex){

            Toast.makeText(this,"HATA - > " + ex.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}
