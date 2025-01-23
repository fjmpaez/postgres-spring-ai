package com.adictosaltrabajo.ai.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "furniture")
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString(exclude = {"embedding"})
@Builder(toBuilder = true)
public class Furniture {

    @Id
    private final UUID id;
    private final String name;
    private final String description;
    private final String type;
    private final String style;
    private final String material;
    private final String color;
    private final float width;
    private final float height;
    private final float depth;
    private final BigDecimal price;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 384)
    private final float[] embedding;

    public String getContent() {
        return name + " " + description + " " + type + " " + style + " " + material + " " + color;
    }
}
