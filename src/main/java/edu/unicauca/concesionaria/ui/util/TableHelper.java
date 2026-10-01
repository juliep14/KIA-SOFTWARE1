package edu.unicauca.concesionaria.ui.util;

import edu.unicauca.concesionaria.ui.Theme;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class TableHelper {

    private TableHelper() {
    }

    public static <T> TableView<T> create() {
        TableView<T> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setStyle(
                "-fx-background-color: " + Theme.BG_INPUT + ";"
                        + "-fx-control-inner-background: " + Theme.BG_INPUT + ";"
                        + "-fx-text-fill: " + Theme.TEXT + ";");
        table.setPlaceholder(new javafx.scene.control.Label("Sin datos"));
        return table;
    }

    public static <T> TableColumn<T, String> col(String title, java.util.function.Function<T, String> getter) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cd -> new SimpleStringProperty(getter.apply(cd.getValue())));
        return col;
    }

    public static <T> void setItems(TableView<T> table, java.util.List<T> items) {
        ObservableList<T> list = FXCollections.observableArrayList(items);
        table.setItems(list);
    }
}
