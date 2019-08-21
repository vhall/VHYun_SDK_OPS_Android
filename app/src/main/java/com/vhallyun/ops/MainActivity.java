package com.vhallyun.ops;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vhall.framework.VhallSDK;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Hank on 2017/12/8.
 */
public class MainActivity extends Activity {

    TextView tv_appid;
    private static final String TAG = "VHLivePusher";
    private static final String KEY_CHAT_ID = "channelId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LSS_ID = "lssId";
    SharedPreferences sp;
    EditText edtChennelId, edtToken, edtLssId;
    private String channelId;
    private String token;
    private String roomId;
    private static final int REQUEST_UPLOAD = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        token = sp.getString(KEY_TOKEN, "");
        channelId = sp.getString(KEY_CHAT_ID, "");
        roomId = sp.getString(KEY_LSS_ID, "");
        tv_appid = this.findViewById(R.id.tv_appid);
        tv_appid.setText(VhallSDK.getInstance().getAPP_ID());
        edtChennelId = findViewById(R.id.edt_channel_id);
        edtToken = findViewById(R.id.edt_token);
        edtLssId = findViewById(R.id.edt_lss_id);
        edtToken.setText(token);
        edtChennelId.setText(channelId);
        edtLssId.setText(roomId);

    }


    //需要文件读取权限
    public void uploadDocument(View view) {
        token = edtToken.getText().toString().trim();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "token need not null!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getUploadPermission()) {
            Intent intent = new Intent(this, UploadDocumentActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
    }

    public void showDoc(View view) {
        token = edtToken.getText().toString().trim();
        channelId = edtChennelId.getText().toString().trim();
        if (TextUtils.isEmpty(channelId) || TextUtils.isEmpty(token)) {
            return;
        }
        roomId = edtLssId.getText().toString().trim();
        sp.edit().putString(KEY_CHAT_ID, channelId).putString(KEY_TOKEN, token).putString(KEY_LSS_ID, roomId).commit();
        Intent intent = new Intent(this, DocActivity.class);
        intent.putExtra("channelId", channelId);
        if (!TextUtils.isEmpty(roomId)) {
            intent.putExtra("roomId", roomId);
        }
        intent.putExtra("token", token);
        startActivity(intent);
    }

    private boolean getUploadPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_UPLOAD);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_UPLOAD) {
            Log.i(TAG, grantResults.length + ":" + grantResults[0]);
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "get REQUEST_PUSH permission success");
                Intent intent = new Intent(this, UploadDocumentActivity.class);
                startActivity(intent);
            }

        }
    }
}
