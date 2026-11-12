# TechTrove - Full-Stack Java DSA + React E-Commerce Recommendation System

**TechTrove** is a complete full-stack e-commerce platform that demonstrates DSA (Data Structures and Algorithms) concepts in action, with a React frontend fetching live data from a Java backend HTTP server.

## Architecture

```
TechTrove/
├── backend/          # Java DSA Backend
│   ├── Product.java        # Product model with id, name, price, rating, category, sku
│   ├── CatalogManager.java # HashMap<String, Product> - O(1) lookups (HASHING topic)
│   ├── RecommendationGraph.java  # Adjacency List HashMap - co-purchase Graphs (GRAPHS topic)
│   └── Main.java           # HttpServer on port 8080 - SORTING & LIVE HTTP SERVER
└── src/              # React Frontend
    └── index.html        # UI with Java backend integration
```

## DSA Topics Implemented

### Hashing (`CatalogManager.java`)
- Uses `HashMap<String, Product>` for O(1) constant-time product lookups by ID
- `addProduct(Product p)` - adds product to the catalog
- `getProduct(String id)` - retrieves product by ID in constant time

### Graphs (`RecommendationGraph.java`)
- Adjacency List using `HashMap<String, List<String>>` 
- Products are Nodes, co-purchases create undirected Edges
- `addProductNode(String id)` - adds a product node to the graph
- `addPurchaseEdge(String prodA, String prodB)` - creates co-purchase edge
- `getRelatedProductIds(String id)` - finds connected neighbor product IDs

### Sorting & Live HTTP Server (`Main.java`)
- Java `com.sun.net.httpserver.HttpServer` on port 8080
- CORS headers (`Access-Control-Allow-Origin: *`) for browser compatibility
- `GET /api/recommendations?id=<PRODUCT_ID>` endpoint:
  1. **Hashing**: Look up product via `CatalogManager`
  2. **Graphs**: Fetch connected neighbor IDs from `RecommendationGraph`
  3. **Sorting**: Sort resulting products by rating descending using `Collections.sort()` with Lambda Comparator
  4. **Live DSA trace logs** printed to Java terminal console
  5. Returns sorted JSON array to frontend

## Running locally

### Prerequisites
- Java JDK 17+ (for the DSA backend)
- Node.js 18+ (for the React frontend)
- npm or yarn

### Step 1: Start the Java DSA Backend

```bash
cd backend
javac *.java
java Main
```

The server will start on `http://localhost:8080` and print DSA trace logs to the console.

### Step 2: Start the React Frontend

```bash
npm install
npm run dev
```

The frontend will be available at `http://localhost:5173` (or http://localhost:3000).

### Step 3: Use the System

1. Visit `http://localhost:5173` in your browser
2. Browse products by category
3. Click "View Product" on any product card or hero slide
4. The Java backend will fetch related product recommendations via:
   - Hashing lookup to find the product
   - Graph traversal to find co-purchased items
   - Sorting by rating descending
5. Recommendations render dynamically below the product details modal
6. If the Java server isn't running, a helpful fallback message displays

## API Endpoint

```
GET http://localhost:8080/api/recommendations?id=<PRODUCT_ID>
```

Returns a JSON array of recommended products sorted by rating descending.

Example response:
```json
[
  {"id":"p3","name":"Wireless Mouse","price":25.99,"rating":4.5,"category":"Mouse","sku":"MOUSE-001"},
  {"id":"p8","monitor","price":299.99,"rating":4.2,"category":"Monitor","sku":"MON-001"}
]
```

## Frontend-Backend Connection

The React frontend fetches live data directly from the Java DSA backend:

```javascript
// Configuration: Ensure Java Main.java server is running on port 8080
const JAVA_BACKEND_BASE = 'http://localhost:8080';

// Fetch recommendations from the Java DSA backend
async function fetchRecommendationsFromBackend(productId) {
  const response = await fetch(`${JAVA_BACKEND_BASE}/api/recommendations?id=${productId}`);
  return await response.json();
}
```

## Technology Stack

- **Backend**: Java, Java built-in HttpServer, HashMap, Graphs (Adjacency List), Collections.sort()
- **Frontend**: React (vanilla), Tailwind CSS CDN, Fetch API
- **DSA Topics**: Hashing, Graphs (Adjacency List), Sorting (Comparators)