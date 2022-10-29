package com.aw.assignment.memegene.meme;

import java.util.List;

public record CreateMemeInput(List<Caption> captionList, Long templateId, String color) {
}
