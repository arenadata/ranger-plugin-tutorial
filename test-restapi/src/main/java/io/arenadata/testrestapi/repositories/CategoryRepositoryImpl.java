package io.arenadata.testrestapi.repositories;
import io.arenadata.testrestapi.contracts.DataRepository;
import io.arenadata.testrestapi.dao.Category;
import io.arenadata.testrestapi.system.ManagedException;
import io.arenadata.testrestapi.system.ManagedExceptionType;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public final class CategoryRepositoryImpl implements DataRepository<Category> {
    private static final String DEFAULT_UNIT_NAME = "test";
    private static final Logger logger = LoggerFactory.getLogger(CategoryRepositoryImpl.class);
    private final EntityManager entityManager;

    public CategoryRepositoryImpl() {
        final EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory(DEFAULT_UNIT_NAME);
        this.entityManager = managerFactory.createEntityManager();
    }

    @Override
    public void startTransaction() throws ManagedException {
        this.checkEntityManager("Can't start the DB transaction");
        this.entityManager.getTransaction().begin();
    }

    @Override
    public void endTransaction() throws ManagedException {
        this.checkEntityManager("Can't end the DB transaction");

        if (!this.entityManager.getTransaction().isActive())
            throw new ManagedException(ManagedExceptionType.INACTIVE_TRANSACTION_USAGE, "Can't end the DB transaction, due it's inactive.");

        this.entityManager.getTransaction().commit();
    }

    @Override
    public void close() throws ManagedException {
        this.checkEntityManager("Can't close the entity manager factory & manager instance");
        this.entityManager.getEntityManagerFactory().close();
        this.entityManager.close();
    }

    @Override
    public List<Category> seedTestData() {
        final List<Category> data = new ArrayList<>();
        final SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            final byte[] bytes = new byte[20];
            random.nextBytes(bytes);

            final Category item = new Category();
            item.setTitle(String.format("Test-value: %d", new BigInteger(bytes).intValue()));
            data.add(item);
            this.entityManager.persist(item);
        }

        this.entityManager.flush();
        return data;
    }

    @Override
    public List<Category> getAllWithOffsetAndLimit(int offset, int limit) {
        final TypedQuery<Category> query = this.entityManager.createQuery("FROM Category", Category.class);
        final List<Category> data = query.getResultList();

        for (final Category item : data)
            logger.info("Fetched item via entity manager: {} -> {}", item.getId(), item.getTitle());

        return data;
    }

    private void checkEntityManager(String errorMessage) throws ManagedException {
        if (errorMessage == null || errorMessage.trim().isEmpty())
            throw new ManagedException(ManagedExceptionType.INPUT_STRING_NULL_OR_EMPTY, "Can't process the string check, due it's null or empty.");

        if (this.entityManager == null)
            throw new ManagedException(ManagedExceptionType.NULLABLE_OBJECT, String.format("%s, due the entity manager object is null.", errorMessage));
    }
}
