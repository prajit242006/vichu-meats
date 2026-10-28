package com.vichumeats.config;

import com.vichumeats.entity.AdminUser;
import com.vichumeats.entity.BusinessSettings;
import com.vichumeats.entity.Product;
import com.vichumeats.entity.Variant;
import com.vichumeats.repository.AdminUserRepository;
import com.vichumeats.repository.BusinessSettingsRepository;
import com.vichumeats.repository.ProductRepository;
import com.vichumeats.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeedData implements CommandLineRunner {

    private final AdminUserRepository admins;
    private final PasswordEncoder encoder;
    private final ProductRepository products;
    private final VariantRepository variants;
    private final BusinessSettingsRepository settings;

    @Value("${app.demo}")
    private boolean demo;

    @Override
    public void run(String... args) {

        // Create initial business settings.
        if (!settings.existsById(1L)) {
            settings.save(new BusinessSettings());
        }

        String username = System.getenv("ADMIN_USERNAME");
        String password = System.getenv("ADMIN_PASSWORD");

        boolean resetPassword = "true".equalsIgnoreCase(
                System.getenv("ADMIN_RESET_PASSWORD")
        );

        // Create the first admin or reset the specified admin password.
        if ((admins.count() == 0 || resetPassword)
                && username != null
                && password != null) {

            if (!username.matches("[A-Za-z0-9._-]{3,50}")) {
                throw new IllegalStateException(
                        "Admin username must contain 3-50 characters: "
                                + "letters, numbers, dots, underscores or hyphens."
                );
            }

            if (password.length() < 12) {
                throw new IllegalStateException(
                        "Admin password must contain at least 12 characters."
                );
            }

            AdminUser admin = admins.findById(username)
                    .orElseGet(AdminUser::new);

            admin.setUsername(username);
            admin.setPassword(encoder.encode(password));

            admins.save(admin);

            System.out.println(
                    "Admin account setup completed for: " + username
            );
        }

        // Add preview products only when the catalogue is empty.
        if (products.count() == 0 && demo) {

            addProduct(
                    "Farm chicken",
                    "Chicken",
                    "Chicken weight options are confirmed by the owner.",
                    "chicken.svg",
                    new String[]{"1 kg"},
                    "kg"
            );

            addProduct(
                    "Farm milk",
                    "Milk",
                    "Farm milk in glass containers. Choose your size.",
                    "milk.svg",
                    new String[]{"100 ml", "200 ml"},
                    "bottle"
            );

            addProduct(
                    "Naatu kozhi eggs",
                    "Eggs",
                    "Country chicken eggs. Pack quantities can be updated by the owner.",
                    "eggs.svg",
                    new String[]{"6 eggs"},
                    "pack"
            );
        }
    }

    private void addProduct(
            String name,
            String category,
            String description,
            String image,
            String[] labels,
            String unit
    ) {

        Product product = new Product();

        product.setName(name);
        product.setCategory(category);
        product.setDescription(description);
        product.setImage(image);
        product.setActive(true);
        product.setDemo(true);

        products.save(product);

        for (String label : labels) {

            Variant variant = new Variant();

            variant.setProduct(product);
            variant.setLabel(label);
            variant.setUnit(unit);
            variant.setAvailable(true);

            // Leave prices unset until the owner adds real prices.
            variant.setPrice(null);

            variants.save(variant);
        }
    }
}