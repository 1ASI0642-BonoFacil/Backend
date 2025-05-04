package com.bonofacil.platform.iam.interfaces.rest.transform;

import com.bonofacil.platform.iam.domain.model.aggregates.User;
import com.bonofacil.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(User entity, String token) {
        return new AuthenticatedUserResource(entity.getId(), entity.getUsername(), token);
    }
}
