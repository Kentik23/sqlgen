package sqlgenclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;

public class MainSceneController {
    @FXML
    public Button settingsButton;
    @FXML
    private TreeView<String> dagTreeView;

    @FXML
    private void initialize() {
//        // Создаем корневой элемент
//        TreeItem<String> rootItem = new TreeItem<>("DAGs");
//        rootItem.setExpanded(true);
//
//        // Пример заполнения (в реальности будет из директории)
//        TreeItem<String> airflowItem = new TreeItem<>("Airflow DAGs");
//        airflowItem.getChildren().addAll(
//                new TreeItem<>("etl_pipeline.py"),
//                new TreeItem<>("data_validation.py")
//        );
//
//        TreeItem<String> sparkItem = new TreeItem<>("Spark Jobs");
//        sparkItem.getChildren().add(new TreeItem<>("spark_etl.py"));
//
//        rootItem.getChildren().addAll(airflowItem, sparkItem);
//
//        // Устанавливаем корень
//        dagTreeView.setRoot(rootItem);
//
//        // Добавляем слушатель выбора
//        dagTreeView.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> {
//                    if (newValue != null) {
//                        handleDagSelection(newValue);
//                    }
//                }
//        );
    }

    private void handleDagSelection(TreeItem<String> selectedItem) {
        // Обработка выбора DAG
        System.out.println("Выбран: " + selectedItem.getValue());
    }

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/settings.fxml");
            loader.setLocation(xmlUrl);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }
}