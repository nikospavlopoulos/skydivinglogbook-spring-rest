package com.nikospavlopoulos.skydivingrest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JumpInsertDTO {

    @NotNull(message = "Altitude can not be empty")
    @Min(value = 3000, message = "Altitude Minimum 3000ft")
    @Max(value = 20000, message = "Altitude Maximum 20000ft")
    private Integer altitude;

    @NotNull(message = "Free Fall Duration can not be empty")
    @Min(value = 0, message = "Minimum freefall seconds is 0 seconds")
    @Max(value = 100, message = "Maximum freefall seconds is 100 seconds")
    private Integer freeFallDuration;

    @NotNull(message = "Date must not be empty")
    @PastOrPresent(message = "Date must not be in the Future")
    private LocalDateTime jumpDate;

    @Size(max = 500, message = "Notes must be under 500 characters")
    private String jumpNotes;

    @NotNull(message = "Aircraft can not be empty")
    private Long aircraftId;

    @NotNull(message = "Dropzone can not be empty")
    private Long dropzoneId;

    @NotNull(message = "Jump Type can not be empty")
    private Long jumptypeId;

    @NotNull(message = "User can not be empty")
    private Long userId;

}
