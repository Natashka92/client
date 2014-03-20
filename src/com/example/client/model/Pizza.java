package com.example.client.model;


public class Pizza {

    private Long id;
    private String name;
    private Double price;

    public Pizza(Long id, String name, Double price) {
        this.id = id;
        this.price = price;
        this.name = name;
    }

    public Pizza() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\"=" + id +
                ",\"name\"=" + name  +
                ",\"price\"=" + price +
                '}';
    }
}
