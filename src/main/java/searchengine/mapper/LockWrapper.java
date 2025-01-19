package searchengine.mapper;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LockWrapper {
  @Autowired private ReentrantReadWriteLock lock;

  public void writeLock(Runnable action) {
    lock.writeLock().lock();
    try {
      action.run();
    } finally {
      lock.writeLock().unlock();
    }
  }

  public <T> T readLock(Supplier<T> action) {
    lock.readLock().lock();
    try {
      return action.get();
    } finally {
      lock.readLock().unlock();
    }
  }
}
