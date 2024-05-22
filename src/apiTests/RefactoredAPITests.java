package apiTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.*;
import utils.*;
import pageObjects.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RefactoredAPITests {
    private static final String BASE_URI = "http://localhost:3000";
    private static final String PRODUCT_DESCRIPTION = "This is a test product";
    private static final String VARIANT_NAME = "Variant1";
    private static final String VARIANT_SKU = "V1";
    private static final double PRICE = 2199.99;
    private static final double ADDITIONAL_COST = 20;
    private static final int STOCK_COUNT = 200;
    
    private String productId;
    private String productName;
    private String invalidProductId;
    private ProductAPI productAPI;
    Utilities utils = new Utilities();
    MongoDBManager mongoDBManager;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        String connectionString = System.getenv("MONGO_URI");
        mongoDBManager = new MongoDBManager(connectionString, "test");
        productAPI = new ProductAPI();
    }

    @Test
    public void testGetAllProducts() {

        Response response = given()
                .when()
                .get("/api/allProducts");

        // Assertions
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("All products have been fetched!"));
    }

    @Test
    public void testCreateProduct() {
        this.productName = utils.generateRandomProductName();
        Product product = productAPI.createProduct(productName, PRODUCT_DESCRIPTION, PRICE, VARIANT_NAME, VARIANT_SKU, ADDITIONAL_COST, STOCK_COUNT);

        ProductResponse productResponse = productAPI.createProductResponse(product);
        
        // Assertions
        Assert.assertTrue(productResponse.isSuccess());
        assertEquals("New Product saved successfully!!", productResponse.getMessage());

        // Use the getters to get the _id field
        this.productId = productResponse.getSavedProduct().get_id();
    }

    @Test
    public void testCreateProductWithSameName() {
        Product product = productAPI.createProduct(this.productName, PRODUCT_DESCRIPTION, PRICE, VARIANT_NAME, VARIANT_SKU, ADDITIONAL_COST, STOCK_COUNT);

        UpdateProductResponse updateProductResponse = productAPI.updateProductResponse(product);
        
        // Assertions
        Assert.assertTrue(updateProductResponse.isSuccess());
        assertEquals("Product's variant updated and saved successfully", updateProductResponse.getMessage());

    }

    @Test
    public void testCreateProductWithMissingVariant() {
        Product product = productAPI.createProduct(productName, PRODUCT_DESCRIPTION, PRICE);

        ProductResponse productResponse = productAPI.createProductResponse(product);

        // Assertions
        assertFalse(productResponse.isSuccess());
        assertEquals("Variants array is required and should not be empty", productResponse.getError());
    }

    @Test
    public void testUpdateProductById() {
        Product product = productAPI.createProduct(productName);
        String endpoint = "/api/updateProduct/{productId}";
        BeforeUpdateProductResponse beforeUpdateProductResponse = productAPI.getProductBeforeUpdate(product, this.productId);

        // Assertions
        Assert.assertTrue(beforeUpdateProductResponse.isSuccess());
        Assert.assertEquals("Product is updated and below is it's older form", beforeUpdateProductResponse.getMessage());
    }

    @Test
    public void testUpdateNonExistentProduct() {
        this.invalidProductId = "664287dae2fdcdac02214ca1";
        Product product = productAPI.createProduct(productName);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(product)
                .pathParam("productId", this.invalidProductId)
                .when()
                .patch("/api/updateProduct/{productId}");

        // Assertions
        response.then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", equalTo("Product was not found."));
    }

    @Test
    public void testSearchItems() {
        String searchTerm = "product"; // Replace with your actual search term

        given()
                .queryParam("q", searchTerm)
                .when()
                .get("/api/product/search")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Item(s) found!"))
                .body("noOfItem", greaterThan(0));
    }

    @Test
    public void testSearchItemsWithNoResults() {
        String searchTerm = "nonexistent"; // Replace with a search term that yields no results

        given()
                .queryParam("q", searchTerm)
                .when()
                .get("/api/product/search")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Item(s) found!"))
                .body("noOfItem", equalTo(0));
    }

    @Test
    public void testDeleteProductById() {
        Response response = given()
                .pathParam("productId", this.productId)
                .when()
                .delete("/api/product/{productId}");

        // Assertions
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Product with id " +this.productId+ " is deleted"));

    }

    @Test
    public void testDeleteNonExistentProductById() {
        this.invalidProductId = "664287dae2fdcdac02214ca1";
        Response response = given()
                .pathParam("productId", this.invalidProductId)
                .when()
                .delete("/api/product/{productId}");

        // Assertions
        response.then()
                .statusCode(404)
                .body("success", equalTo(false))
                .body("message", equalTo("Product was not found."));
    }

    @Test
    public void testDeleteInvalidProduct() {
        Response response = given()
                .pathParam("productId", "invalidproduct")
                .when()
                .delete("/api/product/{productId}");

        // Assertions
        response.then()
                .statusCode(500)
                .body("success", equalTo(false))
                .body("message", equalTo("Internal Server Error!!"));
    }

    @AfterClass
    public void cleanup() {
        mongoDBManager.cleanupCollections("products");
        mongoDBManager.cleanupCollections("variants");
        mongoDBManager.cleanupCollections("test");
        mongoDBManager.close();
    }

}
