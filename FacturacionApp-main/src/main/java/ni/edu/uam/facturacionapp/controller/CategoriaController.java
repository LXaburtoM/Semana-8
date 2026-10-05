package ni.edu.uam.facturacionapp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ni.edu.uam.facturacionapp.dao.CategoriaDAO;
import ni.edu.uam.facturacionapp.modelo.Categoria;

import java.sql.SQLException;
import java.util.Optional;

public class CategoriaController {

    @FXML private TextField txtNombre;
    @FXML private CheckBox chkActiva;
    @FXML private TableView<Categoria> tblCategorias;
    @FXML private TableColumn<Categoria, Integer> colId;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Boolean> colActiva;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    private void initialize() {
        configurarColumnas();
        cargarCategorias();
        chkActiva.setSelected(true);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colActiva.setCellValueFactory(new PropertyValueFactory<>("activa"));
    }

    private void cargarCategorias() {
        ObservableList<Categoria> lista = FXCollections.observableArrayList(categoriaDAO.listar());
        tblCategorias.setItems(lista);
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();

        if (nombre.isEmpty()) {
            mensaje(Alert.AlertType.WARNING, "El nombre de la categoría es obligatorio.");
            txtNombre.requestFocus();
            return;
        }

        try {
            if (categoriaDAO.existeNombre(nombre, null)) {
                mensaje(Alert.AlertType.WARNING, "Ya existe una categoría con ese nombre.");
                return;
            }

            Categoria categoria = new Categoria(null, nombre, chkActiva.isSelected());
            if (categoriaDAO.guardar(categoria)) {
                mensaje(Alert.AlertType.INFORMATION, "Categoría guardada correctamente en la base de datos.");
                limpiar();
                cargarCategorias();
            } else {
                mensaje(Alert.AlertType.ERROR, "Error al guardar la categoría.");
            }
        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "No fue posible completar la operación.");
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminar() {
        Categoria seleccionada = tblCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensaje(Alert.AlertType.WARNING, "Debe seleccionar la categoría que desea eliminar.");
            return;
        }

        try {
            if (categoriaDAO.tieneProductos(seleccionada.getId())) {
                mensaje(Alert.AlertType.WARNING, "No puede eliminar la categoría porque tiene productos asociados.");
                return;
            }

            // Aquí agregarías la lógica de tu método DAO para eliminar la categoría, si lo tuvieras.
            // if(categoriaDAO.eliminar(seleccionada.getId())) { ... }

        } catch (SQLException e) {
            mensaje(Alert.AlertType.ERROR, "No fue posible completar la operación.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }

    private void limpiar() {
        txtNombre.clear();
        chkActiva.setSelected(true);
    }

    private void mensaje(Alert.AlertType tipo, String texto) {
        new Alert(tipo, texto, ButtonType.OK).showAndWait();
    }
}