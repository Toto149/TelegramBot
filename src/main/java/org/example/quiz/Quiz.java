package org.example.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Quiz {
    private final QuizApiService service = new QuizApiService();
    private List<Question> questionsList = new ArrayList<>();
    private int score = 0;
    private final Random r = new Random();

    public Quiz(){
        initQuestions();
    }

    private void initQuestions() {
        questionsList.addAll(service.getQuestions(5,Difficulty.EASY));
    }


    public Question getRandomQuestion() {
        if(questionsList.isEmpty()){
            if(score==5){
                questionsList.addAll(service.getQuestions(5,Difficulty.MEDIUM));
            }
            if(score==10){
                questionsList.addAll(service.getQuestions(5,Difficulty.HARD));
            }
            if(score>11){
                questionsList.addAll(service.getQuestions(10,Difficulty.HARD));
            }
        }
        int index = r.nextInt(0, this.questionsList.size());
        Question question = this.questionsList.get(index);
        this.questionsList.remove(index);
        return question;
    }

    public void increaseScore(){
        score++;
    }

    public int getScore(){
        return score;
    }
}
