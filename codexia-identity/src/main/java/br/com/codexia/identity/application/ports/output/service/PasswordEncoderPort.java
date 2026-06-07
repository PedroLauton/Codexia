package br.com.codexia.identity.application.ports.output.service;

import br.com.codexia.identity.domain.model.valueobject.Password;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;

public interface PasswordEncoderPort {
    PasswordHash encode(Password rawPassword);
    boolean matches(Password rawPassword, PasswordHash passwordHash);
}