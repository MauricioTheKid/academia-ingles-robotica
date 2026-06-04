package com.academia.inglesrobotica.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private String email;
    private String nombre;
    private String apellido;
    private String rol;

    public LoginResponse(String token, String email, String nombre, String apellido, String rol) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
    }
}