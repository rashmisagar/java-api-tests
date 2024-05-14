package pojo;

public class BeforeUpdateProductResponse {
    private boolean success;
    private String message;
    private BeforeUpdateProduct beforeUpdateProduct;


    // getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BeforeUpdateProduct getBeforeUpdateProduct() {
        return beforeUpdateProduct;
    }

    // setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBeforeUpdateProduct(BeforeUpdateProduct beforeUpdateProduct) {
        this.beforeUpdateProduct = beforeUpdateProduct;
    }
}
