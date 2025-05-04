package com.bonofacil.platform.profiles.interfaces.rest.transform;

import com.bonofacil.platform.profiles.domain.model.aggregates.Profile;
import com.bonofacil.platform.profiles.interfaces.rest.resources.ProfileResource;

public class ProfileResourceFromEntityAssembler {
  public static ProfileResource toResourceFromEntity(Profile entity) {
    return new ProfileResource(
            entity.getId(),
            entity.getRucValue(),
            entity.getRazonSocialValue(),
            entity.getEmailAddress(),
            entity.getNombreContactoValue()
    );
  }
}