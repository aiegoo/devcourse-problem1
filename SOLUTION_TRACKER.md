# Problem1 Solution Tracker


## Task1 — Basic REST Endpoints 

**Endpoints implemented:**
- `GET /users` → `{"users": ["Alice", ...]}`  — name strings, sorted ASC
- `GET /products` → `{"products": [{"name":"..","price":..}, ...]}` — name+price only, default sort by `id`
- `GET /categories` → `{"categories": ["T-shirts", ...]}` — name strings, default sort by `id`

**Solution gist:**
- Single `App.java` with all controllers as static inner classes
- `EntityManager` + native SQL queries (no JPA repositories)
- SQLite DB at `./data/problem.sqlite3`
- Port 5000, address `0.0.0.0`

**Key fixes to pass all TCs:**
- `/products` list → `SELECT name, price` only (no `id`, `category_id`)
- `/categories` list → `SELECT name` only (array of strings, not objects), default sort by `id` (T-shirts = id 1 comes first)
- Error messages lowercase: `"product not found"`, `"category not found"`
- Sort param whitelist prevents SQL injection

---

## Task2 — GET /products/{id} Detail 

**Endpoint:**
- `GET /products/{product_id}` → `{"name": "Basic Tee", "price": 62300}` — name+price only
- 404 → `{"error": "product not found"}`

**Solution gist:**
```java
@GetMapping("/{productId}")
public ResponseEntity<?> getProduct(@PathVariable int productId) {
    List<Object[]> rows = entityManager.createNativeQuery(
        "SELECT name, price FROM product WHERE id = :id"
    ).setParameter("id", productId).getResultList();

    if (rows.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "product not found"));
    Object[] row = rows.get(0);
    return ResponseEntity.ok(Map.of("name", row[0], "price", row[1]));
}
```

**Also fixed -2 categories sort — changed `/categories` default from `name` back to `id`.

---

## Task3 — GET /products/category/{id} 

**Endpoint:**
- `GET /products/category/{category_id}` → `{"products": [{"name":"..","price":..}, ...]}` — name+price only, sorted by `id` ASC
- 404 → `{"error": "category not found"}`

**Solution gist:**
- Check category exists first, return 404 if not
- `SELECT name, price FROM product WHERE category_id = :id ORDER BY id`
- Build list of `{name, price}` maps only (removed id/category_id from response)
- Removed `sort` query param — spec requires fixed id-order sort

---

## Task4 — GET /categories list 


**Endpoint:**
- `GET /categories` → `{"categories": ["T-shirts", "Shirts", "Blouses", ...]}` — name strings, sorted by `id` ASC
- Already implemented in task1-fix; tagged `task4` on existing commit `4c25103`

**Solution gist:**
- `SELECT name FROM category ORDER BY id`
- Returns array of name strings bound to `categories` key
- Default sort: `id` (not `name`) — T-shirts (id=1) must come first

---

## Task5 — GET /categories/{id} detail 


**Endpoint:**
- `GET /categories/{category_id}` → `{"name": "T-shirts"}` — name only
- 404 → `{"error": "category not found"}`

**Solution gist:**
- Was returning `{id, name}` — removed `id` field from response map
- `SELECT id, name FROM category WHERE id = :id` (keep id in query for exists check)
- Only `m.put("name", row[1])` in the response

---

## Production Comments — Architecture Documentation

Added `// --- [PRODUCTION: move to ...]` section markers throughout `App.java` documenting where each section would live in a standard Spring Boot structure:

| Marker | Target file |
|---|---|
| Block comment at top | explains full split structure |
| `config/DataSourceConfig.java` | `@Bean DataSource` |
| `entity/User.java` | User entity |
| `entity/Product.java` | Product entity |
| `entity/Category.java` | Category entity |
| `entity/Review.java` | Review entity |
| `controller/UserController.java` | UserController |
| `controller/ProductController.java` | ProductController |
| `controller/CategoryController.java` | CategoryController |


