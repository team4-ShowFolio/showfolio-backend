package com.sec01.showfilo.DTO.response;

import com.sec01.showfilo.entity.UserTechStack;
import lombok.Getter;

@Getter
public class TechStackResponse {
    private Long id;
    private String techName;

    public TechStackResponse(UserTechStack userTechStack) {
        this.id = userTechStack.getId();
        this.techName = userTechStack.getTechName();
    }
}