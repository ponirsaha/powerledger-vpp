package io.powerledger.vpp.controller;

import io.powerledger.vpp.dto.BatteryStatsResponse;
import io.powerledger.vpp.model.Battery;
import io.powerledger.vpp.service.BatteryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing battery-related operations in the Virtual Power Plant (VPP) system.
 */
@RestController
@RequestMapping("/api/v1/batteries")
@RequiredArgsConstructor
@Validated
public class BatteryController {

    private final BatteryService batteryService;

    /**
     * Adds a list of batteries to the system.
     *
     * @param batteries List of Battery objects with name, postcode, and capacity.
     * @return List of persisted Battery entities with HTTP 200 status.
     */
    @PostMapping
    public ResponseEntity<List<Battery>> addBatteries(@Valid @RequestBody List<Battery> batteries) {
        List<Battery> savedBatteries = batteryService.saveBatteries(batteries);
        return ResponseEntity.ok(savedBatteries);
    }

    /**
     * Retrieves battery names and statistics based on postcode and optional capacity range filters.
     *
     * @param startPostcode Starting postcode (inclusive).
     * @param endPostcode   Ending postcode (inclusive).
     * @param minCapacity   (Optional) Minimum capacity filter.
     * @param maxCapacity   (Optional) Maximum capacity filter.
     * @return BatteryStatsResponse including sorted battery names, total capacity, and average capacity.
     */
    @GetMapping
    public ResponseEntity<BatteryStatsResponse> getBatteriesInRange(
            @RequestParam String startPostcode,
            @RequestParam String endPostcode,
            @RequestParam(required = false) Long minCapacity,
            @RequestParam(required = false) Long maxCapacity) {

        BatteryStatsResponse response = batteryService.getBatteriesInRange(
                startPostcode, endPostcode, minCapacity, maxCapacity
        );

        return ResponseEntity.ok(response);
    }
}
