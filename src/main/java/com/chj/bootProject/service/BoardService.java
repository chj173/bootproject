package com.chj.bootProject.service;

import com.chj.bootProject.dto.BoardDTO;
import com.chj.bootProject.entity.BoardEntity;
import com.chj.bootProject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    // 글쓰기
    public void save(BoardDTO boardDTO) {
        // DB에 저장 >> Entity 접근
        // DTO -> Entity
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        boardRepository.save(boardEntity);
    }

    // 전체목록
    public List<BoardDTO> findAll() {
        // DB에서 가져옴 >> DTO 접근
        // Entity -> DTO
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    // 조회수
    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    // id를 이용한 정보
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        } else {
            return null;
        }
    }

    // 글수정
    public BoardDTO update(BoardDTO boardDTO) {
        // form -> controller -> DTO -> service -> Entity -> Repository
        // DTO -> Entity
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}
