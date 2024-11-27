package com.iamdvh.UserService.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int page;
    int sizePage;
    int pageTotal;
    @Builder.Default
    List<T> data = Collections.emptyList();
}
