package com.zkhm.exam.ai.workflow.content;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: zkhmExam
 * @description: 编码实现workflow全局上下文
 * @author: Mr.Zhang
 * @create: 2025-09-27 12:41
 **/

public class WorkflowContext{
    private static final Map<String, Object> data = new ConcurrentHashMap<>();

    public <T> T get(String key, Class<T> type) {
        Object value = data.get(key);
        if (value == null) {
            return null;
        }
        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                    "Type mismatch for key '%s': expected %s but found %s"
                            .formatted(key, type.getSimpleName(), value.getClass().getSimpleName()), e);
        }
    }

    public void put(String key, Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            data.put(key,"输入为空");
            return;
        }
        data.put(key, value);
    }

    public boolean contains(String key) {
         return data.keySet().contains(key);
    }
}












