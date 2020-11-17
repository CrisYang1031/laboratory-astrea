package laboratory.astrea.spring.transaction;

import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ProgrammaticTransactionScope implements TransactionScope {

    private final PlatformTransactionManager transactionManager;

    private final TransactionTemplate writeTemplate;

    private final TransactionTemplate readTemplate;

    public ProgrammaticTransactionScope(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.writeTemplate = new TransactionTemplate(transactionManager, TransactionSpecification.writeSpecification().transactionDefinition());
        this.readTemplate = new TransactionTemplate(transactionManager, TransactionSpecification.readSpecification().transactionDefinition());

    }
    @Override
    public void writeTransaction(@NotNull Runnable transactionBlock) {
        writeTemplate.executeWithoutResult(transactionStatus -> transactionBlock.run());
    }

    @Override
    public <T> T writeTransaction(@NotNull Supplier<T> transactionBlock) {
        return writeTemplate.execute(status -> transactionBlock.get());
    }

    @Override
    public void readTransaction(@NotNull Runnable transactionBlock) {
        readTemplate.executeWithoutResult(transactionStatus -> transactionBlock.run());
    }

    @Override
    public <T> T readTransaction(@NotNull Supplier<T> transactionBlock) {
        return readTemplate.execute(status -> transactionBlock.get());
    }

    @Override
    public void withTransaction(@NotNull TransactionSpecification specification, @NotNull Runnable transactionBlock) {
        new TransactionTemplate(transactionManager, specification.transactionDefinition()).executeWithoutResult(transactionStatus -> transactionBlock.run());
    }

    @Override
    public <T> T withTransaction(@NotNull TransactionSpecification specification, @NotNull Supplier<T> transactionBlock) {
        return new TransactionTemplate(transactionManager, specification.transactionDefinition()).execute(status -> transactionBlock.get());
    }
}
