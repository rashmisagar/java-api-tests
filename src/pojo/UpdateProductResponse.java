package pojo;

public class UpdateProductResponse {
    private boolean success;
    private String message;
    private String error;
    private UpdatedProduct updatedProduct;


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

    public UpdatedProduct getUpdatedProduct() {
        return updatedProduct;
    }

    // setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUpdatedProduct(UpdatedProduct updatedProduct) {
        this.updatedProduct = updatedProduct;
    }
}
