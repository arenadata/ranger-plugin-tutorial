package io.arenadata.testrestapi.contracts;
import io.arenadata.testrestapi.system.ManagedException;
import java.util.List;

public interface DataRepository<T> {
    void startTransaction() throws ManagedException;
    void endTransaction() throws ManagedException;
    void close() throws ManagedException;
    List<T> seedTestData();
    List<T> getAllWithOffsetAndLimit(int offset, int limit);
}
