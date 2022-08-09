package com.bigbank.game.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStatisticsDto {

    private boolean shoppingSuccess;
    private Integer gold;
    private Integer lives;
    private Integer level;
    private Integer turn;
}
