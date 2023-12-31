package tech.jhipster.sample.security;

import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationResponse;
import java.util.Optional;

public class NotAuthenticatedResponse extends AuthenticationFailed {

    private final String message;

    public NotAuthenticatedResponse(String message) {
        this.message = message;
    }

    @Override
    public Optional<String> getMessage() {
        return Optional.of(message);
    }
}
