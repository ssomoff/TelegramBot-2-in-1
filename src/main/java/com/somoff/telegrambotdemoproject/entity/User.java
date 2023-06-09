package com.somoff.telegrambotdemoproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "firstSymbol", nullable = false)
    private char firstSymbol;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_cities", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private List<City> cities;

}
