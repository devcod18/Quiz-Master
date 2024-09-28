package com.example.quizmaster.payload;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private String message;
    private HttpStatus code;
    private Object data;

    public ApiResponse(String message, HttpStatus code) {
        this.message = message;
        this.code = code;
    }

    public ApiResponse(Object data) {
        this.data = data;
    }
}
