/**
 * Product.java
 * Model class representing an electronics product in the TechTrove catalog.
 * This class models electronics items with fields matching the frontend product data.
 */

public class Product {
    private final String id;
    private final String name;
    private final double price;
    private final double rating;
    private final String category;
    private final String sku;

    /**
     * Constructor for Product.
     *
     * @param id    unique product ID (e.g., "p1", "p2")
     * @param name  product name
     * @param price product price in USD
     * @param rating product rating (0.0 - 5.0)
     * @param category product category
     * @param sku   stock keeping unit string
     */
    public Product(String id, String name, double price, double rating, String category, String sku) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.category = category;
        this.sku = sku;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }
    public String getSku() { return sku; }

    /**
     * Returns a valid JSON object string for this product.
     * Useful for serializing product data to send to the frontend.
     */
    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"name\":\"%s\",\"price\":%.2f,\"rating\":%.1f,\"category\":\"%s\",\"sku\":\"%s\"}",
            id, name, price, rating, category, sku
        );
    }

    @Override
    public String toString() {
        return name + " (" + id + ") - $" + price + " | Rating: " + rating + "/" + category;
    }
}