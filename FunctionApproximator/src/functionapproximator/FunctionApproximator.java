package functionapproximator;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FunctionApproximator extends Application{
    double x = 0;
    double y = 0;
    @Override
    public void start(Stage stage) throws Exception {
        NNest.NN nn = new NNest().new NN(0.001,"relu","linear","quadratic","momentum",true,1,20,1);
        Group root = new Group();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart chart = new ScatterChart(xAxis,yAxis);
        chart.setPrefSize(700, 700);
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName("Data");
        chart.getData().add(series);
        
        NumberAxis xAxis2 = new NumberAxis();
        NumberAxis yAxis2 = new NumberAxis();
        xAxis2.setAutoRanging(false);
        yAxis2.setAutoRanging(false);
        xAxis2.setTickLabelsVisible(false);
        yAxis2.setTickLabelsVisible(false);
        xAxis2.setMinorTickVisible(false);
        yAxis2.setMinorTickVisible(false);
        xAxis2.setTickMarkVisible(false);
        yAxis2.setTickMarkVisible(false);
        
        LineChart overlay = new LineChart(xAxis2,yAxis2);
        overlay.setPrefSize(700, 700);
        overlay.setOpacity(.5);
        XYChart.Series<Number,Number> function = new XYChart.Series<>();
        overlay.getData().add(function);
        
        TilePane inputBox = new TilePane();
        Text xLabel = new Text("X Value");
        Text yLabel = new Text("Y Value");
        TextField xInput = new TextField();
        TextField yInput = new TextField();
        Button pointEnter = new Button("Enter Point");
        pointEnter.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
                    x = Double.parseDouble(xInput.getText());
                    y = Double.parseDouble(yInput.getText());
                    series.getData().add(new XYChart.Data<>(x, y));
                    function.getData().clear();
                }
                catch(NumberFormatException e){
                }
            }
        });
        inputBox.getChildren().addAll(xLabel,xInput,yLabel,yInput,pointEnter);
        inputBox.setHgap(-60);
        inputBox.setMaxSize(400,0);
        
        yAxis.upperBoundProperty().addListener(new ChangeListener<Number>() {//x axis changes first, then the y axis, so listener goes on the y axis
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                double xLower = xAxis.getLowerBound();
                double xUpper = xAxis.getUpperBound();
                xAxis2.setUpperBound(xUpper);
                yAxis2.setUpperBound(yAxis.getUpperBound());
                xAxis2.setTickUnit(xAxis.getTickUnit());
                yAxis2.setTickUnit(yAxis.getTickUnit());
                xAxis2.setTickLength(xAxis.getTickLength());
                yAxis2.setTickLength(xAxis.getTickLength());
                function.getData().clear();
                for(double i = xLower; i <= xUpper; i = i + (xUpper-xLower)/1000){
                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
                }
            }
        });
        
        HBox train = new HBox();
        Text sessionsLabel = new Text("Sessions");
        TextField sessionsText = new TextField();
        Button sessionsEnter = new Button("Train");
        sessionsEnter.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                function.getData().clear();
                double xLower = xAxis.getLowerBound();
                double xUpper = xAxis.getUpperBound();
                try{
                    for(int i = 0; i < Integer.parseInt(sessionsText.getText()); i++){
                        int random = (int)(Math.random()*series.getData().size());
                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()}}, new float[][]{{series.getData().get(random).getYValue().floatValue()}});
                    }
                }
                catch(NumberFormatException e){
                }
                for(double i = xLower; i <= xUpper; i = i + (xUpper-xLower)/100){
                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
                }
            }
        });
        train.getChildren().addAll(sessionsLabel,sessionsText,sessionsEnter);
        train.setTranslateY(800);
        train.setTranslateX(10);
        root.getChildren().addAll(chart,overlay,inputBox,train);
        inputBox.setTranslateX(700);
        Scene scene = new Scene(root,0,0);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
