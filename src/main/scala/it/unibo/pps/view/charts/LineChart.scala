package it.unibo.pps.view.charts

import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.{XYDataset, XYSeries, XYSeriesCollection}
import org.jfree.chart.{ChartFactory, ChartPanel, JFreeChart}

import scala.collection.immutable.HashMap
import it.unibo.pps.utility.PimpScala.RichOption.*

/** Scala facade for a 2D JFreeChart Line Chart */
trait LineChart:

  /** Method that add a point in the chart
    * @param x
    *   X value of the point
    * @param y
    *   Y value of the point
    * @param serie
    *   The name of the serie to add the new data to
    */
  def addValue(x: Double, y: Double, serie: String): Unit

  /** Method that wraps the chart into a panel */
  def wrapToPanel(): ChartPanel

  /** Method that adds an empty serie to the chart
    * @param name
    *   The name of the new serie
    */
  def addSerie(name: String): Unit

object LineChart:

  def apply(title: String, xLabel: String, yLabel: String): LineChart =
    new LineChartImpl(title, xLabel, yLabel)

  private class LineChartImpl(title: String, xLabel: String, yLabel: String) extends LineChart:
    private val chart = createChart()
    private var series: HashMap[String, XYSeries] = HashMap.empty

    override def addValue(x: Double, y: Double, serie: String): Unit =
      series.get(serie) --> (_.add(x, y))
      chart.getXYPlot.setDataset(mkDataset())

    override def wrapToPanel(): ChartPanel = ChartPanel(chart)

    override def addSerie(name: String): Unit = series = series + (name -> XYSeries(name))

    private def mkDataset(): XYSeriesCollection =
      val dataset = XYSeriesCollection()
      series.foreach((n, s) => dataset.addSeries(s))
      dataset

    private def createChart(): JFreeChart =
      ChartFactory.createXYLineChart(
        title,
        xLabel,
        yLabel,
        XYSeriesCollection(),
        PlotOrientation.VERTICAL,
        true,
        true,
        false
      )
