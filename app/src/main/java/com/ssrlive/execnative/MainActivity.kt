package com.ssrlive.execnative

import com.ssrlive.execnative.NativeWrapper.runAppNative
import com.ssrlive.execnative.NativeWrapper.stopAppNative
import com.ssrlive.execnative.NativeWrapper.stringFromJNI
import androidx.appcompat.app.AppCompatActivity
import com.ssrlive.execnative.NativeWrapper
import com.ssrlive.execnative.MainActivity.MyThread
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.ssrlive.execnative.R
import android.widget.TextView
import java.io.*
import java.util.ArrayList
import kotlin.Throws

class MainActivity : AppCompatActivity() {
    internal inner class MyThread : Thread() {
        override fun run() {
            super.run()
            val cmd = ArrayList<String?>()
            cmd.add("appnamename")
            cmd.add("sdfsdfsdf")
            cmd.add("--help")
            cmd.add("sdfsdf sdf")
            cmd.add("-c")
            cmd.add("99")
            cmd.add("--deadloop")
            runAppNative(cmd)
        }

        override fun destroy() {
            // super.destroy();
            stopAppNative()
        }
    }

    private var myThread: MyThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        val tv = findViewById<TextView>(R.id.sample_text)
        tv.text = stringFromJNI()
        val btn = findViewById<Button>(R.id.btnCmd)
        btn.setOnClickListener { runApp() }
        val btnRunThread = findViewById<Button>(R.id.btnRunThread)
        btnRunThread.setOnClickListener {
            if (myThread != null && myThread!!.isAlive) {
                myThread!!.destroy()
                myThread = null
            } else {
                myThread = MyThread()
                myThread!!.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (myThread != null && myThread!!.isAlive) {
            myThread!!.destroy()
            myThread = null
        }
    }

    private fun runApp() {
        try {
            execCmd(applicationInfo.nativeLibraryDir + "/libmycommand.so")
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun execCmd(cmd: String) {
        val runtime = Runtime.getRuntime()
        val process = runtime.exec(cmd)
        val `is` = process.inputStream
        val isr = InputStreamReader(`is`)
        val br = BufferedReader(isr)
        var line: String? = null
        while (null != br.readLine().also { line = it }) {
            Log.e("########", line!!)
        }
        try {
            process.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyBigDataToSD(assetPath: String, outFilePath: String) {
        val myInput = this.assets.open(assetPath)
        val myOutput: OutputStream = FileOutputStream(outFilePath)
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        myInput.close()
        myOutput.flush()
        myOutput.close()
    }
}