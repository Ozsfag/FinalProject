package searchengine.aspects;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LockingReadAspect {
  @Autowired private ReentrantReadWriteLock lock;

  @Around("@annotation(searchengine.aspects.annotations.LockableRead)")
  public Object executeWithReadLock(ProceedingJoinPoint joinPoint) throws Throwable {
    lock.readLock().lock();
    try {
      return joinPoint.proceed();
    } finally {
      lock.readLock().unlock();
    }
  }
}
