package com.adictosaltrabajo.ai.adapter.llm;

import java.math.BigDecimal;

public record FurnitureData(String name,
                            String description,
                            String type,
                            String style,
                            String material,
                            String color,
                            float width,
                            float height,
                            float depth,
                            BigDecimal price) {
}
