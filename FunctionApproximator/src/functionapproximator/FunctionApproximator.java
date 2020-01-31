package functionapproximator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FunctionApproximator extends Application{
    double x = 0;
    double y = 0;
    int shrink = 1;
    double lower = 0;
    double upper = 0;
    double maxX = Double.NEGATIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    Scene functionWindow;
    Stage stage1;
    @Override
    public void start(Stage stage) throws Exception {
        NNest.NN nn = new NNest().new NN(0.001,"tanh","tanh","quadratic","momentum",true,1,20,1);
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
                    function.getData().clear();
                    int temp = (int)Math.pow(10, (int)(Math.log10(y)+1));
                    if(temp > shrink)
                        shrink = temp;
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
                    function.getData().clear();
                    int temp = (int)Math.pow(10, (int)(Math.log10(y)+1));
                    if(temp > shrink)
                        shrink = temp;
//                    if(x < minX)
//                        minX = x;
//                    if(x > maxX)
//                        maxX = x;
//                    if(y < minY)
//                        minY = y;
//                    if(y > maxY)
//                        maxY = y;
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
//                lower = xAxis.getLowerBound();
//                upper = xAxis.getUpperBound();
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
                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()/shrink}}, new float[][]{{series.getData().get(random).getYValue().floatValue()/shrink}});
                    }
//                    System.out.println(NNest.globalCost);
                }
                catch(Exception e){
                }
                function.getData().clear();
                double xLower = xAxis.getLowerBound();
                double xUpper = xAxis.getUpperBound();
                for(double i = xLower + (xUpper-xLower)/100; i < xUpper - (xUpper-xLower)/100; i = i + (xUpper-xLower)/100){
                    function.getData().add(new XYChart.Data<>(i,nn.feedforward(new float[][]{{(float)(i/shrink)}})[0][0]*shrink));
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
                    System.out.println(shrink*nn.feedforward(new float[][]{{Float.parseFloat(inputVarField.getText())}})[0][0]/shrink);
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
        
        Group window = new Group();
        Button getFunction = new Button("Get Function");
        getFunction.setTranslateX(300);
        getFunction.setTranslateY(800);
        getFunction.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Text activationFunction = new Text("T(x) = (e^x - e^-x)/(e^x + e^-x)");
                activationFunction.setTranslateY(25);
                TextArea functionTextField = new TextArea();
                functionTextField.setPrefSize(800, 750);
                functionTextField.setTranslateY(50);
                String functionString = "";
                functionString += "(" + shrink + ")T(";
                for(int i = 0; i < nn.getNetworkLayer(0).biases[0].length; i++){
                    functionString += "[" + nn.getNetworkLayer(1).weights[i][0] + "]T(" + nn.getNetworkLayer(0).weights[0][i] + "x + " + nn.getNetworkLayer(0).biases[0][i] + ")";
                    if(i != nn.getNetworkLayer(0).biases[0].length-1)
                        functionString += " + ";
                }
                functionString += " + " + nn.getNetworkLayer(1).biases[0][0] + ")";
                functionTextField.setText(functionString);
                window.getChildren().addAll(functionTextField,activationFunction);
                try{
                    stage1 = new Stage();
                    functionWindow = new Scene(window,800,800);
                    stage1.setScene(functionWindow);
                }
                catch(Exception e){
                    stage1.setScene(functionWindow);
                }
                stage1.show();
            }
        });
        
//        root.getChildren().addAll(chart,overlay,inputBox,train,view);
        root.getChildren().addAll(chart,inputBox,train,view,getFunction);
        inputBox.setTranslateX(700);
        Scene scene = new Scene(root,0,0);
        scene.getStylesheets().add("functionapproximator/ScatterStyle.css");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
//        t = new Thread(()->{
//            while(true){
//                try{
//                    Thread.sleep(1000);
//                    for(int i = 0; i < 10000; i++){
//                        int random = (int)(Math.random()*series.getData().size());
//                        nn.backpropagation(new float[][]{{series.getData().get(random).getXValue().floatValue()/shrink}}, new float[][]{{series.getData().get(random).getYValue().floatValue()/shrink}});
//                    }
//                    Platform.runLater(() -> {
//                        function.getData().clear();
//                        for(double i = minX-(maxX-minX)/10; i <= maxX+(maxX-minX)/10; i = i + (maxX-minX+.0001)/100){
//                            function.getData().add(new XYChart.Data<>(i*shrink,nn.feedforward(new float[][]{{(float)i*shrink}})[0][0]));
//                        }
//                        synchronized(lock){
//                            try{
//                                lock.notifyAll();
//                            }
//                            catch(Exception e){
//                                
//                            }
//                            System.out.println("notify");
//                        }
//                    });
//                    synchronized(lock){
//                        lock.wait();
//                        System.out.println("wait");
//                    }
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
