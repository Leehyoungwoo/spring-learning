package com.springlearning.domain.chatgpt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springlearning.domain.chatgpt.dto.request.QuestionDto;
import com.springlearning.domain.chatgpt.dto.response.GptResponseDto;
import com.springlearning.global.openai.config.ChatGptConfig;
import com.springlearning.global.openai.prompt.Prompt;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GptService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public ChatCompletionResult generated(List<ChatMessage> chatMessages) {
        ChatCompletionRequest build = ChatCompletionRequest.builder()
                .messages(chatMessages)
                .model(ChatGptConfig.MODEL)
                .maxTokens(ChatGptConfig.MAX_TOKEN)
                .temperature(ChatGptConfig.TEMPERATURE)
                .topP(ChatGptConfig.TOP_P)
                .build();

        return openAiService.createChatCompletion(build);
    }

    public List<ChatMessage> generatedQuestionAndAnswerMessage(QuestionDto questionDto) {
        String prompt = Prompt.generateQuestionPrompt(questionDto.content());
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);

        return List.of(chatMessage);
    }

    public GptResponseDto askQuestion(QuestionDto questionDto) {
        List<ChatMessage> chatMessages = generatedQuestionAndAnswerMessage(questionDto);
        ChatCompletionResult result = generated(chatMessages);

        String gptAnswer = result.getChoices().get(0).getMessage().getContent();
        return GptResponseDto.builder()
                .answer(gptAnswer)
                .build();
    }
}
