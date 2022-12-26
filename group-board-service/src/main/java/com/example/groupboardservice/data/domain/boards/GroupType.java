package com.example.groupboardservice.data.domain.boards;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class GroupType {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "group_type_seq")
    private Long groupTypeSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_seq")
    private Board board;

    // 그룹 이름
    @Column(name = "name")
    private String name;

    // 그룹원 총 수
    @Column(name = "total_count")
    private String totalCount;

    // 그룹 생성일
    @Column(name = "created_at")
    private Date createdAt;

    // 그룹 수정
    @Column(name = "updated_at")
    private Date updatedAt;
}
