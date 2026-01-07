package com.example.growingstudy.groupsub.entity;

import com.example.growingstudy.session.entity.Session;
import com.example.growingstudy.studygroup.entity.GroupMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "study_time",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "session_id"})
        }
)
public class StudyTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private GroupMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    private StudyTime (LocalDateTime createdAt, GroupMember member, Session session) {
        this.totalTime = 0;
        this.updatedAt = createdAt; // 장
        this.member = member;
        this.session = session;
    }

    public static StudyTime create(LocalDateTime createdAt, GroupMember member, Session session) {
        return new StudyTime(createdAt, member, session);
    }

    public void addMinutes(Integer time, LocalDateTime createdAt) {
        this.totalTime += time;
        this.updatedAt = createdAt;
    }
}
