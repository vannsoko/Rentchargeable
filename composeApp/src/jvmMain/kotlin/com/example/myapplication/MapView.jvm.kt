package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.OSMTileFactoryInfo
import org.jxmapviewer.input.PanMouseInputListener
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor
import org.jxmapviewer.viewer.DefaultTileFactory
import org.jxmapviewer.viewer.DefaultWaypoint
import org.jxmapviewer.viewer.GeoPosition
import org.jxmapviewer.viewer.WaypointPainter
import java.util.HashSet

private class CustomWaypoint(val label: String, position: GeoPosition) : DefaultWaypoint(position)

/**
 * Die tatsächliche (actual) Implementierung von [MapView] für die Desktop-Plattform (JVM),
 * die die JXMapViewer2-Bibliothek verwendet.
 */
@Composable
actual fun MapView(modifier: Modifier, locations: List<Pair<String, GeoPos>>, onMarkerClick: (String) -> Unit) {
    SwingPanel(
        factory = {
            val mapViewer = JXMapViewer()

            // Dieser Konstruktor setzt den User-Agent korrekt - eine saubere Lösung!
            val info = OSMTileFactoryInfo("RentChargeable App", "https://a.tile.openstreetmap.org")
            val tileFactory = DefaultTileFactory(info)
            mapViewer.tileFactory = tileFactory

            // WIEDERHERGESTELLT: Interaktion für Verschieben und Zoomen hinzufügen
            val panListener = PanMouseInputListener(mapViewer)
            mapViewer.addMouseListener(panListener)
            mapViewer.addMouseMotionListener(panListener)

            val zoomListener = ZoomMouseWheelListenerCursor(mapViewer)
            mapViewer.addMouseWheelListener(zoomListener)

            // Standard-Position und Zoom setzen
            val munich = GeoPosition(48.1351, 11.5820)
            mapViewer.zoom = 7
            mapViewer.addressLocation = munich

            // Erstelle ein Set von Waypoints aus den Orten
            val waypoints = HashSet<CustomWaypoint>()
            for (location in locations) {
                val geoPos = location.second
                waypoints.add(CustomWaypoint(location.first, GeoPosition(geoPos.latitude, geoPos.longitude)))
            }

            // Erstelle einen Waypoint-Painter, der alle Waypoints entgegennimmt
            val waypointPainter = WaypointPainter<CustomWaypoint>()
            waypointPainter.waypoints = waypoints

            mapViewer.overlayPainter = waypointPainter

            mapViewer.addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: java.awt.event.MouseEvent) {
                    if (e.button != java.awt.event.MouseEvent.BUTTON1) {
                        return
                    }

                    val clickPoint = e.point
                    for (wp in waypoints) {
                        val waypointPoint = mapViewer.convertGeoPositionToPoint(wp.position)
                        if (waypointPoint.distance(clickPoint) < 10) { // 10 pixel tolerance
                            onMarkerClick(wp.label)
                            return // Stop after finding the first one
                        }
                    }
                }
            })

            mapViewer
        },
        modifier = modifier
    )
}
