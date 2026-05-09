package com.example.grid07.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bot {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String personaDescription;
}
