package io.powerledger.vpp.repository;


import io.powerledger.vpp.model.Battery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
    List<Battery> findByPostcodeBetween(String start, String end);
}
