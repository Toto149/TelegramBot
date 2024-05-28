package org.example.quiz;

import java.util.ArrayList;
import java.util.HashMap;
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

    public String getMoneyWon(){
        HashMap<Integer,String> scoreToMoney = new HashMap<>();
        scoreToMoney.put(1,"50€");
        scoreToMoney.put(2,"100€");
        scoreToMoney.put(3,"200€");
        scoreToMoney.put(4,"300€");
        scoreToMoney.put(5,"500€");
        scoreToMoney.put(6,"1.000€");
        scoreToMoney.put(7,"2.000€");
        scoreToMoney.put(8,"4.000€");
        scoreToMoney.put(9,"8.000€");
        scoreToMoney.put(10,"16.000€");
        scoreToMoney.put(11,"32.000€");
        scoreToMoney.put(12,"64.000€");
        scoreToMoney.put(13,"125.000€");
        scoreToMoney.put(14,"512.000€");
        scoreToMoney.put(15,"1.000.000€");

        if(score<16) {
            return scoreToMoney.get(this.score);
        } else {
            int result =  1_000_000 * (int) Math.pow(2,score-15);
            return String.valueOf(result) +"€";
        }
    }
}
