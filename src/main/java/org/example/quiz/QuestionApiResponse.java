package org.example.quiz;

public record QuestionApiResponse(
                                  String category,
                                  String id,
                                  String correctAnswer,
                                  String[] incorrectAnswers,
                                  QuestionApi question,
                                  String[] tags,
                                  String type,
                                  String difficulty,
                                  String[] regions,
                                  boolean isNiche
                                  ) {
}
