package pl.gawryszewski.edp_projekt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pl.gawryszewski.edp_projekt.model.Item;

public class ItemDescriptionController {
    private static final String CURRENCY = "PLN";
    @FXML
    private Label priceValue;
    @FXML
    private Label itemDescription;
    @FXML
    private Label itemName;
    public void setValues(Item item)
    {
        setItemName(item.getName());
        setItemDescription(item.getDescription());
        setPriceValue(item.getPrice()+" "+CURRENCY);
    }
    public void setItemName(String text) {

        itemName.setText(text);
    }
    public void setPriceValue(String text) {
        priceValue.setText(text);
    }
    public void setItemDescription(String text) {
        itemDescription.setText(text);
    }
    public void clear()
    {
        setItemName("");
        setPriceValue("");
        setItemDescription("");
    }
}
