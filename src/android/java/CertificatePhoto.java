package certificate.photo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class echoes a string called from JavaScript.
 */
public class CertificatePhoto extends CordovaPlugin {
    //请求权限的code码
    private static final int PERMISSION_REQUEST_CODE = 500;
    // 类标志
    private static final String TAG = "CordovaPlugin.CertificatePhoto";
    //返回对象
    private CallbackContext startCameraCallbackContext;
    //权限数组
    protected String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startCamera")) {
            JSONObject ages = args.getJSONObject(0);
            this.startCamera(ages, callbackContext);
            return true;
        }
        return false;
    }

    protected void pluginInitialize() {
        //请求权限
        cordova.requestPermissions(this, PERMISSION_REQUEST_CODE, needPermissions);
    }


    private void startCamera(JSONObject ages, CallbackContext callbackContext) throws JSONException {

        this.startCameraCallbackContext = callbackContext;
        if (!hasPermissions()) {
            callbackContext.error("没有权限");
            return;
        }

        Intent intent = new Intent(this.cordova.getActivity(), CertificatePhotoActivity.class);

        intent.putExtra("section", ages.getBoolean("section"));

        this.cordova.startActivityForResult(this, intent, 10001);

        //
        JSONObject sendJsonObject = new JSONObject();

        sendJsonObject.put("type", "success");

        this.sendPluginResult(callbackContext, sendJsonObject);
    }

    public boolean hasPermissions() {
        for (String needPermission : needPermissions) {
            if (!cordova.hasPermission(needPermission)) {
                return false;
            }
        }
        return true;
    }


    public void sendPluginResult(CallbackContext startCameraCallbackContext, JSONObject jsonObject) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);

        pluginResult.setKeepCallback(true);

        startCameraCallbackContext.sendPluginResult(pluginResult);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        JSONObject sendJsonObject = new JSONObject();
        switch (resultCode) {
            case 10001:
                try {
                    Uri mUri = Uri.parse(data.getStringExtra("imgPath"));

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cordova.getActivity().getContentResolver(), mUri);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    byte[] bytes = stream.toByteArray();

                    sendJsonObject.put("type", "base64");

                    sendJsonObject.put("base64", Base64.encodeToString(bytes, Base64.DEFAULT));

                    sendPluginResult(startCameraCallbackContext, sendJsonObject);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

}
