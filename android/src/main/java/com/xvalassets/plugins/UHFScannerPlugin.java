package com.xvalassets.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.Arrays;
import java.util.List;

import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import cn.pda.serialport.Tools;
import android.os.Build;
@CapacitorPlugin(name = "UHFScanner")
public class UHFScannerPlugin extends Plugin {
    private static final int f1 = 131;
    private static final int f2 = 132;
    private static final int f3 = 133;
    private static final int f4 = 134;
    private static final int f5 = 135;
    private static final int f6 = 136;
    private static final int f7 = 137;
    private static final int[] allowedKeys = {f1, f2, f3, f4, f5, f6, f7};

    private String prefix = "";

    private String suffix = "";
    private long startTime = 0;
    public static UHFRManager mUhfrManager;//uhf
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String barcode = msg.getData().getString("data");
                    String rssi = msg.getData().getString("rssi");
                    if (barcode == null || barcode.length() == 0) {
                        return;
                    }
                     notifyListeners("BroadcastReceiverEvent", new JSObject().put("result", barcode), true);
                    break;
            }
        }
    };
    private final long TIMEOUT_DELAY = 1000; // Adjust timeout delay as needed

    private Runnable runnable_MainActivity = new Runnable() {
        @Override
        public void run() {
            List<Reader.TAGINFO> list1;
            list1 = mUhfrManager.tagInventoryByTimer((short) 50);
            String data;
            if(list1!= null && list1.size()>0){
                Log.d("Data received", list1.toString());
                for (Reader.TAGINFO tfs : list1) {
                    byte[] epcdata = tfs.EpcId;
                    data = Tools.Bytes2HexString(epcdata, epcdata.length);
                    if(data.startsWith(prefix) && data.endsWith(suffix)){
                        Util.play(1, 0);
                        int rssi = tfs.RSSI;
                        Message msg = new Message();
                        msg.what = 1;
                        Bundle b = new Bundle();
                        b.putString("data", data);
                        b.putString("rssi", rssi + "");
                        msg.setData(b);
                        handler.sendMessage(msg);
                    }
                }
            }
            handler.postDelayed(runnable_MainActivity, 500);
        }
    };

    private boolean keyUpFlag = true;
    private final BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyUpFlag && keyDown && System.currentTimeMillis() - startTime > 500) {
                keyUpFlag = false;
                startTime = System.currentTimeMillis();
                Log.i("keyCode", Integer.toString(keyCode));
                notifyListeners("buttonClicked", new JSObject().put("key", keyCode), true);
            } else if (keyDown) {
                startTime = System.currentTimeMillis();
            } else {
                keyUpFlag = true;
            }
        }
    };

    @Override
    public void load() {
        super.load();
        Util.initSoundPool(getContext());
    }

    @PluginMethod
    public void scanInit(PluginCall call) {
        int readPower = call.getInt("readPower",30);
        prefix= call.getString("prefix","");
        suffix= call.getString("suffix","");
        if (mUhfrManager == null) {
            mUhfrManager = UHFRManager.getInstance();// Init Uhf module
            if(readPower!=0) {
               mUhfrManager.setPower(readPower,30);
            }
            try {
                getContext().unregisterReceiver(keyReceiver);
            } catch (Exception ignored) {
            }
            getContext().registerReceiver(keyReceiver, new IntentFilter("android.rfid.FUN_KEY"));
        }
        call.resolve();
    }
    @PluginMethod
    public void scanDestroy(PluginCall call) {
        if(mUhfrManager != null) {
            mUhfrManager.close();
            mUhfrManager = null;
        }
        call.resolve();
    }


    @PluginMethod
    public void beginScan(PluginCall call) {
        if (mUhfrManager != null) {
            mUhfrManager.setGen2session(false);
            handler.postDelayed(runnable_MainActivity, 500);
        }
        call.resolve();
    }

    @PluginMethod
    public void stopScan(PluginCall call) {
        if (mUhfrManager != null) {
            mUhfrManager.asyncStopReading();
            handler.removeCallbacks(runnable_MainActivity);
        }
        call.resolve();
    }

}
