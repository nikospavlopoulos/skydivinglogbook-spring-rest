package com.nikospavlopoulos.skydivingrest.dto.error;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO that encapsulates the data to return the API Error responses.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponseDTO {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String code;
    private String path;
    private List<FieldErrorDTO> fieldErrors;

}
