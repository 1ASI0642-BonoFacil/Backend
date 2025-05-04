package com.bonofacil.platform.iam.interfaces.rest.transform;

import com.bonofacil.platform.iam.domain.model.commands.SignInCommand;
import com.bonofacil.platform.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(resource.username(), resource.password());
    }
}
