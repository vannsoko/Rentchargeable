package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 
 * @param modifier Modifier for sizing etc.
 * @param locations List of map points [(latitude, longitude)]
 * @param selectedLocation The chosen location, drawn differently (nullable)
 * @param onMarkerClick Called when a marker is tapped (title or info)
 * @param onMapClick Called when user clicks the map elsewhere
 */
@Composable
expect fun MapView(
    modifier: Modifier,
    locations: List<Pair<Double, Double>>,
    selectedLocation: Pair<Double, Double>? = null,
    onMarkerClick: (String) -> Unit = {},
    onMapClick: (lat: Double, long: Double) -> Unit = { _, _ -> }
)

