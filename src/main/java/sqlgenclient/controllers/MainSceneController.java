package sqlgenclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class MainSceneController {
    @FXML
    private TreeView<String> dagTreeView;

    @FXML
    private void initialize() {
        // Создаем корневой элемент
        TreeItem<String> rootItem = new TreeItem<>("DAGs");
        rootItem.setExpanded(true);

        // Пример заполнения (в реальности будет из директории)
        TreeItem<String> airflowItem = new TreeItem<>("Airflow DAGs");
        airflowItem.getChildren().addAll(
                new TreeItem<>("etl_pipeline.py"),
                new TreeItem<>("data_validation.py")
        );

        TreeItem<String> sparkItem = new TreeItem<>("Spark Jobs");
        sparkItem.getChildren().add(new TreeItem<>("spark_etl.py"));

        rootItem.getChildren().addAll(airflowItem, sparkItem);

        // Устанавливаем корень
        dagTreeView.setRoot(rootItem);

        // Добавляем слушатель выбора
        dagTreeView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        handleDagSelection(newValue);
                    }
                }
        );
    }

    private void handleDagSelection(TreeItem<String> selectedItem) {
        // Обработка выбора DAG
        System.out.println("Выбран: " + selectedItem.getValue());
    }
}