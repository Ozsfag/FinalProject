package searchengine.utils.lockWrapper.impl;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LockWrapperImpl implements searchengine.utils.lockWrapper.LockWrapper {
  @Autowired private ReentrantReadWriteLock lock;

  public void writeLock(Runnable action) {
    getLock().writeLock().lock();
    try {
      action.run();
    } finally {
      getLock().writeLock().unlock();
    }
  }

  public <T> T readLock(Supplier<T> action) {
    getLock().readLock().lock();
    try {
      return action.get();
    } finally {
      getLock().readLock().unlock();
    }
  }
}
