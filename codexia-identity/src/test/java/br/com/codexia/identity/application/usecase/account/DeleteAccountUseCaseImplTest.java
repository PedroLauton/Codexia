package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.DeleteAccountCommand;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.usecase.shared.AccountFinder;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.exception.ResourceNotFoundException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteAccountUseCase")
class DeleteAccountUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountFinder accountFinder;

    @InjectMocks
    private DeleteAccountUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<List<OutboxEvent>> outboxCaptor;

    private Account buildActiveAccount() {
        Account account = Account.createWithCredentials(
                Email.of("user@example.com"),
                Name.of("User"),
                new PasswordHash("$2a$10$hash")
        );
        account.pullEvents();
        return account;
    }

    private DeleteAccountCommand buildCommand() {
        return new DeleteAccountCommand(UUID.randomUUID().toString());
    }

    @Nested
    @DisplayName("when account exists and is active")
    class WhenAccountExistsAndIsActive {

        @Test
        @DisplayName("should soft-delete and publish AccountDeletedEvent in outbox")
        void shouldDeleteAndPublishEvent() {
            Account account = buildActiveAccount();
            when(accountFinder.findActiveOrThrow(any())).thenReturn(account);

            assertThatNoException().isThrownBy(() -> useCase.execute(buildCommand()));

            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            List<OutboxEvent> events = outboxCaptor.getValue();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("AccountDeletedEvent");
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

            assertThatThrownBy(() -> useCase.execute(buildCommand()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }
}
