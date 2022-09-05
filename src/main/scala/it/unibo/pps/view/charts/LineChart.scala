package it.unibo.pps.view.charts

import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}

import scala.collection.immutable.HashMap
import it.unibo.pps.utility.PimpScala.RichOption.*
import it.unibo.pps.utility.PimpScala.RichHashMap.*
import java.awt.Color

/** Scala facade for a 2D JFreeChart Line Chart */
trait LineChart:

  /** Method that adds a point in the chart
    * @param x
    *   X value of the point
    * @param y
    *   Y value of the point
    * @param series
    *   The name of the serie to add the new data to
    */
  def addValue(x: Double, y: Double, series: String): Unit

  /** Method that wraps the chart into a panel */
  def wrapToPanel: ChartPanel

  /** Method that adds an empty serie to the chart
    * @param name
    *   The name of the new serie
    * @param color
    *   The desired color for the series
    */
  def addSeries(name: String, color: Color): Unit

  /** Returns the chart title */
  def title: String

object LineChart:

  def apply(title: String, xLabel: String, yLabel: String): LineChart =
    new LineChartImpl(title, xLabel, yLabel)

  private class LineChartImpl(_title: String, xLabel: String, yLabel: String) extends LineChart:
    private val chart = createChart()
    private var series: HashMap[String, XYSeries] = HashMap.empty

    override def addValue(x: Double, y: Double, seriesName: String): Unit =
      series ?--> (seriesName, _.add(x, y))
      chart.getXYPlot.setDataset(mkDataset())

    override def wrapToPanel: ChartPanel = ChartPanel(chart)

    override def addSeries(name: String, color: Color): Unit =
      series = series + (name -> XYSeries(name))
      setSeriesColor(name, color)

    private def setSeriesColor(name: String, color: Color): Unit =
      series.keysIterator.zipWithIndex
        .foreach((seriesName, index) =>
          seriesName match {
            case sn: String if sn.equals(name) => chart.getXYPlot.getRenderer.setSeriesPaint(index, color)
            case _ =>
          }
        )

    private def mkDataset(): XYSeriesCollection =
      val dataset = XYSeriesCollection()
      series.foreach((_, s) => dataset.addSeries(s))
      dataset

    private def createChart(): JFreeChart =
      ChartFactory.createXYLineChart(
        _title,
        xLabel,
        yLabel,
        XYSeriesCollection(),
        PlotOrientation.VERTICAL,
        true,
        true,
        false
      )

    override def title: String = _title
