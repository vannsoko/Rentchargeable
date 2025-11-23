package com.example.myapplication

import android.app.Application
import org.osmdroid.config.Configuration

/**
 * Diese Klasse wird beim Start der Android-App einmalig ausgeführt.
 * Sie ist im AndroidManifest.xml über `android:name=".MainApplication"` registriert.
 * Ihre Hauptaufgabe hier ist die Konfiguration von osmdroid.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Lade die osmdroid-Konfiguration.
        // Der Context (hier 'this') wird benötigt, um den Speicherort für den Cache zu finden.
        Configuration.getInstance().load(
            this,
            getSharedPreferences(packageName + "_osmdroid", MODE_PRIVATE)
        )

        // Setze den zwingend erforderlichen User-Agent. Ohne diesen werden OSM-Server die
        // Anfragen blockieren, und die Karte bleibt leer.
        Configuration.getInstance().userAgentValue = "Rentchargeable App/1.0"
    }
}
