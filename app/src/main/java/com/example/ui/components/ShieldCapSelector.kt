package com.example.ui.components
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoadoutStore
import com.example.service.TactileSoundEngine
import com.example.ui.theme.AnonymousProFamily
import com.example.ui.theme.MonolithCurves
import com.example.ui.theme.NightTextColorSecondary
import com.example.ui.theme.TokenColorOnPrimary

/**
 * The Shield screen's loadout selector (STUDY / WORK / SLEEP) as a
 * sliding cap — the band/sled grammar at screen scale: a recessed
 * track (sled radius 12dp, pad 3dp), an ink cap travelling the stops
 * on the THROW curve (specA 260), the selected word riding in the cap
 * over the settled loadout marker line, stop ticks printed under the
 * track. Sarthak picked this over the rotary index knob on Linea's
 * judgment (Oct 29, 10:16 IST): the Shield screen is a wall of sleds,
 * and this is that object at the top of it.
 *
 * Production fit: the root occupies exactly the 84dp selector slot
 * the RombiKnob AndroidView held, so everything below the selector
 * starts at the same line as before. The cap object itself (36dp
 * track, 30dp cap, radii, ticks, words) is pixel-identical to the
 * rendered variant Sarthak chose; only the zone shrank (108dp render
 * zone -> 84dp slot). Shield's accent budget is 0 (REGISTER): the
 * selector never spends the orange.
 */
/**
 * The selector's pure logic (issue #58 F4/F6) — plain Kotlin, no
 * Compose, so the clamping and the re-tap guard are unit-tested
 * without a composition. The stop count is NOT its own constant:
 * it is LoadoutStore.CAP_ORDER's size, the one index↔loadout
 * source, so a fourth loadout widens this selector by itself.
 */
object ShieldCap {
    /** Display words per stop, presentation only — the loadout
     *  each stop seats comes from LoadoutStore.CAP_ORDER. */
    val STOP_WORDS: List<String> = listOf("STUDY", "WORK", "SLEEP")

    val STOP_COUNT: Int get() = LoadoutStore.CAP_ORDER.size

    /** Any handed index seats on the track: below the first stop
     *  clamps to it, past the last clamps to it. Never a crash,
     *  never a cap animated off the track. */
    fun clampIndex(selectedIndex: Int): Int =
        selectedIndex.coerceIn(0, STOP_COUNT - 1)

    /** A tap fires only when it would move the cap. Re-tapping the
     *  resting stop is not a change: no firm click faking one, no
     *  onSelect re-applying the loadout already in force. */
    fun shouldHandleTap(currentIndex: Int, tappedIndex: Int): Boolean =
        tappedIndex != currentIndex
}

