/**
 * RecommendationGraph.java
 * Uses an Adjacency List (HashMap<String, List<String>>) where product IDs are nodes
 * and co-purchases create undirected edges. Models frequently-bought-together
 * relationships between electronics products.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationGraph {
    // Adjacency List: product ID -> list of related product IDs (co-purchase edges)
    private final HashMap<String, List<String>> adjacencyList;

    public RecommendationGraph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Adds a product node to the graph if it doesn't already exist.
     * Initializes an empty adjacency list for this node.
     *
     * @param id the unique product ID
     */
    public void addProductNode(String id) {
        if (!adjacencyList.containsKey(id)) {
            adjacencyList.put(id, new ArrayList<>());
            System.out.println("[RecommendationGraph] Added product node '" + id + "' to adjacency list.");
        } else {
            System.out.println("[RecommendationGraph] Graph: Product node '" + id + "' already exists. Skipping addition.");
        }
    }

    /**
     * Adds an undirected edge between two product IDs, representing a co-purchase relationship.
     * This creates a connection in both directions (A -> B and B -> A).
     *
     * @param prodA the first product ID
     * @param prodB the second product ID
     */
    public void addPurchaseEdge(String prodA, String prodB) {
        // Ensure both nodes exist
        addProductNode(prodA);
        addProductNode(prodB);

        // Add B to A's adjacency list
        List<String> neighborsA = adjacencyList.get(prodA);
        if (!neighborsA.contains(prodB)) {
            neighborsA.add(prodB);
            System.out.println("[RecommendationGraph] Added undirected edge: '" + prodA + "' <-> '" + prodB + "'.");
        }

        // Add A to B's adjacency list
        List<String> neighborsB = adjacencyList.get(prodB);
        if (!neighborsB.contains(prodA)) {
            neighborsB.add(prodA);
            System.out.println("[RecommendationGraph] Added undirected edge: '" + prodB + "' <-> '" + prodA + "'.");
        }
    }

    /**
     * Gets the related product IDs (neighbors) for a given product ID using graph traversal.
     * Returns an empty list if the product ID is not found in the graph.
     *
     * @param id the product ID to find related products for
     * @return a list of related product IDs (neighbors in the adjacency list)
     */
    public List<String> getRelatedProductIds(String id) {
        if (!adjacencyList.containsKey(id)) {
            System.out.println("[RecommendationGraph] No neighbors found for ID '" + id + "'.");
            return new ArrayList<>();
        }

        List<String> neighbors = adjacencyList.get(id);
        System.out.println("[RecommendationGraph] Traversal from ID '" + id + "': found " + neighbors.size() + " related product IDs.");
        return new ArrayList<>(neighbors);
    }

    /**
     * Returns all product nodes in the graph.
     *
     * @return a set of all product IDs (keys in the adjacency list)
     */
    public java.util.Set<String> getAllProductNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Returns the number of product nodes in the graph.
     *
     * @return the count of product nodes
     */
    public int getNodeCount() {
        return adjacencyList.size();
    }

    /**
     * Checks if a product node exists in the graph.
     *
     * @param id the product ID to check
     * @return true if the node exists, false otherwise
     */
    public boolean containsNode(String id) {
        return adjacencyList.containsKey(id);
    }
}