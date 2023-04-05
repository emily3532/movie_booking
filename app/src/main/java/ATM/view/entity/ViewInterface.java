package ATM.view.entity;

import javafx.fxml.Initializable;

public interface ViewInterface<T> extends Initializable {
    /**
     * Sets the JavaFX parameters (text field, images etc...) based upon an input type's fields.
     * Transaction, Account etc need runtime injection of their data fields. Function should be called in the ATM
     * Window callback after the controller as returned the right T data type.
     *
     * @param entity
     */
    void makeFromEntity(T entity);
    void onClose();
}
