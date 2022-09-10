package it.unibo.pps.view.charts

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.awt.Color

class ChartTest extends AnyFlatSpec with Matchers:

  "Initially the chart" should "be empty" in {
    val chart = LineChart("Try chart", "x", "y")
    val n = chart.wrapToPanel.getChart.getXYPlot.getDataset.getSeriesCount
    n shouldBe 0
  }

  "The chart" should "have the right title" in {
    val chart = LineChart("Degradation", "x", "y")
    chart.title shouldBe "Degradation"
  }

  "Adding a series and a point" should "increase item count" in {
    val chart = LineChart("Try chart", "x", "y")
    chart.addSeries("Ferrari", Color.RED)
    chart.addValue(0, 0, "Ferrari")
    val n = chart.wrapToPanel.getChart.getXYPlot.getDataset.getSeriesCount
    n shouldBe 1
  }
