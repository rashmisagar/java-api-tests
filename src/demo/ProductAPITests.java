package demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class ProductAPITests {

    private static final String BASE_URI = "http://localhost:3000";
    private static final String PRODUCT_DESCRIPTION = "This is a test product";
    private static final String VARIANT_NAME = "Variant1";
    private static final String VARIANT_SKU = "V1";
    private String productId;
    private String productName;
    private String invalidProductId;
    Product product = new Product();
    Variant variant = new Variant();

    private String generateRandomProductName() {
        Random random = new Random();
        int randomNum = random.nextInt(1000);  // Generate a random number between 0 and 999
        return "Laptop-" + randomNum;
    }

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
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
        this.productName = generateRandomProductName();
        createProduct(this.productName, PRODUCT_DESCRIPTION, VARIANT_NAME, VARIANT_SKU);

        RequestSpecification request = given()
                .contentType(ContentType.JSON)
                .body(product);

        // Deserialize the response into a ProductResponse object
        ProductResponse productResponse = request.when().post("/api/createProduct")
                .then().log().all().extract().response().as(ProductResponse.class);

        System.out.println("Response: " + productResponse);

        // Assertions
        Assert.assertTrue(productResponse.isSuccess());
        assertEquals("New Product saved successfully!!", productResponse.getMessage());

        // Use the getters to get the _id field
        this.productId = productResponse.getSavedProduct().get_id();
    }

    @Test
    public void testCreateProductWithSameName() {
        createProduct(this.productName, PRODUCT_DESCRIPTION, VARIANT_NAME, VARIANT_SKU);

        RequestSpecification request = given()
                .contentType(ContentType.JSON)
                .body(product);

        // Deserialize the response into a ProductResponse object
        UpdateProductResponse updatedProductResponse = request.when().post("/api/createProduct")
                .then().log().all().extract().response().as(UpdateProductResponse.class);

        // Assertions
        Assert.assertTrue(updatedProductResponse.isSuccess());
        assertEquals("Product's variant updated and saved successfully", updatedProductResponse.getMessage());

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
        //this.productId = "664381368ef21384a2badb17";
        product.setName("MacBook Pro");

        RequestSpecification request = given()
                .contentType(ContentType.JSON)
                .body(product)
                .pathParam("productId", this.productId);

        // Deserialize the response into a ProductResponse object
        BeforeUpdateProductResponse beforeUpdateProductResponse = request.when().patch("/api/updateProduct/{productId}")
                .then().log().all().extract().as(BeforeUpdateProductResponse.class);

        // Assertions
        Assert.assertTrue(beforeUpdateProductResponse.isSuccess());
        Assert.assertEquals("Product is updated and below is it's older form", beforeUpdateProductResponse.getMessage());
    }

    @Test
    public void testUpdateNonExistentProduct() {
        this.invalidProductId = "664287dae2fdcdac02214ca1";
        product.setName("MacBook Pro");
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
        // Delete all test data
        //collection.deleteMany(new Document());

        // Close the MongoDB connection
        //mongoClient.close();
    }

    private void createProduct(String productName, String productDescription, String variantName, String variantSku) {
        product.setName(productName);
        product.setDescription(productDescription);
        product.setPrice(2199.99);

        variant.setName(variantName);
        variant.setSku(variantSku);
        variant.setAdditionalCost(20);
        variant.setStockCount(200);

        List<Variant> variants = new ArrayList<>();
        variants.add(variant);
        product.setVariants(variants);
    }
}
