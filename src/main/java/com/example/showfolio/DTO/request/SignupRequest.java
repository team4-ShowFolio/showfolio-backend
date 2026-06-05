package com.example.showfolio.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다")
    @Size(min = 4, max = 255, message = "비밀번호는 4자 이상 255자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다")
    @Size(max = 50, message = "닉네임은 50자를 초과할 수 없습니다")
    private String nickname;

    private String profileImage;

    @Size(max = 500, message = "소개글은 500자를 초과할 수 없습니다")
    private String bio;
}