package com.james.spring_security_configuration.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
    private boolean requestSuccessful;
    private String responseMessage;
    private T responseBody;

    public ApiResponse(boolean requestSuccessful, String responseMessage) {
        this.requestSuccessful = requestSuccessful;
        this.responseMessage = responseMessage;
    }
}
