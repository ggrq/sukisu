package com.sukisu.ultra.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

/**
 * ============================================================================
 *  CYBERPUNK / NEON-GRID DESIGN SYSTEM
 * ============================================================================
 *  Pure-presentation layer. It never touches any business logic: it only
 *  supplies colours, backgrounds, borders and decorative composables that the
 *  existing screens can opt into. Everything here is built exclusively on
 *  Compose foundation + ui primitives, so it cannot break Miuix APIs.
 */
object CyberPalette {

    // ---- Deep space background stack -------------------------------------
    val DeepVoid = Color(0xFF04050A)
    val Abyss = Color(0xFF080B14)
    val PanelTop = Color(0xFF131A31)
    val PanelBottom = Color(0xFF0A0E1C)
    val PanelGlass = Color(0xCC0D1326)
    val GridLine = Color(0xFF1C2E52)

    // ---- Neon accents ----------------------------------------------------
    val NeonCyan = Color(0xFF00F0FF)
    val NeonMagenta = Color(0xFFFF2BD6)
    val NeonViolet = Color(0xFF8A5CFF)
    val NeonLime = Color(0xFF7CFF6B)
    val NeonAmber = Color(0xFFFFB347)
    val NeonRed = Color(0xFFFF3B5C)
    val NeonIce = Color(0xFFB9F6FF)

    // ---- Typography ------------------------------------------------------
    val TextPrimary = Color(0xFFE9F7FF)
    val TextSecondary = Color(0xFF9FB6D6)
    val TextDim = Color(0xFF5D7396)

    /** Accent rotation used by decorative elements. */
    val accents = listOf(NeonCyan, NeonMagenta, NeonViolet, NeonLime)

    /** Maps a semantic "status" to a neon hue. */
    fun status(ok: Boolean): Color = if (ok) NeonLime else NeonRed
}

/**
 * Drives every ambient animation in the cyber layer (grid drift, scan sweep,
 * pulse rings). A single infinite transition keeps the cost predictable.
 */
@Composable
fun rememberCyberPhase(periodMillis: Int = 8000): Float {
    val transition = rememberInfiniteTransition(label = "cyber-phase")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cyber-phase-value"
    )
    return phase
}

/** Convenience: an independent phase so widgets do not breathe in lockstep. */
@Composable
fun rememberCyberPulse(periodMillis: Int = 2200): Float {
    val transition = rememberInfiniteTransition(label = "cyber-pulse")
    val value by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cyber-pulse-value"
    )
    return value
}

// ---------------------------------------------------------------------------
//  Background
// ---------------------------------------------------------------------------

/**
 * Full-screen cyberpunk backdrop: perspective grid, drifting scan sweep,
 * volumetric neon blooms and a CRT vignette. Rendered behind page content.
 */
@Composable
fun CyberBackground(
    modifier: Modifier = Modifier,
    primary: Color = CyberPalette.NeonCyan,
    secondary: Color = CyberPalette.NeonMagenta,
    showGrid: Boolean = true,
    showScanSweep: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val phase = rememberCyberPhase(9000)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberPalette.DeepVoid)
    ) {
        // Volumetric neon blooms --------------------------------------------
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(size.width * 0.12f, size.height * 0.06f),
                    radius = size.maxDimension * 0.62f
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(secondary.copy(alpha = 0.13f), Color.Transparent),
                    center = Offset(size.width * 0.95f, size.height * 0.30f),
                    radius = size.maxDimension * 0.55f
                )
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(CyberPalette.NeonViolet.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 1.02f),
                    radius = size.maxDimension * 0.70f
                )
            )
        }

        if (showGrid) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                drawCyberGrid(phase = phase, accent = primary.copy(alpha = 0.35f))
            }
        }

        if (showScanSweep) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                drawScanSweep(phase = phase, accent = secondary)
            }
        }

        // Vignette ----------------------------------------------------------
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberPalette.DeepVoid.copy(alpha = 0.85f),
                        Color.Transparent,
                        CyberPalette.DeepVoid.copy(alpha = 0.92f)
                    )
                )
            )
        }

        content()
    }
}

