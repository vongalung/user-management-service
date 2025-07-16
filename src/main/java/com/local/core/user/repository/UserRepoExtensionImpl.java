package com.local.core.user.repository;

import static com.local.core.user.repository.RepoExtensionHelper.createFieldOrdering;
import static com.local.core.user.repository.RepoExtensionHelper.likePattern;

import com.local.core.user.common.PaginationFactory;
import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.request.UserSearchRequestDto;
import com.local.core.user.model.User;
import com.local.core.user.model.UserDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
public class UserRepoExtensionImpl implements UserRepoExtension {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    RepoExtensionHelper repoExtensionHelper;

    @Override
    public Optional<User> findByUniqueIdentifier(NewUserRequestDto request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query = query.select(root)
                .where(createUniqueIdentityPredicate(cb, root, request));
        return entityManager.createQuery(query).getResultStream().findFirst();
    }

    Predicate createUniqueIdentityPredicate(CriteriaBuilder cb, Root<User> root,
                                            NewUserRequestDto request) {
        Join<UserDetail, User> userDetailJoin = root.join("details");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(cb.upper(root.get("userName")),
                request.userName().toUpperCase()));
        predicates.add(cb.equal(cb.upper(userDetailJoin.get("email")),
                request.email().toUpperCase()));

        if (request.phone() != null && !request.phone().isBlank()) {
            predicates.add(cb.equal(cb.upper(userDetailJoin.get("phone")),
                    request.phone().toUpperCase()));
        }
        return cb.or(predicates.toArray(new Predicate[0]));
    }

    @Override
    public PaginationFactory<User> findAllWithParameters(UserSearchRequestDto parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Join<User, UserDetail> detailJoin = root.join("details");

        query = query.select(root)
                .where(createWherePredicate(cb, root, detailJoin, parameters))
                .orderBy(createOrdering(cb, root));
        TypedQuery<User> fetched = entityManager.createQuery(query);

        Integer page = parameters.getPage();
        Integer limit = parameters.getPagesize();
        if (page != null && limit != null) {
            Long count = repoExtensionHelper.countTotal(User.class, cb,
                    (c, countRoot) -> createWherePredicate(
                            c, countRoot, countRoot.join("details"),
                            parameters));
            Pageable pageable = PageRequest.of(page, limit);
            Stream<User> stream = fetched
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultStream();
            return new PaginationFactory<>(pageable, count, stream);
        }
        Stream<User> stream = fetched.getResultStream();
        return new PaginationFactory<>(null, null, stream);
    }

    Predicate createWherePredicate(CriteriaBuilder cb, Root<User> root,
                                   Join<User, UserDetail> detailJoin,
                                   UserSearchRequestDto parameters) {
        List<Predicate> predicates = new ArrayList<>(
                genericSearch(cb, root, parameters));
        predicates.add(simpleSearch(cb, root, detailJoin, parameters));
        predicates.addAll(advancedSearch(cb, root, detailJoin, parameters));
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    List<Predicate> genericSearch(CriteriaBuilder cb, Root<User> root,
                                  UserSearchRequestDto parameters) {
        List<Predicate> predicates = new ArrayList<>();

        Boolean isActive = parameters.getIsActive();
        if (Boolean.TRUE.equals(isActive)) {
            predicates.add(cb.isNull(root.get("inactiveSince")));
        }
        if (Boolean.FALSE.equals(isActive)) {
            predicates.add(cb.isNotNull(root.get("inactiveSince")));
        }

        if (parameters.getJoinedSinceStart() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                    root.get("createdDate"), parameters.getJoinedSinceStart()));
        }
        if (parameters.getJoinedSinceEnd() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                    root.get("createdDate"), parameters.getJoinedSinceEnd()));
        }

        return predicates;
    }

    Predicate simpleSearch(CriteriaBuilder cb, Root<User> root,
                           Join<User, UserDetail> detailJoin,
                           UserSearchRequestDto parameters) {
        String search = parameters.getSearch();
        if (search == null) {
            return cb.and();
        }
        return cb.or(List.of(
                userNamePredicate(cb, root, search),
                fullNamePredicate(cb, detailJoin, search),
                emailPredicate(cb, detailJoin, search),
                phonePredicate(cb, detailJoin, search))
                .toArray(new Predicate[0]));
    }

    List<Predicate> advancedSearch(CriteriaBuilder cb, Root<User> root,
                                   Join<User, UserDetail> detailJoin,
                                   UserSearchRequestDto parameters) {
        List<Predicate> predicates = new ArrayList<>();

        String userName = parameters.getUserName();
        if (userName != null) {
            predicates.add(userNamePredicate(cb, root, userName));
        }
        String name = parameters.getName();
        if (name != null) {
            predicates.add(fullNamePredicate(cb, detailJoin, name));
        }
        String email = parameters.getEmail();
        if (email != null) {
            predicates.add(emailPredicate(cb, detailJoin, email));
        }
        String phone = parameters.getPhone();
        if (phone != null) {
            predicates.add(phonePredicate(cb, detailJoin, phone));
        }

        return predicates;
    }

    Predicate userNamePredicate(CriteriaBuilder cb, Root<User> root, String userName) {
        return cb.like(cb.upper(root.get("userName")),
                likePattern(userName.toUpperCase()));
    }

    Predicate fullNamePredicate(CriteriaBuilder cb, Join<User, UserDetail> detailJoin,
                                String name) {
        return cb.like(cb.upper(detailJoin.get("fullName")),
                likePattern(name.toUpperCase()));
    }

    Predicate emailPredicate(CriteriaBuilder cb, Join<User, UserDetail> detailJoin,
                             String email) {
        return cb.like(cb.upper(detailJoin.get("email")),
                likePattern(email.toUpperCase()));
    }

    Predicate phonePredicate(CriteriaBuilder cb, Join<User, UserDetail> detailJoin,
                             String phone) {
        return cb.like(cb.upper(detailJoin.get("phone")),
                likePattern(phone.toUpperCase()));
    }

    List<Order> createOrdering(CriteriaBuilder cb, Root<User> root) {
        List<Order> ordering = new ArrayList<>();
        ordering.add(createFieldOrdering(
                cb, root.get("userName"), Sort.Direction.ASC));
        return ordering;
    }
}
