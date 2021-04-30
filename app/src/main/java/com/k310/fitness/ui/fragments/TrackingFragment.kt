package com.k310.fitness.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.k310.fitness.R
import com.k310.fitness.databinding.FragmentTrackingBinding
import com.k310.fitness.db.training.Training
import com.k310.fitness.services.Polyline
import com.k310.fitness.services.TrackingService
import com.k310.fitness.ui.activities.MainActivity
import com.k310.fitness.ui.viewmodels.MainViewModel
import com.k310.fitness.util.Compass
import com.k310.fitness.util.CompassSetup
import com.k310.fitness.util.Constants.ACTION_PAUSE_SERVICE
import com.k310.fitness.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.k310.fitness.util.Constants.ACTION_STOP_SERVICE
import com.k310.fitness.util.Constants.MAP_ZOOM
import com.k310.fitness.util.Constants.POLYLINE_COLOR
import com.k310.fitness.util.Constants.POLYLINE_WIDTH
import com.k310.fitness.util.TrackingUtil
import com.k310.fitness.util.training.TrainingType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.math.round

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TrackingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var cordPoints = mutableListOf<Polyline>()
    private var startTime: Calendar? = null

    private lateinit var binding: FragmentTrackingBinding

    private var map: GoogleMap? = null

    private var currentTimeMs = 0L
    private lateinit var compass: Compass

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTrackingBinding.inflate(layoutInflater)
        binding.mapView.onCreate(savedInstanceState)
        binding.startTraining.setOnClickListener {
            toggleTraining()
        }
        binding.cancelTraining.setOnClickListener {
            showCancelDialog()
        }
        binding.finishTraining.setOnClickListener {
            zoomFull()
            endRunAndSave()
        }
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
        subscribeToObservers()
        compass = activity?.let { Compass(it) }!!
        activity?.let {
            CompassSetup(compass, it, binding.mainImageHands)
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
            binding.tvTimer.text = formattedTime
        })
    }

    private fun toggleTraining() {
        if (isTracking) {
            sendCommand(ACTION_PAUSE_SERVICE)
        } else {
            sendCommand(ACTION_START_OR_RESUME_SERVICE)
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
        binding.tvTimer.text = "00:00:00:00"
        (activity as MainActivity?)!!.toTrainingFragment()
        sendCommand(ACTION_STOP_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && currentTimeMs > 0L) {
            binding.startTraining.text = "Start"
            binding.finishTraining.visibility = View.VISIBLE
            binding.cancelTraining.visibility = View.VISIBLE
        } else if (isTracking) {
            binding.startTraining.text = "Stop"
            binding.finishTraining.visibility = View.GONE
            binding.cancelTraining.visibility = View.GONE
        }
    }

    private fun moveCamera() {
        if (cordPoints.isNotEmpty() && cordPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    cordPoints.last().last(),
                    MAP_ZOOM
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
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.width * 0.05f).toInt()
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
                        TrainingType.CYCLING,
                        it, Calendar.getInstance(),
                        distanceInM.toFloat(),
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
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (cordPoints.isNotEmpty() && cordPoints.last().size > 1) {
            val beforeLastLatLng = cordPoints.last()[cordPoints.last().size - 2]
            val lastLatLng = cordPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(beforeLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
            Timber.d("Polyline Added")
        }
    }

    private fun sendCommand(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        compass.start()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        compass.start()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        compass.stop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        compass.stop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}