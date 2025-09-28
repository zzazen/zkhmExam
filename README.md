## 整体介绍  

### 需求： 
1. 设计一个简单的Workflow
2. 设计一个Workflow的Graph DAG，用于Agent编排
3. 设计三个Agent，Agent1和Agent2完成后，启动Agent3，输出结果 

### 技术栈
- **框架**：LangChain4j
- **大模型**：Qwen-max（通义千问）
- **MCP 工具**：web-search（智谱）
- **RAG 向量模型**：text-embedding-v4
- **私域知识库**：打印机故障、系统故障等解决方案文档  

## 组件  

1. **Workflow**，Condition(And、Or、Not三种实现) + WorkflowContent + WorkflowDefine + WorkflowEngine 组件构成，均自行编码实现
2. **DAG**，Node(SimpleNode) + WorkflowDefine + WorkflowEngine 组件构成，自行编码实现。
3. **Agent1**，SearchAgent，由 通义 model + 智谱 web-serch tool，配合 system-prompt-search 实现
4. **Agent2**，AddBackgroudAgent，由 通义 model + RAG(存储设备、服务器常见问题的解决方案) + 配合RAG的自定义tool，配合 system-prompt-addbg 实现  
5. **Agent3**，MergeAgent，由通义 model，配合 system-prompt-merge 实现  
**注**：Workflow、DAG的实现详见下文的“核心模块说明”。
## 用例
### 接口方法：executeAgentWorkflow( String query )
- 输入参数：类型：String，含义：用户输入的任务  
- 输出参数：类型： WorkflowContext（内置ConCurrentHashMap），包含用户输入、所有中间agent输出的key、value，以及最终agent输出的key、value  
**注**：所有agent的输出的key均为"${agent名}_result"。对于中间agent，输出的value一般为JSON格式，最终agent，输出的value为String格式。 
举例：  
```
    @Test
    void testExecuteAgentWorkflow() {
        String query = "XXX";  // 用户查询
        WorkflowContext workflowContext = workflowService.executeAgentWorkflow(query);  // 调用自定义服务
        String s = workflowContext.get("agent3_result", String.class);  //  根据业务逻辑，查看最终返回值，此处agent3作为最终输出，所以查看agent3_result
    }
```

## 核心模块说明
### 1. 上下文管理 WorkflowContext

功能 : **全局共享数据容器，用于在 Agent 间传递输入、中间结果与状态*  
代码  
```
public class WorkflowContext {
    private static final Map<String, Object> data = new ConcurrentHashMap<>();

    public <T> T get(String key, Class<T> type) { ... }
    public void put(String key, Object value) { ... }
    public boolean contains(String key) { ... }
}
```
### 2. DAG编排 WorkflowDefinition

功能 : **管理节点集合与依赖关系*  
核心结构  
```
private final Map<String, Node> nodes = new LinkedHashMap<>();
private final Map<String, Set<String>> dependentsMap = new HashMap<>();
```
关键方法：  
- addNode()：添加节点并建立反向索引  
- getDependents()：获取某节点的下游节点  
### 3. 节点 SimpleNode
核心字段  
```
private final String id;
private final Set<String> dependencies;
private final Condition enableCondition;
private final Callable<Void> task;
```
### 4. 逻辑表达式 Condition
接口  
```
public interface Condition {
    boolean evaluate(WorkflowContext context);
}
```
### 5. 执行引擎 WorkflowEngine

核心流程  

- 环路检测（拓扑排序）: 主循环：扫描 → 提交可执行任务 → 等待批次完成  
- 异常捕获与上下文记录  
- 并行执行能力  

同一批次中，所有满足依赖的节点并行执行 : **支持 Agent1 与 Agent2 同时运行*
