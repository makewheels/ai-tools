package com.github.makewheels.aitools.food;

public interface TaskStatus {
    // 数据库已创建
    String CREATED = "created";

    // 调用GPT执行中
    String RUNNING = "running";

    // 已完成
    String FINISHED = "finished";
}
