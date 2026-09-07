package com.nuocao.common.utils;

import com.nuocao.common.exception.CommonException;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 */
@Component
public class LockUtils {


    @Inject
    private RedissonClient redissonClient;


    /**
     * 分布式锁(非公平锁）
     * @param lockKey 关键词
     * @param timeoutMillis 最多锁定时长5000，
     * @param runnable 无法返回值
     */
    public void lock(String lockKey, Long timeoutMillis, Runnable runnable) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock(timeoutMillis, TimeUnit.MILLISECONDS);
            // 执行逻辑
            runnable.run();
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 分布式锁(非公平锁）,锁定时长5000毫秒
     * @param lockKey 关键词
     * @param runnable 无法返回值
     */
    public void lock(String lockKey, Runnable runnable) {
        this.lock(lockKey,5000L,runnable);
    }

    /**
     * 通过分布式锁实现任务处理并返回相关值(非公平锁）
     * @param lockKey
     * @param timeoutMillis
     * @param callable
     * @param <T>
     * @return T
     */
    public <T> T lock(String lockKey, Long timeoutMillis, Callable<T> callable) {
        RLock lock = redissonClient.getLock(lockKey);

        T resp = null;
        try {
            lock.lock(timeoutMillis, TimeUnit.MILLISECONDS);
            // 执行逻辑
            resp= callable.call();
        }catch(Exception ex)
        {
            CommonException.throwException("lock failed,lock key:{},error:{}",lockKey,ex.getMessage());
        }
        finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return resp;

    }

    /**
     * 通过分布式锁实现任务处理并返回相关值(非公平锁），锁定时长，5000
     * @param lockKey
     * @param callable
     * @param <T>
     * @return T
     */
    public <T> T lock(String lockKey,  Callable<T> callable) {
        return this.lock(lockKey,5000L,callable);
    }


    /**
     * 分布式锁(公平锁）
     * @param lockKey 关键词
     * @param timeoutMillis 最多锁定时长500，
     * @param runnable 无法返回值
     */
    public void fairLock(String lockKey, Long timeoutMillis, Runnable runnable) {
        RLock lock = redissonClient.getFairLock(lockKey);
        try {
            lock.lock(timeoutMillis, TimeUnit.MILLISECONDS);
            // 执行逻辑
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 通过分布式锁实现任务处理并返回相关值(公平锁）
     * @param lockKey
     * @param timeoutMillis
     * @param callable
     * @param <T>
     * @return T
     */
    public <T> T fairLock(String lockKey, Long timeoutMillis, Callable<T> callable) {
        RLock lock = redissonClient.getFairLock(lockKey);

        //另一种执行方式
    /*
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<T> futureResult = executorService.submit(callable);
        executorService.shutdown(); ///停止接受新任务
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS); //等待5秒
        } catch (Exception ex)
        {

        }
        T result = futureResult.get();
*/


        // 提交任务并获取 Future 对象

        T resp = null;
        try {
            lock.lock(timeoutMillis, TimeUnit.MILLISECONDS);
            // 执行逻辑
            resp= callable.call();
        }catch(Exception ex)
        {
            CommonException.throwException("lock failed,lock key:{},error:{}",lockKey,ex.getMessage());
        }
        finally {
            lock.unlock();
        }

        return resp;

    }


    /**
     * 拿到锁定的分布式锁，使用完后一定要unlock
     * @param lockKey
     * @param waitMillis
     * @return
     */
    public RLock tryLock(String lockKey,Long waitMillis)
    {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(waitMillis, TimeUnit.MILLISECONDS);
            if(!isLocked)
            {
                return null;
            }
        }catch( Exception ex)
        {
            return null;
        }

        return lock;
    }

    public void unlock(RLock lock)
    {   if(lock==null) return;
        if(lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    //锁定数据接口RLock的接口说明
    // public interface RLock extends Lock, RLockAsync {

    //----------------------Lock接口方法-----------------------

    /**
     * 加锁 锁的有效期默认30秒
     */
    //    void lock();

    /**
     * tryLock()方法是有返回值的，它表示用来尝试获取锁，
     * 如果获取成功，则返回true，
     * 如果获取失败（即锁已被其他线程获取），则返回false .
     */
    //    boolean tryLock();

    /**
     * tryLock(long time, TimeUnit unit)方法和tryLock()方法是类似的，
     * 只不过区别在于这个方法在拿不到锁时会等待一定的时间，
     * 在时间期限之内如果还拿不到锁，就返回false。
     * 如果如果一开始拿到锁或者在等待期间内拿到了锁，则返回true。
     *
     * @param time 等待时间
     * @param unit 时间单位 小时、分、秒、毫秒等
     */
    //     boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 解锁
     */
    //    void unlock();

    /**
     * 中断锁 表示该锁可以被中断 假如A和B同时调这个方法，
     * A获取锁，B为获取锁，那么B线程可以通过
     * Thread.currentThread().interrupt(); 方法真正中断该线程
     */
    //     void lockInterruptibly();

    //----------------------RLock接口方法-----------------------
    /**
     * 加锁 上面是默认30秒这里可以手动设置锁的有效时间
     *
     * @param leaseTime 锁有效时间
     * @param unit      时间单位 小时、分、秒、毫秒等
     */
    //   void lock(long leaseTime, TimeUnit unit);

    /**
     * 这里比上面多一个参数，多添加一个锁的有效时间
     *
     * @param waitTime  等待时间
     * @param leaseTime 锁有效时间
     * @param unit      时间单位 小时、分、秒、毫秒等
     */
    //   boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * 检验该锁是否被线程使用，如果被使用返回True
     */
    //    boolean isLocked();

    /**
     * 检查当前线程是否获得此锁（这个和上面的区别就是该方法可以判断是否当前线程获得此锁，而不是此锁是否被线程占有）
     * 这个比上面那个实用
     */
    //   boolean isHeldByCurrentThread();

    /**
     * 中断锁 和上面中断锁差不多，只是这里如果获得锁成功,添加锁的有效时间
     * @param leaseTime  锁有效时间
     * @param unit       时间单位 小时、分、秒、毫秒等
     */
    //   void lockInterruptibly(long leaseTime, TimeUnit unit);
    //}



}