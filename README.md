## zkhmExam
需要实现： 
1. 设计一个简单的Workflow
2. 设计一个Workflow的Graph DAG，用于Agent编排
3. 设计三个Agent，Agent1和Agent2完成后，启动Agent3，输出结果  
## 整体介绍  
1. 框架：LangChain4j，model：Qwen-vl-max（通义），mcp：web-search（智谱），workflow：编码实现 Node + WorkflowEngine组件  
2. 扩展功能：拦截器（guardrail）、rag + 自定义tool，用于构建特定功能的Agent，本项目是用于整合调用者背景信息  
## 介绍本项目组件  
1. Agent1，SearchAgent，由 通义 model + 智谱 web-serch tool，配合 system-prompt-for-search 实现  
2. Agent2，AddBackgroudAgent，由 通义 model + RAG(存储用户、公司等背景信息) + 配合RAG的自定义tool，配合 system-prompt-addbg 实现  
3. Agent3，MergeAgent，由通义 model，配合 system-prompt-merge 实现  
4. Workflow，Node + WorkflowEngine 组件构成，均自行编码实现
