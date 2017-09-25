package com.example.cnr.tez;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.content.Intent;

import android.widget.TextView;
import android.widget.Toast;





import java.io.IOException;


//Excel Kütüphaneleri
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;





public class MainActivity extends Activity implements View.OnClickListener{

    static boolean flag;

    Button yukle,cevap,cevap_sec,optik_tarat;

    TextView textView;

    //Veritabanı nesneleri
    DBController veritabani_kontrol = new DBController(this);
    final Context context = this;
    public static final int requestcode = 1;

    //Gelen excel dosyaları için nesneler
    public String gelen_xsl;
    public int row_count;

    //Seçilen anahtar değişkenleri
    String anahtar_adi;
    int id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        degerAta();
        yukle.setOnClickListener(this);
        cevap.setOnClickListener(this);
        cevap_sec.setOnClickListener(this);
        optik_tarat.setOnClickListener(this);
        cevap.setEnabled(true);


    }

    public void degerAta() {
        yukle = (Button) findViewById(R.id.liste_yukle);
        cevap = (Button) findViewById(R.id.cevap_yukle);
        cevap_sec = (Button) findViewById(R.id.cevap_sec);
        optik_tarat = (Button) findViewById(R.id.optik_tarat);
        textView = (TextView) findViewById(R.id.secili_anahtar);
        //reklamyukle();

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.liste_yukle:
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("gagt/sdf");
        try {
            startActivityForResult(intent, requestcode);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Bu uygulamayı kaydetmek için uygulama bulunamadı", Toast.LENGTH_SHORT).show();
        }
              break;

            case R.id.cevap_yukle:
                Intent camera = new Intent(this,Camera.class);
                startActivity(camera);
                break;

            case R.id.cevap_sec:
                Intent cevapsec = new Intent(this,Anahtar_Sec.class);
                startActivityForResult(cevapsec,0);
                break;

            case R.id.optik_tarat:
                Intent tara = new Intent(this,optik_tara.class);
                tara.putExtra("id",id);
                startActivity(tara);
                break;




    }}

    /*private void reklamyukle(){

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.AnaEkranBannerId));

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ana_banner);
        linearLayout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);



    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 0 && resultCode == RESULT_OK) {
            String ok = "\u2193";
            String asteriks = "\u2042";
            String anahtar_adi = data.getStringExtra("ad");
            id = data.getExtras().getInt("Index");
            DBController db = new DBController(getApplicationContext());
            textView.setText(ok +" Seçilen Anahtar " + ok +"\n"+ asteriks+ " " + anahtar_adi + " " + asteriks);
        }

        if(requestCode == requestcode)
        {
            try{
                SQLiteDatabase db = veritabani_kontrol.getWritableDatabase();
                ContentValues values = new ContentValues();
                gelen_xsl = data.getData().getPath();
                String[][] arrays = read();
                int j = 0;
                int i;


                if (arrays == null) {
                    Toast.makeText(this, "Dosya Boş", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        for (i = 1; i < row_count; i++) {
                            values.put("Ogrenci_No", arrays[i][j]);
                            values.put("Adi_Soyadi", arrays[i][j + 1]);
                            db.insert("ogrenciler", null, values);
                            j = 0;
                        }


                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }}catch(Exception ex){
                Toast.makeText(this,"Uygun bir dosya seçmediniz ",Toast.LENGTH_LONG).show();
            }

                //yukle.setEnabled(false);
            }

    }

    public String[][] read(){


        Workbook workbook = null;
        try
        {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setGCDisabled(true);
            ws.setEncoding("Cp1254");
            workbook = Workbook.getWorkbook(new java.io.File(gelen_xsl), ws);


            Sheet sheet = workbook.getSheet(0);

            row_count = sheet.getRows();
            String result[][]  = new String[row_count][];

            for(int i = 0; i < row_count;i++)
            {
                Cell[] row = sheet.getRow(i);

                result[i] = new String[row.length];
                for(int j = 0; j<2;j++)
                {

                    result[i][j] = row[j].getContents();
                }


            }

            return result;

        }catch(BiffException e){

            Toast.makeText(this, e.getMessage() + "1",Toast.LENGTH_LONG).show();

        }catch (IOException e){

            Toast.makeText(this, e.getMessage() + "2",Toast.LENGTH_LONG).show();
        }catch(Exception e)
        {
            Toast.makeText(this, e.getMessage() + "3",Toast.LENGTH_LONG).show();
        }
        finally{

            if(workbook != null)
            {workbook.close();}
        }

      return null;

    }


}
