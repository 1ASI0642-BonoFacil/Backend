package com.bonofacil.platform.iam.infrastructure.hashing.bcrypt;

import com.bonofacil.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
