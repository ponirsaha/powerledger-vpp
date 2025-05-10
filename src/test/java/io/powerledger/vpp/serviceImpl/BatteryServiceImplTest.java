package io.powerledger.vpp.serviceImpl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.powerledger.vpp.dto.BatteryStatsResponse;
import io.powerledger.vpp.model.Battery;
import io.powerledger.vpp.repository.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link BatteryServiceImpl}.
 */
class BatteryServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private BatteryServiceImpl batteryService;

    private Battery battery1;
    private Battery battery2;
    private Battery battery3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        battery1 = new Battery(1L, "Battery A", "6000", 10000);
        battery2 = new Battery(2L, "Battery B", "6100", 15000);
        battery3 = new Battery(3L, "Battery C", "6200", 20000);

        // Mocking MeterRegistry's Timer to ensure it's used correctly in the service methods.
        Timer mockTimer = mock(Timer.class);
        when(meterRegistry.timer(anyString(), any(String[].class))).thenReturn(mockTimer);
    }

    /**
     * Test that batteries are saved correctly, and a counter for registered batteries is incremented.
     */
    @Test
    void shouldSaveBatteriesAndIncrementCounter() {
        List<Battery> batteryList = List.of(battery1, battery2);

        // Mocking MeterRegistry's counter to track battery registrations.
        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter("batteries.registered.count")).thenReturn(mockCounter);

        // Mocking the repository's save behavior.
        when(batteryRepository.saveAll(batteryList)).thenReturn(batteryList);

        // Calling the method under test.
        List<Battery> saved = batteryService.saveBatteries(batteryList);

        // Assertions
        assertThat(saved).isEqualTo(batteryList);

        // Verifying interactions with the repository and MeterRegistry.
        verify(batteryRepository, times(1)).saveAll(batteryList);
        verify(meterRegistry, times(1)).counter("batteries.registered.count");
        verify(mockCounter, times(1)).increment(batteryList.size());
    }

    /**
     * Test that battery stats are returned correctly for a default query with no capacity filters.
     */
    @Test
    void shouldReturnBatteryStatsWithoutCapacityFilters() {
        List<Battery> batteryList = List.of(battery1, battery2, battery3);
        when(batteryRepository.findByPostcodeBetween("6000", "6200")).thenReturn(batteryList);

        BatteryStatsResponse result = batteryService.getBatteriesInRange("6000", "6200", null, null);

        // Assertions to verify battery stats response
        assertThat(result).isNotNull();
        assertThat(result.getBatteryNames()).containsExactly("Battery A", "Battery B", "Battery C");
        assertThat(result.getTotalCapacity()).isEqualTo(45000);
        assertThat(result.getAverageCapacity()).isEqualTo(15000.0);

        // Verifying repository interaction and timing metric.
        verify(batteryRepository, times(1)).findByPostcodeBetween("6000", "6200");
        verify(meterRegistry, times(1)).timer("batteries.query.response.time");
    }

    /**
     * Test that battery stats are filtered correctly by minimum capacity.
     */
    @Test
    void shouldReturnBatteryStatsWithMinCapacityFilter() {
        List<Battery> batteryList = List.of(battery1, battery2, battery3);
        when(batteryRepository.findByPostcodeBetween("6000", "6200")).thenReturn(batteryList);

        BatteryStatsResponse result = batteryService.getBatteriesInRange("6000", "6200", 15000L, null);

        // Assertions to check if filtering by minCapacity works
        assertThat(result).isNotNull();
        assertThat(result.getBatteryNames()).containsExactly("Battery B", "Battery C");
        assertThat(result.getTotalCapacity()).isEqualTo(35000);
        assertThat(result.getAverageCapacity()).isEqualTo(17500.0);

        // Verifying repository interaction and timing metric.
        verify(batteryRepository, times(1)).findByPostcodeBetween("6000", "6200");
        verify(meterRegistry, times(1)).timer("batteries.query.response.time");
    }

    /**
     * Test that battery stats are filtered correctly by maximum capacity.
     */
    @Test
    void shouldReturnBatteryStatsWithMaxCapacityFilter() {
        List<Battery> batteryList = List.of(battery1, battery2, battery3);
        when(batteryRepository.findByPostcodeBetween("6000", "6200")).thenReturn(batteryList);

        BatteryStatsResponse result = batteryService.getBatteriesInRange("6000", "6200", null, 15000L);

        // Assertions to check if filtering by maxCapacity works
        assertThat(result).isNotNull();
        assertThat(result.getBatteryNames()).containsExactly("Battery A", "Battery B");
        assertThat(result.getTotalCapacity()).isEqualTo(25000);
        assertThat(result.getAverageCapacity()).isEqualTo(12500.0);

        // Verifying repository interaction and timing metric.
        verify(batteryRepository, times(1)).findByPostcodeBetween("6000", "6200");
        verify(meterRegistry, times(1)).timer("batteries.query.response.time");
    }

    /**
     * Test that battery stats are filtered correctly by both minimum and maximum capacity.
     */
    @Test
    void shouldReturnBatteryStatsWithMinAndMaxCapacityFilters() {
        List<Battery> batteryList = List.of(battery1, battery2, battery3);
        when(batteryRepository.findByPostcodeBetween("6000", "6200")).thenReturn(batteryList);

        BatteryStatsResponse result = batteryService.getBatteriesInRange("6000", "6200", 12000L, 20000L);

        // Assertions to check if both min and max capacity filters work
        assertThat(result).isNotNull();
        assertThat(result.getBatteryNames()).containsExactly("Battery B", "Battery C");
        assertThat(result.getTotalCapacity()).isEqualTo(35000);
        assertThat(result.getAverageCapacity()).isEqualTo(17500.0);

        // Verifying repository interaction and timing metric.
        verify(batteryRepository, times(1)).findByPostcodeBetween("6000", "6200");
        verify(meterRegistry, times(1)).timer("batteries.query.response.time");
    }
}