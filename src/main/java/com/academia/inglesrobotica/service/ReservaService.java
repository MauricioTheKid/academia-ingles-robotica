package com.academia.inglesrobotica.service;

import com.academia.inglesrobotica.model.Reserva;
import com.academia.inglesrobotica.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    public List<Reserva> findByHorarioId(Long horarioId) {
        return reservaRepository.findByHorarioId(horarioId);
    }

    public List<Reserva> findByEstado(String estado) {
        return reservaRepository.findByEstado(estado);
    }

    public long count() {
        return reservaRepository.count();
    }
}