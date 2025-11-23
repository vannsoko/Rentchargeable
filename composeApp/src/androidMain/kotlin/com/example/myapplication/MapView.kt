@file:JvmName("AndroidMapViewKt")

package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView as OsmdroidMapView
import org.osmdroid.views.overlay.Marker

@Composable
actual fun MapView(
    modifier: Modifier,
    locations: List<Pair<String, GeoPos>>,
    onMarkerClick: (String) -> Unit,
    onMapClick: (lat: Double, long: Double) -> Unit // <-- Add this parameter!
) {
    val onMarkerClickState by rememberUpdatedState(onMarkerClick)
    val onMapClickState by rememberUpdatedState(onMapClick)

    AndroidView(
        modifier = modifier,
        factory = { context ->
            OsmdroidMapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.0) // Wie in der Desktop-Version
                controller.setCenter(GeoPoint(48.1351, 11.5820)) // München
            }
        },
        update = { mapView ->
            // Der Update-Block wird bei jeder neuen Zusammensetzung ausgeführt.

            // 1. Alte Marker entfernen, um Duplikate zu vermeiden.
            val oldMarkers = mapView.overlays.filterIsInstance<Marker>()
            mapView.overlays.removeAll(oldMarkers)

            // 2. Neue Marker für jeden Standort hinzufügen.
            locations.forEach { (name, geoPos) ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(geoPos.latitude, geoPos.longitude)
                    title = name
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                    // 3. Setze den Klick-Listener direkt auf dem Marker (der Standardweg).
                    setOnMarkerClickListener { clickedMarker, _ ->
                        // Rufe die Callback-Funktion über den sicheren State auf.
                        onMarkerClickState(clickedMarker.title)
                        // Zeige das Info-Fenster des Markers an.
                        clickedMarker.showInfoWindow()

                        // true: Wir haben das Ereignis verarbeitet.
                        true
                    }
                }
                mapView.overlays.add(marker)
            }

            // 4. Karte neu zeichnen, um die Änderungen anzuzeigen.
            // Set a listener for map clicks, if none present (and remove and replace if already set)
            mapView.setOnTouchListener { v, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    val proj = mapView.projection
                    val geoPoint = proj.fromPixels(event.x.toInt(), event.y.toInt())
                    onMapClickState(geoPoint.latitude, geoPoint.longitude)
                }
                false
            }

            mapView.invalidate()
        }
    )
}
