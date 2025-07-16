package com.local.core.user.service;

import com.local.core.user.model.ScopeGroup;
import com.local.core.user.repository.ScopeGroupRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScopeGroupService {
    final ScopeGroupRepo scopeGroupRepo;

    public Optional<ScopeGroup> findByName(String name) {
        return scopeGroupRepo.findByName(name);
    }
}
