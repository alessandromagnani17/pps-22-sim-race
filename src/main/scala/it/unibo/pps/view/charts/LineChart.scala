package it.unibo.pps.view.charts

import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}

/** Scala facade for a 2D JFreeChart Line Chart */
trait LineChart:

  /** Method that add a point in the chart
    * @param x
    *   X value of the point
    * @param y
    *   Y value of the point
    */
  def addValue(x: Double, y: Double): Unit

  /** Method that wraps the chart into a panel */
  def wrapToPanel(): ChartPanel

object LineChart:

  def apply(title: String, xLabel: String, yLabel: String, serieName: String): LineChart =
    new LineChartImpl(title, xLabel, yLabel, serieName)

  private class LineChartImpl(title: String, xLabel: String, yLabel: String, serieName: String) extends LineChart:
    private val serie = XYSeries(serieName)
    private val chart = createChart(serie)

    override def addValue(x: Double, y: Double): Unit =
      serie.add(x, y)
      chart.getXYPlot.setDataset(XYSeriesCollection(serie))

    override def wrapToPanel(): ChartPanel =
      ChartPanel(chart)

    private def createChart(dataset: XYSeries): JFreeChart =
      ChartFactory.createXYLineChart(
        title,
        xLabel,
        yLabel,
        XYSeriesCollection(dataset),
        PlotOrientation.VERTICAL,
        true,
        true,
        false
      )
