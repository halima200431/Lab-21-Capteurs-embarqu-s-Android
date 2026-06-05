package com.example.lab21.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lab21.utils.SensorEnvironment;
import com.example.lab21.utils.UiKit;

public class CompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private TextView directionView;
    private TextView statusView;

    private final float[] gravityValues = new float[3];
    private final float[] magneticValues = new float[3];

    private boolean hasGravity = false;
    private boolean hasMagnetic = false;

    private float simulatedDegree = 0f;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup parent,
            @Nullable Bundle savedInstanceState) {

        sensorManager = (SensorManager)
                requireActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        ScrollView scrollView = new ScrollView(requireContext());
        LinearLayout root = UiKit.createScrollableRoot(requireContext(), scrollView);

        root.addView(UiKit.title(requireContext(), "Boussole numérique"));
        root.addView(UiKit.subtitle(
                requireContext(),
                "Correction : boussole simulée sur émulateur, réelle sur téléphone."
        ));

        directionView = UiKit.valueChip(requireContext(), "Direction : --");
        statusView = UiKit.body(requireContext(), "Initialisation...");

        LinearLayout card = UiKit.createCard(requireContext());
        card.addView(directionView);
        card.addView(statusView);

        root.addView(card);

        return scrollView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SensorEnvironment.isEmulator()) {
            statusView.setText("Émulateur détecté : rotation de boussole simulée.");
            startSimulation();
            return;
        }

        if (accelerometer == null || magnetometer == null) {
            statusView.setText("Capteur manquant. Simulation activée.");
            startSimulation();
            return;
        }

        statusView.setText("Capteurs réels actifs : accéléromètre + magnétomètre.");

        sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            );

        sensorManager.registerListener(
                this,
                magnetometer,
                SensorManager.SENSOR_DELAY_UI
            );
    }

    private void startSimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simulatedDegree += 12f;

                if (simulatedDegree >= 360f) {
                    simulatedDegree = 0f;
                }

                directionView.setText(
                        String.format("%.1f°", simulatedDegree)
                                + "\n" + SensorEnvironment.directionName(simulatedDegree)
                );

                handler.postDelayed(this, 900);
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3);
            hasGravity = true;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, 3);
            hasMagnetic = true;
        }

        if (hasGravity && hasMagnetic) {
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];

            boolean success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    gravityValues,
                    magneticValues
            );

            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuthDegrees = (float) Math.toDegrees(orientation[0]);

                if (azimuthDegrees < 0) {
                    azimuthDegrees += 360;
                }

                directionView.setText(
                        String.format("%.1f°", azimuthDegrees)
                                + "\n" + SensorEnvironment.directionName(azimuthDegrees)
                );
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}