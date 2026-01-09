package com.example.growingstudy.session.entity;

import com.example.growingstudy.session.dto.SessionInfoRequest;
import com.example.growingstudy.session.dto.SessionInfoResponse;
import com.example.growingstudy.studygroup.entity.StudyGroup;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "session", uniqueConstraints = @UniqueConstraint(columnNames={"group_id","session_order"}))
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id BIGINT (PK)

    @Column(name = "session_order", nullable = false)
    private Integer sessionOrder;

    @Column(name = "title", length = 150)
    private String title; // title VARCHAR(150)

    //필요하면 startDay로 이름 변경
    @Column(name = "start_time")
    private LocalDate startTime; // start_time DATETIME

    @Column(name = "end_time")
    private LocalDate endTime; // end_time DATETIME

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup group; // group_id BIGINT (FK)

    private Session(Integer sessionOrder, String title, LocalDate startTime, LocalDate endTime, StudyGroup group) {
        this.sessionOrder = sessionOrder;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.group = group;
    }

    public static Session of(Integer sessionOrder, String title, LocalDate startTime, LocalDate endTime, StudyGroup group) {
        return new Session(sessionOrder, title, startTime, endTime, group);
    }

//    public static Session createFirst(LocalDate startTime, LocalDate endTime, StudyGroup group) {
//        return new Session(1, null , startTime, endTime, group);
//    }
//
//    public void update(SessionInfoRequest request, StudyGroup group) {
//                this.sessionOrder = request.sessionOrder();
//                this.title = request.title();
//                this.startTime = request.startTime();
//                this.endTime = request.endTime();
//                this.group = group;
//    }
}