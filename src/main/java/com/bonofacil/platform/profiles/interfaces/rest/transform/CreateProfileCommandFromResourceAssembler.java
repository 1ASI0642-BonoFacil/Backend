package com.bonofacil.platform.profiles.interfaces.rest.transform;

import com.bonofacil.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.bonofacil.platform.profiles.interfaces.rest.resources.CreateProfileResource;

public class CreateProfileCommandFromResourceAssembler {
    public static CreateProfileCommand toCommandFromResource(CreateProfileResource resource) {
        return new CreateProfileCommand(
                resource.ruc(),
                resource.razonSocial(),
                resource.email(),
                resource.password(),
                resource.nombreContacto()
        );
    }
}