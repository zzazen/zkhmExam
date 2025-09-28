package com.zkhm.exam.ai;

import com.google.common.io.Resources;
import com.zkhm.exam.ai.agents.AddBGAgent;
import com.zkhm.exam.ai.agents.MergeAgent;
import com.zkhm.exam.ai.agents.SearchAgent;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.rag.content.Content;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @program: zkhmExam
 * @description:
 * @author: Mr.Zhang
 * @create: 2025-09-25 18:52
 **/

@SpringBootTest
public class AiCodeHelperT {

    @Autowired
    AddBGAgent addBGAgent;

    @Autowired
    SearchAgent searchAgent;

    @Autowired
    MergeAgent mergeAgent;

    @Test
    public void testSearch() {
        String ans = searchAgent.search("公司打印机有问题怎么办").content().text();
        System.out.println(ans);
    }

    @Test
    public void testAddBG() {
        String ans = addBGAgent.addBG("公司打印机有问题怎么办").content().text();
        System.out.println(ans);
    }

    @Test
    public void testMerge() {
        String ans1 = searchAgent.search("公司打印机有问题怎么办").content().text();
        String ans2 = addBGAgent.addBG("公司打印机有问题怎么办").content().text();
        String ans = mergeAgent.merge(ans1 + ans2);
        System.out.println(ans);
    }

    @Test
    public void testLoadPrompt() throws IOException {
        String prompt = Resources.toString(
                Resources.getResource("system-prompt-search.txt"),
                StandardCharsets.UTF_8
        );
        System.out.println(prompt);
    }

}
