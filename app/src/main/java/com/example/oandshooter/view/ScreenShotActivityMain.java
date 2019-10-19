package com.example.oandshooter.view;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.oandshooter.R;
import com.example.oandshooter.utils.MyData;

public class ScreenShotActivityMain extends Activity {
    private static final int REQUEST_SCREENSHOT=59706;
    private MediaProjectionManager mgr;
    Button button;

    //public static URL = "http://digimonk.net/vediocallapp/app/api/check_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mgr=(MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        Log.e("onCreate ScreenShot" ,"onCreate" );
        startActivityForResult(mgr.createScreenCaptureIntent(),
                REQUEST_SCREENSHOT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_SCREENSHOT) {
            if (resultCode==RESULT_OK) {
                Log.e("onActivityResult Shot" ,"onActivityResult" );
                MyData.i=
                        new Intent(this, ScreenshotService.class)
                                .putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                                .putExtra(ScreenshotService.EXTRA_RESULT_INTENT, data);

                startService(MyData.i);
            }
        }

      finish();
    }
}
