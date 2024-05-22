package pageObjects;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;
import pojo.Product;
import pojo.ProductResponse;
import pojo.Variant;
import pojo.UpdateProductResponse;
import pojo.BeforeUpdateProductResponse;

import java.util.ArrayList;
import java.util.List;


public class ProductAPI {

    //private static final String PRODUCT_ENDPOINT = "/api/products";
    private static final String CREATE_PRODUCT_ENDPOINT = "/api/products";
    private static final String UPDATE_PRODUCT_ENDPOINT = "/api/products/{productId}";
    private Product product;
    private Variant variant;

    public RequestSpecification request;

    public ProductAPI() {
        request = given().contentType(ContentType.JSON);
    }

    public Product createProduct(String productName, String productDescription, double price, String variantName, String variantSku, double additionalCost, int stockCount) {
        product = new Product();
        variant = new Variant();

        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(price);

        variant.setName(variantName);
        variant.setSku(variantSku);
        variant.setAdditionalCost(additionalCost);
        variant.setStockCount(stockCount);

        List<Variant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);
        return product;
    }

    public Product createProduct(String productName, String productDescription, double price) {
        product = new Product();
        variant = new Variant();

        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(price);

        List<Variant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);
        return product;
    }

    public Product createProduct(String productName) {
        product = new Product();
        product.setName(productName);
        return product;
    }

    public ProductResponse createProductResponse(Product product) {
        return request.body(product)
                .when().post(CREATE_PRODUCT_ENDPOINT)
                .then().log().all().extract().response().as(ProductResponse.class);
    }

    public UpdateProductResponse updateProductResponse(Product product) {
        return request.body(product)
                .when().put(CREATE_PRODUCT_ENDPOINT)
                .then().log().all().extract().response().as(UpdateProductResponse.class);
    }

    public BeforeUpdateProductResponse getProductBeforeUpdate(Product product, String productId) {
        return request.pathParam("productId", productId)
                .when().get(UPDATE_PRODUCT_ENDPOINT + "/{productId}")
                .then().log().all().extract().response().as(BeforeUpdateProductResponse.class);
    }

}
