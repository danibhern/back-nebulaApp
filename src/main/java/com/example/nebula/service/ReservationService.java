package com.example.nebula.service;

import com.example.nebula.model.Reservation;
import com.example.nebula.model.User;
import com.example.nebula.repository.ReservationRepository;
import com.example.nebula.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public Reservation createReservation(Reservation reservation, Long userId) {
        // Validar que no haya reserva en misma fecha y hora
        if (reservationRepository.existsByFechaAndHora(reservation.getFecha(), reservation.getHora())) {
            throw new RuntimeException("Ya existe una reserva para esta fecha y hora");
        }

        // Validar que sea fecha futura
        if (reservation.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("La fecha de reserva debe ser futura");
        }

        // Asociar usuario si existe
        if (userId != null) {
            Optional<User> userOptional = userRepository.findById(userId);
            userOptional.ifPresent(reservation::setUser);
        }

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return reservationRepository.findByUser(userOptional.get());
        }
        return List.of();
    }

    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByEmailCliente(email);
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}