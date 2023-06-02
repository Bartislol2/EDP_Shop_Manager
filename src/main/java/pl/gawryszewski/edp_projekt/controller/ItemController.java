package pl.gawryszewski.edp_projekt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pl.gawryszewski.edp_projekt.application.MyListener;
import pl.gawryszewski.edp_projekt.model.Item;

import java.io.ByteArrayInputStream;

public class ItemController {
    private static final String CURRENCY = "PLN";

    @FXML
    private Label currencyValue;
    @FXML
    private ImageView itemImage;
    @FXML
    private Label itemName;
    @FXML
    private Label priceValue;
    @FXML
    private VBox contentBox;
    private MyListener myListener;
    private Item item;
    private AnchorPane itemView;
    @FXML
    private void click(MouseEvent event){
        myListener.onClickListener(this);
    }

    public void setItem(Item item, MyListener listener)
    {
        setItemName(item.getName());
        setPriceValue(item.getPrice());
        setCurrencyValue();
        if(item.getImageData()!=null){
            setItemImage(new Image(new ByteArrayInputStream(item.getImageData())));
        }
        this.myListener = listener;
        this.item = item;
    }

    public void updateItem(Item updatedItem)
    {
        item.update(updatedItem.getName(), updatedItem.getPrice(),
                updatedItem.getDescription(), updatedItem.getImageData());
        setItem(item, myListener);
    }
    public Item getItem() {
        return item;
    }

    public void setClicked()
    {
        contentBox.setStyle("-fx-border-color: white;");
    }

    public void setUnclicked()
    {
        contentBox.setStyle("-fx-border-color: #a8a2a2;");
    }

    public void setItemImage(Image image) {
        if(image!=null) {
            itemImage.setImage(image);
            itemImage.setPreserveRatio(true);
            itemImage.setFitWidth(230);
            itemImage.setFitHeight(200);
        }
    }
    public void setItemName(String text) {
        itemName.setText(text);
    }
    public void setPriceValue(String text) {
        priceValue.setText(text);
    }
    public void setCurrencyValue() {
        currencyValue.setText(CURRENCY);
    }
    public void setItemView(AnchorPane view){
        this.itemView = view;
    }
    public AnchorPane getItemView(){
        return itemView;
    }

}
