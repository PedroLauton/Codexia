package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ChangeEmailCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.exception.account.AccountAlreadyExistsException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangeEmailUseCase")
class ChangeEmailUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountFinder accountFinder;

    @Mock
    private AccountValidationService accountValidationService;

    @InjectMocks
    private ChangeEmailUseCaseImpl useCase;

    private Account buildActiveAccount() {
        Account account = Account.createWithCredentials(
                Email.of("old@example.com"),
                Name.of("User"),
                new PasswordHash("$2a$10$hash")
        );
        account.pullEvents();
        return account;
    }

    private ChangeEmailCommand buildCommand(String newEmail) {
        return new ChangeEmailCommand(UUID.randomUUID().toString(), newEmail);
    }

    @Nested
    @DisplayName("when email is available")
    class WhenEmailIsAvailable {

        @Test
        @DisplayName("should change email and persist")
        void shouldChangeEmailAndPersist() {
            Account account = buildActiveAccount();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);

            AccountResponse response = useCase.execute(buildCommand("new@example.com"));

            assertThat(response).isNotNull();
            assertThat(response.email()).isEqualTo("new@example.com");
            verify(accountCommandPort).save(eq(account), eq(List.of()));
        }
    }

    @Nested
    @DisplayName("when email is already taken")
    class WhenEmailIsAlreadyTaken {

        @Test
        @DisplayName("should throw AccountAlreadyExistsException and never persist")
        void shouldThrowAndNeverPersist() {
            Account account = buildActiveAccount();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);
            doThrow(new AccountAlreadyExistsException("taken@example.com"))
                    .when(accountValidationService).validateEmailAvailability(any());

            assertThatThrownBy(() -> useCase.execute(buildCommand("taken@example.com")))
                    .isInstanceOf(AccountAlreadyExistsException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }

    @Nested
    @DisplayName("when account does not exist")
    class WhenAccountDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException and never validate nor persist")
        void shouldThrowAndNeverValidateNorPersist() {
            when(accountFinder.findActiveOrThrow(any()))
                    .thenThrow(new ResourceNotFoundException("Account not found"));

            assertThatThrownBy(() -> useCase.execute(buildCommand("new@example.com")))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(accountValidationService);
            verifyNoInteractions(accountCommandPort);
        }
    }
}
