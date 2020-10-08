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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBarChartTemp()

        setupLineChartHumidity()
    }


    private fun setupLineChartHumidity() {
        val yVals = ArrayList<Entry>()

        var i = 0
        val db = FirebaseFirestore.getInstance()

        db.collection("cloud-functions-firestore")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jsonObject = JSONObject(document.data)
                    val humid = jsonObject.optString("humidity").toFloat()
                    yVals.add(Entry((i+1).toFloat(),humid))
                    i++
                }
                println(message = "Hello $yVals")

                val set1: LineDataSet
                set1 = LineDataSet(yVals, "DataSet 1")
                println(message = "Set1: $set1")

                set1.color = Color.BLUE
                set1.setCircleColor(Color.BLUE)
                set1.lineWidth = 1f
                set1.circleRadius = 3f
                set1.setDrawCircleHole(false)
                set1.valueTextSize = 0f
                set1.setDrawFilled(false)

                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(set1)
                println(message = "dataset: $dataSets")
                val data = LineData(dataSets)
                println(message = "LineData $data")

                // set data
                lineChart.setData(data)
                lineChart.description.isEnabled = false
                lineChart.legend.isEnabled = false
                lineChart.setPinchZoom(true)
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

        val db = FirebaseFirestore.getInstance()
        val bargroup = ArrayList<BarEntry>()
        var i = -1

        db.collection("cloud-functions-firestore")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jsonObject = JSONObject(document.data)
                    val temp = jsonObject.optString("temperature").toFloat()
                    bargroup.add(BarEntry((i+1).toFloat(),temp))
                    i++
                }
                println(message = "Hello $bargroup")

                // creating dataset for Bar Group
                val barDataSet = BarDataSet(bargroup, "Bar DataSet")
                println(message = "Bar DataSet: $barDataSet")

                barDataSet.color = ContextCompat.getColor(this, R.color.amber)

                val data = BarData(barDataSet)
                println(message = "Bar Data: $data")
                barChart.setData(data)
                barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                barChart.xAxis.labelCount = i
                barChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
                barChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
                barChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
                barChart.description.isEnabled = false
                barChart.animateY(1000)
                barChart.legend.isEnabled = false
                barChart.setPinchZoom(true)
                barChart.data.setDrawValues(false)
            }
            .addOnFailureListener { exception ->
                Log.w("errordb", "Error getting documents.", exception)
            }

    }
}