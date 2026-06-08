package com.example.showfolio.repository;

import com.example.showfolio.dto.ProjectSearchCondition;
import com.example.showfolio.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    Page<Project> search(ProjectSearchCondition condition, Pageable pageable);
}
