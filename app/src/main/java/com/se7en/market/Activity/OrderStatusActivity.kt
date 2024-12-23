package com.se7en.market.Activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.LineStyle
import com.carto.styles.LineStyleBuilder
import com.carto.styles.MarkerStyleBuilder
import com.se7en.market.Adapter.OrderStatusAdapter
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.ViewModel.OrderViewModel
import com.se7en.market.databinding.ActivityOrderStatusBinding
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import org.neshan.common.model.LatLng
import org.neshan.common.utils.PolylineEncoding
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import org.neshan.mapsdk.model.Polyline
import org.neshan.servicessdk.direction.NeshanDirection
import org.neshan.servicessdk.direction.model.NeshanDirectionResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderStatusBinding
    private lateinit var map: MapView
    private var markers: MutableList<Marker> = mutableListOf()
    private var routeOverviewPolylinePoints: ArrayList<LatLng> = arrayListOf()
    private var decodedStepByStepPath: ArrayList<LatLng> = arrayListOf()
    private var onMapPolyline: Polyline? = null
    private var overview = false

    private val orderViewModel: OrderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clickButtons()
        initMap()
        getDistanceMatrix()

        val statuses = listOf(
            "سفارش تأیید شد",
            "کالاها در حال آماده‌سازی هستند",
            "سفارش به پیک تحویل داده شد",
            "سفارش در حال ارسال است",
            "سفارش تحویل داده شد"
        )
        val recyclerView = binding.statusView
        recyclerView.adapter = OrderStatusAdapter(statuses, 2)

    }

    private fun clickButtons() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun initMap() {
        map = binding.map
        map.setStaticMap(true)

        markers.add(createMarker(LatLng(36.30908687275, 59.643053102879)))
        markers.add(createMarker(LatLng(36.297828915961, 59.640982747078)))
        orderViewModel.getDistanceMatrix(
            "${markers[0].latLng.latitude},${markers[0].latLng.longitude}",
            "${markers[1].latLng.latitude},${markers[1].latLng.longitude}"
        )
        map.addMarker(createMarker(markers[0].latLng, R.drawable.home_map))
        map.addMarker(
            createMarker(markers[1].latLng, R.drawable.shop_map, getColor(R.color.light_black))
        )
        mapSetPosition(overview)
        neshanRoutingApi()
    }

    private fun createMarker(
        loc: LatLng,
        markerIcon: Int = R.drawable.marker,
        circleColor: Int = getColor(R.color.light_green),
        extraRadius: Float = 350f
    ): Marker {
        val markerBitmap = BitmapFactory.decodeResource(resources, markerIcon)
        val markerWidth = markerBitmap.width
        val markerHeight = markerBitmap.height

        val circleBitmap = Bitmap.createBitmap(
            (markerWidth + extraRadius * 2).toInt(),
            (markerHeight + extraRadius * 2).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(circleBitmap)
        val paint = Paint().apply {
            color = circleColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val radius = markerWidth / 2f + extraRadius
        canvas.drawCircle(
            circleBitmap.width / 2f,
            circleBitmap.height / 2f,
            radius,
            paint
        )

        canvas.drawBitmap(
            markerBitmap,
            extraRadius,
            extraRadius,
            null
        )

        val animStBl = AnimationStyleBuilder().apply {
            fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
            sizeAnimationType = AnimationType.ANIMATION_TYPE_SPRING
            phaseInDuration = 0.5f
            phaseOutDuration = 0.5f
        }
        val animSt = animStBl.buildStyle()

        val markStCr = MarkerStyleBuilder().apply {
            size = 40f
            bitmap = BitmapUtils.createBitmapFromAndroidBitmap(circleBitmap)
            animationStyle = animSt
            anchorPointX = 0.1f
            anchorPointY = 0.1f
        }
        val markSt = markStCr.buildStyle()
        return Marker(loc, markSt)
    }

    private fun neshanRoutingApi() {
        NeshanDirection.Builder(
            "service.b8ad9848b3614526bcd2dfb35fae9b14",
            markers[0].latLng,
            markers[1].latLng
        )
            .build().call(object : Callback<NeshanDirectionResult?> {
                override fun onResponse(
                    call: Call<NeshanDirectionResult?>,
                    response: Response<NeshanDirectionResult?>
                ) {
                    if (response.body() != null && response.body()!!.routes != null && response.body()!!
                            .routes.isNotEmpty()
                    ) {
                        val route = response.body()!!.routes[0]
                        routeOverviewPolylinePoints =
                            ArrayList(PolylineEncoding.decode(route.overviewPolyline.encodedPolyline))
                        decodedStepByStepPath = ArrayList()

                        // decoding each segment of steps and putting to an array
                        for (step in route.legs[0].directionSteps) {
                            decodedStepByStepPath.addAll(PolylineEncoding.decode(step.encodedPolyline))
                        }

                        onMapPolyline = Polyline(routeOverviewPolylinePoints, getLineStyle())
                        //draw polyline between route points
                        map.addPolyline(onMapPolyline)
                        // focusing camera on first point of drawn line
                        overview = true
                        mapSetPosition(overview)
                    } else {
                        Toast.makeText(
                            this@OrderStatusActivity,
                            "مسیری یافت نشد",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<NeshanDirectionResult?>, t: Throwable) {
                }
            })
    }

    private fun mapSetPosition(overview: Boolean) {
        val centerFirstMarkerX: Double = markers[0].latLng.latitude
        val centerFirstMarkerY: Double = markers[0].latLng.longitude
        if (overview) {
            val centerFocalPositionX: Double =
                (centerFirstMarkerX + markers[1].latLng.latitude) / 2
            val centerFocalPositionY: Double =
                (centerFirstMarkerY + markers[1].latLng.longitude) / 2
            map.moveCamera(LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f)
            map.setZoom(14.5f, 0.5f)
        } else {
            map.moveCamera(LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f)
            map.setZoom(18f, 0.5f)
        }
    }

    private fun getLineStyle(): LineStyle {
        val lineStCr = LineStyleBuilder()
        lineStCr.color =
            com.carto.graphics.Color(76.toShort(), 175.toShort(), 85.toShort(), 255.toShort())
        lineStCr.width = 10f
        lineStCr.stretchFactor = 0f
        return lineStCr.buildStyle()
    }

    private fun getDistanceMatrix() {
        orderViewModel.distanceStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.detailLayout.loadSkeleton()
                }

                is NetworkResult.Success -> {
                    Log.i(TAG, "getDistanceMatrix: ${result.response}")
                    binding.detailLayout.hideSkeleton()
                    binding.deliveryTimeTxt.text = result.response.rows[0].elements[0].duration.text
                }

                is NetworkResult.Error -> {
                    Log.i(TAG, "getDistanceMatrix: ${result.message}")
                    binding.detailLayout.hideSkeleton()
                }
            }
        }
    }

    companion object {
        private const val TAG = "OrderStatusActivity"
    }

}