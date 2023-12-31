package com.sba.recordingserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseDataDto<T> {
    private String message;
    private int status;
    private T data;
    public ResponseDataDto(String message, int status, T data)
    {
        this.message = message;
        this.status = status;
        this.data = data;
    }

}
