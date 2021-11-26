package com.ssrlive.execnative;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();

            ArrayList<String> cmd = new ArrayList<String>();
            cmd.add("appnamename");
            cmd.add("sdfsdfsdf");
            cmd.add("--help");
            cmd.add("sdfsdf sdf");
            cmd.add("-c");
            cmd.add("99");
            cmd.add("--deadloop");

            NativeWrapper.runAppNative(cmd);
        }

        @Override
        public void destroy() {
            // super.destroy();
            NativeWrapper.stopAppNative();
        }
    }

    private MyThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(NativeWrapper.stringFromJNI());

        Button btn = findViewById(R.id.btnCmd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runApp();
            }
        });

        Button btnRunThread = findViewById(R.id.btnRunThread);
        btnRunThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myThread!=null && myThread.isAlive()) {
                    myThread.destroy();
                    myThread = null;
                } else {
                    myThread = new MyThread();
                    myThread.start();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThread!=null && myThread.isAlive()) {
            myThread.destroy();
            myThread = null;
        }
    }

    private void runApp() {
        try {
            execCmd(getApplicationInfo().nativeLibraryDir + "/libmycommand.so");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void execCmd(String cmd) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        while (null != (line = br.readLine())) {
            Log.e("########", line);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void copyBigDataToSD(String assetPath, String outFilePath) throws IOException {
        InputStream myInput = this.getAssets().open(assetPath);
        OutputStream myOutput = new FileOutputStream(outFilePath);
        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }
}
