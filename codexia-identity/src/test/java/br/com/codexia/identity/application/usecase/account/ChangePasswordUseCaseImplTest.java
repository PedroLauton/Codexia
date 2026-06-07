package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ChangePasswordCommand;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.service.PasswordEncoderPort;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.domain.exception.account.InvalidCredentialsException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.AvatarUrl;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.identity.domain.model.valueobject.ProviderName;
import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordUseCase")
class ChangePasswordUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountFinder accountFinder;

    @Mock
    private PasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private ChangePasswordUseCaseImpl useCase;

    private static final String VALID_CURRENT = "CurrentP@ss1";
    private static final String VALID_NEW = "NewP@ss1234";

    private Account buildAccountWithCredentials() {
        Account account = Account.createWithCredentials(
                Email.of("user@example.com"),
                Name.of("User"),
                new PasswordHash("$2a$10$oldhash")
        );
        account.pullEvents();
        return account;
    }

    private Account buildOAuthAccount() {
        Account account = Account.createWithOAuth(
                Email.of("user@example.com"),
                Name.of("User"),
                AvatarUrl.of("https://example.com/avatar.jpg"),
                ProviderName.of("google"),
                "google-123"
        );
        account.pullEvents();
        return account;
    }

    private ChangePasswordCommand buildCommand(String currentPassword, String newPassword) {
        return new ChangePasswordCommand(UUID.randomUUID().toString(), currentPassword, newPassword);
    }

    @Nested
    @DisplayName("when current password matches")
    class WhenCurrentPasswordMatches {

        @Test
        @DisplayName("should change password and persist")
        void shouldChangePasswordAndPersist() {
            Account account = buildAccountWithCredentials();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);
            when(passwordEncoderPort.matches(any(), any())).thenReturn(true);
            when(passwordEncoderPort.encode(any())).thenReturn(new PasswordHash("$2a$10$newhash"));

            assertThatNoException().isThrownBy(() -> useCase.execute(buildCommand(VALID_CURRENT, VALID_NEW)));

            verify(accountCommandPort).save(eq(account), eq(List.of()));
        }
    }

    @Nested
    @DisplayName("when current password does not match")
    class WhenCurrentPasswordDoesNotMatch {

        @Test
        @DisplayName("should throw InvalidCredentialsException and never persist")
        void shouldThrowAndNeverPersist() {
            Account account = buildAccountWithCredentials();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);
            when(passwordEncoderPort.matches(any(), any())).thenReturn(false);

            assertThatThrownBy(() -> useCase.execute(buildCommand(VALID_CURRENT, VALID_NEW)))
                    .isInstanceOf(InvalidCredentialsException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }

    @Nested
    @DisplayName("when account has no local credential")
    class WhenAccountHasNoLocalCredential {

        @Test
        @DisplayName("should throw InvalidCredentialsException before calling encoder")
        void shouldThrowBeforeCallingEncoder() {
            Account account = buildOAuthAccount();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);

            assertThatThrownBy(() -> useCase.execute(buildCommand(VALID_CURRENT, VALID_NEW)))
                    .isInstanceOf(InvalidCredentialsException.class);

            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }

    @Nested
    @DisplayName("when account does not exist")
    class WhenAccountDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException and never call encoder nor persist")
        void shouldThrowAndNeverCallEncoder() {
            when(accountFinder.findActiveOrThrow(any()))
                    .thenThrow(new ResourceNotFoundException("Account not found"));

            assertThatThrownBy(() -> useCase.execute(buildCommand(VALID_CURRENT, VALID_NEW)))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }

    @Nested
    @DisplayName("when new password is invalid")
    class WhenNewPasswordIsInvalid {

        @Test
        @DisplayName("should throw IllegalArgumentException before any port is called")
        void shouldThrowBeforeAnyPortIsCalled() {
            assertThatThrownBy(() -> useCase.execute(buildCommand(VALID_CURRENT, "weak")))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(accountFinder);
            verifyNoInteractions(accountCommandPort);
            verifyNoInteractions(passwordEncoderPort);
        }
    }
}
