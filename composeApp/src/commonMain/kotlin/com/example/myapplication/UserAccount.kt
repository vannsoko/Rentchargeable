package com.example.myapplication


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Ein Composable, das ein Benutzerprofil-Header mit Icon und Namen anzeigt.
 *
 * @param name Der Name des Benutzers, der angezeigt werden soll.
 * @param modifier Der Modifier, der auf die gesamte Zeile angewendet wird.
 */
@Composable
fun UserAccountHeader(name: String, modifier: Modifier = Modifier) {
    // Row ordnet Kind-Elemente horizontal (nebeneinander) an.
    Row(
        modifier = modifier.padding(16.dp), // Fügt einen Außenabstand hinzu
        verticalAlignment = Alignment.CenterVertically // Zentriert Icon und Text vertikal
    ) {
        // Das Personen-Icon
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Benutzerprofil", // Wichtig für Barrierefreiheit
            modifier = Modifier.size(40.dp) // Legt die Größe des Icons fest
        )

        // Abstand zwischen Icon und Text
        Spacer(modifier = Modifier.width(12.dp))

        // Der Benutzername
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge // Verwendet einen vordefinierten Textstil
        )
    }
}

/**
 * Eine Vorschau-Funktion, um das Composable direkt in Android Studio anzuzeigen,
 * ohne die App jedes Mal starten zu müssen.
 */
@Preview(showBackground = true)
@Composable
fun UserAccountHeaderPreview() {
    // Hier können Sie das Aussehen mit einem Beispielnamen testen.
    UserAccountHeader(name = "Benedikt")
}

/**
 * Stellt eine komplette Profilseite dar.
 * Diese Seite verwendet den UserAccountHeader und fügt weitere Informationen hinzu.
 */
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    // Column ordnet Elemente vertikal (untereinander) an.
    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Verwenden Sie das bereits erstellte Header-Composable
        UserAccountHeader(name = "Benedikt")

        // 2. Eine Trennlinie zur visuellen Abgrenzung
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        // 3. Ein Platzhalter für weitere Inhalte
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Weitere Profileinstellungen:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "- E-Mail ändern",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "- Passwort zurücksetzen",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = "- Abmelden",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

/**
 * Eine Vorschau für die gesamte Profilseite.
 */
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    // Scaffold bietet eine grundlegende Material-Design-Layoutstruktur.
    Scaffold { paddingValues ->
        ProfileScreen(modifier = Modifier.padding(paddingValues))
    }
}
