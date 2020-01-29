package functionapproximator;

import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.event.HyperlinkEvent;

public class FunctionApproximator extends Application{
    double x = 0;
    double y = 0;
//    double i = 0;
    @Override
    public void start(Stage stage) throws Exception {
        NNest.NN nn = new NNest().new NN(0.001,"tanh","linear","quadratic","momentum",true,1,5,5,5,1);
        Group root = new Group();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        ScatterChart chart = new ScatterChart(xAxis,yAxis);
        chart.setPrefSize(700, 700);
        XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName("Data");
        
//        NumberAxis xAxis2 = new NumberAxis();
//        NumberAxis yAxis2 = new NumberAxis();
//        xAxis2.setAnimated(false);
//        yAxis2.setAnimated(false);
//        xAxis2.setAutoRanging(false);
//        yAxis2.setAutoRanging(false);
//        xAxis2.setTickLabelsVisible(false);
//        yAxis2.setTickLabelsVisible(false);
//        xAxis2.setMinorTickVisible(false);
//        yAxis2.setMinorTickVisible(false);
//        xAxis2.setTickMarkVisible(false);
//        yAxis2.setTickMarkVisible(false);
        
//        LineChart overlay = new LineChart(xAxis2,yAxis2);
//        overlay.setPrefSize(700, 700);
//        overlay.setOpacity(.3);
        XYChart.Series<Number,Number> function = new XYChart.Series<>();
        function.setName("Function");
//        overlay.getData().add(function);
        chart.getData().addAll(series,function);
        
        TilePane inputBox = new TilePane();
        Text xLabel = new Text("X Value");
        Text yLabel = new Text("Y Value");
        TextField xInput = new TextField();
        TextField yInput = new TextField();
        xInput.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
                    x = Double.parseDouble(xInput.getText());
                    y = Double.parseDouble(yInput.getText());
                    series.getData().add(new XYChart.Data<>(x, y));
                }
                catch(NumberFormatException e){
                }
            }
        });
        yInput.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
                    x = Double.parseDouble(xInput.getText());
                    y = Double.parseDouble(yInput.getText());
                    series.getData().add(new XYChart.Data<>(x, y));
                }
                catch(NumberFormatException e){
                }
            }
        });
//        Button pointEnter = new Button("Enter Point");
//        pointEnter.setOnAction(new EventHandler<ActionEvent>(){
//            @Override
//            public void handle(ActionEvent t) {
//                try{
//                    x = Double.parseDouble(xInput.getText());
//                    y = Double.parseDouble(yInput.getText());
//                    series.getData().add(new XYChart.Data<>(x, y));
////                    function.getData().clear();
//                }
//                catch(NumberFormatException e){
//                }
//            }
//        });
        inputBox.getChildren().addAll(xLabel,xInput,yLabel,yInput);
        inputBox.setHgap(-60);
        inputBox.setMaxSize(400,0);
        
//        yAxis.upperBoundProperty().addListener(new ChangeListener<Number>() {//x axis changes first, then the y axis, so listener goes on the y axis
//            @Override
//            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
//                double xLower = xAxis.getLowerBound();
//                double xUpper = xAxis.getUpperBound();
//                xAxis2.setUpperBound(xUpper);
//                yAxis2.setUpperBound(yAxis.getUpperBound());
//                xAxis2.setTickUnit(xAxis.getTickUnit());
//                yAxis2.setTickUnit(yAxis.getTickUnit());
//                xAxis2.setTickLength(xAxis.getTickLength());
//                yAxis2.setTickLength(xAxis.getTickLength());
//                xAxis2.setLayoutX(xAxis.getLayoutX());
//                xAxis2.setLayoutY(xAxis.getLayoutY());
//                yAxis2.setLayoutX(yAxis.getLayoutX());
//                yAxis2.setLayoutY(yAxis.getLayoutY());
////                function.getData().clear();
////                for(double i = xLower; i <= xUpper; i = i + (xUpper-xLower)/100){
////                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
////                }
//            }
//        });
        
        HBox train = new HBox();
        train.setSpacing(10);
        Text sessionsLabel = new Text("Sessions");
        TextField sessionsText = new TextField();
        sessionsText.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
                    for(int i = 0; i < Integer.parseInt(sessionsText.getText()); i++){
                        int random = (int)(Math.random()*series.getData().size());
                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()}}, new float[][]{{series.getData().get(random).getYValue().floatValue()}});
                    }
