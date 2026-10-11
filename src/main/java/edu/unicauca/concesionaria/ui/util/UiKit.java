package edu.unicauca.concesionaria.ui.util;

import edu.unicauca.concesionaria.ui.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class UiKit {

    private UiKit() {
    }

    public static void styleRoot(Node node) {
        node.setStyle("-fx-background-color: " + Theme.BG_DARK + ";");
    }

    public static Label title(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + Theme.ACCENT_SOFT + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-font-size: 11px;");
        label.setWrapText(true);
        return label;
    }

    public static TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(inputStyle());
        HBox.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }

    public static DatePicker datePicker(String prompt) {
        DatePicker dp = new DatePicker();
        dp.setPromptText(prompt);
        dp.setStyle(inputStyle());
        return dp;
    }

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT_HOVER + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;"));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + Theme.ACCENT + ";"
                        + "-fx-text-fill: white; -fx-font-weight: bold;"
                        + "-fx-background-radius: 6; -fx-padding: 8 16;"));
        return btn;
    }

    public static Button secondaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + Theme.BG_INPUT + ";"
                        + "-fx-text-fill: " + Theme.TEXT + "; -fx-border-color: " + Theme.BORDER + ";"
                        + "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 16;");
        return btn;
    }

    public static TitledPane section(String title, Node content) {
        TitledPane pane = new TitledPane(title, content);
        pane.setExpanded(false);
        pane.setStyle(
                "-fx-text-fill: " + Theme.TEXT + ";"
                        + "-fx-background-color: " + Theme.BG_PANEL + ";");
        return pane;
    }

    public static VBox panelVBox(Node... children) {
        VBox box = new VBox(12, children);
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color: " + Theme.BG_PANEL + "; -fx-background-radius: 8;");
        return box;
    }

    public static ScrollPane scrollable(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + Theme.BG_DARK + "; -fx-background-color: " + Theme.BG_DARK + ";");
        return scroll;
    }

    public static GridPane formGrid(int columns) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(8));
        return grid;
    }

    public static void addFormRow(GridPane grid, int row, String label, Node input) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + Theme.TEXT + ";");
        lbl.setMinWidth(120);
        grid.add(lbl, 0, row);
        grid.add(input, 1, row);
        GridPane.setHgrow(input, Priority.ALWAYS);
    }

    public static HBox buttonRow(Button... buttons) {
        HBox row = new HBox(10, buttons);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 0, 0));
        return row;
    }

    public static void run(Runnable action) {
        try {
            action.run();
            showInfo("Operación Exitosa", "La operación se ejecutó correctamente.");
        } catch (Throwable ex) {
            handleError(ex);
        }
    }

    public static void runSilent(Runnable action) {
        try {
            action.run();
        } catch (Throwable ex) {
            handleError(ex);
        }
    }

    public static void showInfo(String header, String content) {
        showCustomDialog(header, "Información de la operación", "✓ Operación Exitosa", "#34d399", content, null);
    }

    public static void showError(String header, String content) {
        handleError(new RuntimeException(content != null ? content : header));
    }

    public static void handleError(Throwable ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = ex.toString();
        }
        while (msg.startsWith("Error: ") || msg.startsWith("Error : ")) {
            msg = msg.substring(msg.indexOf(':') + 1).trim();
        }

        String title = "Error en la Operación";
        String subtitle = "No se pudo completar la operación solicitada.";
        String badgeText = "✕ Error";
        String badgeColor = "#ef4444";
        List<String> bullets = new ArrayList<>();

        if (msg.contains("Faltan los siguientes campos obligatorios") || msg.contains("Falta el siguiente campo obligatorio") || msg.contains("incompletos")) {
            title = "Datos Obligatorios Incompletos";
            subtitle = "Por favor complete la información requerida antes de confirmar.";
            badgeText = "⚠ Campos Faltantes";
            badgeColor = "#f59e0b";
            int idx = msg.indexOf(':');
            if (idx >= 0 && idx < msg.length() - 1) {
                String intro = msg.substring(0, idx + 1);
                String itemsPart = msg.substring(idx + 1).trim();
                if (itemsPart.endsWith(".")) {
                    itemsPart = itemsPart.substring(0, itemsPart.length() - 1);
                }
                String[] items = itemsPart.split(",\\s*");
                for (String item : items) {
                    bullets.add(item.trim());
                }
                msg = intro;
            }
        } else if (msg.contains("ya se encuentra registrado") || msg.contains("ya pertenece") || msg.contains("ya existe") || msg.contains("ya está en uso") || msg.contains("duplicad")) {
            title = "Documento o Registro Duplicado";
            subtitle = "El identificador o documento ya pertenece a otro registro en el sistema.";
            badgeText = "✕ Conflicto Duplicado";
            badgeColor = "#ef4444";
        } else if (msg.contains("no encontrado") || msg.contains("no fue encontrado") || msg.contains("no existe")) {
            title = "Elemento No Encontrado";
            subtitle = "El cliente o recurso solicitado no existe en el sistema.";
            badgeText = "🔍 No Encontrado";
            badgeColor = "#0284c7";
        } else if (msg.contains("ORA-") || msg.contains("Base de Datos") || msg.contains("SQL")) {
            title = "Error de Base de Datos";
            subtitle = "Ocurrió una restricción o problema en el motor de base de datos.";
            badgeText = "🗄 Error BD";
            badgeColor = "#dc2626";
        } else if (msg.contains("inválid") || msg.contains("formato") || msg.contains("Teléfono") || msg.contains("Email") || msg.contains("Rol") || msg.contains("Salario")) {
            title = "Validación de Formato";
            subtitle = "Uno de los valores ingresados no cumple con el formato permitido.";
            badgeText = "⚠ Formato Inválido";
            badgeColor = "#f59e0b";
        }

        showCustomDialog(title, subtitle, badgeText, badgeColor, msg, bullets);
    }

    public static void showCustomDialog(String title, String subtitle, String badgeText, String badgeColor, String mainMessage, List<String> bulletPoints) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("KIA Concesionaria");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(ButtonType.CLOSE);
        Node closeBtn = pane.lookupButton(ButtonType.CLOSE);
        if (closeBtn != null) {
            closeBtn.setVisible(false);
            closeBtn.setManaged(false);
        }

        pane.setStyle(
                "-fx-background-color: " + Theme.BG_DARK + ";"
                        + "-fx-border-color: " + Theme.BORDER + ";"
                        + "-fx-border-width: 1;"
                        + "-fx-border-radius: 10;"
                        + "-fx-background-radius: 10;"
                        + "-fx-padding: 10;");

        Label badge = new Label(badgeText);
        badge.setStyle(
                "-fx-background-color: " + badgeColor + "22;"
                        + "-fx-text-fill: " + badgeColor + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
                        + "-fx-border-color: " + badgeColor + ";"
                        + "-fx-border-radius: 12;"
                        + "-fx-background-radius: 12;"
                        + "-fx-padding: 3 10;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: " + Theme.TEXT + "; -fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblSubtitle = new Label(subtitle);
        lblSubtitle.setStyle("-fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-font-size: 12px;");
        lblSubtitle.setWrapText(true);

        VBox headerText = new VBox(4, lblTitle, lblSubtitle);
        HBox headerBox = new HBox(12, badge, headerText);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(6, 6, 12, 6));

        VBox bodyBox = new VBox(8);
        bodyBox.setPadding(new Insets(14));
        bodyBox.setStyle(
                "-fx-background-color: " + Theme.BG_PANEL + ";"
                        + "-fx-border-color: " + Theme.BORDER + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;");

        Label lblMessage = new Label(mainMessage);
        lblMessage.setStyle("-fx-text-fill: " + Theme.TEXT + "; -fx-font-size: 13px;");
        lblMessage.setWrapText(true);
        bodyBox.getChildren().add(lblMessage);

        if (bulletPoints != null && !bulletPoints.isEmpty()) {
            VBox bulletBox = new VBox(6);
            bulletBox.setPadding(new Insets(6, 0, 0, 10));
            for (String item : bulletPoints) {
                Label bullet = new Label("• " + item);
                bullet.setStyle("-fx-text-fill: " + Theme.ACCENT_SOFT + "; -fx-font-size: 13px; -fx-font-weight: bold;");
                bulletBox.getChildren().add(bullet);
            }
            bodyBox.getChildren().add(bulletBox);
        }

        Button okButton = new Button("Entendido");
        okButton.setStyle(
                "-fx-background-color: " + Theme.ACCENT + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 13px;"
                        + "-fx-background-radius: 6;"
                        + "-fx-padding: 8 22;"
                        + "-fx-cursor: hand;");
        okButton.setOnAction(e -> {
            dialog.setResult(null);
            dialog.close();
        });

        HBox footerBox = new HBox(okButton);
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.setPadding(new Insets(12, 6, 4, 6));

        VBox contentBox = new VBox(10, headerBox, bodyBox, footerBox);
        contentBox.setPrefWidth(440);
        pane.setContent(contentBox);

        try {
            var css = UiKit.class.getResource("/styles/dark-purple.css");
            if (css != null) {
                pane.getStylesheets().add(css.toExternalForm());
            }
        } catch (Exception ignored) {
        }

        dialog.showAndWait();
    }

    public static class FeedbackBanner extends HBox {
        private final Label lblIcon = new Label();
        private final Label lblText = new Label();
        private final Button btnClose = new Button("✕");

        public FeedbackBanner() {
            setAlignment(Pos.CENTER_LEFT);
            setSpacing(10);
            setPadding(new Insets(10, 14, 10, 14));
            HBox.setHgrow(lblText, Priority.ALWAYS);
            lblText.setWrapText(true);
            lblText.setStyle("-fx-font-size: 13px;");
            lblIcon.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

            btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-font-size: 12px; -fx-cursor: hand;");
            btnClose.setOnAction(e -> clear());
            getChildren().addAll(lblIcon, lblText, btnClose);
            clear();
        }

        public void showSuccess(String msg) {
            lblIcon.setText("✓");
            lblIcon.setStyle("-fx-text-fill: #34d399; -fx-font-size: 15px; -fx-font-weight: bold;");
            lblText.setText(msg);
            lblText.setStyle("-fx-text-fill: #34d399; -fx-font-size: 13px; -fx-font-weight: bold;");
            setStyle("-fx-background-color: #064e3b33; -fx-border-color: #34d399; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
            setVisible(true);
            setManaged(true);
        }

        public void showError(String msg) {
            lblIcon.setText("⚠");
            lblIcon.setStyle("-fx-text-fill: #f87171; -fx-font-size: 15px; -fx-font-weight: bold;");
            lblText.setText(msg);
            lblText.setStyle("-fx-text-fill: #f87171; -fx-font-size: 13px; -fx-font-weight: bold;");
            setStyle("-fx-background-color: #450a0a33; -fx-border-color: #f87171; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
            setVisible(true);
            setManaged(true);
        }

        public void showInfo(String msg) {
            lblIcon.setText("ℹ");
            lblIcon.setStyle("-fx-text-fill: " + Theme.ACCENT_SOFT + "; -fx-font-size: 15px; -fx-font-weight: bold;");
            lblText.setText(msg);
            lblText.setStyle("-fx-text-fill: " + Theme.TEXT + "; -fx-font-size: 13px;");
            setStyle("-fx-background-color: #1e1b4b33; -fx-border-color: " + Theme.ACCENT + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
            setVisible(true);
            setManaged(true);
        }

        public void clear() {
            setVisible(false);
            setManaged(false);
        }
    }

    public static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + fieldName + "' debe ser un número entero válido.");
        }
    }

    public static Integer parseIntOrNull(String value) {
        String v = emptyToNull(value);
        if (v == null) {
            return null;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ser un número entero válido.");
        }
    }

    public static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Campo '" + fieldName + "' debe ser un número válido.");
        }
    }

    public static Double parseDoubleOrNull(String value) {
        String v = emptyToNull(value);
        if (v == null) {
            return null;
        }
        return Double.parseDouble(v);
    }

    public static Date toSqlDate(DatePicker picker) {
        LocalDate ld = picker.getValue();
        if (ld == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        return Date.valueOf(ld);
    }

    public static Date toSqlDateOrNull(DatePicker picker) {
        LocalDate ld = picker.getValue();
        if (ld == null) {
            return null;
        }
        return Date.valueOf(ld);
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Campo obligatorio: " + fieldName);
        }
        return value.trim();
    }

    private static String inputStyle() {
        return "-fx-background-color: " + Theme.BG_INPUT + ";"
                + "-fx-text-fill: " + Theme.TEXT + ";"
                + "-fx-prompt-text-fill: " + Theme.TEXT_MUTED + ";"
                + "-fx-background-radius: 6;";
    }
}
