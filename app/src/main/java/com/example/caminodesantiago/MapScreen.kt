package com.example.caminodesantiago

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(repo: Repository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var monasteries by remember { mutableStateOf<List<Monastery>>(emptyList()) }
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var selected by remember { mutableStateOf<Monastery?>(null) }

    // явное состояние листа — изначально скрыт
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    // загрузка данных
    LaunchedEffect(Unit) {
        monasteries = repo.loadMonasteries()
        routes = repo.loadRoutes()
    }

    // при изменении selected показываем/скрываем лист
    LaunchedEffect(selected) {
        if (selected != null) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    Scaffold { padding ->
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                // sheetContent: если ничего не выбрано — пустой тонкий контейнер,
                // иначе показываем содержимое выбранного монастыря
                val m = selected
                if (m == null) {
                    Box(modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()) {}
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = m.name,
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (m.imageResName.isNotBlank()) {
                            val resId = context.resources.getIdentifier(m.imageResName, "drawable", context.packageName)
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = m.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp, max = 260.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                ) {
                                    Text("Изображение не найдено в drawable", color = MaterialTheme.colors.onSurface)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                Text("Фотография отсутствует", color = MaterialTheme.colors.onSurface)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val descText = if (m.description.isNotBlank()) m.description else "Описание отсутствует"
                        Text(
                            text = descText,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                // закрываем лист, сбрасывая selected
                                selected = null
                            }) {
                                Text("Закрыть")
                            }
                        }
                    }
                }
            }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ) {
                AndroidView(factory = { ctx ->
                    Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                    val map = MapView(ctx)
                    map.setMultiTouchControls(true)
                    map.controller.setZoom(6.0)
                    map.controller.setCenter(GeoPoint(42.5, -4.0))
                    map.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    map
                }, update = { mapView ->
                    // безопасное удаление старых polyline и маркеров
                    val oldPolylines = mapView.overlays.filterIsInstance<Polyline>()
                    if (oldPolylines.isNotEmpty()) mapView.overlays.removeAll(oldPolylines)
                    val oldMarkers = mapView.overlays.filterIsInstance<Marker>()
                    if (oldMarkers.isNotEmpty()) mapView.overlays.removeAll(oldMarkers)

                    // рисуем все маршруты (всегда видимы)
                    routes.forEach { route ->
                        val geoPoints = route.pointIds.mapNotNull { pid ->
                            monasteries.find { it.id == pid }?.let { GeoPoint(it.lat, it.lon) }
                        }
                        if (geoPoints.size >= 2) {
                            val polyline = Polyline(mapView)
                            polyline.setPoints(geoPoints)
                            val colorInt = try {
                                Color.parseColor(route.color)
                            } catch (_: Throwable) {
                                Color.BLACK
                            }
                            polyline.outlinePaint.strokeWidth = 10f
                            polyline.outlinePaint.color = colorInt
                            polyline.outlinePaint.alpha = 220
                            mapView.overlays.add(polyline)
                        }
                    }

                    // добавляем маркеры (поверх линий)
                    monasteries.forEach { m ->
                        val marker = Marker(mapView)
                        marker.position = GeoPoint(m.lat, m.lon)
                        marker.title = m.name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.setOnMarkerClickListener { _, _ ->
                            // центрируем карту на выбранной точке и показываем лист
                            mapView.controller.setCenter(marker.position)
                            selected = m
                            true
                        }
                        mapView.overlays.add(marker)
                    }

                    mapView.invalidate()
                }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
