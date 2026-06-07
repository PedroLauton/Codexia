package br.com.codexia.identity.application.usecase.account;

import br.com.codexia.identity.application.dto.command.ResolveOAuthAccountCommand;
import br.com.codexia.identity.application.dto.response.AccountResponse;
import br.com.codexia.identity.application.ports.output.command.AccountCommandPort;
import br.com.codexia.identity.application.ports.output.query.AccountQueryPort;
import br.com.codexia.identity.application.usecase.shared.AccountValidationService;
import br.com.codexia.identity.domain.exception.account.AccountDeletedException;
import br.com.codexia.identity.domain.model.aggregate.Account;
import br.com.codexia.identity.domain.model.valueobject.AvatarUrl;
import br.com.codexia.identity.domain.model.valueobject.Email;
import br.com.codexia.identity.domain.model.valueobject.Name;
import br.com.codexia.identity.domain.model.valueobject.ProviderName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResolveOAuthAccountUseCase")
class ResolveOAuthAccountUseCaseImplTest {

    @Mock
    private AccountCommandPort accountCommandPort;

    @Mock
    private AccountQueryPort accountQueryPort;

    @Mock
    private AccountValidationService accountValidationService;

    @InjectMocks
    private ResolveOAuthAccountUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<List<OutboxEvent>> outboxCaptor;

    private ResolveOAuthAccountCommand buildCommand(String providerName, String providerId) {
        return new ResolveOAuthAccountCommand(
                providerName, providerId,
                "user@example.com", "User",
                "https://example.com/avatar.jpg"
        );
    }

    private Account buildOAuthAccount(String providerName) {
        Account account = Account.createWithOAuth(
                Email.of("user@example.com"),
                Name.of("User"),
                AvatarUrl.of("https://example.com/avatar.jpg"),
                ProviderName.of(providerName),
                providerName + "-id-123"
        );
        account.pullEvents();
        return account;
    }

    @Nested
    @DisplayName("when account does not exist")
    class WhenAccountDoesNotExist {

        @Test
        @DisplayName("should create account and publish AccountCreatedEvent in outbox")
        void shouldCreateAndPublishCreatedEvent() {
            when(accountQueryPort.findByEmail(any())).thenReturn(Optional.empty());

            AccountResponse response = useCase.execute(buildCommand("google", "google-999"));

            assertThat(response).isNotNull();
            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            List<OutboxEvent> events = outboxCaptor.getValue();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("AccountCreatedEvent");
        }
    }

    @Nested
    @DisplayName("when account exists without the requested provider")
    class WhenAccountExistsWithoutProvider {

        @Test
        @DisplayName("should link provider and persist with empty outbox")
        void shouldLinkProviderAndPersist() {
            Account existingAccount = buildOAuthAccount("google");
            when(accountQueryPort.findByEmail(any())).thenReturn(Optional.of(existingAccount));

            AccountResponse response = useCase.execute(buildCommand("github", "github-456"));

            assertThat(response).isNotNull();
            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            assertThat(outboxCaptor.getValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("when account exists with the same provider already linked")
    class WhenAccountExistsWithProvider {

        @Test
        @DisplayName("should persist with empty outbox since no state changed")
        void shouldPersistWithEmptyOutbox() {
            Account existingAccount = buildOAuthAccount("google");
            when(accountQueryPort.findByEmail(any())).thenReturn(Optional.of(existingAccount));

            AccountResponse response = useCase.execute(buildCommand("google", "google-id-123"));

            assertThat(response).isNotNull();
            verify(accountCommandPort).save(any(Account.class), outboxCaptor.capture());
            assertThat(outboxCaptor.getValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("when account is deleted")
    class WhenAccountIsDeleted {

        @Test
        @DisplayName("should throw AccountDeletedException and never persist")
        void shouldThrowAndNeverPersist() {
            Account deletedAccount = buildOAuthAccount("google");
            when(accountQueryPort.findByEmail(any())).thenReturn(Optional.of(deletedAccount));
            doThrow(new AccountDeletedException(deletedAccount.getId()))
                    .when(accountValidationService).validateAccountNotDeleted(any());

            assertThatThrownBy(() -> useCase.execute(buildCommand("google", "google-id-123")))
                    .isInstanceOf(AccountDeletedException.class);

            verifyNoInteractions(accountCommandPort);
        }
    }
}
