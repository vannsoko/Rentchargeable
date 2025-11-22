package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class GeoPos(val latitude: Double, val longitude: Double)

/**
 * Erwartete (expect) Composable-Funktion zur Anzeige einer Karte.
 * Jede Zielplattform (android, desktop, ios) muss eine tatsächliche (actual)
 * Implementierung für diese Funktion bereitstellen.
 *
 * @param modifier Der Modifier, der auf dieses Composable angewendet wird.
 */
@Composable
expect fun MapView(
    modifier: Modifier,
    locations: List<Pair<String, GeoPos>>,
    onMarkerClick: (String) -> Unit
)


//examples

val munichLocations = listOf(
    "Marienplatz" to GeoPos(48.137154, 11.575382),
    "Englischer Garten" to GeoPos(48.1643, 11.6053),
    "Deutsches Museum" to GeoPos(48.1306, 11.5855)
)

fun onMarkerClick(markerLabel: String) {
        // Hier kannst du implementieren, was passieren soll, wenn auf einen Marker geklickt wird.
        // Vorerst geben wir den Namen nur auf der Konsole aus.
        println("Marker angeklickt: $markerLabel")
    }

@Composable
fun MapViewTest (modifier: Modifier) {
    MapView(
        modifier,
        munichLocations,
        ::onMarkerClick
    )
}

