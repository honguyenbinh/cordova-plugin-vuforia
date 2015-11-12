package com.bdnetwork.vuforia;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.bdnetwork.vuforia.app.ImageTargets;

public class VuforiaPlugin extends CordovaPlugin {
    static final String LOGTAG = "Cordova Vuforia Plugin";

    static final int IMAGE_REC_REQUEST = 1;
    CallbackContext callback;

    public VuforiaPlugin() {
    }

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(LOGTAG, "Plugin initialized.");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String targetFile = args.getString(0);
        String targets = args.getJSONArray(1).toString();
        String overlayText = args.getString(2);
        String vuforiaLicense = args.getString(3);

        Log.d(LOGTAG, "Args: "+args);
        Log.d(LOGTAG, "Text: "+overlayText);
        Log.d(LOGTAG, "License: "+vuforiaLicense);

        callback = callbackContext;

        Context context =  cordova.getActivity().getApplicationContext();

        Intent intent = new Intent(context, ImageTargets.class);
        intent.putExtra("IMAGE_TARGET_FILE", targetFile);
        intent.putExtra("IMAGE_TARGETS", targets);
        intent.putExtra("OVERLAY_TEXT", overlayText);
        intent.putExtra("LICENSE_KEY", vuforiaLicense);

        // Launch a new activity with Vuforia in it. Expect it to return a result.
        cordova.startActivityForResult(this, intent, IMAGE_REC_REQUEST);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;

        if (data == null)
        {
            name = "ERROR";
        } else {
            name = data.getStringExtra("name");
        }

        Log.d(LOGTAG, "Plugin Received: '" + name + "' from Vuforia.");

        // Check which request we're responding to
        if (requestCode == IMAGE_REC_REQUEST) {
            // Make sure the request was successful
            if (resultCode == 0) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("imageName", name);
                    callback.sendPluginResult(new PluginResult(PluginResult.Status.OK, json));
                } catch( JSONException e ) {
                    Log.d(LOGTAG, "JSON ERROR: " + e);
                }
            } else {
                Log.d(LOGTAG, "Error - received code: " + resultCode);
            }
        }
    }
}