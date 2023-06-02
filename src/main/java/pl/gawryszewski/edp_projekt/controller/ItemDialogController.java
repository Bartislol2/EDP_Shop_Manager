package pl.gawryszewski.edp_projekt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.gawryszewski.edp_projekt.model.Item;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ItemDialogController {
    @FXML
    private TextField itemImagePath;
    @FXML
    private TextField itemDescription;
    @FXML
    private TextField itemPrice;
    @FXML
    private TextField itemName;
    public Item createItem() {
        String name = itemName.getText();
        String price = itemPrice.getText();
        String desc = itemDescription.getText();
        Path imagePath = Paths.get(itemImagePath.getText());
        try {
            return new Item(name, price, desc, Files.readAllBytes(imagePath));
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not load image data");
            return new Item(name, price, desc, null);
        }
    }
    @FXML
    private void browseImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));
        Stage stage = new Stage();
        stage.initOwner(itemImagePath.getScene().getWindow());
        stage.setFullScreen(false);
        stage.setResizable(false);
        try{
            String filePath = fileChooser.showOpenDialog(stage).getPath();
            itemImagePath.setText(filePath);
        } catch(NullPointerException e){
            System.out.println("Image loading cancelled");
        }
    }

    public void setValues(Item item) {
        setItemName(item.getName());
        setItemPrice(item.getPrice());
        setItemDescription(item.getDescription());
    }

    private void setItemDescription(String text) {
        itemDescription.setText(text);
    }

    private void setItemPrice(String text) {
        itemPrice.setText(text);
    }

    private void setItemName(String text) {
        itemName.setText(text);
    }

    public TextField getItemPrice(){
        return itemPrice;
    }
}
