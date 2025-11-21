package com.securebank.bank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerStatusUpdateRequest {

    @NotNull(message = "Active flag is required")
    private Boolean active;
}


