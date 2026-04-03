package com.finance.backend.config;

import com.finance.backend.enums.Role;
import com.finance.backend.enums.Status;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.model.FinancialRecord;
import com.finance.backend.model.User;
import com.finance.backend.repository.RecordRepository;
import com.finance.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataLoader {

    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RecordRepository recordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("Seeding initial data...");

                // Create admin user
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@finance.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.ACTIVE);

                // Create analyst user
                User analyst = new User();
                analyst.setName("Analyst User");
                analyst.setEmail("analyst@finance.com");
                analyst.setPassword(passwordEncoder.encode("analyst123"));
                analyst.setRole(Role.ANALYST);
                analyst.setStatus(Status.ACTIVE);

                // Create viewer user
                User viewer = new User();
                viewer.setName("Viewer User");
                viewer.setEmail("viewer@finance.com");
                viewer.setPassword(passwordEncoder.encode("viewer123"));
                viewer.setRole(Role.VIEWER);
                viewer.setStatus(Status.ACTIVE);

                userRepository.save(admin);
                userRepository.save(analyst);
                userRepository.save(viewer);

                System.out.println("Created default users");

                // Create sample financial records
                List<FinancialRecord> records = new ArrayList<>();

                String[] categories = {
                        "Salary", "Rent", "Food", "Investment", "Utilities",
                        "Transport", "Entertainment", "Medical"
                };

                // Generate 20 records spread over the last 6 months
                for (int i = 0; i < 20; i++) {
                    BigDecimal amount;
                    TransactionType type;

                    if (i % 3 == 0) {
                        type = TransactionType.INCOME;
                        amount = BigDecimal.valueOf(3000 + (Math.random() * 20000));
                    } else {
                        type = TransactionType.EXPENSE;
                        amount = BigDecimal.valueOf(100 + (Math.random() * 3000));
                    }

                    // Round to 2 decimal places
                    amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                    // Random date within last 6 months
                    LocalDate date = LocalDate.now().minusDays((long) (Math.random() * 180));

                    String category = categories[(int) (Math.random() * categories.length)];

                    FinancialRecord record = new FinancialRecord();
                    record.setAmount(amount);
                    record.setType(type);
                    record.setCategory(category);
                    record.setDate(date);
                    record.setNotes(i % 2 == 0 ? "Sample note for record " + (i + 1) : null);
                    record.setIsDeleted(false);
                    record.setCreatedBy(admin);

                    records.add(record);
                }

                recordRepository.saveAll(records);
                System.out.println("Created " + records.size() + " sample financial records");
            } else {
                System.out.println("Database already contains data, skipping seed.");
            }
        };
    }
}
