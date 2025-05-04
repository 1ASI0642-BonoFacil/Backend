package com.bonofacil.platform.profiles.domain.model.queries;

import  com.bonofacil.platform.profiles.domain.model.valueobjects.EmailAddress;

public record GetProfileByEmailQuery(EmailAddress emailAddress) {
}