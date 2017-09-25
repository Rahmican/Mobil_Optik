package com.example.cnr.tez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class Anahtar_Sec extends Activity implements AdapterView.OnItemClickListener {

    List<anahtar_veritabanı> myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anahtar__sec);

        DBController dba = new DBController(getApplicationContext());


        try{
            myData= dba.cevapları_al();
            String[] yazılacaklar = new String[myData.size()];
            for(int i = 0;i<myData.size();i++)
            {
                yazılacaklar[i] = "Grup -> " + myData.get(i).grup + "\t Anahtar Adı -> " + myData.get(i).anahtar_adi;
            }

            ListView listView = (ListView) findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,yazılacaklar);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);


        }catch(Exception ex){

            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id){

        final int index = myData.get(position).anahtar_id;
        final String anahtar_adi= myData.get(position).anahtar_adi;

        AlertDialog.Builder uyari = new AlertDialog.Builder(this);

        uyari.setTitle("Uyarı");

        uyari.setMessage("Anahatara hangi işlem yapılsın");

        uyari.setPositiveButton("SEÇ",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Anahtar_Sec.this,MainActivity.class);

                intent.putExtra("Index",index);
                intent.putExtra("ad",anahtar_adi);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        uyari.setNegativeButton("SİL",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {


                try {

                   DBController dba = new DBController(getApplicationContext());
                   dba.anahtar_sil(index);


               }catch(Exception ex){
                   Log.i("Sıkıntı",ex.getMessage());
               }
            }
        });

        AlertDialog pencere = uyari.create();
        pencere.show();

    }

}

