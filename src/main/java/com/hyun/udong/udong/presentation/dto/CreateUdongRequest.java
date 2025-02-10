package com.hyun.udong.udong.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUdongRequest {

    @NotEmpty(message = "도시를 1개 이상 선택해주세요.")
    private List<Long> cityIds;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 1, max = 100, message = "제목은 100자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = 1000, message = "내용은 1000자 이내로 작성해주세요.")
    private String description;

    @Min(value = 1, message = "모집 인원은 1명 이상이어야 합니다.")
    @Max(value = 10, message = "모집 인원은 최대 10명까지 가능합니다.")
    private int recruitmentCount;

    @NotNull(message = "시작일을 선택해주세요.")
    @FutureOrPresent(message = "시작일은 현재 또는 미래 날짜여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일을 선택해주세요.")
    @Future(message = "종료일은 미래 날짜여야 합니다.")
    private LocalDate endDate;

    @Size(max = 5, message = "태그는 최대 5개까지 선택 가능합니다.")
    private List<String> tags;
}
