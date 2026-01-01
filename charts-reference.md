# Charts
The `org.httprpc.sierra.charts` package contains classes for producing charts. Sierra supports the following chart types:

* [Pie/Doughnut](#pie-charts)
* [Bar/Stacked Bar](#bar-charts)
* [Time Series](#time-series-charts)
* [Scatter](#scatter-charts)
* [Candlestick](#candlestick-charts)

Each is discussed in more detail below.

## Data Sets
Instances of the `DataSet` class represent the data to be presented by a chart:

```java
public class DataSet<K extends Comparable<? super K>, V> { ... }
```

`DataSet` defines the following properties:

* "label" - a description of the data set, as a `String`
* "color" - the color associated with the data set, as a `Color`
* "stroke" - the stroke associated with the data set, as an instance of `BasicStroke`
* "dataPoints" - the data set's data points, as an instance of `SortedMap`

Map keys typically represent the chart's domain, with their associated values representing the range. 

For example, the following code creates a simple data set for use by a time series chart:

```java
var dataSet = new DataSet<Double, Double>("Positive Values", Color.RED);

dataSet.setDataPoints(sortedMapOf(
    entry(0.0, 0.0),
    entry(1.0, 10.0),
    entry(2.0, 20.0),
    entry(3.0, 30.0),
    entry(4.0, 40.0)
));

chart.setDataSets(listOf(dataSet));
```

The resulting output is shown below:

<img src="sierra/src/test/resources/org/httprpc/sierra/charts/time-series-chart-positive-values.svg" width="640"/>

## ChartPane
The `ChartPane` component displays a chart. Chart panes can be created either programmatically or declaratively. For example:

```xml
<column-panel tabTitle="Pie Chart" opaque="true" background="TextArea.background" spacing="16" padding="16">
    <label font="h1.font" text="Sales by Region" horizontalAlignment="center"/>
    <chart-pane name="pieChartPane" weight="1"/>
    <row-panel>
        <spacer weight="1"/>
        <row-panel name="pieChartLegendPanel" spacing="16"/>
        <spacer weight="1"/>
    </row-panel>
</column-panel>
```

The following code creates a pie chart and populates the associated legend panel using the labels from the chart's data set and instances of the `PieChart.LegendIcon` type:

```java
pieChartPane.setChart(createPieChart());

for (var dataSet : pieChartPane.getChart().getDataSets()) {
    pieChartLegendPanel.add(new JLabel(dataSet.getLabel(),
        new PieChart.LegendIcon(dataSet),
        SwingConstants.LEADING));
}
```

## Pie Charts
TODO

<img src="README/charts/pie.png" width="630">

### Doughnut Charts
TODO 

<img src="README/charts/doughnut.png" width="630">

## Bar Charts
TODO

<img src="README/charts/bar.png" width="630">

### Stacked Bar Charts
TODO

<img src="README/charts/stacked-bar.png" width="630">

## Time Series Charts
TODO

<img src="README/charts/time-series.png" width="630">

### Value Markers
TODO

<img src="README/charts/time-series-value-markers.png" width="630">

## Scatter Charts
TODO

<img src="README/charts/scatter.png" width="630">

### Trend Lines
TODO

<img src="README/charts/scatter-trend-lines.png" width="630">

## Candlestick Charts
TODO

<img src="README/charts/candlestick.png" width="630">

# Headless Usage
Charts can also be used in a headless environment. For example, the following code uses [Apache Batik](https://xmlgraphics.apache.org/batik/) to create an SVG representation of a chart:

```java
var document = domImplementation.createDocument("http://www.w3.org/2000/svg", "svg", null);

var svgGraphics = new SVGGraphics2D(document);

chart.draw(svgGraphics, WIDTH, HEIGHT);

var writer = new StringWriter();

svgGraphics.stream(writer, false);
```
