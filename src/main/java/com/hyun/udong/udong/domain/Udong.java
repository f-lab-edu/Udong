package com.hyun.udong.udong.domain;

import com.hyun.udong.common.entity.BaseTimeEntity;
import com.hyun.udong.common.exception.InvalidParameterException;
import com.hyun.udong.travelschedule.domain.City;
import com.hyun.udong.udong.exception.InvalidParticipationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Udong extends BaseTimeEntity {
    private static final int MAX_WAITING_COUNT = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "udong_id")
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Embedded
    private Content content;

    @Embedded
    private RecruitPlanner recruitPlanner;

    @Embedded
    private TravelPlanner travelPlanner;

    @Embedded
    private AttachedTags attachedTags;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UdongStatus status;

    @OneToMany(mappedBy = "udong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelCity> travelCities = new ArrayList<>();

    @OneToMany(mappedBy = "udong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitingMember> waitingMembers;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int currentWaitingMemberCount;

    @Version
    private Long version;

    public Udong(Long ownerId,
                 String title,
                 String description,
                 int recruitmentCount,
                 LocalDate startDate,
                 LocalDate endDate,
                 UdongStatus status) {
        this.ownerId = ownerId;
        this.content = Content.of(title, description);
        this.recruitPlanner = RecruitPlanner.from(recruitmentCount);
        this.travelPlanner = TravelPlanner.of(startDate, endDate);
        this.status = status;
    }

    @Builder
    public Udong(Content content,
                 RecruitPlanner recruitPlanner,
                 TravelPlanner travelPlanner,
                 AttachedTags attachedTags,
                 Long ownerId) {
        if (ownerId == null) {
            throw new InvalidParameterException("ownerId는 필수값입니다.");
        }
        this.ownerId = ownerId;
        this.content = content;
        this.recruitPlanner = recruitPlanner;
        this.travelPlanner = travelPlanner;
        this.attachedTags = attachedTags;
        if (travelPlanner.getStartDate().isEqual(LocalDate.now())) {
            this.status = UdongStatus.IN_PROGRESS;
        } else {
            this.status = UdongStatus.PREPARE;
        }
    }

    Udong(Long memberId) {
        this.ownerId = memberId;
    }

    public void addCities(List<City> cities) {
        cities.forEach(this::addCity);
    }

    private void addCity(City city) {
        boolean isDuplicate = travelCities.stream()
                .anyMatch(travelCity -> travelCity.getCity().getId().equals(city.getId()));

        if (!isDuplicate) {
            TravelCity travelCity = new TravelCity(this, city);
            travelCities.add(travelCity);
        }
    }

    public void validateParticipation(Long memberId, int currentParticipantCount) {
        if (status != UdongStatus.PREPARE) {
            throw new InvalidParticipationException("여행이 시작되었거나 종료된 우동에는 참여할 수 없습니다.");
        }

        if (isOwner(memberId)) {
            throw new InvalidParticipationException("자신이 생성한 우동에는 참여할 수 없습니다.");
        }

        if (!recruitPlanner.isRecruitmentAvailable(currentParticipantCount)) {
            throw new InvalidParticipationException("모집 인원이 이미 다 찼습니다.");
        }
    }

    public void validateOwner(Long ownerId) {
        if (!isOwner(ownerId)) {
            throw new InvalidParticipationException("승인/거부할 권한이 없습니다.");
        }
    }

    public boolean isOwner(Long memberId) {
        return this.ownerId.equals(memberId);
    }

    public WaitingMember addWaitingMember(Long memberId) {
        if (this.waitingMembers.size() >= MAX_WAITING_COUNT) {
            throw new InvalidParticipationException("대기 인원이 초과되었습니다.");
        }
        WaitingMember waitingMember = WaitingMember.of(this, memberId);
        this.waitingMembers.add(waitingMember);
        currentWaitingMemberCount = this.waitingMembers.size();
        return waitingMember;
    }

    public WaitingMember addWaitingMember(Long memberId) {
        WaitingMember waitingMember = WaitingMember.of(this, memberId);
        this.waitingMembers.add(waitingMember);
        currentWaitingMemberCount = this.waitingMembers.size();
        return waitingMember;
    }

    @Override
    public String toString() {
        return "Udong{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", currentWaitingMemberCount=" + currentWaitingMemberCount +
                ", version=" + version +
                '}';
    }
}
