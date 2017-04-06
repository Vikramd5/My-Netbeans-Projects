/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.link.monitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.*;
import javafx.util.Callback;

/**
 *
 * @author Vikram
 */
public class UserStatShow extends Application implements Runnable, EventHandler<WindowEvent>
{

    Scene scene;
    ObservableList<UserStat> data;
    boolean fetch = true;
    TableView table = new TableView();
    ContextMenu menu;
    UserStatChart chart;
    int x = 5;

    public UserStatShow()
    {
        data = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        addMenu();
        table.setContextMenu(menu);
        table.setItems(data);
        addColumns();
        table.sort();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        StackPane p = new StackPane();
        p.getChildren().add(table);
        stage.setTitle("TP-Link Monitor");
        scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new Image(getClass().getResource("tplink.png").toExternalForm()));
        stage.setAlwaysOnTop(true);
        new Thread(this).start();
        stage.setOnCloseRequest(this);
    }

    @Override
    public void run()
    {
        int c1 = 5;
        HashMap<String, String> nmList = new HashMap<>();
        ArrayList<String> arpList = new ArrayList<String>();
        while (fetch)
        {
            try
            {
                if (c1 == 5)
                {
                    fillNameList(nmList);
                    c1 = 0;
                }
                c1++;
                fillArpList(arpList);

                Matcher m = TpLink.getMatcher("SystemStatisticRpm", "interval=5&sortType=1&Num_per_page=50&Goto_page=1", "\\n(\\d+),\"(\\S+)\",\"(\\S+)\",(\\d+),(\\d+),(\\d+),(\\d+),\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,\\d+,");
                System.out.println("*******************************************************************************************************");
                System.out.printf("%3s %15s    %17s %15s %15s %15s %15s\n", "ID", "IP", "mac", "totalPac", "totalSize", "curPac", "curSize");
                data.clear();
                while (m.find())
                {
                    String ip, mac;
                    long totalPac, totalSz, curPac, curSz;
                    ip = m.group(2);
                    mac = m.group(3);
                    totalPac = Long.parseLong(m.group(4));
                    totalSz = Long.parseLong(m.group(5));
                    curPac = Long.parseLong(m.group(6));
                    curSz = Long.parseLong(m.group(7));
                    int id = Integer.parseInt(m.group(1));
                    System.out.printf("%3d %15s    %17s %15s %15s %15s %15s\n", id, ip, mac, totalPac, TpLink.toSize(totalSz), curPac, TpLink.toSize(curSz));
                    String nm = nmList.get(ip);
                    if (nm == null)
                    {
                        nm = "N/A";
                    }
                    UserStat u = new UserStat(ip, mac, nm, totalPac, totalSz, curPac, curSz, id, arpList.contains(ip));
                    data.add(u);
                    if (chart != null && chart.isShowing())
                    {
                        chart.addUserData(u, x);
                    }
                }
                if (x > 0)
                {
                    x += 5;
                }
                table.sort();
                Thread.sleep(5000);

            } catch (Exception ex)
            {
                showException(ex);
                try
                {
                    Thread.sleep(10000);
                } catch (InterruptedException ex1)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void handle(WindowEvent t)
    {
        fetch = false;
        System.exit(0);
    }

    private void fillNameList(HashMap<String, String> nmList) throws IOException
    {
        String p = "\"(.+)\",\\n"
                + "\"[0-9A-F-]+\",\\n"
                + "\"([\\d\\.]+)\",\\n"
                + "\"\\S+\",\\n";
        System.out.println("\n\nRefreshing the Adress allocation list");
        Matcher m = TpLink.getMatcher("AssignedIpAddrListRpm", "", p);
        nmList.clear();
        System.out.printf("%30s   %15s\n", "IP Address", "Host Name");
        while (m.find())
        {
            String ip = m.group(2), nm = m.group(1);
            System.out.printf("%30s   %15s\n", ip, nm);
            nmList.put(ip, nm);
        }

    }

    private void fillArpList(ArrayList<String> arpList) throws IOException
    {
        String p = "\\n\\d+,\"[0-9A-F-]+\",\"([\\d\\.]+)\",";
        System.out.println("\n\nRefreshing the ARP list");
        Matcher m = TpLink.getMatcher("LanArpBindingListRpm", "", p);
        arpList.clear();
        System.out.printf("%15s\n", "IP Address");
        while (m.find())
        {
            String ip = m.group(1);
            System.out.printf("%15s\n", ip);
            arpList.add(ip);
        }
    }

    private void addColumns()
    {
        TableColumn c1 = new TableColumn("IP Address");
        c1.setCellValueFactory(new PropertyValueFactory<UserStat, String>("ip"));
        c1.setMinWidth(100);
        TableColumn c2 = new TableColumn("MAC");
        c2.setCellValueFactory(new PropertyValueFactory<UserStat, String>("mac"));
        c2.setMinWidth(120);
        TableColumn c3 = new TableColumn("Total Packets");
        c3.setCellValueFactory(new PropertyValueFactory<UserStat, Long>("totalPac"));
        TableColumn c4 = new TableColumn("Total Data");
        c4.setCellValueFactory(new PropertyValueFactory<UserStat, Long>("totalSz"));
        c4.setSortType(TableColumn.SortType.DESCENDING);
        c4.setSortable(true);
        TableColumn c5 = new TableColumn("Current Packets");
        c5.setCellValueFactory(new PropertyValueFactory<UserStat, Long>("curPac"));
        TableColumn c6 = new TableColumn("Current Data");
        c6.setCellValueFactory(new PropertyValueFactory<UserStat, Long>("curSz"));
        c6.setSortType(TableColumn.SortType.DESCENDING);
        c6.setSortable(true);
        TableColumn c7 = new TableColumn("Total Data");
        c7.setMinWidth(70);
        c7.setCellValueFactory(new PropertyValueFactory<UserStat, String>("szTotalSz"));
        TableColumn c8 = new TableColumn("Current Data");
        c8.setMinWidth(70);
        c8.setCellValueFactory(new PropertyValueFactory<UserStat, String>("szCurSz"));
        TableColumn c9 = new TableColumn("Name");
        c9.setMinWidth(170);
        c9.setCellValueFactory(new PropertyValueFactory<UserStat, String>("name"));
        TableColumn c10 = new TableColumn("Active");
        c10.setMinWidth(10);
        c10.setCellValueFactory(new PropertyValueFactory<UserStat, Boolean>("active"));
        c10.setSortType(TableColumn.SortType.DESCENDING);
        c10.setSortable(true);

        table.getSortOrder().clear();
        table.getSortOrder().addAll(c6, c10, c4);
        table.setRowFactory(new Callback<TableView, TableRow<UserStat>>()
        {
            public TableRow<UserStat> call(TableView tableObj)
            {
                return new TableRow<UserStat>()
                {
                    @Override
                    protected void updateItem(UserStat t, boolean empty)
                    {
                        super.updateItem(t, empty); //To change body of generated methods, choose Tools | Templates.
                        if (!empty && !t.isActive())
                        {
                            this.setStyle("-fx-background-color: #F88;");
                        } else
                        {
                            this.setStyle("");
                        }
                    }
                };
            }
        });
        table.getColumns().addAll(c1, c8, c7, c9, c2, c3, c5, c4, c6, c10);
    }

    private void deleteStat(int id)
    {
        System.out.println("Deleting " + id);
        try
        {
            String qr = (id == -1 ? "DeleteAll=All" : ("delone=" + id)) + "&interval=5&autoRefresh=0&Num_per_page=50&Goto_page=1&sortType=1&Num_per_page=50&Goto_page=1";
            int r = TpLink.getConnecttion("SystemStatisticRpm", qr).getResponseCode();
            if (r == 200)
            {
                if (id == -1)
                {
                    table.getItems().clear();
                }
                showMessage("Deleted Successfully");
            } else
            {
                showError("Failed to delete! Response code:  " + r);
            }
        } catch (IOException ex)
        {
            showException(ex);
        }
    }

    static void showMessage(String msg)
    {
        showAlert(Alert.AlertType.INFORMATION, msg, "Message");
    }

    static void showError(String msg)
    {
        showAlert(Alert.AlertType.INFORMATION, msg, "Error");
    }

    static private void showAlert(Alert.AlertType t, String msg, String title)
    {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setContentText(msg);
        a.setHeaderText(null);
        a.show();
    }

    static void showException(Exception ex)
    {
        Platform.runLater(() ->
        {
            ex.printStackTrace();
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String s = sw.toString();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Error: " + ex.getLocalizedMessage());
            a.setTitle("Exception Occured");
            a.setHeaderText(null);
            TextArea t = new TextArea(s);
            t.setWrapText(true);
            t.setEditable(false);
            a.getDialogPane().setExpandableContent(t);
            a.showAndWait();
        });
    }

    private void addMenu()
    {
        MenuItem deleteMenu = new MenuItem("Delete");
        MenuItem deleteAllMenu = new MenuItem("Delete All");
        MenuItem showLChartMenu = new MenuItem("Show Line Chart");
        MenuItem showAChartMenu = new MenuItem("Show Area Chart");
        MenuItem showSAChartMenu = new MenuItem("Show Stacked Area Chart");
        menu = new ContextMenu(deleteMenu, deleteAllMenu, showLChartMenu, showAChartMenu, showSAChartMenu);

        deleteMenu.setOnAction(ev ->
        {
            UserStat s = (UserStat) table.getSelectionModel().getSelectedItem();
            if (s != null)
            {
                deleteStat(s.id);
            }
        });

        deleteAllMenu.setOnAction(ev ->
        {
            deleteStat(-1);
        });

        EventHandler<ActionEvent> eh = ev ->
        {
            ObservableList<UserStat> selectedUsers = table.getSelectionModel().getSelectedItems();
            if (selectedUsers != null && selectedUsers.size() > 0)
            {
                if (chart != null)
                {
                    chart.hide();
                    chart = null;
                }
                x = 5;
                int cType;
                if (ev.getSource() == showLChartMenu)
                {
                    cType = UserStatChart.LINE_CHART;
                } else if (ev.getSource() == showAChartMenu)
                {
                    cType = UserStatChart.AREA_CHART;
                } else
                {
                    cType = UserStatChart.STACKED_AREA_CHART;
                }
                chart = new UserStatChart(selectedUsers, cType);
                chart.show();
                chart.addOnClose((WindowEvent event) ->
                {
                    x = -1;
                });
            } else
            {
                showError("No Users selected!");
            }
        };
        showLChartMenu.setOnAction(eh);
        showAChartMenu.setOnAction(eh);
        showSAChartMenu.setOnAction(eh);
    }
}
