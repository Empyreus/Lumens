package com.empyreandesign.lumens;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.empyreandesign.lumens.R;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

/**
 * Activity which gives hint for manual pushlink. needs to add <activity
 * android:theme="@android:style/Theme.Dialog" /> in manifest file
 */

public class PushLink extends Activity {
    private static final int MAX_TIME = 30;
    private ProgressBar pbar;
    private PHHueSDK phHueSDK;
    private boolean isDialogShowing;
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> arg0) {
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint arg0) {
        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {
            if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                incrementProgress();
            } else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                incrementProgress();

                if (!isDialogShowing) {
                    isDialogShowing = true;
                    PushLink.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PushLink.this);
                            builder.setMessage(message).setNeutralButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });

                            builder.create();
                            builder.show();
                        }
                    });
                }

            }

        } // End of On Error

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_link);
        setTitle("PushLink");
        isDialogShowing = false;
        phHueSDK = PHHueSDK.getInstance();

        pbar = (ProgressBar) findViewById(R.id.countdownPB);
        pbar.setMax(MAX_TIME);

        phHueSDK.getNotificationManager().registerSDKListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        phHueSDK.getNotificationManager().unregisterSDKListener(listener);
    }

    public void incrementProgress() {
        pbar.incrementProgressBy(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
    }

}
