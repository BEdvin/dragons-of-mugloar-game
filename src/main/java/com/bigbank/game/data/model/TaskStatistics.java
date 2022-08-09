package com.bigbank.game.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatistics {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    @Column
    private boolean success;
    @Column
    private Integer lives;
    @Column
    private Integer gold;
    @Column
    private Integer score;
    @Column
    private Integer highScore;
    @Column
    private Integer turn;
    @Column
    private Integer responseStatusCode;
    @Column
    private String message;
    @Column
    private boolean skipped;
}
