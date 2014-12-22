package io.catchlab.kevin.waver_android;

import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.io.IOException;
import android.os.Handler;


public class Waver extends ActionBarActivity {
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = "";
    private static final int COMPLETED = 0;
    private MediaRecorder mRecorder = null;
    private static final long REFRESH_INTERVAL_MS = 21;
    private boolean keepGoing = true;
    private DrawView view;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {

                view.phase += view.phaseShift;
                view.amplitude = Math.max(  mRecorder.getMaxAmplitude()/ 51805.5336f, 0.01f);

                view.invalidate();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waver);

        LinearLayout layout= (LinearLayout) findViewById(R.id.root);
        view = new DrawView(this);
        view.invalidate();
        layout.addView(view);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.aac";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runGameLoop();
            }
        });

        thread.start();

    }



    private void runGameLoop() {
        // update the game repeatedly
        while (keepGoing) {
            long durationMs = redraw();
            try {
                Thread.sleep(Math.max(0, REFRESH_INTERVAL_MS - durationMs));
            } catch (InterruptedException e) {
            }
        }
    }

    private long redraw() {

        long t = System.currentTimeMillis();

        // At this point perform changes to the model that the component will
        // redraw

        updateModel();

        // draw the model state to a buffered image which will get
        // painted by component.paint().
        drawModelToImageBuffer();

        // asynchronously signals the paint to happen in the awt event
        // dispatcher thread
//        component.repaint();

        // use a lock here that is only released once the paintComponent
        // has happened so that we know exactly when the paint was completed and
        // thus know how long to pause till the next redraw.
        waitForPaint();

        // return time taken to do redraw in ms
        return System.currentTimeMillis() - t;
    }

    private void updateModel() {
        // do stuff here to the model based on time
        display_game();
    }

    private void drawModelToImageBuffer() {

    }

    private void waitForPaint() {

    }

    private void display_game() {


        Message msg = new Message();
        msg.what = COMPLETED;
        handler.sendMessage(msg);

//        Log.v("Game", "Display Game" + view.phase);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
