package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
@Composable
fun ShieldCapSelector(
    selectedIndex: Int,
    soundEngine: TactileSoundEngine? = null,
    onSelect: (Int) -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val words = listOf("STUDY", "WORK", "SLEEP")
    // REGISTER: the settled loadout marker line, both modes.
    val marker = TokenColorOnPrimary
    // Unselected stops print in the house text-2 tier (the rail's
    // unselected words, CHROME-BUILD-READY §2.3 / F-05): day
    // onSurfaceVariant IS text-2 (#5F5856), but at night
    // onSurfaceVariant is the muted tier (#776A60, 3.68:1 on the
    // #100E0C recess — measured on the chrome lane's n02_shield
    // render, Linea's adaptation pass) where text-2 is
    // NightTextColorSecondary (#9C8E84, 6.07:1 on the same recess).
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
        val capOffset by animateDpAsState(
            targetValue = pad + cellW * selectedIndex,
            animationSpec = MonolithCurves.specA(260),
            label = "capTravel"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.surfaceVariant)
            ) {
                // Words printed on the track at the unselected stops.
                Row(modifier = Modifier.fillMaxSize()) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (i != selectedIndex) {
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
                            text = words[selectedIndex],
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

                // Tap zones over each stop (topmost layer).
                Row(modifier = Modifier.fillMaxSize()) {
                    for (i in 0..2) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    soundEngine?.playFirmClick()
                                    onSelect(i)
                                }
                                .testTag("shield_cap_stop_$i")
                        )
                    }
                }
            }

            // Stop ticks printed under the track.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                for (i in 0..2) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(8.dp)
                                .background(
                                    if (i == selectedIndex) scheme.onBackground
                                    else scheme.outlineVariant
                                )
                        )
                    }
                }
            }
        }
    }
}
