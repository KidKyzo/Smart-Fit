package com.example.smartfit.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

class StepSensor(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private companion object {
        private const val TAG = "StepSensor"
    }

    val stepFlow: Flow<Int> = callbackFlow {
        if (stepSensor == null) {
            Log.w(TAG, "Step counter sensor not available on this device")
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Step counter sensor found: ${stepSensor.name}, vendor: ${stepSensor.vendor}")

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val stepCount = it.values[0].toInt()
                    Log.d(TAG, "Step count received: $stepCount")

                    // Only send positive values to avoid confusion
                    if (stepCount >= 0) {
                        try {
                            trySend(stepCount)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending step count: ${e.message}")
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d(TAG, "Accuracy changed: $accuracy")
            }
        }

        // Register the listener with the sensor
        val registered = sensorManager.registerListener(
            listener,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        if (registered) {
            Log.d(TAG, "Step sensor listener registered successfully")
        } else {
            Log.e(TAG, "Failed to register step sensor listener")
            close()
            return@callbackFlow
        }

        // Cleanup when flow is closed
        awaitClose {
            Log.d(TAG, "Unregistering step sensor listener")
            sensorManager.unregisterListener(listener)
        }
    }
}


