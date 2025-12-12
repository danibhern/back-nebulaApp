package com.example.nebula.controller;

import com.example.nebula.dto.ApiResponse;
import com.example.nebula.dto.reservation.ReservaDto;
import com.example.nebula.dto.reservation.ReservaResponse;
import com.example.nebula.model.Reservation;
import com.example.nebula.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservaResponse>> crearReserva(@Valid @RequestBody ReservaDto reservaDto) {
        try {
            Reservation reservation = new Reservation();
            reservation.setNombreCliente(reservaDto.getNombreCliente());
            reservation.setEmailCliente(reservaDto.getEmailCliente());
            reservation.setTelefonoCliente(reservaDto.getTelefonoCliente());
            reservation.setFecha(reservaDto.getFecha());
            reservation.setHora(reservaDto.getHora());
            reservation.setCantidadPersonas(reservaDto.getCantidadPersonas());

            Reservation savedReservation = reservationService.createReservation(reservation, reservaDto.getUserId());

            ReservaResponse response = new ReservaResponse(
                    savedReservation.getId(),
                    savedReservation.getNombreCliente(),
                    savedReservation.getFecha(),
                    savedReservation.getHora()
            );

            return ResponseEntity.ok(ApiResponse.success("Reserva creada exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<java.util.List<ReservaResponse>>> getReservationsByUser(@PathVariable Long userId) {
        try {
            List<ReservaResponse> responses = reservationService.getReservationsByUser(userId)
                    .stream()
                    .map(reserva -> new ReservaResponse(
                            reserva.getId(),
                            reserva.getNombreCliente(),
                            reserva.getFecha(),
                            reserva.getHora()
                    ))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}