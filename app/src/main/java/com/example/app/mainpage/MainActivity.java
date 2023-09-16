package com.example.app.mainpage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.DialogInterface;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.example.app.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.AlimeHelper;
import utils.PermissionsUtil;


import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_MICROPHONE_PERMISSION = 1001;
    private static final int REQUEST_STORAGE_PERMISSION = 1002;
    private static final int PERMISSIONS_REQUEST_CODE = 12345;
    String[] requiredPermissions = {
            Manifest.permission.RECORD_AUDIO,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
        showPermanentDeniedDialog();


        // Load the top fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_top, new TopFragment())
                    .commit();
        }

        // Load the bottom fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_bottom, new BottomFragment())
                    .commit();
        }
    }


    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                // 这里提供一个解释为什么需要这个权限
                showExplanationDialog("语音权限", "我们需要这个权限来访问您的麦克风", Manifest.permission.RECORD_AUDIO, REQUEST_MICROPHONE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_MICROPHONE_PERMISSION);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 同样为存储权限提供解释
                showExplanationDialog("存储权限", "我们需要这个权限来访问您的存储空间", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }


    private void showExplanationDialog(String title, String message, final String permission, final int requestCode) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        // 用户已经永久拒绝了这个权限
                        showPermanentDeniedDialog();
                        return;
                    } else {
                        Toast.makeText(this, "您需要授予所有权限以使用此应用", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            // 如果所有权限都被授权，可以继续应用的其他初始化
        }
    }

    private void checkForPermanentlyDeniedPermissions() {
        boolean isAnyPermissionPermanentlyDenied = false;

        // 检查语音权限是否被永久拒绝
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            isAnyPermissionPermanentlyDenied = true;
        }
        if (isAnyPermissionPermanentlyDenied) {
            showPermanentDeniedDialog();
        }
    }

    private void showPermanentDeniedDialog() {
        // 先检查所有关键权限是否都已经被授权了
        boolean areAllPermissionsGranted = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            areAllPermissionsGranted = false;
        }
        // 如果所有关键权限都已经被授权，直接返回，不显示弹窗
        if (areAllPermissionsGranted) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("权限被拒绝")
                .setMessage("您已经拒绝了一些必要的权限，这可能会影响应用的功能。请到设置中手动授予权限。")
                .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

}