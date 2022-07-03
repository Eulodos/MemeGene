package com.aw.assignment.memegene.meme;

import java.util.List;

public record CreateMemeInput(List<Captions> captionsList, Long templateId) {
}
