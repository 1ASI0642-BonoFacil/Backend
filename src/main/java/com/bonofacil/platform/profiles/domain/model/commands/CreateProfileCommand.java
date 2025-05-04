package com.bonofacil.platform.profiles.domain.model.commands;

public record CreateProfileCommand(String ruc,
                                   String razonSocial,
                                   String email,
                                   String password,
                                   String nombreContacto) {
}