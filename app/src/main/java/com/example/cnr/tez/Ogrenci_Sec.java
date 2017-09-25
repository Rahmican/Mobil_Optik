package com.example.cnr.tez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;




import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class Ogrenci_Sec extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{

    List<ogrenci_veritabanı> myData;
    Button cikti_al;
    EditText cikti_ismi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogrenci__sec);

        String dolu = "\u2605";
        String bos = "\u2622";
        cikti_ismi = (EditText) findViewById(R.id.cikti_ismi);
        cikti_al = (Button) findViewById(R.id.cikti_al);
        cikti_al.setOnClickListener(this);
        DBController db = new DBController(getApplicationContext());


        try{
            myData = db.ogrencileri_al();
            String[] yazılıs = new String[myData.size()] ;
            for(int i= 0 ; i < myData.size() ; i++){

                if(myData.get(i).isEmpty == 0) {
                    yazılıs[i] = myData.get(i).Ogrenci_No + " -- " + myData.get(i).Adi_Soyadi + "\n\t" +
                            "Dogru Sayisi -> " + "Girilmedi" + "\t\t" + bos  ;
                }else {
                    yazılıs[i] = myData.get(i).Ogrenci_No + " -- " + myData.get(i).Adi_Soyadi + "\n\t" +
                            "Dogru Sayisi -> " + myData.get(i).Dogru_Sayisi + "\t\t" + dolu  ;
                }
            }

            ListView listView = (ListView) findViewById(R.id.ogrenci_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,yazılıs);
            listView.setAdapter(adapter);
           //renklendirme işlemi
            listView.setOnItemClickListener(this);

        }catch(Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id){


        String gelen = getIntent().getExtras().getString("anahtar_id");

        final int idea = Integer.parseInt(gelen);
        final int index = myData.get(position).id;
        final int dogru_sayisi = getIntent().getExtras().getInt("dogru");

        AlertDialog.Builder uyari = new AlertDialog.Builder(this);

        uyari.setTitle("UYARI");
        uyari.setMessage("Kayıt işlemi yapmadan sonuçların doğruluğunu kontrol ediniz.");

        uyari.setPositiveButton("KAYDET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    DBController db = new DBController(Ogrenci_Sec.this);
                    db.ogrenci_kaydet(index, dogru_sayisi);
                    Intent intent = new Intent(Ogrenci_Sec.this,optik_tara.class);
                    intent.putExtra("id",idea);
                    startActivity(intent);


                }catch(Exception ex){
                    Log.i("Sıkıntı ogrenci_sec" , ex.getMessage() );
                }
            }
        });

        uyari.setNegativeButton("Geri Dön", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Ogrenci_Sec.this,optik_tara.class);
                intent.putExtra("id",idea);
                startActivity(intent);

            }
        });

        uyari.setNeutralButton("Kapat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog pencere = uyari.create();
        pencere.show();

    }

    @Override
    public void onClick(View v) {

        String isim = cikti_ismi.getText().toString();
        DBController db = new DBController(this);


        java.io.File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String csvFile = isim+".xls";

        java.io.File directory = new java.io.File(sd.getAbsolutePath());
        //Eğer yoksa directory yi oluştur

        try{
            java.io.File file = new java.io.File(directory,csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("tr","TR")); //burada sııntı olabilir
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file,wbSettings);
            //Excel ismi. 0 ilk dosyayı belirtir
            WritableSheet sheet = workbook.createSheet("Sonuclar",0);
            //sütun ve satırlar
            sheet.addCell(new Label(0,0,"Öğrenci No"));
            sheet.addCell(new Label(1,0,"Adı Soyadı"));
            sheet.addCell(new Label(2,0,"Puan"));

            for(int i = 0 ; i < myData.size();i++){

                    sheet.addCell(new Label(0,i+1,myData.get(i).Ogrenci_No));
                    sheet.addCell(new Label(1,i+1,myData.get(i).Adi_Soyadi));
                    sheet.addCell(new Label(2,i+1,myData.get(i).Dogru_Sayisi));

            }

            workbook.write();
            workbook.close();
            db.bosalt();
            db.close();
            Toast.makeText(this,"EXPORT BAŞARILI "+"\u2053"+" Yeni Sınıf Listesini Yükleyiniz",Toast.LENGTH_SHORT).show();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
