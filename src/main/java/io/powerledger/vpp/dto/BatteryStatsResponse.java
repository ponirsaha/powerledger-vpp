package io.powerledger.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatteryStatsResponse {
    private List<String> batteryNames;
    private long totalCapacity;
    private double averageCapacity;
}
