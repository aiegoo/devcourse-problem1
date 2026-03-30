package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.*;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    
    // 예시로 주어진 기본 코드입니다. 수정하지 않아도 됩니다. 먼저 코드 채점을 눌러보세요.
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:./data/problem.sqlite3");
        return dataSource;
    }

    @jakarta.persistence.Entity
    @jakarta.persistence.Table(name = "user")
    public static class User {
        @jakarta.persistence.Id
        @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
        private Long id;
        private String name;

        public User() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @jakarta.persistence.Entity
    @jakarta.persistence.Table(name = "product")
    public static class Product {
        @jakarta.persistence.Id
        @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
        private Long id;
        private String name;
        private Integer price;

        @jakarta.persistence.ManyToOne
        @jakarta.persistence.JoinColumn(name = "category_id")
        private Category category;

        public Product() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public Category getCategory() { return category; }
        public void setCategory(Category category) { this.category = category; }
    }

    @jakarta.persistence.Entity
    @jakarta.persistence.Table(name = "category")
    public static class Category {
        @jakarta.persistence.Id
        @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
        private Long id;
        private String name;

        public Category() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @jakarta.persistence.Entity
    @jakarta.persistence.Table(name = "review")
    public static class Review {
        @jakarta.persistence.Id
        @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
        private Long id;
        private String content;

        @jakarta.persistence.ManyToOne
        @jakarta.persistence.JoinColumn(name = "user_id")
        private User user;

        @jakarta.persistence.ManyToOne
        @jakarta.persistence.JoinColumn(name = "product_id")
        private Product product;

        public Review() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public Product getProduct() { return product; }
        public void setProduct(Product product) { this.product = product; }
    }

    
    @RestController
    @RequestMapping("/users")
    public static class UserController {
        @PersistenceContext
        private EntityManager entityManager;

        @GetMapping
        public ResponseEntity<?> getUsers() {
            Query query = entityManager.createNativeQuery("SELECT name FROM user ORDER BY name");
            List<String> userNames = query.getResultList();
            
            return ResponseEntity.ok(Map.of("users", userNames));
        }
    }

    // 여기에 코드를 작성하세요.
    @RestController
    @RequestMapping("/products")
    public static class ProductController {
        @PersistenceContext
        private EntityManager entityManager;

        private String resolveProductSort(String sort) {
            if ("name".equals(sort)) return "name";
            if ("price".equals(sort)) return "price";
            return "id";
        }

        @GetMapping
        public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "id") String sort) {
            String orderField = resolveProductSort(sort);
            @SuppressWarnings("unchecked")
            List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT name, price FROM product ORDER BY " + orderField
            ).getResultList();

            List<Map<String, Object>> productList = new ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> m = new HashMap<>();
                m.put("name", row[0]);
                m.put("price", row[1]);
                productList.add(m);
            }
            return ResponseEntity.ok(Map.of("products", productList));
        }

        @GetMapping("/{productId}")
        public ResponseEntity<?> getProduct(@PathVariable int productId) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT name, price FROM product WHERE id = :id"
            ).setParameter("id", productId).getResultList();

            if (rows.isEmpty()) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "product not found");
                return ResponseEntity.status(404).body(err);
            }
            Object[] row = rows.get(0);
            Map<String, Object> m = new HashMap<>();
            m.put("name", row[0]);
            m.put("price", row[1]);
            return ResponseEntity.ok(m);
        }

        @GetMapping("/category/{categoryId}")
        public ResponseEntity<?> getProductsByCategory(@PathVariable int categoryId) {

            @SuppressWarnings("unchecked")
            List<Object[]> catCheck = entityManager.createNativeQuery(
                "SELECT id FROM category WHERE id = :id"
            ).setParameter("id", categoryId).getResultList();
            if (catCheck.isEmpty()) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "category not found");
                return ResponseEntity.status(404).body(err);
            }

            @SuppressWarnings("unchecked")
            List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT name, price FROM product WHERE category_id = :categoryId ORDER BY id"
            ).setParameter("categoryId", categoryId).getResultList();

            List<Map<String, Object>> productList = new ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> m = new HashMap<>();
                m.put("name", row[0]);
                m.put("price", row[1]);
                productList.add(m);
            }
            return ResponseEntity.ok(Map.of("products", productList));
        }
    }

    @RestController
    @RequestMapping("/categories")
    public static class CategoryController {
        @PersistenceContext
        private EntityManager entityManager;

        @GetMapping
        public ResponseEntity<?> getCategories(@RequestParam(defaultValue = "id") String sort) {
            String orderField = "id".equals(sort) ? "id" : "name";
            @SuppressWarnings("unchecked")
            List<String> names = entityManager.createNativeQuery(
                "SELECT name FROM category ORDER BY " + orderField
            ).getResultList();

            return ResponseEntity.ok(Map.of("categories", names));
        }

        @GetMapping("/{categoryId}")
        public ResponseEntity<?> getCategory(@PathVariable int categoryId) {
            @SuppressWarnings("unchecked")
            List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT id, name FROM category WHERE id = :id"
            ).setParameter("id", categoryId).getResultList();

            if (rows.isEmpty()) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "category not found");
                return ResponseEntity.status(404).body(err);
            }
            Object[] row = rows.get(0);
            Map<String, Object> m = new HashMap<>();
            m.put("id", row[0]);
            m.put("name", row[1]);
            return ResponseEntity.ok(m);
        }
    }
}