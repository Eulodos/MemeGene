package com.aw.assignment.memegene.meme;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemeRepository extends JpaRepository<MemeEntity, Long> {
}