/** Horizontal + vertical grid with a slow parallax drift. */
private fun DrawScope.drawCyberGrid(phase: Float, accent: Color) {
    val step = 46.dp.toPx()
    val drift = step * phase
    val line = 1.dp.toPx()

    var x = -step + drift
    while (x < size.width + step) {
        drawLine(
            color = accent.copy(alpha = 0.055f),
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = line
        )
        x += step
    }

    var y = -step + drift * 0.6f
    while (y < size.height + step) {
        drawLine(
            color = accent.copy(alpha = 0.045f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = line
        )
        y += step
    }
}

/** A soft neon bar sweeping top-to-bottom, plus a faint CRT scanline comb. */
private fun DrawScope.drawScanSweep(phase: Float, accent: Color) {
    val sweepHeight = size.height * 0.30f
    val top = (size.height + sweepHeight) * phase - sweepHeight
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                accent.copy(alpha = 0.055f),
                Color.Transparent
            ),
            startY = top,
            endY = top + sweepHeight
        ),
        topLeft = Offset(0f, top),
        size = Size(size.width, sweepHeight)
    )

    // CRT comb
    val comb = 3.dp.toPx()
    var y = 0f
    while (y < size.height) {
        drawLine(
            color = Color.White.copy(alpha = 0.012f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += comb
    }
}

// ---------------------------------------------------------------------------
//  Modifiers
// ---------------------------------------------------------------------------

/**
 * Neon rim-light. Draws a stack of progressively wider, fainter strokes to
 * fake a real glow, then a crisp bright 1dp edge on top.
 */
fun Modifier.cyberNeonBorder(
    color: Color,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    glow: Dp = 12.dp,
    glowAlpha: Float = 0.30f,
    enabled: Boolean = true
): Modifier = if (!enabled) this else this.drawWithContent {
    val radiusPx = cornerRadius.toPx()
    val strokePx = borderWidth.toPx()
    val glowPx = glow.toPx()
    val corner = CornerRadius(radiusPx, radiusPx)

    val layers = 6
    var width = strokePx
    var alpha = glowAlpha
    repeat(layers) {
        drawRoundRect(
            color = color.copy(alpha = alpha),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, size.height),
            cornerRadius = corner,
            style = Stroke(width = width)
        )
        width += glowPx / layers
        alpha *= 0.55f
    }

    drawRoundRect(
        color = color.copy(alpha = 0.95f),
        topLeft = Offset(0f, 0f),
        size = Size(size.width, size.height),
        cornerRadius = corner,
        style = Stroke(width = strokePx)
    )

    drawContent()
}

/** Frosted glass panel fill with a subtle top-lit gradient. */
fun Modifier.cyberGlass(
    cornerRadius: Dp = 16.dp,
    top: Color = CyberPalette.PanelTop,
    bottom: Color = CyberPalette.PanelBottom,
    alpha: Float = 0.92f
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        Brush.verticalGradient(
            colors = listOf(top.copy(alpha = alpha), bottom.copy(alpha = alpha))
        )
    )

/** Soft neon halo painted *behind* the element (works on text and boxes). */
fun Modifier.cyberGlow(
    color: Color,
    cornerRadius: Dp = 12.dp,
    radius: Dp = 18.dp,
    alpha: Float = 0.45f
): Modifier = this.drawBehind {
    val r = cornerRadius.toPx()
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = Offset(size.width / 2f, size.height / 2f),
            radius = (size.maxDimension / 2f) + radius.toPx()
        ),
        topLeft = Offset.Zero,
        size = Size(size.width, size.height),
        cornerRadius = CornerRadius(r, r)
    )
}

/**
 * HUD corner brackets. Pure decoration; drawn on top of the container so it
 * never disturbs child layout.
 */
fun Modifier.cyberCorners(
    color: Color,
    length: Dp = 14.dp,
    thickness: Dp = 2.dp,
    inset: Dp = 6.dp
): Modifier = this.drawWithContent {
    drawContent()
    val l = length.toPx()
    val t = thickness.toPx()
    val i = inset.toPx()
    val w = size.width
    val h = size.height

    fun bracket(x: Float, y: Float, dx: Float, dy: Float) {
        drawLine(color, Offset(x, y), Offset(x + dx * l, y), strokeWidth = t)
        drawLine(color, Offset(x, y), Offset(x, y + dy * l), strokeWidth = t)
    }

    bracket(i, i, 1f, 1f)
    bracket(w - i, i, -1f, 1f)
    bracket(i, h - i, 1f, -1f)
    bracket(w - i, h - i, -1f, -1f)
}

/**
 * Diagonal "holo-stripe" hatch used on headers and accent strips.
 */
