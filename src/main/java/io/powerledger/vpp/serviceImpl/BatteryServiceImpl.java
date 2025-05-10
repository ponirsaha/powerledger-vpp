package io.powerledger.vpp.serviceImpl;

import io.micrometer.core.instrument.MeterRegistry;
import io.powerledger.vpp.dto.BatteryStatsResponse;
import io.powerledger.vpp.model.Battery;
import io.powerledger.vpp.repository.BatteryRepository;
import io.powerledger.vpp.service.BatteryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Implementation of the BatteryService interface.
 * Provides methods to save batteries and query battery statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatteryServiceImpl implements BatteryService {

    private final BatteryRepository batteryRepository;
    private final MeterRegistry meterRegistry;

    /**
     * Saves a list of batteries to the repository.
     * Also increments a custom metric counter for battery registration.
     *
     * @param batteries List of batteries to save.
     * @return List of persisted battery entities.
     */
    @Override
    public List<Battery> saveBatteries(List<Battery> batteries) {
        log.info("Saving {} batteries", batteries.size());
        meterRegistry.counter("batteries.registered.count").increment(batteries.size());
        return batteryRepository.saveAll(batteries);
    }

    /**
     * Retrieves batteries within a specified postcode range and optional capacity bounds.
     * Also tracks the execution time using Micrometer.
     *
     * @param startPostcode Starting postcode (inclusive).
     * @param endPostcode   Ending postcode (inclusive).
     * @param minCapacity   Minimum battery capacity (nullable).
     * @param maxCapacity   Maximum battery capacity (nullable).
     * @return BatteryStatsResponse containing sorted battery names and capacity statistics.
     */
    @Override
    public BatteryStatsResponse getBatteriesInRange(String startPostcode, String endPostcode, Long minCapacity, Long maxCapacity) {
        log.info("Fetching batteries between postcodes {} and {} with capacity between {} and {}",
                startPostcode, endPostcode, minCapacity, maxCapacity);

        long startTime = System.currentTimeMillis();

        List<Battery> batteries = batteryRepository.findByPostcodeBetween(startPostcode, endPostcode);

        // If no batteries found, throw EntityNotFoundException
        if (batteries.isEmpty()) {
            throw new EntityNotFoundException("No batteries found for the specified range.");
        }

        long endTime = System.currentTimeMillis();
        meterRegistry.timer("batteries.query.response.time")
                .record(endTime - startTime, TimeUnit.MILLISECONDS);

        List<Battery> filteredBatteries = batteries.stream()
                .filter(b -> (minCapacity == null || b.getCapacity() >= minCapacity) &&
                        (maxCapacity == null || b.getCapacity() <= maxCapacity))
                .toList();

        List<String> sortedNames = filteredBatteries.stream()
                .map(Battery::getName)
                .sorted()
                .collect(Collectors.toList());

        long totalCapacity = filteredBatteries.stream()
                .mapToLong(Battery::getCapacity)
                .sum();

        double averageCapacity = filteredBatteries.isEmpty()
                ? 0
                : (double) totalCapacity / filteredBatteries.size();

        return new BatteryStatsResponse(sortedNames, totalCapacity, averageCapacity);
    }
}
