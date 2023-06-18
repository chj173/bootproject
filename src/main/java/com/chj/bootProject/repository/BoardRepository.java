package com.chj.bootProject.repository;

import com.chj.bootProject.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // update board_table set board_hits=board_hits+1 where id=?
    @Modifying
    @Query(value = "UPDATE BoardEntity b SET b.boardHits=b.boardHits+1 WHERE b.id=:id")
    void updateHits(@Param("id") Long id);
}
