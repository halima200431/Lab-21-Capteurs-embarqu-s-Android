package com.example.lab21.utils;

import android.hardware.Sensor;

public class SensorFormatter {

    /**
     * Transforme les caractéristiques techniques d'un capteur en texte lisible.
     * Cette méthode est séparée du fragment pour garder le code propre.
     */
    public static String format(Sensor sensor) {

        String acquisitionSpeed;

        if (sensor.getMinDelay() > 0) {
            float hz = 1_000_000f / sensor.getMinDelay();
            acquisitionSpeed = String.format("%.2f Hz", hz);
        } else {
            acquisitionSpeed = "Non définie / événement spécial";
        }

        return "ID : " + sensor.getId() + "\n"
                + "Nom : " + sensor.getName() + "\n"
                + "Fabricant : " + sensor.getVendor() + "\n"
                + "Version : " + sensor.getVersion() + "\n"
                + "Type texte : " + sensor.getStringType() + "\n"
                + "Int Type : " + sensor.getType() + "\n"
                + "Résolution : " + sensor.getResolution() + "\n"
                + "Énergie : " + sensor.getPower() + " mA\n"
                + "Maximum Range : " + sensor.getMaximumRange() + "\n"
                + "Min Delay : " + sensor.getMinDelay() + " µs\n"
                + "Vitesse max : " + acquisitionSpeed;
    }
}