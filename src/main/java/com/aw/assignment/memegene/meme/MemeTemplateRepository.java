package com.aw.assignment.memegene.meme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemeTemplateRepository extends JpaRepository<MemeTemplateEntity, Long> {
    List<TestRecord> findAllProjectedBy();
}
