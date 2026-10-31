package com.example.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.example.MainActivity

/**
 * Issue #274 (Audit R5 — Platform execution): the Quick Settings tile.
 *
 * Before this, Monolith had NO tile at all — every session action
 * forced a full app open, and the standing notification was a dead
 * label with no actions. The tile is an in-session control surface:
 * it shows the live remaining time from [SessionMirror] and one tap
 * lands on Monolith (the Deck when idle, the session/block surface
 * while sealed — MainActivity routes by live session state).
 *
 * The tile deliberately cannot END a session: ending is the app's
 * most consequential act and stays behind the in-app exit path
 * (60-second emergency hold / normal completion). A tile that could
 * kill a block in one QS tap would be an escape hatch, not a control.
 */
class MonolithTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun refreshTile() {
        val tile = qsTile ?: return
        val mirror = SessionMirror.read(this)
        if (mirror.active) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Monolith"
            tile.subtitle = SessionMirrorFormat.remainingLabel(
                mirror.endWallMs, System.currentTimeMillis()
            ) ?: "Session live"
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Monolith"
            tile.subtitle = "Tap to open"
        }
        tile.updateTile()
    }
}
