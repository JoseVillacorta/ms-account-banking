package com.banking.ms_account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private int status;

    // Metodo estatico de ayudo para hacer el codigo mas limpio
    public static <T> ApiResponse<T> success(T data, String message, int status){
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status){
        return ApiResponse.<T>builder()
                .data(null)
                .message(message)
                .status(status)
                .build();
    }

}
