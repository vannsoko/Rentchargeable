@file:JvmName("AndroidMapViewKt")

package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView as OsmdroidMapView
import org.osmdroid.views.overlay.Marker

@Composable
actual fun MapView(
    modifier: Modifier,
    locations: List<Pair<Double, Double>>,
    selectedLocation: Pair<Double, Double>?,
    onMarkerClick: (String) -> Unit,
    onMapClick: (Double, Double) -> Unit
) {
    val onMarkerClickState by rememberUpdatedState(onMarkerClick)
    val onMapClickState by rememberUpdatedState(onMapClick)
    val context = LocalContext.current

    fun getDefaultMarker(): Drawable? =
        context.getDrawable(org.osmdroid.library.R.drawable.marker_default)

    fun getRedMarker(): Drawable? =
        context.getDrawable(org.osmdroid.library.R.drawable.marker_default)?.apply {
            setTint(Color.RED)
        }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            OsmdroidMapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.0)
                controller.setCenter(GeoPoint(48.1351, 11.5820)) // Default: Munich
            }
        },
        update = { mapView ->
            // Remove old markers
            val oldMarkers = mapView.overlays.filterIsInstance<Marker>()
            mapView.overlays.removeAll(oldMarkers)

            // Draw all regular markers (blue)
            locations.forEachIndexed { i, (lat, lng) ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(lat, lng)
                    title = "Point ${i+1}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = getDefaultMarker()
                    setOnMarkerClickListener { clickedMarker, _ ->
                        onMarkerClickState(clickedMarker.title ?: "")
                        clickedMarker.showInfoWindow()
                        true
                    }
                }
                mapView.overlays.add(marker)
            }
            // Draw selected marker (red) if not already in locations
            selectedLocation?.let { (lat, lng) ->
                val duplicate = locations.any { it.first == lat && it.second == lng }
                if (!duplicate) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(lat, lng)
                        title = "Selected"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        icon = getRedMarker()
                        setOnMarkerClickListener { clickedMarker, _ ->
                            onMarkerClickState("Selected")
                            clickedMarker.showInfoWindow()
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            }
            // Map click: notify where user tapped
            mapView.setOnTouchListener { _, event ->
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
