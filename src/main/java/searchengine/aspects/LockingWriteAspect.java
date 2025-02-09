package searchengine.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Aspect
@Component
public class LockingWriteAspect {
  @Autowired private ReentrantReadWriteLock lock;

  @Around("@annotation(searchengine.aspects.annotations.LockableWrite)")
  public void writeLock(ProceedingJoinPoint joinPoint) throws Throwable {
    lock.writeLock().lock();
    try {
      joinPoint.proceed();
    } finally {
      lock.writeLock().unlock();
    }
  }
}
