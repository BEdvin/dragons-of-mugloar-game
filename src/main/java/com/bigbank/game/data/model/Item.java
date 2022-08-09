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
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    private String itemId;
    @Column
    private String name;
    @Column
    private Integer cost;
    @Column
    private boolean available;

}
