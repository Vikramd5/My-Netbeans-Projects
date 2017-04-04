/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.link.monitor;

import java.util.HashMap;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Vikram
 */
public class UserStatChart
{

    javafx.scene.chart.NumberAxis xAxis, yAxis;
    HashMap<String, XYChart.Series<Number, Number>> userDataMap;
    javafx.scene.chart.XYChart<Number, Number> chart;
    Stage stage;
    int maxSize = 10000;

    public UserStatChart(ObservableList<UserStat> selectedUsers, boolean lineChart)
    {
        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        if (lineChart)
        {
            chart = new LineChart<>(xAxis, yAxis);
        } else
        {
            chart = new AreaChart<>(xAxis, yAxis);
        }
        userDataMap = new HashMap<>(selectedUsers.size());
        for (UserStat u : selectedUsers)
        {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(u.getName());
            long s = u.getCurSz();
            int spd = (int) (s / 1024);
            series.getData().add(new XYChart.Data<>(1, spd));
            chart.getData().add(series);
            userDataMap.put(u.getIp(), series);
        }
        chart.setTitle("User Speed Chart");
        Scene scene = new Scene(chart);
        stage = new Stage();
        stage.setTitle("User Speed Chart");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.getIcons().add(new Image(getClass().getResource("tplink.png").toExternalForm()));
    }

    public void addUserData(UserStat u, int x)
    {
        Platform.runLater(() ->
        {
            if (userDataMap.containsKey(u.getIp()))
            {
                //add data
                long s = u.getCurSz();
                int spd = (int) (s / 1024);
                ObservableList<XYChart.Data<Number, Number>> data = userDataMap.get(u.getIp()).getData();
                data.add(new XYChart.Data<>(x, spd));
                if (data.size() > maxSize)
                {
                    data.remove(0);
                }
            }
        });
    }

    public void show()
    {
        stage.show();
    }

    public void hide()
    {
        stage.close();
    }

    public boolean isShowing()
    {
        return stage.isShowing();
    }
    
    public void addOnClose(EventHandler<WindowEvent> e)
    {
    stage.setOnHidden(e);
    }
}
