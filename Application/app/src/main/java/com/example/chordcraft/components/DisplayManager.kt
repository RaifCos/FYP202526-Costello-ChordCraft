package com.example.chordcraft.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chordcraft.ui.theme.*

private const val STRINGS  = 6
private const val FRETS  = 5

// Function to generate a scrolling row of Guitar Chords.
@Composable
fun CreateFretBoards(chordList: List<Chord>) {
    if (chordList.isEmpty()) {
        DefaultFretDisplay()
        return
    }

    val guitarChords = generateGuitarChords(chordList)
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FretBackTop,
                        FretBackBottom
                    )
                )
            )
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { items(guitarChords) { guitarChord -> DrawFretBoard(guitarChord) } }
}

// Function to create a Fret Board from a given Guitar Chord.
@Composable
fun DrawFretBoard(guitarChord: GuitarChord?) {
    if (guitarChord == null) return

    // Dimensions
    val cellW: Dp = 36.dp
    val cellH: Dp = 32.dp
    val dotRadius: Dp = 10.dp
    val stringNames = listOf("e", "B", "G", "D", "A", "E")

    // Chose Starting Fret.
    val startFret = if (guitarChord.minFret <= 1) 1 else guitarChord.minFret
    val noteMap: Map<Int, Int> = guitarChord.notes.associate { it.string to it.fret }

    // Fret Board Structure:
    Column(
        modifier = Modifier
            .background(FretCard, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Chord Label:
        Text(
            text = guitarChord.chord.label,
            color = Label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Chord Timing:
        val startTimeString = generateTimestamp(guitarChord.chord.startTime)
        val endTimeString = generateTimestamp(guitarChord.chord.endTime)

        Text(
            text = ("$startTimeString - $endTimeString"),
            color = Label,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Mute/Open Indicators
        Row {
            Spacer(Modifier.width(18.dp))
            for (string in 0 until STRINGS) {
                Box(
                    modifier = Modifier.width(cellW),
                    contentAlignment = Alignment.Center
                ) {
                    when ( noteMap[string] ) {
                        null -> Text(
                            "✕",
                            color = MutedX,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold)
                        0 -> Text(
                            "○",
                            color = OpenDot,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold)
                        else -> Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        // Fret Grid
        Row(verticalAlignment = Alignment.Top) {

            // Column for Fret Numbers:
            Column {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(cellH),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (startFret > 1) {
                        Text(
                            text = "$startFret",
                            color = FretNumber,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(end = 3.dp)
                        )
                    }
                }
                repeat(FRETS - 1) {
                    Spacer(Modifier.width(18.dp).height(cellH))
                }
            }

            // String Columns:
            for (string in 0 until STRINGS) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    for (row in 0 until FRETS) {
                        val fretNumber = startFret + row
                        val isNut      = (startFret == 1 && row == 0)
                        val hasDot     = noteMap[string] == fretNumber

                        // Draw Shape based on whether or not there's a note being played:
                        Box(
                            modifier = Modifier.size(cellW, cellH),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCell(
                                    drawScope     = this,
                                    isNut         = isNut,
                                    isFirstString = (string == 0),
                                    isLastString  = (string == STRINGS - 1),
                                    cellW         = size.width,
                                    cellH         = size.height
                                )
                                if (hasDot) {
                                    drawCircle(
                                        color  = Dot,
                                        radius = dotRadius.toPx(),
                                        center = Offset(size.width / 2f, size.height / 2f)
                                    )
                                }
                            }
                            if (hasDot) {
                                Text(
                                    text       = "$fretNumber",
                                    color      = DotText,
                                    fontSize   = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // String Name Labels:
        Row {
            Spacer(Modifier.width(18.dp))
            stringNames.forEach { name ->
                Text(
                    text      = name,
                    color     = FretNumber,
                    fontSize  = 10.sp,
                    modifier  = Modifier.width(cellW),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun generateTimestamp(time: Double): String {
    val min = time.toInt()
    val sec = ((time - min) * 60).toInt()
    return "%d:%02d".format(min, sec)
}

// Function to draw a Fret Board cell.
private fun drawCell(
    drawScope: DrawScope,
    isNut: Boolean,
    isFirstString: Boolean,
    isLastString: Boolean,
    cellW: Float,
    cellH: Float
) = with(drawScope) {
    val cx = cellW / 2f
    val nutThickness = 5f
    val fretThickness = 1.5f
    val stringThickness = 1.5f

    // Top Wire:
    if (isNut) {
        // Thick Row
        drawLine(
            color       = Nut,
            start       = Offset(if (isFirstString) cx else 0f, 0f),
            end         = Offset(if (isLastString)  cx else cellW, 0f),
            strokeWidth = nutThickness,
            cap         = StrokeCap.Square
        )
    } else {
        // Thin Wire
        drawLine(
            color       = FretWire,
            start       = Offset(if (isFirstString) cx else 0f, 0f),
            end         = Offset(if (isLastString)  cx else cellW, 0f),
            strokeWidth = fretThickness,
            cap         = StrokeCap.Square
        )
    }

    // Actual String:
    drawLine(
        color       = GuitarString,
        start       = Offset(cx, 0f),
        end         = Offset(cx, cellH),
        strokeWidth = stringThickness,
        cap         = StrokeCap.Square
    )

    // Fretboard Background:
    drawRect(
        color    = Fretboard.copy(alpha = 0.15f),
        topLeft  = Offset(0f, 0f),
        size     = androidx.compose.ui.geometry.Size(cellW, cellH)
    )
}

@Composable
fun DefaultFretDisplay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(266.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FretBackTop,
                        FretBackBottom
                    )
                )
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = "Your Chords will appear here.\nFollow the instructions below to generate them.",
            color      = DotText,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center
        )
    }
}
