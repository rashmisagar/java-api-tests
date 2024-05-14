package pojo;

import java.util.List;

public class SavedProduct {
    private String name;
    private String description;
    private double price;
    private List<String> variants;
    private String _id;
    private String createdAt;
    private String updatedAt;
    private int __v;

    // getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getVariants() {
        return variants;
    }

    public String get_id() {
        return _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public int get__v() {
        return __v;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
