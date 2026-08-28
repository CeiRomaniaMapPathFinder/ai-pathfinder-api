package com.ceiromania.pathfinder.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Routedtos {
    @NotBlank(message = "Start point is required")
    String start;

    @NotBlank(message = "End point is required")
    String end;
}