package com.local.core.user.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record PaginationFactory<D>(Pageable pageable, Long count, Stream<D> stream) {
    public <R> PaginationFactory<R> replacePagination(Stream<R> stream) {
        return new PaginationFactory<>(pageable, count, stream);
    }

    public Page<D> finalizePage() {
        List<D> result = stream.collect(Collectors.toList());
        if (pageable == null) {
            return new PageImpl<>(result);
        }
        return new PageImpl<>(result, pageable, count);
    }
}
