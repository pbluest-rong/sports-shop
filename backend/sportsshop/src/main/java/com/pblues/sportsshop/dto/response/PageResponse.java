package com.pblues.sportsshop.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public <T> PageResponse<T> mapPage(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
