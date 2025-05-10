package io.powerledger.vpp.controller;

import io.powerledger.vpp.dto.BatteryStatsResponse;
import io.powerledger.vpp.model.Battery;
import io.powerledger.vpp.service.BatteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BatteryController}.
 */
class BatteryControllerTest {

    @Mock
    private BatteryService batteryService;

    @InjectMocks
    private BatteryController batteryController;

    private Battery battery1;
    private Battery battery2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        battery1 = new Battery(1L, "Battery A", "6000", 10000);
        battery2 = new Battery(2L, "Battery B", "6100", 20000);
    }

    /**
     * Test that the controller correctly delegates battery saving to the service
     * and returns the saved list of batteries.
     */
    @Test
    void shouldSaveAndReturnListOfBatteries() {
        List<Battery> batteryList = List.of(battery1, battery2);

        // Mock the service response
        when(batteryService.saveBatteries(batteryList)).thenReturn(batteryList);

        // Call the controller method
        ResponseEntity<List<Battery>> response = batteryController.addBatteries(batteryList);

        // Assert the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsExactly(battery1, battery2);

        // Verify interaction with the service
        verify(batteryService, times(1)).saveBatteries(batteryList);
    }

    /**
     * Test that the controller returns battery statistics correctly
     * when queried with a postcode range and optional capacity filters.
     */
    @Test
    void shouldReturnBatteryStatsForGivenPostcodeRange() {
        BatteryStatsResponse mockResponse = new BatteryStatsResponse(
                List.of("Battery A", "Battery B"),
                30000,
                15000.0
        );

        // Mock the service response
        when(batteryService.getBatteriesInRange("6000", "6200", null, null)).thenReturn(mockResponse);

        // Call the controller method
        ResponseEntity<BatteryStatsResponse> response = batteryController.getBatteriesInRange("6000", "6200", null, null);

        // Assert the response
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBatteryNames()).containsExactly("Battery A", "Battery B");
        assertThat(response.getBody().getTotalCapacity()).isEqualTo(30000);
        assertThat(response.getBody().getAverageCapacity()).isEqualTo(15000.0);

        // Verify interaction with the service
        verify(batteryService, times(1)).getBatteriesInRange("6000", "6200", null, null);
    }
}