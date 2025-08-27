package com.nikospavlopoulos.skydivingrest.dto.error;

import lombok.*;

/**
 * DTO That encapsulates the field data for the {@link ApiErrorResponseDTO}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldErrorDTO {

    private String field;
    private String rejectedValue;
    private String message;

}
