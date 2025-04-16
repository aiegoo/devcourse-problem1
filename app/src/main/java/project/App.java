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

        @GetMapping
        public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "id") String sort) {
            return ResponseEntity.ok(Map.of());
        }

        @GetMapping("/{productId}")
        public ResponseEntity<?> getProduct(@PathVariable int productId) {
            return ResponseEntity.ok(Map.of());
        }

        @GetMapping("/category/{categoryId}")
        public ResponseEntity<?> getProductsByCategory(
                @PathVariable int categoryId,
                @RequestParam(defaultValue = "id") String sort) {
            
            return ResponseEntity.ok(Map.of());
        }
    }

    @RestController
    @RequestMapping("/categories")
    public static class CategoryController {
        @PersistenceContext
        private EntityManager entityManager;

        @GetMapping
        public ResponseEntity<?> getCategories(@RequestParam(defaultValue = "id") String sort) {
            return ResponseEntity.ok(Map.of());
        }

        @GetMapping("/{categoryId}")
        public ResponseEntity<?> getCategory(@PathVariable int categoryId) {
            return ResponseEntity.ok(Map.of());
        }
    }
}