/**
 * CatalogManager.java
 * DSA TOPIC: HASHING
 * Uses a Java HashMap<String, Product> to store products by their unique ID
 * for O(1) constant-time lookups. This implements the hashing concept
 * where product IDs serve as keys for immediate retrieval.
 */

import java.util.HashMap;
import java.util.Map;

public class CatalogManager {
    // HashMap mapping product ID -> Product object for O(1) lookups
    private final HashMap<String, Product> catalog;

    public CatalogManager() {
        this.catalog = new HashMap<>();
    }

    /**
     * Adds a product to the catalog.
     * If a product with the same ID already exists, it overwrites the existing entry.
     *
     * @param p the Product to add
     */
    public void addProduct(Product p) {
        catalog.put(p.getId(), p);
        System.out.println("[CatalogManager] Hashing lookup: Added product '" + p.getName() + "' with ID '" + p.getId() + "' to catalog. Hash bucket: " + p.getId().hashCode());
    }

    /**
     * Retrieves a product by its unique ID using hashing.
     * Constant-time O(1) lookup on average.
     *
     * @param id the unique product ID
     * @return the Product if found, or null if no product with that ID exists
     */
    public Product getProduct(String id) {
        Product p = catalog.get(id);
        if (p != null) {
            System.out.println("[CatalogManager] Hashing lookup: Found product '" + p.getName() + "' (ID: '" + id + "') in catalog via hash map get().");
        } else {
            System.out.println("[CatalogManager] Hashing lookup: No product found with ID '" + id + "'. Catalog size: " + catalog.size() + " entries.");
        }
        return p;
    }

    /**
     * Returns all products in the catalog.
     *
     * @return a new HashMap containing all products (copy for safety)
     */
    public Map<String, Product> getAllProducts() {
        return new HashMap<>(catalog);
    }

    /**
     * Returns the number of products in the catalog.
     *
     * @return the count of products
     */
    public int getProductCount() {
        return catalog.size();
    }

    /**
     * Checks if a product with the given ID exists in the catalog.
     *
     * @param id the product ID to check
     * @return true if the product exists, false otherwise
     */
    public boolean containsProduct(String id) {
        boolean exists = catalog.containsKey(id);
        System.out.println("[CatalogManager] Hashing lookup: Contains ID '" + id + "': " + exists);
        return exists;
    }
}