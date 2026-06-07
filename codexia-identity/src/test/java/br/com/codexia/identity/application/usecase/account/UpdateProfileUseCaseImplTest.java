package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.UpdateProfileCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProfileUseCase")
class UpdateProfileUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountFinder accountFinder;

    @InjectMocks
    private UpdateProfileUseCaseImpl useCase;

    private Account buildActiveAccount() {
        Account account = Account.createWithCredentials(
                Email.of("user@example.com"),
                Name.of("Old Name"),
                new PasswordHash("$2a$10$hash")
        );
        account.pullEvents();
        return account;
    }

    @Nested
    @DisplayName("when account exists and is active")
    class WhenAccountExistsAndIsActive {

        @Test
        @DisplayName("should update name and persist")
        void shouldUpdateNameAndPersist() {
            Account account = buildActiveAccount();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);

            UpdateProfileCommand command = new UpdateProfileCommand(
                    UUID.randomUUID().toString(), "New Name"
            );

            AccountResponse response = useCase.execute(command);

            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("New Name");
            verify(accountCommandPort).save(eq(account), eq(List.of()));
        }
    }

    @Nested
    @DisplayName("when account does not exist")
    class WhenAccountDoesNotExist {

        @Test
        @DisplayName("should throw ResourceNotFoundException and never persist")
        void shouldThrowAndNeverPersist() {
            when(accountFinder.findActiveOrThrow(any()))
                    .thenThrow(new ResourceNotFoundException("Account not found"));

            UpdateProfileCommand command = new UpdateProfileCommand(
                    UUID.randomUUID().toString(), "New Name"
            );

            assertThatThrownBy(() -> useCase.execute(command))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }
}
