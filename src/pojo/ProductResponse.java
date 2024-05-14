package pojo;
public class ProductResponse {
    private boolean success;
    private String message;
    private String error;
    private SavedProduct savedProduct;

    // getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public SavedProduct getSavedProduct() {
        return savedProduct;
    }

    // setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSavedProduct(SavedProduct savedProduct) {
        this.savedProduct = savedProduct;
    }
}