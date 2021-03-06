package com.gaidnative;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.provider.Settings.Secure;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;

public class GAIDExtensionContext extends FREContext {
    public static final String TAG = "GAIDANE";
    public Activity act;
    
    @Override
    public void dispose() {
    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
        functionMap.put(GetGAIDFunction.NAME, new GetGAIDFunction());
        return functionMap;
    }
    
    public void getGaidThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(act.getApplicationContext());
                    
                    JSONObject gaidJson = new JSONObject();
                    try {
                        gaidJson.put("gaid", adInfo.getId());
                        gaidJson.put("isLAT", adInfo.isLimitAdTrackingEnabled());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dispatchStatusEventAsync("getGoogleAdvertisingId", gaidJson.toString());
                } catch (Exception e) {
                    JSONObject androidId = new JSONObject();
                    try {
                        androidId.put("androidId", Secure.getString(act.getContentResolver(), Secure.ANDROID_ID));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    dispatchStatusEventAsync("getGoogleAdvertisingId", androidId.toString());
                }
            }
        }).start();
    }
}
