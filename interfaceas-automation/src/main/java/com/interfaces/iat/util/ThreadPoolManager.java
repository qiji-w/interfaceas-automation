package com.interfaces.iat.util;

import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolManager {

    //根据cpu的数量动态的配置核心线程数和最大线程数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数 = CPU核心数 + 1050
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1050;
    //线程池最大线程数 = CPU核心数 * 2 + 100000
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 100000;
    //非核心线程闲置时间 = 超时1s
    private static final int KEEP_ALIVE = 1;
    Lock lock = new ReentrantLock();

    // 要确保该类只有一个实例对象，避免产生过多对象消费资源，所以采用单例模式
    private ThreadPoolManager() {
    }

    private static ThreadPoolManager sInstance;

    //创建线程池
    public synchronized static ThreadPoolManager getsInstance() {
        if (sInstance == null) {
            sInstance = new ThreadPoolManager();
        }
        return sInstance;
    }

    // 线程池的对象
    private ThreadPoolExecutor executor;

    /**
     * 提交任务到线程池中执行
     * corePoolSize:核心线程数
     * maximumPoolSize：线程池所容纳最大线程数(workQueue队列满了之后才开启)
     * keepAliveTime：非核心线程闲置时间超时时长
     * unit：keepAliveTime的单位
     * workQueue：等待队列，存储还未执行的任务
     * threadFactory：线程创建的工厂
     * handler：异常处理机制
     *
     * @param r
     */
    //转为运行时异常
    @SneakyThrows
    public void execute(Runnable r) {
        lock.lock(); // 上锁
        if (executor == null) {
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        }
        try {
            executor.execute(r);// 把一个任务丢到了线程池中
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.unlock(); // 解锁
    }

    /**
     * 提交需要获取返回值的任务
     *
     * @param command
     * @return
     */
    //转为运行时异常
    @SneakyThrows
    public Future submit(Callable command) {
        lock.lock(); // 上锁
        if (executor == null) {
            executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        }
        Future submit = executor.submit(command);
        lock.unlock(); // 解锁
        return submit;
    }

    /**
     * 把任务移除等待队列
     *
     * @param r
     */
    public void cancel(Runnable r) {
        if (r != null) {
            executor.getQueue().remove(r);
        }
    }

    /**
     * 停止线程池
     */
    public void stop() {
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    //转为运行时异常
    @SneakyThrows
    public void executeDemo() {
        class QaNumeratorThread implements Runnable {


            @Override
            public void run() {
                System.out.println("1111111111111");
            }
        }
        //TODO -----------------------------------------------------------------
        ThreadPoolManager myThreadPoolManager = ThreadPoolManager.getsInstance();
        QaNumeratorThread qaNumeratorThread = new QaNumeratorThread();
        new Thread(() -> {
            try {
                myThreadPoolManager.execute(qaNumeratorThread);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            myThreadPoolManager.stop();
        }).start();


    }

    //转为运行时异常
    @SneakyThrows
    public void submitDemo() {
        class TestRandomNum implements Callable<Integer> {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt(10);
            }
        }
        //TODO -----------------------------------------------------------------
        ThreadPoolManager myThreadPoolManager = ThreadPoolManager.getsInstance();
        TestRandomNum testRandomNum = new TestRandomNum();
        //设置并发5个 异步线程
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Future submit = myThreadPoolManager.submit(testRandomNum);
                try {
                    Object object = submit.get();
                    System.out.println(object.toString());//多线程返回值
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    myThreadPoolManager.stop();
                }
            }).start();
        }

        System.out.println("主线程");
    }

    public static void main(String[] args) {
        ThreadPoolManager myThreadPoolManager = ThreadPoolManager.getsInstance();
        myThreadPoolManager.submitDemo();

    }

}

