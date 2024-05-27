package org.example.quiz;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.util.Objects.requireNonNull;


public class QuizApiService {
    private final Random r = new Random();
    public List<QuestionApiResponse> getQuestionApiResponse(int amount){
        String baseUrl = "https://the-trivia-api.com/v2/questions/";
        List<QuestionApiResponse> response = requireNonNull( WebClient.create()
                .get()
                .uri(baseUrl +"?limit=" + amount)
                .retrieve()
                .bodyToFlux(QuestionApiResponse.class)
                .collectList()
                .block());
        if(requireNonNull(response).isEmpty()){
            throw new NoSuchElementException();
        }
        return response;
    }

    public List<QuestionApiResponse> getQuestionApiResponse(int amount, Difficulty difficulty){
        String baseUrl = "https://the-trivia-api.com/v2/questions/";
        List<QuestionApiResponse> response = requireNonNull(WebClient.create()
                .get()
                .uri(baseUrl + "?limit=" + amount +"&difficulties=" +difficulty.toString().toLowerCase())
                .retrieve()
                .bodyToFlux(QuestionApiResponse.class)
                .collectList()
                .block());
        if(requireNonNull(response).isEmpty()){
            throw new NoSuchElementException();
        }
        return response;
    }

    public List<Question> getQuestions(int amount){
        List<QuestionApiResponse> apiResponseList = this.getQuestionApiResponse(amount);

       return apiResponseList.stream()
                .map(q -> {
                    List<String> listOptionsFixedSolutionPlace = new ArrayList<>();
                    listOptionsFixedSolutionPlace.add(q.correctAnswer());
                    Collections.addAll(listOptionsFixedSolutionPlace, q.incorrectAnswers());
                    Set<String> optionsSet = new HashSet<>();
                    while(optionsSet.isEmpty() || optionsSet.size()<4){
                        int index = r.nextInt(0,4);
                        optionsSet.add(listOptionsFixedSolutionPlace.get(index));
                    }
                    return new Question(q.category(),
                            q.question().text(),
                            optionsSet.stream().toList(),
                            q.correctAnswer());
                })
                .toList();
    }

    public List<Question> getQuestions(int amount, Difficulty difficulty){
        List<QuestionApiResponse> apiResponseList = this.getQuestionApiResponse(amount,difficulty);

        System.out.println("New Questions Recuested");

        return apiResponseList.stream()
                .map(q -> {
                    List<String> listOptionsFixedSolutionPlace = new ArrayList<>();
                    listOptionsFixedSolutionPlace.add(q.correctAnswer());
                    Collections.addAll(listOptionsFixedSolutionPlace, q.incorrectAnswers());
                    Set<String> optionsSet = new HashSet<>();

                    while(optionsSet.isEmpty() || optionsSet.size()<4){
                        int index = r.nextInt(0,4);
                        optionsSet.add(listOptionsFixedSolutionPlace.get(index));
                    }
                    return new Question(q.category()
                            ,q.question().text()
                            ,optionsSet.stream().toList()
                            ,q.correctAnswer());
                })
                .toList();
    }
}
