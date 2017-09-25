package com.example.cnr.tez;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CNR on 28.11.2016.
 */

public class DBController extends SQLiteOpenHelper {

    private static final String LOGCAT = null;

    public static final String Anahtar_Tablo = "cevap_anahtari";
    public static final String Ogrenci_Tablo = "ogrenciler";


    public DBController(Context applicationContext) {
        super(applicationContext, "OgrenciDB.db", null, 1);
        Log.d(LOGCAT, "Veritabanı Oluşturuldu");

    }

    @Override
    public void onCreate(SQLiteDatabase veritabani) {
        String query, querycevap,querysecili;


        query = "CREATE TABLE IF NOT EXISTS ogrenciler (id INTEGER PRIMARY KEY AUTOINCREMENT,Ogrenci_No VARCHAR, Adi_Soyadi VARCHAR , Dogru_Sayisi INTEGER , isEmpty INTEGER)";
        querycevap = "CREATE TABLE IF NOT EXISTS cevap_anahtari (id INTEGER PRIMARY KEY AUTOINCREMENT,Anahtar_adi TEXT, cevaplar VARCHAR , grup VARCHAR)";


        veritabani.execSQL(query);
        veritabani.execSQL(querycevap);

    }

    @Override
    public void onUpgrade(SQLiteDatabase veritabani, int version_old, int current_version) {
        //Veritabanı olan veritabanını düşürüp 0 dan oluşturur
        String query;
        query = "DROP TABLE IF EXISTS ogrenciler,cevap_anahtari";
        veritabani.execSQL(query);
        onCreate(veritabani);
    }

    public void AnahtarKaydet(String anahtar_ismi,String tablo, String grup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Anahtar_adi" , anahtar_ismi);
        values.put("cevaplar" , tablo);
        values.put("Grup" , grup);

        db.insert(Anahtar_Tablo,null,values);
        db.close();


    }

    public List<anahtar_veritabanı> cevapları_al(){

        List<anahtar_veritabanı> arrData = new ArrayList<anahtar_veritabanı>();
        SQLiteDatabase db = this.getReadableDatabase();

        String strSQL = "SELECT * FROM " + Anahtar_Tablo;
        Cursor cursor = db.rawQuery(strSQL,null);

        if(cursor!=null){
            if(cursor.moveToFirst()){
                //Eğer ilk satıra gelmişse
                do{
                   anahtar_veritabanı an = new anahtar_veritabanı();
                    an.setAnahtar_id(cursor.getInt(0));
                    an.setAnahtar_adi(cursor.getString(1));
                    an.setCevaplar(cursor.getString(2));
                    an.setGrup(cursor.getString(3));

                    arrData.add(an);

                }while(cursor.moveToNext());



            }
        }
        cursor.close();
        db.close();
        return arrData;


    }

    public void anahtar_sil(int index){


        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + Anahtar_Tablo + " WHERE id = "+index+" ";
        db.execSQL(updateQuery);
        db.close();

    }

    public anahtar_veritabanı anahtar_sec(int index){

        SQLiteDatabase db = getWritableDatabase();
        anahtar_veritabanı donen = new anahtar_veritabanı();
        Cursor c;//cursorda sql çalışsa bile yer ataması yapılmalı (movetoFirst)!!

        String sql = "SELECT Anahtar_adi,cevaplar,grup FROM " + Anahtar_Tablo + " WHERE id = "+index+" ";
        c = db.rawQuery(sql,null);

        if(c!=null && c.moveToFirst()) {

            donen.setAnahtar_adi(c.getString(0));
            donen.setCevaplar(c.getString(1));
            donen.setGrup(c.getString(2));
        }
        c.close();
        db.close();

        return donen;
    }

    public List<ogrenci_veritabanı> ogrencileri_al(){

        List<ogrenci_veritabanı> arrData = new ArrayList<ogrenci_veritabanı>();
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " +Ogrenci_Tablo;
        Cursor c =db.rawQuery(sql,null);

        if(c != null){
            if(c.moveToFirst()){

                do{
                    ogrenci_veritabanı og = new ogrenci_veritabanı();
                    og.setId(c.getInt(0));
                    og.setOgrenci_No(c.getString(1));
                    og.setAdi_Soyadi(c.getString(2));
                    og.setDogru_Sayisi(c.getString(3));
                    og.setIsEmpty(c.getInt(4));

                    arrData.add(og);

                }while(c.moveToNext());
            }
        }

        c.close();
        db.close();
        return arrData;
    }

    public void ogrenci_kaydet(int index,int d_sayi){

        int i = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        String updateQuery = "UPDATE " + Ogrenci_Tablo + " SET Dogru_Sayisi = "+d_sayi+", isEmpty = "+i+" WHERE id = "+index+" ";
        db.execSQL(updateQuery);
        db.close();

    }

    public void bosalt(){

        SQLiteDatabase db = this.getWritableDatabase();

        String updateSQL = "DELETE FROM " +Ogrenci_Tablo+" ";

        db.execSQL(updateSQL);

        db.close();


    }



}
