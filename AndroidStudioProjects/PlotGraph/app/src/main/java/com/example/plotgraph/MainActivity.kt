package com.example.plotgraph

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    companion object{
        val db = FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBarChartTemp()

        setupLineChartHumidity()
    }


    private fun setupLineChartHumidity() {

        val xValsDateLabel = ArrayList<String>()
        val yVals = ArrayList<Entry>()
        var i = -1
        db.collection("cloud-functions-firestore").orderBy("timecollected", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jsonObject = JSONObject(document.data)
                    val humid = jsonObject.optString("humidity").toFloat()
                    val timecollected = jsonObject.optString("timecollected")
                    xValsDateLabel.add(timecollected)
                    yVals.add(Entry((i+1).toFloat(),humid))
                    i++
                }
                println(message = "Hello $yVals")

                val set1: LineDataSet
                set1 = LineDataSet(yVals, "DataSet 1")

                set1.color = Color.BLUE
                set1.setCircleColor(Color.BLUE)
                set1.lineWidth = 1f
                set1.circleRadius = 3f
                set1.setDrawCircleHole(false)
                set1.valueTextSize = 0f
                set1.setDrawFilled(false)

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)

                val data = LineData(dataSets)
                // set data
                lineChart.setData(data)
                lineChart.xAxis.labelRotationAngle = (-80).toFloat()
                lineChart.setVisibleXRangeMaximum(16F)
                lineChart.setViewPortOffsets(50F, 10f, 50f, 250f)
                lineChart.xAxis.valueFormatter = (MyValueFormatter(xValsDateLabel))
                lineChart.description.isEnabled = false
                lineChart.legend.isEnabled = false
                lineChart.setPinchZoom(true)
                lineChart.setTouchEnabled(true)
                lineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
                lineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
                lineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
                //lineChart.setDrawGridBackground()
                lineChart.xAxis.labelCount = i
                lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            }
            .addOnFailureListener { exception ->
                Log.w("errordb", "Error getting documents.", exception)
            }
    }

    private fun setupBarChartTemp() {
        // create BarEntry for Bar Group

        val xValsDateLabel = ArrayList<String>()
        val bargroup = ArrayList<BarEntry>()
        var i = 1
        db.collection("cloud-functions-firestore").orderBy("timecollected", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jsonObject = JSONObject(document.data)
                    val temp = jsonObject.optString("temperature").toFloat()
                    val timecollected = jsonObject.optString("timecollected")
                    xValsDateLabel.add(timecollected)
                    bargroup.add(BarEntry((i+1).toFloat(),temp))
                    i++
                }
                println(message = "Hello $bargroup")

                // creating dataset for Bar Group
                val barDataSet = BarDataSet(bargroup, "Bar DataSet")

                barDataSet.color = ContextCompat.getColor(this, R.color.amber)

                val data = BarData(barDataSet)
                barChart.setData(data)

                barChart.setVisibleXRangeMaximum(15F)
                barChart.xAxis.labelRotationAngle = (-75).toFloat()
                barChart.setViewPortOffsets(50F, 10f, 50f, 250f)
                barChart.xAxis.valueFormatter = (MyValueFormatter(xValsDateLabel))
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.labelCount = i
                barChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
                barChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
                barChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
                barChart.description.isEnabled = false
                barChart.animateY(1000)
                barChart.legend.isEnabled = false
                barChart.setPinchZoom(true)
                barChart.setTouchEnabled(true)
                barChart.data.setDrawValues(false)
            }
            .addOnFailureListener { exception ->
                Log.w("errordb", "Error getting documents.", exception)
            }

    }

    class MyValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }
}