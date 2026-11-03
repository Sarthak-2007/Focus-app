package com.example.ui.sealed

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.foundation.StrongroomInks
import com.example.ui.foundation.StrongroomRoles
import com.example.ui.foundation.StrongroomType

/** One allowed app on the sealed screen's grid: the package, its
 *  phone-given name. Icons are read from the PackageManager at
 *  render (the phone's own art for its own apps). */
data class SealedAppEntry(
    val packageName: String,
    val label: String,
)

/**
 * NAV-ALL (the NORMAL-APP redirect) — the shell's SEALED screen:
 * the plain status face (SealedFace: title, locked-until, his
 * sentence, KNOCK, ALLOWED APPS) with the day's allowed apps
 * under it as a normal grid — icon + name, the way every app
 * lists apps. Tapping a tile is the grille's act (onGrilleOpen):
 * it stands the antechamber, where the app is actually taken
 * under the day's allowance — the grid is the door's directory,
 * never a launcher of its own (containment stays the law
 * underneath, untouched).
 */
@Composable
fun SealedScreen(
    state: SealedFaceState,
    actions: SealedFaceActions,
    allowedApps: List<SealedAppEntry>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StrongroomRoles.Field)
    ) {
        SealedFace(
            state = state,
            actions = actions,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (allowedApps.isNotEmpty()) {
            Text(
                text = "Allowed today",
                style = StrongroomType.Scr2,
                color = StrongroomRoles.OnGround,
                modifier = Modifier.padding(
                    start = 24.dp, end = 24.dp, top = 4.dp, bottom = 4.dp
                )
            )
            Text(
                text = "Tap an app to open the room and take it.",
                style = StrongroomType.Anon1,
                color = StrongroomRoles.OnGround.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp)
            )
            val rows = (allowedApps.size + 3) / 4
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((rows * 96).dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allowedApps.size) { index ->
                    SealedAppTile(
                        entry = allowedApps[index],
                        onClick = { actions.onGrilleOpen() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SealedAppTile(entry: SealedAppEntry, onClick: () -> Unit) {
    val context = LocalContext.current
    val iconBitmap: Bitmap? = remember(entry.packageName) {
        try {
            val drawable = context.packageManager
                .getApplicationIcon(entry.packageName)
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (t: Throwable) {
            null
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap.asImageBitmap(),
                contentDescription = entry.label,
                modifier = Modifier.size(48.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(StrongroomInks.Violet),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.label.take(1).uppercase(),
                    style = StrongroomType.Scr2,
                    color = StrongroomRoles.OnGround
                )
            }
        }
        Text(
            text = entry.label,
            style = StrongroomType.Anon0,
            color = StrongroomRoles.OnGround,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
