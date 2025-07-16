package com.local.core.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RepoExtensionHelper {
    final EntityManager entityManager;

    public static String likePattern(String text) {
        return "%" + text + "%";
    }

    public static Order createFieldOrdering(CriteriaBuilder cb, Expression<?> field,
                                            Direction direction) {
        if (direction == Direction.ASC) {
            return cb.asc(field);
        }
        return cb.desc(field);
    }

    public <E> Long countTotalDistinct(Class<E> rootClass, CriteriaBuilder cb,
                                       Function<Root<E>, Expression<?>> criteriaProvider,
                                       BiFunction<CriteriaBuilder, Root<E>, Predicate> predicateProvider) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<E> countRoot = countQuery.from(rootClass);
        countQuery = countQuery.select(cb.countDistinct(criteriaProvider.apply(countRoot)))
                .where(predicateProvider.apply(cb, countRoot));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public <E> Long countTotal(Class<E> rootClass, CriteriaBuilder cb,
                               BiFunction<CriteriaBuilder, Root<E>, Predicate> predicateProvider) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<E> countRoot = countQuery.from(rootClass);
        countQuery = countQuery.select(cb.count(countRoot))
                .where(predicateProvider.apply(cb, countRoot));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
