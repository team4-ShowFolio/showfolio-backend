package com.example.showfolio.project.repository;

import com.example.showfolio.project.dto.ProjectSearchCondition;
import com.example.showfolio.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {
    Page<Project> search(ProjectSearchCondition condition, Pageable pageable);
}
