package laboratory.astrea.spring.transaction;

import io.vavr.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import org.springframework.transaction.TransactionDefinition;

import java.time.Duration;

@SuppressWarnings("unused")
public interface TransactionSpecification {


    TransactionSpecification propagationBehavior(PropagationBehavior propagationBehavior);


    TransactionSpecification isolationLevel(IsolationLevel isolationLevel);


    TransactionSpecification timeout(Duration timeout);


    TransactionSpecification readOnly();


    TransactionDefinition transactionDefinition();



    static TransactionSpecification defaultSpecification() {
        return Companion.Default;
    }


    static TransactionSpecification writeSpecification() {
        return Companion.Write;
    }


    static TransactionSpecification readSpecification() {
        return Companion.Read;
    }


    @RequiredArgsConstructor
    enum PropagationBehavior {

        PROPAGATION_REQUIRED(0),
        PROPAGATION_SUPPORTS(1),
        PROPAGATION_REQUIRES_NEW(3),
        ;

        public final int value;
    }

    @RequiredArgsConstructor
    enum IsolationLevel {

        ISOLATION_DEFAULT(-1),
        ISOLATION_READ_UNCOMMITTED(1),
        ISOLATION_READ_COMMITTED(2),
        ISOLATION_REPEATABLE_READ(4),
        ISOLATION_SERIALIZABLE(8),
        ;

        public final int value;
    }

    @Value
    class Companion implements TransactionSpecification {

        static final TransactionSpecification Default = new Companion(PropagationBehavior.PROPAGATION_REQUIRED, IsolationLevel.ISOLATION_DEFAULT, Duration.ZERO, false);

        static final TransactionSpecification Write = new Companion(PropagationBehavior.PROPAGATION_REQUIRED, IsolationLevel.ISOLATION_REPEATABLE_READ, Duration.ofSeconds(3), false);

        static final TransactionSpecification Read = new Companion(PropagationBehavior.PROPAGATION_SUPPORTS, IsolationLevel.ISOLATION_DEFAULT, Duration.ZERO, true);


        @With
        PropagationBehavior propagationBehavior;

        @With
        IsolationLevel isolationLevel;

        @With
        Duration timeout;

        @With
        boolean readOnly;

        Lazy<TransactionDefinition> transactionDefinition = Lazy.of(this::generateTransactionDefinition);

        @Override
        public TransactionSpecification propagationBehavior(PropagationBehavior propagationBehavior) {
            return this.withPropagationBehavior(propagationBehavior);
        }

        @Override
        public TransactionSpecification isolationLevel(IsolationLevel isolationLevel) {
            return this.withIsolationLevel(isolationLevel);
        }

        @Override
        public TransactionSpecification timeout(Duration timeout) {
            return this.withTimeout(timeout);
        }

        @Override
        public TransactionSpecification readOnly() {
            return this.withReadOnly(true);
        }

        @Override
        public TransactionDefinition transactionDefinition() {
            return transactionDefinition.get();
        }


        private TransactionDefinition generateTransactionDefinition() {

            return new TransactionDefinition() {
                @Override
                public int getPropagationBehavior() {
                    return propagationBehavior.value;
                }

                @Override
                public int getIsolationLevel() {
                    return isolationLevel.value;
                }

                @Override
                public int getTimeout() {
                    return timeout.getSeconds() < 1 ? -1 : (int) timeout.getSeconds();
                }

                @Override
                public boolean isReadOnly() {
                    return readOnly;
                }

                @Override
                public String getName() {
                    return this.getClass().getName();
                }
            };
        }

    }


}


