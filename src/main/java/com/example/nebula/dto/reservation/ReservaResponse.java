package com.example.nebula.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaResponse {
    private Long id;
    private String nombreCliente;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime hora;

    private String mensaje;

    public ReservaResponse(Long id, String nombreCliente, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.hora = hora;
        this.mensaje = String.format(
                "Reserva confirmada para %s el %s a las %s",
                nombreCliente,
                fecha,
                hora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}