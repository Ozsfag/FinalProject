package searchengine.utils.lockWrapper;

import java.util.function.Supplier;

public interface LockWrapper {
  void writeLock(Runnable action);

  <T> T readLock(Supplier<T> action);
}
