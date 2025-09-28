package com.zkhm.exam.ai.guardrail;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;

import java.util.Set;

/**
 * @program: zkhmExam
 * @description:
 * @author: Mr.Zhang
 * @create: 2025-09-26 14:57
 **/

public class SafeInputGuardRail implements InputGuardrail {

    private static final Set<String> sensitiveWords = Set.of("password", "credit card", "social security");

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String inputText = userMessage.singleText().toLowerCase();
        String[] words = inputText.split("\\W+");
        for(String word : words){
            if(sensitiveWords.contains(word)){
                return fatal("sensitive word detected: " + word);
            }
        }
        return success();
    }
}
