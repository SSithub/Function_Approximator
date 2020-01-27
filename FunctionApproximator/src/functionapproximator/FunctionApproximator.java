package functionapproximator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FunctionApproximator extends Application{
    double x = 0;
    double y = 0;
    @Override
    public void start(Stage stage) throws Exception {
        NNest.NN nn = new NNest().new NN(0.01,"leakyrelu","linear","quadratic",1,10,10,1);
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
        yAxis.upperBoundProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                xAxis2.setUpperBound(xAxis.getUpperBound());
                yAxis2.setUpperBound(yAxis.getUpperBound());
            }
        });
        LineChart overlay = new LineChart(xAxis2,yAxis2);
        overlay.setPrefSize(700, 700);
        overlay.setOpacity(.5);
        XYChart.Series<Number,Number> function = new XYChart.Series<>();
        overlay.getData().add(function);
        
        TilePane inputBox = new TilePane();
        Text labelX = new Text("X Value");
        Text labelY = new Text("Y Value");
        TextField inputX = new TextField();
        TextField inputY = new TextField();
        Button enter = new Button("Enter Point");
        enter.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                try{
                    x = Double.parseDouble(inputX.getText());
                    y = Double.parseDouble(inputY.getText());
                    series.getData().add(new XYChart.Data<>(x, y));
                    
                }
                catch(Exception e){
                }
            }
        });
        inputBox.getChildren().addAll(labelX,inputX,labelY,inputY,enter);
        inputBox.setHgap(-60);
        inputBox.setMaxSize(400,0);
        
        root.getChildren().addAll(overlay,chart,inputBox);
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
