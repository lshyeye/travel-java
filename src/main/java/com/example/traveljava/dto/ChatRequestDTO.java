package com.example.traveljava.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatRequestDTO {
    @NotBlank(message = "消息不能为空")

    private String message;
}
