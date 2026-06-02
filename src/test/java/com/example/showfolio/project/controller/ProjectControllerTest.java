package com.example.showfolio.project.controller;

import com.example.showfolio.project.dto.ProjectCreateRequest;
import com.example.showfolio.project.dto.ProjectLikeResponse;
import com.example.showfolio.project.dto.ProjectResponse;
import com.example.showfolio.project.entity.Visibility;
import com.example.showfolio.project.exception.ProjectAccessDeniedException;
import com.example.showfolio.project.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser(username = "7")   // 인증된 사용자 memberId = 7
class ProjectControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean ProjectService projectService;   // Boot 4: @MockBean → @MockitoBean

    private ProjectResponse sample() {
        return new ProjectResponse(
                1L, 7L, "포트폴리오", "설명", "https://gh", "https://deploy",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 5, 1),
                true, 4, "백엔드", Visibility.PUBLIC, 10, 3,
                LocalDateTime.now(), LocalDateTime.now(),
                List.of("Spring", "QueryDSL"));
    }

    @Test
    void 프로젝트_등록_201() throws Exception {
        given(projectService.create(eq(7L), any())).willReturn(sample());

        String body = """
                {
                  "title":"포트폴리오","description":"설명",
                  "githubUrl":"https://gh","deployUrl":"https://deploy",
                  "startDate":"2026-03-01","endDate":"2026-05-01",
                  "isTeam":true,"teamSize":4,"myRole":"백엔드","visibility":"PUBLIC"
                }
                """;

        mockMvc.perform(post("/api/projects").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("포트폴리오"));

        verify(projectService).create(eq(7L), any());
    }


    @Test
    void 제목_누락시_400() throws Exception {
        String body = "{\"title\":\"\",\"visibility\":\"PUBLIC\"}";
        mockMvc.perform(post("/api/projects").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 단건조회_200() throws Exception {
        given(projectService.get(1L, 7L)).willReturn(sample());
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewCount").value(10));
    }

    @Test
    void 검색_200() throws Exception {
        given(projectService.search(any(), any(), any(), any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(sample())));
        mockMvc.perform(get("/api/projects/search")
                        .param("keyword", "포트").param("author", "jinwook").param("sort", "likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void 좋아요_토글_200() throws Exception {
        given(projectService.toggleLike(1L, 7L)).willReturn(new ProjectLikeResponse(true, 11));
        mockMvc.perform(post("/api/projects/1/likes").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(11));
    }

    @Test
    void 삭제_204() throws Exception {
        mockMvc.perform(delete("/api/projects/1").with(csrf()))
                .andExpect(status().isNoContent());
        verify(projectService).delete(1L, 7L);
    }

    @Test
    void PRIVATE_타인조회시_403() throws Exception {
        given(projectService.get(1L, 7L)).willThrow(new ProjectAccessDeniedException());
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isForbidden());
    }
}
