package com.example.showfolio.port;

import com.example.showfolio.enums.MemberStatus;

public interface MemberStatusUpdater {

    void updateStatus(Long userId, MemberStatus status);
}
