package com.chj.bootProject.service;

import com.chj.bootProject.dto.BoardDTO;
import com.chj.bootProject.entity.BoardEntity;
import com.chj.bootProject.entity.BoardFileEntity;
import com.chj.bootProject.repository.BoardFileRepository;
import com.chj.bootProject.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    // 글쓰기
    public void save(BoardDTO boardDTO) throws IOException {
        // DB에 저장 >> Entity 접근
        // DTO -> Entity
        // 파일 첨부 여부에 따라 로직 분리
        if (boardDTO.getBoardFile().isEmpty()) {
            // 첨부파일이 없으면
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        } else {
            // 첨부파일이 있다면
            // 다중 파일 첨부 ( 부모 데이터가 먼저 저장 되야함 )
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();
            BoardEntity board = boardRepository.findById(savedId).get();
            for (MultipartFile boardFile : boardDTO.getBoardFile()) {
                String originalFilename = boardFile.getOriginalFilename();
                String storedFilename = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "C:\\springboot_img\\" + storedFilename;
                boardFile.transferTo(new File(savePath));

                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFilename);
                boardFileRepository.save(boardFileEntity);
            }

//            /*
//                단일 파일 첨부
//                1. DTO에 담긴 파일 꺼냄
//                2. 파일의 이름 가져옴
//                3. 서버 저장용 이름 만듦 // 사진.jpg => 23423432_사진.jpg
//                4. 저장 경로 설정
//                5. 해당 경로에 파일 저장
//                6. board_table에 해당 데이터 save 처리
//                7. board_file_table에 해당 데이터 save 처리
//             */
//            MultipartFile boardFile = boardDTO.getBoardFile(); // 1
//            String originalFilename = boardFile.getOriginalFilename(); // 2
//            String storedFilename = System.currentTimeMillis() + "_" + originalFilename; // 3
//            String savePath = "C:\\springboot_img\\" + storedFilename; // 4
//            boardFile.transferTo(new File(savePath)); // 5
//            // 6 board_table에 첨부 파일 유무를 저장하고 id 가져옴
//            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
//            Long savedId = boardRepository.save(boardEntity).getId();
//            BoardEntity board = boardRepository.findById(savedId).get();
//
//            // 7 board_file_table에 파일 데이터 저장
//            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFilename);
//            boardFileRepository.save(boardFileEntity);

        }
    }

    // 전체목록
    @Transactional
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
    @Transactional
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

    // 글삭제
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    // 페이징처리
    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 3; // 한 페이지에 보여줄 글 갯수
        // 한페이지당 3개씩 , 정렬기준 id값 내림차순
        // page 위치 기본값은 0
        Page<BoardEntity> boardEntities =
                boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 목록 : id, writer, title, hits, createdTime
        // map(entity -> boardDTO)로 변환시 boardEntities 메서드 기능 가져옴
        Page<BoardDTO> boardDTOS =
                boardEntities.map(board -> new BoardDTO(
                board.getId(),
                board.getBoardWriter(),
                board.getBoardTitle(),
                board.getBoardHits(),
                board.getCreatedTime()
        ));
        return boardDTOS;
    }
}
