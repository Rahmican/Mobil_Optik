package com.example.cnr.tez;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;


import org.opencv.core.CvType;
import org.opencv.core.Mat;


import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class optik_tara extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnTouchListener {

    Mat mgray;//gray level veri döndürür

    Mat mrgba;//renkli veri döndürür

    Point pt1,pt2,pt3;//Ekrana çizilen çizgiler

    ImageView imageview;

    JavaCameraView javaCameraView;//Kamera değişkeni



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Full Screen Modu
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Kameranın açılması
        setContentView(R.layout.activity_optik_tara);
        javaCameraView = (JavaCameraView) findViewById(R.id.javacamera);


        //Opencv nin Static olarak yüklenmesi
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV","Unable to load OpenCV");
        } else {
            Log.e("OpenCV","OpenCV has loaded!");
        }

        try{
            javaCameraView.setCameraIndex(0);
            javaCameraView.setCvCameraViewListener(this);
            javaCameraView.setVisibility(View.VISIBLE);
            javaCameraView.enableView();
            javaCameraView.setOnTouchListener(this);

        }catch (Exception ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();

        }




    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mrgba = inputFrame.rgba();
        mgray = inputFrame.gray();
        AlanCiz();

        return mrgba;
    }

    @Override
    protected void onPause(){

        super.onPause();
        if(javaCameraView.isEnabled()){

            javaCameraView.disableView();
        }
    }

    public void onDestroy() {

        super.onDestroy();
        if(javaCameraView.isEnabled()){

            javaCameraView.disableView();
        }}

    public void AlanCiz(){

        //Ekrandaki Çizgiler
        pt1 = new Point(mrgba.cols()/5,mrgba.rows()/2.5);
        pt2 = new Point(mrgba.cols()/5,2.8*mrgba.rows()/3);
        pt3 = new Point(mrgba.cols()/1.4,2.8*mrgba.rows()/3);


        Imgproc.line(mrgba,pt3,pt2,new Scalar(0,0,255),10);
        Imgproc.line(mrgba,pt2,pt1,new Scalar(0,0,255),10);



    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Mat submat = mgray.submat((int) pt1.y,(int) pt2.y,(int) pt2.x,(int) pt3.x );
        FindContour(submat);
        return false;
    }

    public void FindContour(Mat submat) {
        //Contour için kullanılan Değişkenler
        Mat yazılan = submat.clone();

        //Matris içindeki şekillerin kaydedilmesi
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        //Max dörtgeni bulmak için kullanılan değişkenler
        double areas;
        double maksArea = 200000;
        int maxidx = 0;
        MatOfPoint2f approxcurve = new MatOfPoint2f();
        MatOfPoint2f maxCurve = new MatOfPoint2f();

        //Matrisler
        Mat grup,aTablo,bTablo,adsoyRoi,numRoi;

        //Image Preprocessing
        Imgproc.GaussianBlur(submat,submat,new Size(5,5),0);
        Imgproc.adaptiveThreshold(submat,submat,255,1,1,11,2);

        //Sadece en dışarda kalan şekillerin bulunması EXTERNAL
        Imgproc.findContours(submat,contours,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        //Belirlenen şekillerden en büyüğünün bulunması
        for(int i = 0; i < contours.size();i++)
        {
            areas = Imgproc.contourArea(contours.get(i));

            if(areas > maksArea){

                //Bulunan şekilden kare oluşturulmaya çalışılıyor
                MatOfPoint2f new_Mat = new MatOfPoint2f(contours.get(i).toArray());
                int contoursize = (int) contours.get(i).total();
                Imgproc.approxPolyDP(new_Mat,approxcurve,contoursize * 0.05,true);

                //Eğer kare ise
                if(approxcurve.total() == 4){
                    maxCurve = approxcurve;
                    maxidx = i;
                    maksArea = areas;
                }
            }
        }

        if(maxCurve.total() == 4) {//Eğer bir dörtgen bulunmuşsa

            List<Point> srcpoints = new ArrayList<>();
            List<Point> dstpoints = new ArrayList<>();

            //maximum dörtgenin köşe noktaları Pointlere aktarılıyor
            double temp_double[] = maxCurve.get(0, 0);
            Point p1 = new Point(temp_double[0], temp_double[1]);


            temp_double = maxCurve.get(1, 0);
            Point p2 = new Point(temp_double[0], temp_double[1]);


            temp_double = maxCurve.get(2, 0);
            Point p3 = new Point(temp_double[0], temp_double[1]);


            temp_double = maxCurve.get(3, 0);
            Point p4 = new Point(temp_double[0], temp_double[1]);

            if(p1.x < p3.x && p1.y < p3.y) {

                //Kenarların Listeye atılması
                srcpoints.add(p1);
                srcpoints.add(p4);
                srcpoints.add(p3);
                srcpoints.add(p2);

                //İstenilen boyuta göre hedef noktalar
                dstpoints.add(new Point(0, 0));
                dstpoints.add(new Point(500, 0));
                dstpoints.add(new Point(500, 500));
                dstpoints.add(new Point(0, 500));


                //Noktaları Mata çevirme CV_32F & 32FS gibi tipleri kabul ediyor
                Mat corners = Converters.vector_Point_to_Mat(srcpoints, CvType.CV_32F);
                Mat new_corners = Converters.vector_Point_to_Mat(dstpoints, CvType.CV_32F);

                //Transform matrisinin belirlenmesi
                Mat transmatrix = Imgproc.getPerspectiveTransform(corners, new_corners);

                //Perspektif dönüşüm işlemi
                Imgproc.warpPerspective(yazılan, yazılan, transmatrix, new Size(500, 500));

                //Resmin Roi'lerinin parçalanması
                Rect grupRoi = new Rect(new Point(285,500/1.3),new Point(187,500/1.35));
                Rect aRoi = new Rect(new Point(392,500/1.5),new Point(253,500/8.8));
                Rect bRoi = new Rect(new Point(217,500/1.5),new Point(80,500/8.8));

                //Resimden Roilerin çıkartılması
                 grup = yazılan.submat(grupRoi);
                aTablo = yazılan.submat(aRoi);
                bTablo = yazılan.submat(bRoi);


                Uyari(grupsonuc(grup),aTabloSonuc(aTablo),bTabloSonuc(bTablo));

                //adSoySonuc(adsoyRoi);

                /*Imgproc.resize(adro,adro,new Size(500,50));

                Bitmap bmp = Bitmap.createBitmap(adro.width(), adro.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(adro, bmp);
                imageview.setImageBitmap(bmp);*/


            }
            else {

                Toast.makeText(this,"Tekrar Deneyiniz",Toast.LENGTH_SHORT).show();

            }
        }
        else{
            Toast.makeText(this,"Yeniden Deneyiniz",Toast.LENGTH_SHORT).show();
        }
    }

    public String grupsonuc (Mat grup){

        String answer = "";

       /* Imgproc.medianBlur(grup,grup,9);
        Imgproc.GaussianBlur(grup,grup,new Size(9,9),0);*/


        Imgproc.threshold(grup, grup, 0, 255,Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        Imgproc.dilate(grup,grup,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(7,7)));

        Imgproc.resize(grup,grup,new Size(5,1));


        double[] d = new double[0];


        for(int j = 0; j < grup.cols() ; j++)
        {

            d = grup.get(0,j);
            if(d[0] == 0){

                if(j == 0){
                    answer += "E";
                }
                if(j == 1) {
                    answer += "D";
                }
                if(j == 2) {
                    answer += "C";
                }
                if(j == 3) {
                    answer += "B";
                }
                if(j == 4) {
                    answer += "A";
                }

            }

        }
        if(answer.length() == 0){

            answer = "X";
            return answer;
        }
        if(answer.length() > 1){

            answer = "Z";
            return  answer;

        }
        return answer;

    }

    public String aTabloSonuc(Mat atablo){


        String answer = "";
        int sayac = 0;
        Imgproc.threshold(atablo, atablo, 0, 255,Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        Imgproc.dilate(atablo,atablo,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(7,7)));
        Imgproc.resize(atablo,atablo,new Size(5,13));


        double[] d = new double[0];
        for(int i = 0; i < atablo.rows();i++ ){
            for(int j = 0; j < atablo.cols() ; j++) {

                d = atablo.get(i,j);
                if(d[0] == 0){

                    if(j == 0){
                        answer += "E";
                        sayac++;

                    }
                    if(j == 1) {
                        answer+= "D";
                        sayac++;

                    }
                    if(j == 2) {
                        answer += "C";
                        sayac++;

                    }
                    if(j == 3) {
                        answer += "B";
                        sayac++;
                    }
                    if(j == 4) {
                        answer+= "A";
                        sayac++;
                    }
                }

            }
            if(answer.length() < i+1){
                answer+="X";
            }
            if(sayac >= 2){

                StringBuilder sb = new StringBuilder(answer);
                answer = sb.delete(i,i+sayac).toString();
                answer+="Z";

            }
            sayac = 0;

        }

        return answer;
    }

    public String bTabloSonuc(Mat btablo){

        String answer = "";
        int sayac = 0;


        Imgproc.threshold(btablo, btablo, 0, 255,Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        //Imgproc.medianBlur(atablo,atablo,9);
        //Imgproc.GaussianBlur(atablo,atablo,new Size(9,9),0);

        Imgproc.dilate(btablo,btablo,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(7,7)));
        Imgproc.resize(btablo,btablo,new Size(5,13));


        double[] d = new double[0];
        for(int i = 0; i < btablo.rows();i++ ){

            for(int j = 0; j < btablo.cols() ; j++)
            {

                d = btablo.get(i,j);
                if(d[0] == 0){

                    if(j == 0){
                        answer += "E";
                        sayac++;

                    }
                    if(j == 1) {
                        answer+= "D";
                        sayac++;

                    }
                    if(j == 2) {
                        answer += "C";
                        sayac++;

                    }
                    if(j == 3) {
                        answer += "B";
                        sayac++;

                    }
                    if(j == 4) {
                        answer+= "A";
                        sayac++;

                    }
                }

            }
            if(answer.length() < i+1){
                answer+="X";
            }
            if(sayac >= 2){

                StringBuilder sb = new StringBuilder(answer);
                answer = sb.delete(i,i+sayac).toString();
                answer+="Z";
            }

            sayac = 0;
        }

        return answer;
    }

    public void Uyari(String grup, final String aTablo, final String bTablo)
    {
        final String atab = aTablo;
        final String btab = bTablo;
        final String grupo = grup;



        final String[] sonuclar = dogruları_bul(aTablo,bTablo,grup);
        final int puan = round((sonuclar[2].length() * 100)/26);

        AlertDialog.Builder uyari = new AlertDialog.Builder(this);

        uyari.setTitle("SONUÇLAR");

        uyari.setMessage("Grup - > " + sonuclar[0]

                + "\n" + "Sonuçlar "
                + "1 - " + sonuclar[1].charAt(0) + "\t"
                + "2 - " + sonuclar[1].charAt(1)+ "\t"
                + "3 - " + sonuclar[1].charAt(2)+ "\t"
                + "4 - " + sonuclar[1].charAt(3)+ "\t"
                + "5 - " + sonuclar[1].charAt(4)+ "\t"
                + "6 - " + sonuclar[1].charAt(5)+ "\t"
                + "7 - " + sonuclar[1].charAt(6)+ "\t"
                + "8 - " + sonuclar[1].charAt(7)+ "\t"
                + "9 - " + sonuclar[1].charAt(8)+ "\t"
                + "10 - " + sonuclar[1].charAt(9)+ "\t"
                + "11 - " + sonuclar[1].charAt(10)+ "\t"
                + "12 - " + sonuclar[1].charAt(11)+ "\t"
                + "13 - " + sonuclar[1].charAt(12)+ "\t"
                + "14 - " + sonuclar[1].charAt(13) + "\t"
                + "15 - " + sonuclar[1].charAt(14)+ "\t"
                + "16 - " + sonuclar[1].charAt(15)+ "\t"
                + "17 - " + sonuclar[1].charAt(16)+ "\t"
                + "18 - " + sonuclar[1].charAt(17)+ "\t"
                + "19 - " + sonuclar[1].charAt(18)+ "\t"
                + "20 - " + sonuclar[1].charAt(19)+ "\t"
                + "21 - " + sonuclar[1].charAt(20)+ "\t"
                + "22 - " + sonuclar[1].charAt(21)+ "\t"
                + "23 - " + sonuclar[1].charAt(22)+ "\t"
                + "24 - " + sonuclar[1].charAt(23)+ "\t"
                + "25 - " + sonuclar[1].charAt(24)+ "\t"
                + "26 - " + sonuclar[1].charAt(25)+ "\t\n"
                + "Dogru Sayısı -> " + sonuclar[2].length()

        );

        uyari.setPositiveButton("Kaydet", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent baslat = new Intent(optik_tara.this,Ogrenci_Sec.class);
                baslat.putExtra("dogru",puan);
                baslat.putExtra("anahtar_id",sonuclar[3]);
                startActivity(baslat);
            }
        });
        uyari.setNegativeButton("Tekrar Dene", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog pencere = uyari.create();
        pencere.show();


    }

    public String[] dogruları_bul(String aTab,String bTab,String grup){

        int id = getIntent().getExtras().getInt("id");


        anahtar_veritabanı as = new anahtar_veritabanı();
        DBController dbController = new DBController(this);
        as = dbController.anahtar_sec(id);

        String anahtar_cevaplar = as.cevaplar;
        String anahtar_grup = as.grup;


        String tick = "\u2714";
        String cross = "\u274C";
        String sonuclar = new String();
        String dogrusayısı = new String();
        String dogru_grup;

        String[] donen  = new  String[4];
        int forb = 12;

        if(grup.equals(anahtar_grup)){
            dogru_grup = "Grup Dogru İşaretlenmiş";
        }
        else{
            dogru_grup = "Grup Boş veya Hatalı !";
        }


        for(int i = 12 ; i >=0 ; i--){


            if(anahtar_cevaplar.charAt(i) == aTab.charAt(i)){
                sonuclar+=tick;
                dogrusayısı+=tick;

            }
            else {
                sonuclar+=cross;
            }

        }

        for(int x = 25 ; x >= 13 ; x-- ){


            if(anahtar_cevaplar.charAt(x) == bTab.charAt(forb)){
                sonuclar+=tick;
                 dogrusayısı+=tick;
            }
            else {
                sonuclar+=cross;
            }
               forb--;
        }


        donen[0] = dogru_grup;
        donen[1] = sonuclar;
        donen[2] = dogrusayısı;
        donen[3] = Integer.toString(id);

        return donen;

    }

}