@Composable
fun ShieldCapSelector(
    selectedIndex: Int,
    soundEngine: TactileSoundEngine? = null,
    onSelect: (Int) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val words = ShieldCap.STOP_WORDS
    // F6: the handed index is clamped before ANY use — word lookup,
    // cap offset, tick, semantics — so an out-of-range index can
    // neither crash the lookup nor animate the cap off the track.
    val clampedIndex = ShieldCap.clampIndex(selectedIndex)
    // REGISTER: the settled loadout marker line, both modes.
    val marker = TokenColorOnPrimary
    // Unselected stops print in the house text-2 tier (the rail's
    // unselected words, CHROME-BUILD-READY §2.3 / F-05): day
    // onSurfaceVariant IS text-2 (#5F5856), but at night
    // onSurfaceVariant was the muted tier (#776A60, 3.68:1 on the
    // #100E0C recess — measured on the chrome lane's n02_shield
    // render, Linea's adaptation pass) where text-2 is
    // NightTextColorSecondary (#9C8E84, 6.07:1 on the same recess).
    // (Issue #70 F5 has since raised the muted tier itself to
    // #9C8E84, so the two now agree; this explicit night pick
    // stays as the measured-on-render choice.)
    // Night detection follows inkControlFill()'s house pattern.
    val unselectedWord =
        if (scheme.background.luminance() < 0.5f) NightTextColorSecondary
        else scheme.onSurfaceVariant

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .testTag("shield_selector_cap")
    ) {
        val pad = 3.dp
        val cellW = (maxWidth - pad * 2) / 3
        // #88: the word Row and the tap zones mirror in RTL, but a
        // physical offset(x) does not — in an RTL locale the cap sat
        // over the WRONG word while the mirrored zones fired the
        // original index. Mirror the travel: LTR formula untouched.
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val capOffset by animateDpAsState(
            targetValue = if (isRtl) maxWidth - pad - cellW * (clampedIndex + 1)
                          else pad + cellW * clampedIndex,
            animationSpec = MonolithCurves.specA(260),
            label = "capTravel"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                // F3 pixel law: NOT centred. Old content (36 track +
                // 6 pad + 8 ticks = 50dp) centred in the 84dp slot
                // put the track top at 17dp and the ticks at 59dp.
                // New content is 48 + 8 = 56dp; centring it would
                // drop the track to 20dp — a 3dp shift. Top-aligned
                // with an 11dp offset instead: wrapper top 11dp,
                // track top 11 + 6 = 17dp, ticks top 11 + 48 = 59dp.
                // Every printed Y is exactly the old Y.
                .align(Alignment.TopCenter)
                .offset(y = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // F3 touch targets: the visible track stays 36dp, but
            // each stop's hit area is the 48dp minimum. This 48dp
            // wrapper centres the unchanged 36dp track (6dp of
            // invisible overhang above and below — it paints
            // nothing), and the Column's top alignment + 11dp offset
            // (above) lands the track at exactly its old Y, so the
            // cap geometry — 36dp track, 30dp cap, radii, words,
            // position — is untouched. The ticks below lose their
            // 6dp top padding for the same reason: the wrapper's
            // lower overhang IS that 6dp now, so the printed gap
            // under the track is unchanged too.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
              Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
              ) {
                // Words printed on the track at the unselected stops.
                Row(modifier = Modifier.fillMaxSize()) {
                    for (i in words.indices) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i != clampedIndex) {
                                Text(
                                    text = words[i],
                                    fontFamily = AnonymousProFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp,
                                    color = unselectedWord,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // The cap: ink fill, the word riding in it over the
                // settled marker line (sled cap grammar, band family).
                Box(
                    modifier = Modifier
                        .offset(x = capOffset, y = pad)
                        .width(cellW)
                        .height(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(inkControlFill()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = words[clampedIndex],
                            fontFamily = AnonymousProFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = onInkControlFill(),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .width(10.dp)
                                .height(2.dp)
                                .background(marker.copy(alpha = 0.9f))
                        )
                    }
                }

              }

                // Tap zones over each stop (topmost layer, the full
                // 48dp wrapper height). F2 accessibility: each stop
                // is a named radio button — selectable() with
                // Role.RadioButton publishes the role and the
                // selected state, and the semantics name the loadout
                // it seats, so TalkBack announces e.g. "SLEEP loadout,
                // selected" instead of three bare click targets.
                // F4: a re-tap of the resting stop is swallowed by
                // ShieldCap.shouldHandleTap — no firm click faking a
                // change, no onSelect re-applying the loadout.
                Row(modifier = Modifier.fillMaxSize()) {
                    for (i in words.indices) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .semantics {
                                    contentDescription = "${words[i]} loadout"
                                }
                                .selectable(
                                    selected = i == clampedIndex,
                                    role = Role.RadioButton,
                                    onClick = {
                                        if (ShieldCap.shouldHandleTap(clampedIndex, i)) {
                                            soundEngine?.playFirmClick()
                                            onSelect(i)
                                        }
                                    }
                                )
                                .testTag("shield_cap_stop_$i")
                        )
                    }
                }
            }

            // Stop ticks printed under the track (the 6dp gap is
            // the hit wrapper's lower overhang now — see F3 above).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for (i in words.indices) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(8.dp)
                                .background(
                                    if (i == clampedIndex) scheme.onBackground
                                    else scheme.outlineVariant
                                )
                        )
                    }
                }
            }
        }
    }
}
