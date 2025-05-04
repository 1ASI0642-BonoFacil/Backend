package com.bonofacil.platform.iam.domain.services;

import com.bonofacil.platform.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
