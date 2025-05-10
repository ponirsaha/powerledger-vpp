package io.powerledger.vpp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batteries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Postcode cannot be blank")
    @Pattern(regexp = "\\d{4}", message = "Postcode must be a 4-digit number")
    private String postcode;

    @NotNull(message = "Capacity cannot be null")
    @Positive(message = "Capacity must be a positive value")
    private Integer capacity;
}