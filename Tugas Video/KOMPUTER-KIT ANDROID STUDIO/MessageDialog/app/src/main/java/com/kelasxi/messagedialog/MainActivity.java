package com.kelasxi.messagedialog;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ActivityLifecycle";
    private TextView statusTextView;
    private StringBuilder lifecycleLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi TextView dan StringBuilder untuk menampilkan log
        statusTextView = findViewById(R.id.statusTextView);
        lifecycleLog = new StringBuilder();

        // Log untuk onCreate
        String message = "onCreate() dipanggil - Aktivitas sedang dibuat";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onCreate()");
        updateDisplay();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Log untuk onStart
        String message = "onStart() dipanggil - Aktivitas akan menjadi visible";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onStart()");
        updateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Log untuk onResume
        String message = "onResume() dipanggil - Aktivitas siap berinteraksi dengan user";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onResume()");
        updateDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Log untuk onPause
        String message = "onPause() dipanggil - Aktivitas kehilangan fokus";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onPause()");
        updateDisplay();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Log untuk onStop
        String message = "onStop() dipanggil - Aktivitas tidak lagi visible";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Log untuk onRestart
        String message = "onRestart() dipanggil - Aktivitas akan dimulai ulang";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onRestart()");
        updateDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Log untuk onDestroy
        String message = "onDestroy() dipanggil - Aktivitas akan dihancurkan";
        Log.d(TAG, message);
        System.out.println(TAG + ": " + message);

        addToLog("onDestroy()");
    }

    // Method helper untuk menambahkan log ke StringBuilder
    private void addToLog(String method) {
        if (lifecycleLog.length() > 0) {
            lifecycleLog.append(" → ");
        }
        lifecycleLog.append(method);
    }

    // Method untuk mengupdate display di TextView
    private void updateDisplay() {
        if (statusTextView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTextView.setText("Urutan Lifecycle:\n" + lifecycleLog.toString());
                }
            });
        }
    }
}