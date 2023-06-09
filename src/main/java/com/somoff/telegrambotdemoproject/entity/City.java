package com.somoff.telegrambotdemoproject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cities")
@Data
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long Id;
    @Column(name = "region", nullable = false)
    String region;
    @Column(name = "city", nullable = false)
    String city;
}
