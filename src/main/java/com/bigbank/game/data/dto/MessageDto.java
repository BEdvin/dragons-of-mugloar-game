package com.bigbank.game.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private String adId;
    private String message;
    private Integer reward;
    private Integer expiresIn;
    private String probability;
}
