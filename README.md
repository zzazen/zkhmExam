## 整体介绍  
1. 框架：**LangChain4j**，model：Qwen-max（通义），mcp：web-search（智谱）， workflow：编码实现 Condition + WorkflowContent组件  ，dag：编码实现 Node + WorkflowDefine + WorkflowEngine 组件
2. 扩展功能：拦截器（guardrail）、rag：text-embedding-v4 + 自定义tool，用于构建特定功能的Agent，本项目是用于调用私域知识库，包含打印机故障、系统故障等解决方案  

## 需求： 
1. 设计一个简单的Workflow
2. 设计一个Workflow的Graph DAG，用于Agent编排
3. 设计三个Agent，Agent1和Agent2完成后，启动Agent3，输出结果  

## 组件  

1. **Workflow**，Condition(And、Or、Not三种实现) + WorkflowContent + WorkflowDefine + WorkflowEngine 组件构成，均自行编码实现
2. **DAG**，Node(SimpleNode) + WorkflowDefine + WorkflowEngine 组件构成，自行编码实现
3. **Agent1**，SearchAgent，由 通义 model + 智谱 web-serch tool，配合 system-prompt-search 实现
4. **Agent2**，AddBackgroudAgent，由 通义 model + RAG(存储设备、服务器常见问题的解决方案) + 配合RAG的自定义tool，配合 system-prompt-addbg 实现  
5. **Agent3**，MergeAgent，由通义 model，配合 system-prompt-merge 实现  

## 用例
### 接口方法：executeAgentWorkflow( String query )
输入参数：类型：String，含义：用户输入的任务  
输出参数：类型： WorkflowContext（内置ConCurrentHashMap），即包含用户输入、所有中间agent输出的key、value，以及最终agent输出的key、value  
注：agent的输出key为"${agent名}_result"  
举例：  
```
@SpringBootTest
class WorkflowServiceTest {
    @Autowired
    private WorkflowService workflowService;

    @Test
    void testExecuteAgentWorkflow() {
        String query = "然并卵";
        WorkflowContext workflowContext = workflowService.executeAgentWorkflow(query);
        String s = workflowContext.get("agent3_result", String.class);
        System.out.println(s);
    }
}
```
