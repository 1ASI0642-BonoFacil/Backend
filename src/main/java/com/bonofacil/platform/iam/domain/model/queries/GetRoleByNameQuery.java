package com.bonofacil.platform.iam.domain.model.queries;

import com.bonofacil.platform.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles name) {
}
