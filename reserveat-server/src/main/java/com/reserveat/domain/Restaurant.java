package com.reserveat.domain;

public class Restaurant {
    private final Integer id;
    private final String name;
    private final String description;

    public Restaurant(String name, String description) {
        this(null, name, description);
    }

    public Restaurant(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
