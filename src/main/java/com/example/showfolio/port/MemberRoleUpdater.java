package com.example.showfolio.port;

import com.example.showfolio.enums.Role;

public interface MemberRoleUpdater {

    void updateRole(Long userId, Role role);

}
