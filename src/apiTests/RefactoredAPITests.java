package apiTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.*;
import utils.*;
import pageObjects.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
    private static final String MISSING_STRING = null;
    private static final int MISSING_INT = 0;
    private static final String ALL_PRODUCTS_ENDPOINT = "/api/allProducts";
    private static final String CREATE_PRODUCT_ENDPOINT = "/api/createProduct";
    private static final String UPDATE_PRODUCT_ENDPOINT = "/api/updateProduct/{productId}";
    private static final String DELETE_PRODUCT_ENDPOINT = "/api/product/{productId}";
    private static final String SEARCH_PRODUCT_ENDPOINT = "/api/product/search";
    
    private String productId;
    private String productName;
    private String invalidProductId;
    Product product = new Product();
    Variant variant = new Variant();
    private ProductAPI productAPI;
    Utilities utils = new Utilities();
    MongoDBManager mongoDBManager;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        //String connectionString = System.getenv("MONGO_URI");
        //mongoDBManager = new MongoDBManager(connectionString, "test");
        productAPI = new ProductAPI();
    }

    @Test
    public void testGetAllProducts() {
        Response response = given()
                .when()
                .get(ALL_PRODUCTS_ENDPOINT);

        // Assertions
        utils.assertResponse(response, 200, true, "All products have been fetched!");
    }

    @Test
    public void testCreateProduct() {
        this.productName = utils.generateRandomProductName();
        Product product = productAPI.createProduct(productName, PRODUCT_DESCRIPTION, PRICE, VARIANT_NAME, VARIANT_SKU, ADDITIONAL_COST, STOCK_COUNT);
        ProductResponse productResponse = productAPI.createProductResponse(product, CREATE_PRODUCT_ENDPOINT);
        // Assertions
        Assert.assertTrue(productResponse.isSuccess());
        assertEquals("New Product saved successfully!!", productResponse.getMessage());
        // Use the getters to get the _id field
        this.productId = productResponse.getSavedProduct().get_id();
    }

    @Test
    public void testCreateProductWithSameName() {
        Product product = productAPI.createProduct(this.productName, PRODUCT_DESCRIPTION, PRICE, VARIANT_NAME, VARIANT_SKU, ADDITIONAL_COST, STOCK_COUNT);
        UpdateProductResponse updateProductResponse = productAPI.updateProductResponse(product, CREATE_PRODUCT_ENDPOINT);
        // Assertions
        Assert.assertTrue(updateProductResponse.isSuccess());
        assertEquals("Product's variant updated and saved successfully", updateProductResponse.getMessage());

    }

    @Test
    public void testCreateProductWithMissingVariant() {
        product.setName("Test Product15");
        product.setDescription("This is a test product");
        product.setPrice(2199.99);

        List<Variant> variants = new ArrayList<>();
        product.setVariants(variants);

        RequestSpecification request = given()
                .contentType(ContentType.JSON)
                .body(product);

        // Deserialize the response into a ProductResponse object
        ProductResponse productResponse = request.when().post("/api/createProduct")
                .then().log().all().extract().response().as(ProductResponse.class);

        // Assertions
        assertFalse(productResponse.isSuccess());
        assertEquals("Variants array is required and should not be empty", productResponse.getError());
    }

    @Test
    public void testUpdateProductById() {
        //Product product = productAPI.createProduct(productName);
        product.setName("MacBook Pro");
        BeforeUpdateProductResponse beforeUpdateProductResponse = productAPI.getProductBeforeUpdate(product, this.productId, UPDATE_PRODUCT_ENDPOINT);
        // Assertions
        Assert.assertTrue(beforeUpdateProductResponse.isSuccess());
        Assert.assertEquals("Product is updated and below is it's older form", beforeUpdateProductResponse.getMessage());
    }

    @Test
    public void testUpdateNonExistentProduct() {
        this.invalidProductId = "664287dae2fdcdac02214ca1";
        product.setName("MacBook Pro");
        Product product = productAPI.createProduct(productName);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(product)
                .pathParam("productId", invalidProductId)
                .when()
                .patch(UPDATE_PRODUCT_ENDPOINT);

        // Assertions
        utils.assertResponse(response, 404, false, "Product was not found.");
    }

    @Test
    public void testSearchItems() {
        String searchTerm = "Laptop";

        given()
                .queryParam("q", searchTerm)
                .when()
                .get(SEARCH_PRODUCT_ENDPOINT)
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
                .get(SEARCH_PRODUCT_ENDPOINT)
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
                .delete(DELETE_PRODUCT_ENDPOINT);

        // Assertions
        utils.assertResponse(response, 200, true, "Product with id " +this.productId+ " is deleted");
    }

    @Test
    public void testDeleteNonExistentProductById() {
        this.invalidProductId = "664287dae2fdcdac02214ca1";
        Response response = given()
                .pathParam("productId", this.invalidProductId)
                .when()
                .delete(DELETE_PRODUCT_ENDPOINT);

        // Assertions
        utils.assertResponse(response, 404, false, "Product was not found.");
    }

    @Test
    public void testDeleteInvalidProduct() {
        Response response = given()
                .pathParam("productId", "invalidproduct")
                .when()
                .delete(DELETE_PRODUCT_ENDPOINT);

        // Assertions
        utils.assertResponse(response, 500, false, "Internal Server Error!!");
    }

    @AfterClass
    public void cleanup() {
        //mongoDBManager.cleanupCollections("products");
        //mongoDBManager.cleanupCollections("variants");
        //mongoDBManager.cleanupCollections("test");
        //mongoDBManager.close();
    }

}
