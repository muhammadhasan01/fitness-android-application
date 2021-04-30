package com.k310.fitness.ui.fragments

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.k310.fitness.R
import com.k310.fitness.databinding.FragmentCounterBinding
import com.k310.fitness.db.training.Training
import com.k310.fitness.services.Polyline
import com.k310.fitness.services.TrackingService
import com.k310.fitness.ui.activities.MainActivity
import com.k310.fitness.ui.viewmodels.MainViewModel
import com.k310.fitness.util.Compass
import com.k310.fitness.util.CompassSetup
import com.k310.fitness.util.Constants
import com.k310.fitness.util.TrackingUtil
import com.k310.fitness.util.training.TrainingType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class CounterFragment : Fragment(), SensorEventListener {
    private val viewModel: MainViewModel by viewModels()

    private val context = null
    private var isTracking = false
    private var cordPoints = mutableListOf<Polyline>()
    private var startTime: Calendar? = null

    private lateinit var binding: FragmentCounterBinding

    private var map: GoogleMap? = null

    private var currentTimeMs = 0L
    private lateinit var compass: Compass

    private var distanceInM = 0f

    private var stepCount = 0
    private var totalStep = 0f
    private var prevTotal = 0f

    private var sensorManager: SensorManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        binding = FragmentCounterBinding.inflate(layoutInflater)
        binding.mapViewC.onCreate(savedInstanceState)
        binding.startTrainingC.setOnClickListener {
            toggleTraining()
        }
        binding.cancelTrainingC.setOnClickListener {
            showCancelDialog()
        }
        binding.finishTrainingC.setOnClickListener {
            zoomFull()
            endRunAndSave()
        }
        binding.mapViewC.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
        compass = activity?.let { Compass(it) }!!
        activity?.let {
            CompassSetup(compass, it, binding.mainImageHandsC)
        }
        return binding.root
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.cordPoints.observe(viewLifecycleOwner, Observer {
            cordPoints = it
            addLatestPolyline()
            moveCamera()
        })

        TrackingService.runtimeInMs.observe(viewLifecycleOwner, Observer {
            currentTimeMs = it
            val formattedTime = TrackingUtil.getFormattedTime(currentTimeMs, true)
            binding.tvTimerC.text = formattedTime
        })
    }

    private fun toggleTraining() {
        if (isTracking) {
            sendCommand(Constants.ACTION_PAUSE_SERVICE)
        } else {
            sendCommand(Constants.ACTION_START_OR_RESUME_SERVICE)
            if (startTime == null) {
                startTime = Calendar.getInstance()
            }
        }
    }

    private fun showCancelDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel Training?")
            .setMessage("Are you sure want to cancel the training?")
            .setIcon(R.drawable.icon_delete)
            .setPositiveButton("Yes") { _, _ ->
                stopTraining()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopTraining() {
        prevTotal = totalStep
        stepCount = 0
        binding.counter.text = "0"
        binding.tvTimerC.text = "00:00:00:00"
        sendCommand(Constants.ACTION_STOP_SERVICE)
        (activity as MainActivity?)!!.toTrainingFragment()
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && currentTimeMs > 0L) {
            binding.startTrainingC.text = "Start"
            binding.finishTrainingC.visibility = View.VISIBLE
            binding.cancelTrainingC.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.startTrainingC.text = "Stop"
            binding.finishTrainingC.visibility = View.GONE
            binding.cancelTrainingC.visibility = View.GONE
        }
    }

    private fun moveCamera() {
        if (cordPoints.isNotEmpty() && cordPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    cordPoints.last().last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    private fun zoomFull() {
        val bounds = LatLngBounds.Builder()
        for (polyline in cordPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapViewC.width,
                binding.mapViewC.height,
                (binding.mapViewC.width * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSave() {
        map?.snapshot { bmp ->
            var distanceInM = 0
            for (polyline in cordPoints) {
                distanceInM += TrackingUtil.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInM / 1000f) / (currentTimeMs / 1000f / 60 / 60) * 10) / 10f
            startTime?.let {
                viewModel.insertTraining(
                    Training(
                        bmp,
                        TrainingType.RUNNING,
                        it, Calendar.getInstance(),
                        prevTotal.toFloat(),
                        currentTimeMs,
                        avgSpeed
                    )
                )
                Snackbar.make(
                    requireActivity().findViewById(R.id.rootView),
                    "Training saved",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            stopTraining()
        }
    }

    private fun addAllPolylines() {
        for (polyline in cordPoints) {
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (cordPoints.isNotEmpty() && cordPoints.last().size > 1) {
            val beforeLastLatLng = cordPoints.last()[cordPoints.last().size - 2]
            val lastLatLng = cordPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .add(beforeLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
            distanceInM += TrackingUtil.calculatePolylineLength(cordPoints.last()).toInt()
            Timber.d("Polyline Added, ${stepCount}")
        }
    }

    private fun sendCommand(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapViewC.onResume()
        compass.start()
        val stepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter == null) {
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "No Sensor Detected",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            sensorManager?.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapViewC.onStart()
        compass.start()
    }

    override fun onStop() {
        super.onStop()
        binding.mapViewC.onStop()
        compass.stop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewC.onPause()
        compass.stop()
        sensorManager?.unregisterListener(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewC.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapViewC.onSaveInstanceState(outState)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isTracking) {
            totalStep = event!!.values[0]
            stepCount = totalStep.toInt() - prevTotal.toInt()
            binding.counter.text = ("$stepCount")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}