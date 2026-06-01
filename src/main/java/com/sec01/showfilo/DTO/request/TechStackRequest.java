package com.sec01.showfilo.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TechStackRequest {
    private List<String> techNames;
}