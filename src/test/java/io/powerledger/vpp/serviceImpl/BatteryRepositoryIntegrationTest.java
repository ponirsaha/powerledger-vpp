package io.powerledger.vpp.serviceImpl;

import io.powerledger.vpp.model.Battery;
import io.powerledger.vpp.repository.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for BatteryRepository to ensure that it performs correct database operations.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BatteryRepositoryIntegrationTest {

    // PostgreSQL container setup for testing
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Dynamically setting the datasource URL, username, and password from Testcontainers PostgreSQL container
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BatteryRepository batteryRepository;

    // Sample battery data setup before each test
    @BeforeEach
    void setup() {
        batteryRepository.saveAll(List.of(
                new Battery(null, "Battery A", "6000", 10000),
                new Battery(null, "Battery B", "6050", 20000),
                new Battery(null, "Battery C", "6100", 30000)
        ));
    }

    /**
     * Test to verify that batteries with postcodes within the specified range are returned correctly.
     */
    @Test
    void shouldReturnBatteriesWithinSpecifiedPostcodeRange() {
        var batteries = batteryRepository.findByPostcodeBetween("6000", "6100");

        // Asserting that the result contains 3 batteries within the specified range
        assertThat(batteries).hasSize(3);

        // Verifying each battery's postcode is within the defined range
        assertThat(batteries).extracting("postcode").containsExactlyInAnyOrder("6000", "6050", "6100");

        // Verifying the batteries' capacities to ensure correct records were returned
        assertThat(batteries).extracting("capacity").containsExactlyInAnyOrder(10000, 20000, 30000);
    }
}
