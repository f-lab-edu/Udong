package com.hyun.udong.udong.infrastructure.repository;

import com.hyun.udong.common.config.QueryDslConfig;
import com.hyun.udong.travelschedule.infrastructure.repository.CityRepository;
import com.hyun.udong.udong.domain.*;
import com.hyun.udong.udong.presentation.dto.FindUdongsCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@Import(QueryDslConfig.class)
class UdongRepositoryImplTest {

    public static final LocalDate START_DATE = LocalDate.of(2025, 12, 1);
    public static final LocalDate END_DATE = LocalDate.of(2025, 12, 5);
    public static final List<Long> CITR_ID_LIST = List.of(1L, 2L);

    @Autowired
    private UdongRepository udongRepository;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        Content content = Content.of("여행 동행 모집", "즐겁게 여행할 동행을 구합니다.");
        RecruitPlanner recruitPlanner = RecruitPlanner.from(5);
        TravelPlanner travelPlanner = TravelPlanner.of(START_DATE, END_DATE);
        AttachedTags attachedTags = AttachedTags.of(List.of("자연", "배낭여행"));
        Udong udong = Udong.builder()
                .content(content)
                .recruitPlanner(recruitPlanner)
                .travelPlanner(travelPlanner)
                .attachedTags(attachedTags)
                .ownerId(1L)
                .build();
        udong.addCities(cityRepository.findAllById(CITR_ID_LIST));
        udongRepository.save(udong);
    }

    @Test
    void 검색조건_없이_모든_우동을_조회한다() {
        FindUdongsCondition searchCondition = new FindUdongsCondition(null, null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<Udong> result = udongRepository.findByFilter(searchCondition, pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent().getTitle()).isEqualTo("여행 동행 모집");
        List<TravelCity> travelCities = result.getContent().get(0).getTravelCities();
        assertThat(travelCities).hasSize(2);
        assertThat(travelCities.get(0).getCity().getId()).isEqualTo(1L);
        assertThat(travelCities.get(1).getCity().getId()).isEqualTo(2L);
    }

    @Test
    void 조건을_검색하면_해당_우동만_반환한다() {
        FindUdongsCondition searchCondition = new FindUdongsCondition(1L, CITR_ID_LIST, START_DATE, END_DATE, null);
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<Udong> result = udongRepository.findByFilter(searchCondition, pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        List<TravelCity> travelCities = result.getContent().get(0).getTravelCities();
        assertThat(travelCities).hasSize(2);
        assertThat(travelCities.get(0).getCity().getId()).isEqualTo(1L);
        assertThat(travelCities.get(1).getCity().getId()).isEqualTo(2L);
    }
}