fun Modifier.cyberHatch(color: Color, gap: Dp = 8.dp, stroke: Dp = 1.dp): Modifier =
    this.drawBehind {
        val g = gap.toPx()
        val s = stroke.toPx()
        var x = -size.height
        while (x < size.width + size.height) {
            rotate(degrees = 45f, pivot = Offset(size.width / 2f, size.height / 2f)) {
                drawLine(
                    color = color,
                    start = Offset(x, -size.height),
                    end = Offset(x, size.height * 2f),
                    strokeWidth = s
                )
            }
            x += g
        }
    }

// ---------------------------------------------------------------------------
//  Building blocks
// ---------------------------------------------------------------------------

/**
 * The canonical cyberpunk surface. Replaces a plain Card visually while
 * keeping the exact same content slot semantics.
 */
@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan,
    cornerRadius: Dp = 16.dp,
    showCorners: Boolean = true,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .cyberGlass(cornerRadius = cornerRadius)
            .cyberNeonBorder(
                color = accent,
                cornerRadius = cornerRadius,
                glow = 10.dp,
                glowAlpha = 0.22f
            )
            .then(
                if (showCorners) {
                    Modifier.cyberCorners(
                        color = accent.copy(alpha = 0.85f),
                        length = 14.dp,
                        thickness = 2.dp,
                        inset = 5.dp
                    )
                } else Modifier
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Chromatic-aberration title. Two offset ghost copies in cyan/magenta sit
 * behind a bright foreground copy — the classic glitch look, zero animation
 * cost when static.
 */
@Composable
fun GlitchText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 28,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = CyberPalette.TextPrimary,
    ghostShift: Dp = 1.5.dp,
    animated: Boolean = false
) {
    val pulse = if (animated) rememberCyberPulse(2600) else 0f
    val shift = ghostShift * (1f + pulse * 0.6f)

    Box(modifier = modifier) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = FontFamily.Monospace,
            color = CyberPalette.NeonMagenta.copy(alpha = 0.55f),
            modifier = Modifier.offset(x = -shift, y = shift * 0.35f)
        )
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = FontFamily.Monospace,
            color = CyberPalette.NeonCyan.copy(alpha = 0.55f),
            modifier = Modifier.offset(x = shift, y = -shift * 0.35f)
        )
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = FontFamily.Monospace,
            color = color,
            modifier = Modifier.cyberGlow(CyberPalette.NeonCyan, alpha = 0.35f)
        )
    }
}

/** Section header: neon glyph bar + monospaced label + hairline rail. */
@Composable
fun CyberSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
                    .cyberGlow(accent, radius = 8.dp, alpha = 0.7f)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = title.uppercase(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = CyberPalette.TextSecondary
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.55f), Color.Transparent)
                        )
                    )
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** Thin neon separator with a bright travelling node. */
@Composable
fun CyberDivider(
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan,
    animated: Boolean = true
) {
    val phase = if (animated) rememberCyberPhase(3200) else 0f

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            brush = Brush.horizontalGradient(
                listOf(Color.Transparent, accent.copy(alpha = 0.45f), Color.Transparent)
            ),
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = size.height
        )
        val x = size.width * phase
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent, Color.Transparent),
                center = Offset(x, size.height / 2f),
                radius = 14.dp.toPx()
            ),
            radius = 14.dp.toPx(),
            center = Offset(x, size.height / 2f)
        )
    }
}

/** Blinking status dot — used for "live" indicators. */
@Composable
fun CyberPulseDot(
    modifier: Modifier = Modifier,
    color: Color = CyberPalette.NeonLime,
    size: Dp = 8.dp,
    animated: Boolean = true
) {
    val pulse = if (animated) rememberCyberPulse(1600) else 0.5f
    val haloAlpha = 0.55f - 0.45f * pulse

    Box(modifier = modifier.size(size * 3f), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            val c = Offset(this.size.width / 2f, this.size.height / 2f)
            drawCircle(
                color = color.copy(alpha = haloAlpha),
                radius = (this.size.minDimension / 2f) * (0.45f + pulse * 0.55f),
                center = c,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                    center = c,
                    radius = this.size.minDimension / 2f
                ),
                radius = this.size.minDimension / 2f,
                center = c
            )
            drawCircle(color = color, radius = this.size.minDimension / 6f, center = c)
        }
    }
}

/** Tag / chip with a neon outline and monospaced uppercase label. */
@Composable
fun CyberChip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan,
    filled: Boolean = false
) {
    val shape = RoundedCornerShape(6.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (filled) Modifier.background(accent.copy(alpha = 0.18f)) else Modifier
            )
            .cyberNeonBorder(
                color = accent.copy(alpha = 0.85f),
                cornerRadius = 6.dp,
                glow = 6.dp,
                glowAlpha = 0.25f
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = if (filled) accent else CyberPalette.TextSecondary
        )
    }
}

