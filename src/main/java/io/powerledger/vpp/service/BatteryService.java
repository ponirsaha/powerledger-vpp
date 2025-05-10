package io.powerledger.vpp.service;

import io.powerledger.vpp.dto.BatteryStatsResponse;
import io.powerledger.vpp.model.Battery;

import java.util.List;

/**
 * Service interface for battery-related operations.
 */
public interface BatteryService {

    /**
     * Save a list of battery entities.
     *
     * @param batteries List of Battery objects to be saved.
     * @return List of saved Battery entities.
     */
    List<Battery> saveBatteries(List<Battery> batteries);

    /**
     * Retrieve batteries within a given postcode range and optional capacity range.
     *
     * @param startPostcode Starting postcode (inclusive).
     * @param endPostcode   Ending postcode (inclusive).
     * @param minCapacity   Optional minimum capacity filter (nullable).
     * @param maxCapacity   Optional maximum capacity filter (nullable).
     * @return BatteryStatsResponse containing filtered battery names and statistics.
     */
    BatteryStatsResponse getBatteriesInRange(String startPostcode, String endPostcode, Long minCapacity, Long maxCapacity);
}
