package com.example.showfolio.dto.response;

import com.example.showfolio.entity.UserTechStack;
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