package com.aw.assignment.memegene.meme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemeRepository extends JpaRepository<MemeEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT id FROM MEME_ENTITY ORDER BY id DESC LIMIT 1")
    Integer findGreatestId();

    MemeEntity findFirstByHash(int hash);
}
