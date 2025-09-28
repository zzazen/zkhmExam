package com.zkhm.exam.ai.rag;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @program: zkhmExam
 * @description:
 * @author: Mr.Zhang
 * @create: 2025-09-26 11:04
 **/

@Configuration
public class RagConfig {

    @Autowired
    QwenEmbeddingModel qwenEmbeddingModel;

    @Autowired
    EmbeddingStore<TextSegment> qwenEmbeddingStore;

    @Bean
    ContentRetriever myContentRetriever() {
        // 1.加载文档
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/Users/zhangzan/zkhmExam/exam/src/main/resources/docx");
        // 2.文档切片，每一段一个切片, 最大1000个字符（可使用Tokenizer进行token计数），每次最多重叠200个字符
        DocumentByCharacterSplitter documentByCharacterSplitter =
                new DocumentByCharacterSplitter(1000, 200);
        // 3.自定义文档加载器，把文档转化成向量并保存在向量数据库中
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentByCharacterSplitter)
                // 为了提高文档质量，为切割后的文档 TextSegment 添加文档名称，作为元信息
                .textSegmentTransformer(textSegment -> {
                    return TextSegment.from(textSegment.metadata().getString("file_name")
                            + "\n" + textSegment.text(), textSegment.metadata());
                })
                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(qwenEmbeddingStore)
                .build();
        // 4.加载文档
        ingestor.ingest(documents);
        // 5.自定义内容加载器
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(qwenEmbeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5)
                .minScore(0.7)
                .build();
        return contentRetriever;
    }
}
