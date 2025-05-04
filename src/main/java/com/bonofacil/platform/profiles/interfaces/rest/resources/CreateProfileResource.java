package com.bonofacil.platform.profiles.interfaces.rest.resources;

public record CreateProfileResource(String ruc, String razonSocial, String email, String password, String nombreContacto) {
}