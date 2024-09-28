package com.example.quizmaster.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Pageable {

    private int size;
    private int page;
    private int totalPage;
    private long totalElements;
    private Object data;
}