//                    System.out.println(NNest.globalCost);
                }
                catch(NumberFormatException e){
                }
                function.getData().clear();
                double xLower = xAxis.getLowerBound();
                double xUpper = xAxis.getUpperBound();
                for(double i = xLower; i < xUpper; i = i + (xUpper-xLower)/100){
                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
                }
            }
        });
//        Button sessionsEnter = new Button("Train");
//        sessionsEnter.setOnAction(new EventHandler<ActionEvent>(){
//            @Override
//            public void handle(ActionEvent t) {
////                System.out.println(xAxis.getPrefHeight());
////                System.out.println(xAxis.getLayoutX());
////                System.out.println(xAxis.getZeroPosition());
////                System.out.println(xAxis2.getPrefHeight());
////                System.out.println(xAxis2.getLayoutX());
////                System.out.println(xAxis2.getZeroPosition());
////        System.out.println(xAxis.getScale());
////        System.out.println(xAxis2.getScale());
////        System.out.println(yAxis.getScale());
////        System.out.println(yAxis2.getScale());
//                double xLower = xAxis.getLowerBound();
//                double xUpper = xAxis.getUpperBound();
//                try{
//                    for(int i = 0; i < Integer.parseInt(sessionsText.getText()); i++){
//                        int random = (int)(Math.random()*series.getData().size());
//                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()}}, new float[][]{{series.getData().get(random).getYValue().floatValue()}});
//                    }
////                    System.out.println(NNest.globalCost);
//                }
//                catch(NumberFormatException e){
//                }
//                function.getData().clear();
//                for(double i = xLower; i < xUpper; i = i + (xUpper-xLower)/100){
////                    System.out.println("in " + i);
//                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
////                    System.out.println("out " + nn.feedforward(new float[][]{{(float)i}})[0][0]);
//                }
//            }
//        });
        train.getChildren().addAll(sessionsLabel,sessionsText);
        train.setTranslateY(800);
        train.setTranslateX(10);
        
        VBox view = new VBox();
        Text inputVar = new Text("Input an X value");
        TextField inputVarField = new TextField();
        inputVarField.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
//                    xAxis2.setScaleX(xAxis.getScale());
                    System.out.println(nn.feedforward(new float[][]{{Float.parseFloat(inputVarField.getText())}})[0][0]);
//                    double xLower = xAxis.getLowerBound();
//                    double xUpper = xAxis.getUpperBound();
////                    System.out.println(xLower);
////                    System.out.println(xUpper);
//                    for(double i = xLower; i <= xUpper; i = i + (xUpper-xLower)/1000){
//    //                    System.out.println("in " + i);
//                        function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0]));
//    //                    System.out.println("out " + nn.feedforward(new float[][]{{(float)i}})[0][0]);
//                    }
//                    System.out.println(xAxis.getScale());
//                    System.out.println(xAxis2.getScale());
                }
                catch(Exception e){
                }
            }
        });
        view.getChildren().addAll(inputVar,inputVarField);
        view.setTranslateX(1100);
        
//        root.getChildren().addAll(chart,overlay,inputBox,train,view);
        root.getChildren().addAll(chart,inputBox,train,view);
        inputBox.setTranslateX(700);
        Scene scene = new Scene(root,0,0);
        scene.getStylesheets().add("functionapproximator/ScatterStyle.css");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
//        new Thread(()->{
//            while(true){
//                try{
//                    Thread.sleep(5000);
//                    for(int i = 0; i < 1000; i++){
//                        int random = (int)(Math.random()*series.getData().size());
//                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()}}, new float[][]{{series.getData().get(random).getYValue().floatValue()}});
//                    }
//                    function.getData().clear();
//                    for(i = xAxis.getLowerBound(); i <= xAxis.getUpperBound(); i = i + (xAxis.getUpperBound()-xAxis.getLowerBound())/100){
//                        Platform.runLater(() -> function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)i}})[0][0])));
//                    }
//                }
//                catch(Exception e){
//                }
//            }
//        }).start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
