package com.example.SmartEcommercePlatform.Dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PaginatedResponse<T> implements Serializable {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}