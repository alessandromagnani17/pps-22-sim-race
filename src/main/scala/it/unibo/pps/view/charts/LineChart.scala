package it.unibo.pps.view.charts

trait LineChart:
  def addValue(x: Double, y: Double): Unit

object LineChart:

  def apply(): LineChart = new LineChartImpl()

  private class LineChartImpl() extends LineChart:

    override def addValue(x: Double, y: Double): Unit = ???
