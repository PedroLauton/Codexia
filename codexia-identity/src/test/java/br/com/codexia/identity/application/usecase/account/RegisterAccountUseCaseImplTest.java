package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.RegisterAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.service.PasswordEncoderPort;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.exception.account.AccountAlreadyExistsException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterAccountUseCase")
class RegisterAccountUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountValidationService accountValidationService;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private RegisterAccountUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<List<OutboxEvent>> outboxCaptor;

    private RegisterAccountCommand buildCommand(String email, String name, String rawPassword) {
        return new RegisterAccountCommand(email, name, rawPassword);
    }

    @Nested
    @DisplayName("when all inputs are valid")
    class WhenAllInputsAreValid {

        @Test
        @DisplayName("should register account and publish AccountCreatedEvent in outbox")
        void shouldRegisterAndPublishCreatedEvent() {
            when(passwordEncoderPort.encode(any())).thenReturn(new PasswordHash("$2a$10$hash"));

            AccountResponse response = useCase.execute(
                    buildCommand("new@example.com", "Alice", "Secure1!")
            );

            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo("new@example.com");

            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            List<OutboxEvent> events = outboxCaptor.getValue();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("AccountCreatedEvent");
        }
    }

    @Nested
    @DisplayName("when email is already taken")
    class WhenEmailIsAlreadyTaken {

        @Test
        @DisplayName("should throw AccountAlreadyExistsException and never persist")
        void shouldThrowAndNeverPersist() {
            doThrow(new AccountAlreadyExistsException("existing@example.com"))
                    .when(accountValidationService).validateEmailAvailability(any());

            assertThatThrownBy(() -> useCase.execute(
                    buildCommand("existing@example.com", "Alice", "Secure1!")))
                    .isInstanceOf(AccountAlreadyExistsException.class);

            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }

    @Nested
    @DisplayName("when password does not meet requirements")
    class WhenPasswordIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException before any port is called")
        void shouldThrowBeforeAnyPortIsCalled() {
            assertThatThrownBy(() -> useCase.execute(
                    buildCommand("new@example.com", "Alice", "weakpassword")))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(accountValidationService);
            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }

    @Nested
    @DisplayName("when email format is invalid")
    class WhenEmailFormatIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException before any port is called")
        void shouldThrowBeforeAnyPortIsCalled() {
            assertThatThrownBy(() -> useCase.execute(
                    buildCommand("not-an-email", "Alice", "Secure1!")))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(accountValidationService);
            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }
}
