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

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button btn = findViewById(R.id.btnCmd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runApp();
            }
        });
    }

    private void runApp() {
        try {
            String cmdName = "mycommand";

            String assetPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                assetPath = Build.SUPPORTED_ABIS[0];
            } else {
                assetPath = Build.CPU_ABI; // noinspection deprecation
            }
            assetPath = assetPath + "/" + cmdName;

            Context context = getApplicationContext();
            String exePath = context.getFilesDir() + "/" + cmdName;

            copyBigDataToSD(assetPath, exePath);

            File exe_file = new File(exePath);
            exe_file.setExecutable(true, true);

            execCmd(exePath);
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
