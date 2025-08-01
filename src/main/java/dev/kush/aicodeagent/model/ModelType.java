package dev.kush.aicodeagent.model;

public enum ModelType {
    OPENAI_CHAT("openAiChatModel", "chat", """
            Moonshot Kimi K2 Instruct – Open-weight, MoE-based agentic model with high efficiency
            
            Model: kimi-k2-1t-moe
            Use for: Autonomous AI agents, advanced tool use, multi-turn code reasoning, enterprise automation, complex planning
            Strengths: 1T parameter MoE with 32B active experts, long context (128k), strong in code and reasoning, extremely low token cost
            Weaknesses: Infrastructure closed-source, limited English community adoption, stability may vary
            Best when: You need a high-capacity open model for tool-augmented agents or long-context problem solving
            Pricing level: cheap
            """),

    AZURE_CHAT("azureChatModel", "chat", """
            Azure OpenAI GPT-4.1 – Enterprise-grade AI with deep reasoning and security
            
            Model: gpt-4.1
            Use for: Complex reasoning, multimodal inputs, code generation, instruction following, long-context agents, secure workflows
            Strengths: 1M token context, strong code/math performance, enterprise compliance, robust multimodal support
            Weaknesses: Higher cost and latency, Azure-only deployment
            Best when: You need secure, high-performance AI for business-critical or compliance-heavy applications'
            Pricing level: expensive
            """),

    AZURE_MINI_CHAT("azureMiniChatModel", "chat", """
            Azure GPT-4.1 Mini – Fast, cost-efficient model for conversational agents
            
            Model: gpt-4.1-mini
            Use for: High-volume chatbots, lightweight assistants, DevOps/CI agents, knowledge retrieval, finance/healthcare workflows
            Strengths: Lower latency and cost, supports long context, suitable for scalable deployment
            Weaknesses: Slightly reduced reasoning vs full GPT-4.1, fewer fine-tuned capabilities
            Best when: Optimizing for speed and cost in high-throughput environments without complex reasoning
            Pricing level: medium
            """);


    private final String beanName;
    private final String type;
    private final String aiGuidance;

    ModelType(String beanName, String type, String aiGuidance) {
        this.beanName = beanName;
        this.type = type;
        this.aiGuidance = aiGuidance;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getType() {
        return type;
    }

    public String getAiGuidance() {
        return aiGuidance;
    }

    public boolean isChat() {
        return "chat".equals(type);
    }

    public boolean isEmbedding() {
        return "embedding".equals(type);
    }

    public static String getAllInfoAsString() {
        // all of the above for all models
        StringBuilder sb = new StringBuilder();
        for (ModelType model : ModelType.values()) {
            sb.append(model.name()).append(":\n")
                    .append("Bean Name: ").append(model.getBeanName()).append("\n")
                    .append("Type: ").append(model.getType()).append("\n")
                    .append("AI Guidance: ").append(model.getAiGuidance()).append("\n\n");
        }
        return sb.toString();
    }
}