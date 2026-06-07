package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.RestoreAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.domain.exception.account.AccountGracePeriodExpiredException;
import br.com.codexia.identity.domain.exception.account.AccountNotFoundException;
import br.com.codexia.identity.domain.exception.account.AccountNotDeletedException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.PasswordHash;
import br.com.codexia.shared.application.outbox.OutboxEvent;
import br.com.codexia.shared.domain.model.AccountId;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestoreAccountUseCase")
class RestoreAccountUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountQueryPort accountQueryPort;

    @InjectMocks
    private RestoreAccountUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<List<OutboxEvent>> outboxCaptor;

    private Account buildDeletedAccount() {
        Account account = Account.createWithCredentials(
                Email.of("user@example.com"),
                Name.of("User"),
                new PasswordHash("$2a$10$hash")
        );
        account.pullEvents();
        account.delete();
        account.pullEvents();
        return account;
    }

    private Account buildActiveAccount() {
        Account account = Account.createWithCredentials(
                Email.of("user@example.com"),
                Name.of("User"),
                new PasswordHash("$2a$10$hash")
        );
        account.pullEvents();
        return account;
    }

    private RestoreAccountCommand buildCommand() {
        return new RestoreAccountCommand(UUID.randomUUID().toString());
    }

    @Nested
    @DisplayName("when account is deleted and within grace period")
    class WhenAccountIsDeletedAndWithinGracePeriod {

        @Test
        @DisplayName("should restore account and publish AccountRestoredEvent in outbox")
        void shouldRestoreAndPublishEvent() {
            Account account = buildDeletedAccount();
            when(accountQueryPort.findDeletedById(any())).thenReturn(Optional.of(account));

            AccountResponse response = useCase.execute(buildCommand());

            assertThat(response).isNotNull();
            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            List<OutboxEvent> events = outboxCaptor.getValue();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("AccountRestoredEvent");
        }
    }

    @Nested
    @DisplayName("when account is not deleted")
    class WhenAccountIsNotDeleted {

        @Test
        @DisplayName("should throw AccountNotDeletedException and never persist")
        void shouldThrowAndNeverPersist() {
            Account activeAccount = buildActiveAccount();
            when(accountQueryPort.findDeletedById(any())).thenReturn(Optional.of(activeAccount));

            assertThatThrownBy(() -> useCase.execute(buildCommand()))
                    .isInstanceOf(AccountNotDeletedException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }

    @Nested
    @DisplayName("when grace period has expired")
    class WhenGracePeriodHasExpired {

        @Test
        @DisplayName("should throw AccountGracePeriodExpiredException and never persist")
        void shouldThrowAndNeverPersist() {
            Account expiredAccount = mock(Account.class);
            doThrow(new AccountGracePeriodExpiredException(AccountId.generate()))
                    .when(expiredAccount).restore();
            when(accountQueryPort.findDeletedById(any())).thenReturn(Optional.of(expiredAccount));

            assertThatThrownBy(() -> useCase.execute(buildCommand()))
                    .isInstanceOf(AccountGracePeriodExpiredException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }

    @Nested
    @DisplayName("when account does not exist")
    class WhenAccountDoesNotExist {

        @Test
        @DisplayName("should throw AccountNotFoundException and never persist")
        void shouldThrowAndNeverPersist() {
            when(accountQueryPort.findDeletedById(any())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.execute(buildCommand()))
                    .isInstanceOf(AccountNotFoundException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }
}
