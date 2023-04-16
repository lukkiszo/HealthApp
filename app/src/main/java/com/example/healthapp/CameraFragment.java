package com.example.healthapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class CameraFragment extends Fragment {
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private String stringResult = "";
    private Button takePhotoButton;
    public static String valueRead = "";

    private void textRecognizer() {
        textRecognizer = new TextRecognizer.Builder(Objects.requireNonNull(getContext())).build();
        cameraSource = new CameraSource.Builder(Objects.requireNonNull(getContext()), textRecognizer)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull @NotNull Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < sparseArray.size(); i++){
                    TextBlock textBlock = sparseArray.valueAt(i);
                    if (textBlock != null && textBlock.getValue() != null){
                        stringBuilder.append(textBlock.getValue() + " ");
                    }
                }

                String stringFullText = stringBuilder.toString();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringResult = stringFullText;
                    }
                });

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        surfaceView = view.findViewById(R.id.camera2View);
        takePhotoButton = view.findViewById(R.id.buttonTakePhoto);

        Fragment thisFragment = this;

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueRead = stringResult;
                TextRecognitionActivity.setValue(valueRead);
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
            }
        });
        textRecognizer();
        super.onViewCreated(view, savedInstanceState);
    }
}