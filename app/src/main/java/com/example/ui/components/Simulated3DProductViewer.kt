package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Simulated3DProductViewer(
    product: Product,
    modifier: Modifier = Modifier
) {
    var rotationY by remember { mutableStateOf(0f) }
    var rotationX by remember { mutableStateOf(15f) }
    var zoomScale by remember { mutableStateOf(1.0f) }
    var isAutoRotating by remember { mutableStateOf(true) }
    var show3DGrid by remember { mutableStateOf(true) }

    // Auto rotate infinite animation
    val infiniteTransition = rememberInfiniteTransition(label = "3d_spin")
    val autoSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val effectiveRotationY = if (isAutoRotating) (rotationY + autoSpin) % 360f else rotationY

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ShopnovaBlueDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("3d_product_viewer")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Interactive 3D Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isAutoRotating) {
                        detectDragGestures(
                            onDragStart = { isAutoRotating = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                rotationY = (rotationY + dragAmount.x * 0.5f) % 360f
                                rotationX = (rotationX - dragAmount.y * 0.3f).coerceIn(-45f, 45f)
                            }
                        )
                    }
            ) {
                val cx = size.width / 2
                val cy = size.height / 2

                // 1. Draw 3D Radial Lighting Floor
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ShopnovaBlue.copy(alpha = 0.4f), Color.Transparent),
                        center = Offset(cx, cy + 40.dp.toPx()),
                        radius = 120.dp.toPx()
                    ),
                    center = Offset(cx, cy + 40.dp.toPx()),
                    radius = 120.dp.toPx()
                )

                // 2. Draw 3D Perspective Grid rings
                if (show3DGrid) {
                    val angleRad = Math.toRadians(effectiveRotationY.toDouble())
                    for (r in 1..4) {
                        val ringRadius = (35 * r).dp.toPx() * zoomScale
                        drawOval(
                            color = Color.White.copy(alpha = 0.08f),
                            topLeft = Offset(cx - ringRadius, cy + 30.dp.toPx() - (ringRadius * 0.25f)),
                            size = androidx.compose.ui.geometry.Size(ringRadius * 2, ringRadius * 0.5f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }

                // 3. Draw simulated 3D Holographic Cube & Product Node
                val cubeSize = 55.dp.toPx() * zoomScale
                val radY = Math.toRadians(effectiveRotationY.toDouble())
                val radX = Math.toRadians(rotationX.toDouble())

                // 8 Vertices of 3D bounding geometry
                val nodes = listOf(
                    Triple(-1f, -1f, -1f), Triple(1f, -1f, -1f),
                    Triple(1f, 1f, -1f), Triple(-1f, 1f, -1f),
                    Triple(-1f, -1f, 1f), Triple(1f, -1f, 1f),
                    Triple(1f, 1f, 1f), Triple(-1f, 1f, 1f)
                ).map { (x, y, z) ->
                    // Rotate around Y
                    val x1 = x * cos(radY) + z * sin(radY)
                    val z1 = -x * sin(radY) + z * cos(radY)
                    // Rotate around X
                    val y2 = y * cos(radX) - z1 * sin(radX)
                    val z2 = y * sin(radX) + z1 * cos(radX)

                    val perspective = 1.0f / (1.0f - (z2 * 0.15f).toFloat())
                    Offset(
                        (cx + x1 * cubeSize * perspective).toFloat(),
                        (cy + y2 * cubeSize * perspective).toFloat()
                    )
                }

                // Draw 3D Cube Edges
                val edges = listOf(
                    0 to 1, 1 to 2, 2 to 3, 3 to 0,
                    4 to 5, 5 to 6, 6 to 7, 7 to 4,
                    0 to 4, 1 to 5, 2 to 6, 3 to 7
                )
                edges.forEach { (start, end) ->
                    drawLine(
                        color = ShopnovaGold.copy(alpha = 0.5f),
                        start = nodes[start],
                        end = nodes[end],
                        strokeWidth = 1.8.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                    )
                }

                // Center Holographic Core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, ShopnovaBlueLight, getCategoryColor(product.category)),
                        center = Offset(cx, cy),
                        radius = 45.dp.toPx() * zoomScale
                    ),
                    center = Offset(cx, cy),
                    radius = 38.dp.toPx() * zoomScale
                )

                // Specular Light Gleam
                val gleamX = cx + (cos(radY) * 20.dp.toPx()).toFloat()
                val gleamY = cy - 15.dp.toPx()
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    center = Offset(gleamX, gleamY),
                    radius = 8.dp.toPx()
                )
            }

            // Top Status Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = ShopnovaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "3D Interactive Mode",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Drag to Rotate 360°",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }

            // Bottom Control Toolbar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Auto rotate toggle
                IconButton(
                    onClick = { isAutoRotating = !isAutoRotating },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (isAutoRotating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Auto Spin",
                        tint = if (isAutoRotating) ShopnovaGold else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Grid toggle
                IconButton(
                    onClick = { show3DGrid = !show3DGrid },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Grid4x4,
                        contentDescription = "Grid",
                        tint = if (show3DGrid) ShopnovaGold else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Zoom in / out
                IconButton(
                    onClick = { zoomScale = if (zoomScale > 1.2f) 0.9f else zoomScale + 0.2f },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomIn,
                        contentDescription = "Zoom",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reset Orientation
                IconButton(
                    onClick = {
                        rotationX = 15f
                        rotationY = 0f
                        zoomScale = 1.0f
                        isAutoRotating = true
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.RestartAlt,
                        contentDescription = "Reset",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
