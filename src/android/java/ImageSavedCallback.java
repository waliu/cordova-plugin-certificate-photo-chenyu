package certificate.photo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;


public class ImageSavedCallback implements ImageCapture.OnImageSavedCallback {
    CertificatePhotoActivity certificatePhotoActivity;

    public ImageSavedCallback(CertificatePhotoActivity context) {
        this.certificatePhotoActivity = context;
    }

    @Override
    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
        certificatePhotoActivity.sendBase64(outputFileResults.getSavedUri().toString());
    }

    @Override
    public void onError(@NonNull ImageCaptureException exception) {
        Log.d("保存失败", exception.getMessage());
    }
}
