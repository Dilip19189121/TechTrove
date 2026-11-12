/**
 * Main.java
 * DSA TOPIC: SORTING & LIVE HTTP SERVER
 * Loads the electronics catalog into CatalogManager (Hashing) and maps
 * co-purchase relationships in RecommendationGraph (Graphs).
 * Starts a lightweight HTTP Server on port 8080 using Java's built-in
 * com.sun.net.httpserver.HttpServer. Adds CORS headers so the React app
 * can connect without browser blocks.
 * Creates endpoint GET /api/recommendations?id=<PRODUCT_ID> that:
 *   1. Looks up the clicked product using CatalogManager (Hashing)
 *   2. Fetches connected neighbor IDs from RecommendationGraph (Graphs)
 *   3. Sorts the resulting List<Product> by rating in descending order
 *      using Collections.sort() / Lambda Comparator (Sorting)
 *   4. Prints live DSA trace logs to the Java terminal console
 *   5. Returns the sorted list as a JSON array to the frontend
 */

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        // --- Step 1: Load electronics catalog into CatalogManager (Hashing) ---
        CatalogManager catalogManager = new CatalogManager();

        // Electronics items matching the frontend product cards:
        // White Headphone (p1), 4K Monitor (p2), Blue Headphone (p3), Wireless Mouse (p4), Laptop (p5), Keyboard (p6)
        // Plus additional items from the full catalog: Camera (p7), CCTV (p8), Earbuds (p9), Headphone (p10), Joystick (p11), Keyboard (p12)
        // We'll use the 12 product IDs from the frontend: p1-p12

        Product p1 = new Product("p1", "White Headphone", 299.99, 4.8, "Headphone", "WH-HEAD-001");
        Product p2 = new Product("p2", "4K Monitor", 499.99, 4.5, "Monitor", "MK-4K-001");
        Product p3 = new Product("p3", "Blue Headphone", 199.99, 4.3, "Headphone", "BH-HEAD-001");
        Product p4 = new Product("p4", "Wireless Mouse", 49.99, 4.2, "Mouse", "WM-MOUSE-001");
        Product p5 = new Product("p5", "Laptop", 999.99, 4.7, "Laptop", "LT-LAP-001");
        Product p6 = new Product("p6", "Keyboard", 79.99, 4.4, "Keyboard", "KB-KEY-001");
        Product p7 = new Product("p7", "Camera", 349.99, 4.6, "Camera", "CAM-001");
        Product p8 = new Product("p8", "CCTV", 249.99, 4.1, "CCTV", "CTV-001");
        Product p9 = new Product("p9", "Earbuds", 129.99, 4.5, "Earbuds", "EB-001");
        Product p10 = new Product("p10", "Joystick", 89.99, 4.0, "Joystick", "JS-001");
        Product p11 = new Product("p11", "Speaker", 159.99, 4.3, "Speaker", "SP-001");
        Product p12 = new Product("p12", "Watch", 229.99, 4.2, "Watch", "WT-001");

        // Add all products to the catalog (Hashing)
        catalogManager.addProduct(p1);
        catalogManager.addProduct(p2);
        catalogManager.addProduct(p3);
        catalogManager.addProduct(p4);
        catalogManager.addProduct(p5);
        catalogManager.addProduct(p6);
        catalogManager.addProduct(p7);
        catalogManager.addProduct(p8);
        catalogManager.addProduct(p9);
        catalogManager.addProduct(p10);
        catalogManager.addProduct(p11);
        catalogManager.addProduct(p12);

        System.out.println("[Main] Catalog loaded: " + catalogManager.getProductCount() + " products added via HashMap hashing.");

        // --- Step 2: Map co-purchase relationships in RecommendationGraph (Graphs) ---
        RecommendationGraph recGraph = new RecommendationGraph();

        // Co-purchase edges: which products are frequently bought together
        // These are based on typical electronics bundle patterns
        recGraph.addPurchaseEdge("p1", "p3");   // White Headphone <-> Blue Headphone (same category)
        recGraph.addPurchaseEdge("p1", "p4");   // White Headphone <-> Wireless Mouse (accessory)
        recGraph.addPurchaseEdge("p1", "p5");   // White Headphone <-> Laptop (bundle)
        recGraph.addPurchaseEdge("p1", "p2");   // White Headphone <-> 4K Monitor (bundle)
        recGraph.addPurchaseEdge("p3", "p4");   // Blue Headphone <-> Wireless Mouse (accessory)
        recGraph.addPurchaseEdge("p3", "p1");   // Blue Headphone <-> White Headphone (same category) - undirected
        recGraph.addPurchaseEdge("p5", "p6");   // Laptop <-> Keyboard (peripheral bundle)
        recGraph.addPurchaseEdge("p5", "p2");   // Laptop <-> 4K Monitor (display bundle)
        recGraph.addPurchaseEdge("p5", "p1");   // Laptop <-> White Headphone (bundle) - undirected
        recGraph.addPurchaseEdge("p2", "p10");  // 4K Monitor <-> Joystick (gaming setup)
        recGraph.addPurchaseEdge("p2", "p8");   // 4K Monitor <-> CCTV (monitor security)
        recGraph.addPurchaseEdge("p4", "p10");  // Wireless Mouse <-> Joystick (PC gaming)
        recGraph.addPurchaseEdge("p6", "p11");  // Keyboard <-> Speaker (audio setup)
        recGraph.addPurchaseEdge("p9", "p3");   // Earbuds <-> Blue Headphone (same category)
        recGraph.addPurchaseEdge("p7", "p9");   // Camera <-> Earbuds (photography gear)
        recGraph.addPurchaseEdge("p10", "p4"); // Joystick <-> Wireless Mouse (PC gaming)
        recGraph.addPurchaseEdge("p8", "p12");  // CCTV <-> Watch (tech accessories)

        System.out.println("[Main] Recommendation graph built with " + recGraph.getNodeCount() + " product nodes and co-purchase edges.");

        // --- Step 3: Start lightweight HTTP Server on port 8080 with CORS ---
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Enable CORS for all origins on all exchanges
        server.createContext("/", exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            String path = exchange.getRequestURI().getPath();

            // Handle OPTIONS preflight requests
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.getResponseBody().close();
                return;
            }

            if ("/api/recommendations".equals(path) && "GET".equals(exchange.getRequestMethod())) {
                handleRecommendationsEndpoint(exchange, catalogManager, recGraph);
            } else {
                String notFound = "{\"error\": \"Not found\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes());
                os.close();
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("[Main] HTTP Server started on port " + port);
        System.out.println("[Main] CORS headers enabled: Access-Control-Allow-Origin: *");
        System.out.println("[Main] Endpoint: GET /api/recommendations?id=<PRODUCT_ID>");
        System.out.println("[Main] Visit http://localhost:8080/api/recommendations?id=p1 to test the DSA trace logs.");
    }

    /**
     * Handles the GET /api/recommendations endpoint.
     * Steps:
     *  1. Look up the clicked product using CatalogManager (Hashing)
     *  2. Fetch connected neighbor IDs from RecommendationGraph (Graphs)
     *  3. Sort the resulting List<Product> by rating in descending order
     *     using Collections.sort() / Lambda Comparator (Sorting)
     *  4. Print live DSA trace logs to the Java terminal console
     *  5. Return the sorted list as a JSON array to the frontend
     */
    private static void handleRecommendationsEndpoint(HttpExchange exchange, CatalogManager catalogManager, RecommendationGraph recGraph) {
        // Parse the product ID from query parameters
        String query = exchange.getRequestURI().getQuery();
        System.out.println("[Main] Received request. Query string: '" + query + "'");

        String productId = null;
        if (query != null) {
            // Parse id=<PRODUCT_ID> from query string
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "id".equals(pair[0])) {
                    productId = pair[1];
                    System.out.println("[Main] Parsed productId from query: '" + productId + "'");
                    break;
                }
            }
        }

        if (productId == null) {
            String error = "{\"error\": \"Missing product ID parameter. Usage: /api/recommendations?id=<PRODUCT_ID>\"}";
            try {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(400, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // --- DSA STEP 1: Hashing lookup - Look up the clicked product using CatalogManager ---
        System.out.println("========== DSA TRACE: Starting Recommendation Pipeline ==========");
        System.out.println("[DSA Step 1] Hashing: Looking up product with ID '" + productId + "' via CatalogManager HashMap...");
        Product clickedProduct = catalogManager.getProduct(productId);

        if (clickedProduct == null) {
            String error = "{\"error\": \"Product with ID '" + productId + "' not found in catalog.\"}";
            try {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(404, error.length());
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[DSA Step 1] Hashing: Product not found. Ending pipeline.");
            return;
        }

        System.out.println("[DSA Step 1] Hashing: Found product - '" + clickedProduct.getName() + "' (Rating: " + clickedProduct.getRating() + ")");

        // --- DSA STEP 2: Graph traversal - Fetch connected neighbor IDs from RecommendationGraph ---
        System.out.println("[DSA Step 2] Graphs: Fetching related product IDs from RecommendationGraph adjacency list...");
        List<String> relatedProductIds = recGraph.getRelatedProductIds(productId);

        if (relatedProductIds.isEmpty()) {
            System.out.println("[DSA Step 2] Graphs: No related products found for '" + productId + "'. Returning clicked product as sole recommendation.");
            // Return the clicked product itself as a recommendation
            List<Product> singleResult = new ArrayList<>();
            singleResult.add(clickedProduct);
            String json = sortProductsByRatingDescending(singleResult);
            sendJsonResponse(exchange, json);
            System.out.println("[DSA Step 3] Sorting: Returned single product (no neighbors) as JSON.");
            System.out.println("========== DSA TRACE: Pipeline Complete ==========");
            return;
        }

        // --- DSA STEP 3: Sorting - Sort the resulting List<Product> by rating descending ---
        System.out.println("[DSA Step 3] Sorting: Sorting " + relatedProductIds.size() + " products by rating descending using Collections.sort() / Lambda Comparator...");

        // Map the related product IDs to Product objects from the catalog
        List<Product> relatedProducts = new ArrayList<>();
        for (String relatedId : relatedProductIds) {
            Product relatedProduct = catalogManager.getProduct(relatedId);
            if (relatedProduct != null) {
                relatedProducts.add(relatedProduct);
            }
        }

        // Sort by rating in descending order using Collections.sort with Lambda Comparator
        Collections.sort(relatedProducts, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                // Descending order: higher rating first
                double r1 = p1.getRating();
                double r2 = p2.getRating();
                if (r1 > r2) return -1;
                if (r1 < r2) return 1;
                return 0;
            }
        });

        // Alternatively, using Java 8 Lambda (uncomment if desired):
        // Collections.sort(relatedProducts, (p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        System.out.println("[DSA Step 3] Sorting: Sorted products by rating descending: ");
        for (Product p : relatedProducts) {
            System.out.println("  - " + p.getName() + ": rating=" + p.getRating());
        }

        // Convert sorted products to JSON array
        String json = sortProductsByRatingDescending(relatedProducts);

        // Send JSON response
        sendJsonResponse(exchange, json);

        System.out.println("[DSA Step 3] Sorting: Returned sorted JSON array to frontend.");
        System.out.println("========== DSA TRACE: Pipeline Complete ==========");
    }

    /**
     * Sorts a List<Product> by rating in descending order and returns a JSON array string.
     * Uses Collections.sort() with a Lambda Comparator for the sorting step.
     */
    private static String sortProductsByRatingDescending(List<Product> products) {
        // Make a copy to avoid modifying the original order if needed later
        List<Product sorted = new ArrayList<>(products);

        // Sort by rating in descending order using Collections.sort and Lambda Comparator
        Collections.sort(sorted, (p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));

        // Build JSON array string
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < sorted.size(); i++) {
            Product p = sorted.get(i);
            sb.append(p.toJson());
            if (i < sorted.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Sends a JSON response with proper CORS headers.
     */
    private static void sendJsonResponse(HttpExchange exchange, String json) {
        try {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}