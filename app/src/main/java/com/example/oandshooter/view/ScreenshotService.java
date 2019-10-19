
package com.example.oandshooter.view;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.oandshooter.BuildConfig;
import com.example.oandshooter.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ScreenshotService extends Service {
    public static final String TAG = "MyServiceTag";
    private static final int NOTIFY_ID = 9906;
    static final String EXTRA_RESULT_CODE = "resultCode";
    static final String EXTRA_RESULT_INTENT = "resultIntent";
    static final String ACTION_RECORD =
            BuildConfig.APPLICATION_ID + ".RECORD";
    static final String ACTION_SHUTDOWN =
            BuildConfig.APPLICATION_ID + ".SHUTDOWN";
    static final int VIRT_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection projection;
    private VirtualDisplay vdisplay;
    final private HandlerThread handlerThread =
            new HandlerThread(getClass().getSimpleName(),
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;
    private MediaProjectionManager mgr;
    private WindowManager wmgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultData;
    final private ToneGenerator beeper =
            new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    private String currentDate;
    private String d;


    @Override
    public void onCreate() {
        super.onCreate();

//        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
//
//// This schedule a runnable task every 2 minutes
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                handlerThread.start();
//            }
//        }, 0, 2, TimeUnit.MINUTES);
        Log.e("onCreate service" ,"service" );
        mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        wmgr = (WindowManager) getSystemService(WINDOW_SERVICE);

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        Log.e("onStartCommand" ,"service" );
        if (i.getAction() == null) {
            resultCode = i.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData = i.getParcelableExtra(EXTRA_RESULT_INTENT);

            //Code for Show Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startMyOwnForeground2();
            else
                startForeground(1, new Notification());
      //   foregroundify();
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            }*/
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startMyOwnForeground();
            }
            else
            {
                foregroundify();
            }*/


        }if (resultData != null) {
                startCapture();
            } else {
            Log.e("onStartCommand" ,"startCapture else" );
                Intent ui = new Intent(this, ScreenShotActivityMain.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(ui);
            }
        /* else if (ACTION_SHUTDOWN.equals(i.getAction())) {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK);
            stopForeground(true);
            stopSelf();
        }
*/
        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        stopCapture();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    WindowManager getWindowManager() {
        return (wmgr);
    }

    Handler getHandler() {
        return (handler);
    }

    void processImage(final byte[] png) {

        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)

            @Override
            public void run() {


                Date date = new Date();
                d = date.toString();



                //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                //String date = sdf.format(new Date());


                File output = new File(getExternalFilesDir(null),
                        d + ".png");

                try {
                    FileOutputStream fos = new FileOutputStream(output);

                    fos.write(png);
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();

                    MediaScannerConnection.scanFile(ScreenshotService.this,
                            new String[]{output.getAbsolutePath()},
                            new String[]{"image/png"},
                            null);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Exception writing out screenshot", e);
                }



            }

        }.start();


        //  stopCapture();
        }

    private void stopCapture() {
        if (projection != null) {
            projection.stop();
            vdisplay.release();
            projection = null;
        }
    }


    private void startCapture() {
        Log.e("startCapture" ,"startCapture" );
        projection = mgr.getMediaProjection(resultCode, resultData);
        it = new ImageTransmogrifier(this);

        MediaProjection.Callback cb = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                vdisplay.release();
            }
        };

        vdisplay = projection.createVirtualDisplay("andshooter",
                it.getWidth(), it.getHeight(),
                getResources().getDisplayMetrics().densityDpi,
                VIRT_DISPLAY_FLAGS, it.getSurface(), null, handler);
        projection.registerCallback(cb, handler);
    }

    private void foregroundify() {
        Log.e("foregroundify" ,"foregroundify" );
        NotificationCompat.Builder b =
                new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        b.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.app_name));

     /*   b.addAction(R.drawable.ic_record_white_24dp,
                getString(R.string.notify_record),
                buildPendingIntent(ACTION_RECORD));

        b.addAction(R.drawable.ic_eject_white_24dp,
                getString(R.string.notify_shutdown),
                buildPendingIntent(ACTION_SHUTDOWN));
*/
        startForeground(NOTIFY_ID, b.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                /*.setSmallIcon(R.drawable.ic_launcher_foreground)*/
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(NOTIFICATION_CHANNEL_ID));

        startForeground(NOTIFY_ID, notificationBuilder.build());

    }

    private PendingIntent buildPendingIntent(String action) {
        Log.e("buildPendingIntent" ,"buildPendingIntent" );
        Intent i = new Intent(this, getClass());

        i.setAction(action);

        return (PendingIntent.getService(this, 0, i, 0));
    }

    private void startMyOwnForeground2(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

}
