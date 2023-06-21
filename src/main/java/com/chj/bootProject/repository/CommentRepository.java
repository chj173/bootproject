package com.chj.bootProject.repository;

import com.chj.bootProject.entity.BoardEntity;
import com.chj.bootProject.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // SELECT * FROM comment_table WHERE board_id=? ORDER BY id DESC;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);
}
