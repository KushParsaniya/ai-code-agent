package dev.kush.aicodeagent.model;

public enum ModelType {
    OPENAI_CHAT("openAiChatModel", "chat", """
        Groq LLaMA 3.3 70B – Open-source, high-speed conversational model

        Model: llama-3.3-70b-versatile
        Use for: Real-time AI agents, multilingual chat, customer support, math/code reasoning, function/tool calls
        Strengths: Ultra-fast inference, strong reasoning, open-source, low cost
        Weaknesses: Less creative and polished than GPT models
        Best when: Prioritizing speed, cost, and openness in production-grade chat interfaces
    """),

    AZURE_CHAT("azureChatModel", "chat", """
        Azure OpenAI GPT-4.1 – Enterprise-grade AI with deep reasoning and security

        Model: gpt-4.1
        Use for: Complex reasoning, code generation, instruction following, long-context agents, secure enterprise workflows
        Strengths: Long context, strong reasoning, enterprise compliance and support
        Weaknesses: Higher latency and cost; requires Azure ecosystem
        Best when: Needing secure, reliable, high-performance AI for business-critical applications
    """),

    AZURE_MINI_CHAT("azureMiniChatModel", "chat", """
        Azure GPT-4.1 Mini – Lightweight, fast, cost-effective conversational model

        Model: gpt-4.1-mini
        Use for: High-throughput chatbots, FAQs, lightweight assistants, DevOps/CI pipelines, healthcare/finance/government apps
        Strengths: Fast responses, cost-efficient for frequent queries
        Weaknesses: Less capable in complex reasoning and structured output
        Best when: Handling simple, high-volume interactions with budget or latency constraints
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