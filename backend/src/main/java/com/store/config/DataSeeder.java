package com.store.config;

import com.store.dao.BrandDAO;
import com.store.dao.CategoryDAO;
import com.store.dao.ProductDAO;
import com.store.dao.RoleDAO;
import com.store.dao.UserDAO;
import com.store.entity.Brand;
import com.store.entity.Category;
import com.store.entity.Product;
import com.store.entity.Role;
import com.store.entity.User;
import com.store.entity.enums.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedCatalog(
            CategoryDAO categoryDAO,
            BrandDAO brandDAO,
            ProductDAO productDAO,
            RoleDAO roleDAO,
            UserDAO userDAO,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            Role adminRole = roleDAO.findByName(RoleName.ADMIN)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(RoleName.ADMIN);
                        return roleDAO.save(role);
                    });

            Role customerRole = roleDAO.findByName(RoleName.CUSTOMER)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(RoleName.CUSTOMER);
                        return roleDAO.save(role);
                    });

            if (!userDAO.existsByEmail("admin@store.com")) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@store.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setPhone("9876543210");
                admin.setActive(true);
                admin.setRoles(Set.of(adminRole, customerRole));
                userDAO.save(admin);
            }

            if (categoryDAO.count() == 0 &&
                    brandDAO.count() == 0 &&
                    productDAO.count() == 0) {

                Category running = new Category();
                running.setName("Running");
                running.setDescription(
                        "Performance shoes designed for running"
                );
                running.setImageUrl(
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
                );
                running.setActive(true);

                Category casual = new Category();
                casual.setName("Casual");
                casual.setDescription(
                        "Comfortable shoes for everyday wear"
                );
                casual.setImageUrl(
                        "https://images.unsplash.com/photo-1549298916-b41d501d3772"
                );
                casual.setActive(true);

                Category basketball = new Category();
                basketball.setName("Basketball");
                basketball.setDescription(
                        "Performance basketball footwear"
                );
                basketball.setImageUrl(
                        "https://images.unsplash.com/photo-1552346154-21d32810aba3"
                );
                basketball.setActive(true);

                running = categoryDAO.save(running);
                casual = categoryDAO.save(casual);
                basketball = categoryDAO.save(basketball);

                Brand nike = new Brand();
                nike.setName("Nike");
                nike.setDescription(
                        "Global sportswear and footwear brand"
                );
                nike.setActive(true);

                Brand adidas = new Brand();
                adidas.setName("Adidas");
                adidas.setDescription(
                        "Sportswear and lifestyle footwear brand"
                );
                adidas.setActive(true);

                Brand puma = new Brand();
                puma.setName("Puma");
                puma.setDescription(
                        "Sports and lifestyle footwear brand"
                );
                puma.setActive(true);

                nike = brandDAO.save(nike);
                adidas = brandDAO.save(adidas);
                puma = brandDAO.save(puma);

                Product product1 = new Product();
                product1.setName("Air Runner Pro");
                product1.setDescription(
                        "Premium running shoes with responsive cushioning"
                );
                product1.setBrand(nike);
                product1.setCategory(running);
                product1.setPrice(new BigDecimal("12999.00"));
                product1.setStockQuantity(25);
                product1.setImageUrl(
                        "https://images.unsplash.com/photo-1542291026-7eec264c27ff"
                );
                product1.setSku("NIKE-RUN-001");
                product1.setActive(true);

                Product product2 = new Product();
                product2.setName("Ultraboost Style");
                product2.setDescription(
                        "Comfort-focused running shoes for everyday training"
                );
                product2.setBrand(adidas);
                product2.setCategory(running);
                product2.setPrice(new BigDecimal("14999.00"));
                product2.setStockQuantity(18);
                product2.setImageUrl(
                        "https://images.unsplash.com/photo-1608231387042-66d1773070a5"
                );
                product2.setSku("ADIDAS-RUN-001");
                product2.setActive(true);

                Product product3 = new Product();
                product3.setName("Classic Street");
                product3.setDescription(
                        "Premium casual sneakers for everyday style"
                );
                product3.setBrand(puma);
                product3.setCategory(casual);
                product3.setPrice(new BigDecimal("7999.00"));
                product3.setStockQuantity(30);
                product3.setImageUrl(
                        "https://images.unsplash.com/photo-1549298916-b41d501d3772"
                );
                product3.setSku("PUMA-CAS-001");
                product3.setActive(true);

                Product product4 = new Product();
                product4.setName("Court Elite");
                product4.setDescription(
                        "High-performance basketball shoes"
                );
                product4.setBrand(nike);
                product4.setCategory(basketball);
                product4.setPrice(new BigDecimal("16999.00"));
                product4.setStockQuantity(12);
                product4.setImageUrl(
                        "https://images.unsplash.com/photo-1552346154-21d32810aba3"
                );
                product4.setSku("NIKE-BAS-001");
                product4.setActive(true);

                Product product5 = new Product();
                product5.setName("Urban Runner");
                product5.setDescription(
                        "Modern lifestyle sneakers with lightweight comfort"
                );
                product5.setBrand(adidas);
                product5.setCategory(casual);
                product5.setPrice(new BigDecimal("9999.00"));
                product5.setStockQuantity(22);
                product5.setImageUrl(
                        "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3"
                );
                product5.setSku("ADIDAS-CAS-001");
                product5.setActive(true);

                productDAO.save(product1);
                productDAO.save(product2);
                productDAO.save(product3);
                productDAO.save(product4);
                productDAO.save(product5);
            }
        };
    }
}
