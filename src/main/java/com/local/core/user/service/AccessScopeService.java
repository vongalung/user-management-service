package com.local.core.user.service;

import com.local.core.user.config.UserManagementConfig;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.exception.ScopeGroupNotFoundException;
import com.local.core.user.model.AccessScope;
import com.local.core.user.model.ScopeGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccessScopeService {
    final ScopeGroupService scopeGroupService;
    final UserManagementConfig userManagementConfig;

    public Set<AccessScope> getDefault() throws BaseApplicationException {
        String groupName = userManagementConfig.getDefaultScopeGroup();
        if (groupName == null || groupName.isBlank()) {
            return Set.of();
        }
        return findByGroupName(groupName);
    }

    public Set<AccessScope> findByGroupName(String groupName) throws BaseApplicationException {
        return scopeGroupService.findByName(groupName)
                .map(ScopeGroup::getScopes)
                .orElseThrow(ScopeGroupNotFoundException::new);
    }
}
