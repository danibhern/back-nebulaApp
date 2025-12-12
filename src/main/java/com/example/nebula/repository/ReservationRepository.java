package com.example.nebula.repository;

import com.example.nebula.model.Reservation;
import com.example.nebula.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    boolean existsByFechaAndHora(LocalDate fecha, LocalTime hora);
    List<Reservation> findByEmailCliente(String email);
}