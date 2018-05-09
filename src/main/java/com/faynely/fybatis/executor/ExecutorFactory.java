package com.faynely.fybatis.executor;

/**
 * Executor 工厂
 * @author NickFayne 2018-05-08 21:27
 */
public class ExecutorFactory {
    private final String SIMPLE = "SIMPLE";
    private final String CACHE = "CACHE";

    public enum ExecutorType{
        SIMPLE,
        CACHE;
    }

    public static Executor newInstance(String executorType){
        if(executorType.equals(ExecutorType.CACHE.name())){
            return new CachingExecutor(new SimpleExecutor());
        }

        if(executorType.equals(ExecutorType.SIMPLE.name())){
            return new SimpleExecutor();
        }

        throw new RuntimeException("Executor type error");
    }
}
