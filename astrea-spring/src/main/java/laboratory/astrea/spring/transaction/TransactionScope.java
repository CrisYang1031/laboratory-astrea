package laboratory.astrea.spring.transaction;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


@SuppressWarnings("unused")
public interface TransactionScope {


    void writeTransaction(@NotNull Runnable transactionBlock);


    <T> T writeTransaction(@NotNull Supplier<T> transactionBlock);


    void readTransaction(@NotNull Runnable transactionBlock);


    <T> T readTransaction(@NotNull Supplier<T> transactionBlock);


    void withTransaction(@NotNull TransactionSpecification specification, @NotNull Runnable transactionBlock);


    <T> T withTransaction(@NotNull TransactionSpecification specification, @NotNull Supplier<T> transactionBlock);

}