/**
 * A labelled data row in HUD style: dim key on the left, bright value on the
 * right, hairline rail underneath. Purely visual replacement for InfoText.
 */
@Composable
fun CyberDataRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan,
    showRail: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = CyberPalette.TextDim
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = CyberPalette.TextPrimary,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
        if (showRail) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accent.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )
        }
    }
}

/** Rotating dashed HUD ring. Handy as a loading / activity ornament. */
@Composable
fun CyberRing(
    modifier: Modifier = Modifier,
    accent: Color = CyberPalette.NeonCyan,
    diameter: Dp = 72.dp,
    animated: Boolean = true
) {
    val phase = if (animated) rememberCyberPhase(4200) else 0f
    androidx.compose.foundation.Canvas(modifier = modifier.size(diameter)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f - 2.dp.toPx()
        rotate(degrees = phase * 360f, pivot = c) {
            drawCircle(
                color = accent.copy(alpha = 0.85f),
                radius = r,
                center = c,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawArc(
                color = CyberPalette.NeonMagenta.copy(alpha = 0.9f),
                startAngle = 20f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(c.x - r, c.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 2.5.dp.toPx())
            )
            drawArc(
                color = CyberPalette.NeonViolet.copy(alpha = 0.75f),
                startAngle = 200f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = Offset(c.x - r, c.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 2.dp.toPx())
            )
        }
        drawCircle(
            color = accent.copy(alpha = 0.25f),
            radius = r * 0.62f,
            center = c,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

// ---------------------------------------------------------------------------
//  Ambient overlay
// ---------------------------------------------------------------------------

/**
 * Composition-local flag telling the presentation layer whether the cyberpunk
 * skin is active. It is provided exactly once by [KernelSUTheme] and read by
 * decorative composables. Purely visual — it carries no business meaning, so
 * it can never alter app behaviour.
 */
val LocalCyberpunkMode = androidx.compose.runtime.staticCompositionLocalOf { false }

/**
 * Non-interactive ambient overlay.
 *
 * It is emitted *after* the app content, so it tints every screen (home,
 * superuser, modules, settings, …) without touching a single screen
 * implementation and without ever consuming pointer input — Compose only
 * hit-tests nodes that register a pointer modifier, so taps, scrolls and
 * gestures fall straight through to the UI underneath.
 *
 * Every layer is deliberately low alpha: this must read as a CRT / hologram
 * wash over the interface, never as a readability tax.
 */
@Composable
fun CyberAmbientOverlay(
    modifier: Modifier = Modifier,
    primary: Color = CyberPalette.NeonCyan,
    secondary: Color = CyberPalette.NeonMagenta,
    scrimAlpha: Float = 0.20f,
    showGrid: Boolean = true,
    showScanSweep: Boolean = true,
    showVignette: Boolean = true,
) {
    val phase = rememberCyberPhase(13000)

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {

        // ---- Deep-void blue shift ----------------------------------------
        drawRect(color = CyberPalette.DeepVoid.copy(alpha = scrimAlpha))

        // ---- Volumetric neon blooms --------------------------------------
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * 0.08f, size.height * 0.03f),
                radius = size.maxDimension * 0.55f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = 0.09f), Color.Transparent),
                center = Offset(size.width * 1.02f, size.height * 0.26f),
                radius = size.maxDimension * 0.50f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(CyberPalette.NeonViolet.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * 1.02f),
                radius = size.maxDimension * 0.62f
            )
        )

        // ---- Drifting grid + scan sweep ----------------------------------
        if (showGrid) {
            drawCyberGrid(phase = phase, accent = primary.copy(alpha = 0.30f))
        }
        if (showScanSweep) {
            drawScanSweep(phase = phase, accent = secondary)
        }

        // ---- CRT vignette ------------------------------------------------
        if (showVignette) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberPalette.DeepVoid.copy(alpha = 0.50f),
                        Color.Transparent,
                        CyberPalette.DeepVoid.copy(alpha = 0.62f)
                    )
                )
            )
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        CyberPalette.DeepVoid.copy(alpha = 0.42f),
                        Color.Transparent,
                        CyberPalette.DeepVoid.copy(alpha = 0.42f)
                    )
                )
            )
        }
    }
}