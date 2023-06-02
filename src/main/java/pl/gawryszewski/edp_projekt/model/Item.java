package pl.gawryszewski.edp_projekt.model;

public class Item {
    private String name;
    private String price;
    private String description;
    private byte[] imageData;
    private int id;

    public Item(String name, String price, String description, byte[] data)
    {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageData = data;
    }

    public Item(int id, String name, String price, String description, byte[] data)
    {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageData = data;
    }

    public void update(String newName, String newPrice, String newDescription, byte[] newData){
        name = newName;
        price = newPrice;
        description = newDescription;
        imageData = newData;
    }
    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
    public byte[] getImageData() {
        return imageData;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
