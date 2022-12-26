package com.example.groupboardservice.data.domain.boards;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "board_seq")
    private Long boardSeq;

    @OneToMany(mappedBy = "board")
    private List<GroupType> groupType = new ArrayList<>();

    // 제목
    @Column
    private String title;

    // 내용
    @Column
    private String contents;

    // 조회수
    @Column
    private String views;

    // 생성일
    @Column(name = "created_at")
    private Date createdAt;

    // 수정일
    @Column(name = "updated_at")
    private Date updatedAt;
}
