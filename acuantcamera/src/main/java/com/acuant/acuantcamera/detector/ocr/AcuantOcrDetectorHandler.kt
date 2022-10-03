package com.acuant.acuantcamera.detector.ocr

import android.graphics.Point
import androidx.annotation.UiThread

interface AcuantOcrDetectorHandler{
    @UiThread
    fun onOcrDetected(textBlock: String?)

    @UiThread
    fun onPointsDetected(points: Array<Point>?)
}
