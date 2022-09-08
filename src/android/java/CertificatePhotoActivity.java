package certificate.photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.certificate.photo.R;
import com.google.common.util.concurrent.ListenableFuture;


import java.util.concurrent.ExecutionException;


public class CertificatePhotoActivity extends AppCompatActivity {

    private Camera camera;
    //预览view
    private PreviewView previewView;
    //相机服务
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //摄像对象
    private ImageCapture imageCapture;
    //截图的宽度
    private int mScreenWidth;
    //截图的高度
    private int mScreenHeight;
    //身份证头像
    private ImageView positiveImg;
    //背面
    private ImageView backImg;

    private TextView tvHint;

    private Shadow shadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_certificate_photo);

        //获取 预览view
        previewView = findViewById(R.id.previewView);
        //遮罩层
        shadow = findViewById(R.id.shadow);
        //显示证明的头像
        positiveImg = findViewById(R.id.positive_img);
        //显示背面的头像
        backImg = findViewById(R.id.back_img);

        tvHint=(TextView) findViewById(R.id.tv_hint);


        Intent intent = getIntent();
        //获取数据
        boolean section = intent.getBooleanExtra("section",false);

        if (section){
            backImg.setVisibility(View.GONE);
            tvHint.setText("请把身份证正面放入取景框,水平拍摄");
        }else {
            positiveImg.setVisibility(View.GONE);
            tvHint.setText("请把身份证背面放入取景框,水平拍摄");
        }

        //初始化相机保存参数
        imageCapture = new ImageCapture.Builder().setTargetRotation(
                this.getWindowManager().getDefaultDisplay().getRotation()).build();

        //获取相机服务
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //添加相机服务监听
        cameraProviderFuture.addListener(() -> {
            try {
                // 一个单例，可用于将相机的生命周期绑定到LifecycleOwner 应用程序进程中的任何一个。
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                // 将单例绑定 view 上
                bindPreview(cameraProvider);
                // 将单例绑定 摄像对象上。
                bindImageCapture(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        getScreenMetrics(this);

    }


    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        //图片浏览对象
        Preview preview = new Preview.Builder()
                .build();
        // 设置摄像头的参数
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        //将界面的 previewView 设置到 SurfaceProvider 中
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        //将摄像头摄像绑定到界面上
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);

    }

    void bindImageCapture(@NonNull ProcessCameraProvider cameraProvider) {

        //摄像对象
        imageCapture =
                new ImageCapture.Builder()
//                        .setTargetRotation(view.getDisplay().getRotation())

                        .setTargetResolution(new Size(shadow.shadowHeight, shadow.shadowWidth))
                        .build();
        //设置拍照 摄像头的参数
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        //将摄像头捕捉的画面绑定到拍照对象上
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageCapture);

    }

    public void takePicture(View view) {
//        Log.d("截图1", "图片的尺寸：" + mScreenWidth + "," + mScreenHeight);
//        Log.d("截图2", "图片的尺寸：" + shadow.shadowWidth + "," + shadow.shadowHeight);
        // 保存图片的参数
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        // 保存 输出file 的参数
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
                .build();
        // 保存图片
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageSavedCallback(this));
        //

    }


    private void getScreenMetrics(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }


    public void sendBase64(String imgPath) {
        Intent intent = getIntent();
        intent.putExtra("imgPath", imgPath);
        setResult(10001, intent);
        finish();
    }


}