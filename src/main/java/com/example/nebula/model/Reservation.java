package com.example.nebula.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "email_cliente", nullable = false)
    private String emailCliente;

    @Column(name = "telefono_cliente")
    private String telefonoCliente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "cantidad_personas", nullable = false)
    private Integer cantidadPersonas;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}