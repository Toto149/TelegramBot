package org.example.quiz;

import java.util.List;
import java.util.stream.Collectors;

public class Question {
    private String category;
    private String text;
    private List<String> options;
    private String solution;

    public Question(String category, String text, List<String> options, String solution) {
        this.category = category;
        this.text = text;
        this.options = options;
        this.solution = solution;
    }

    public String toString() {
        String opt = this.options.stream().collect(Collectors.joining("\n"));
        return this.category + "\n" + this.text + "\n" + opt;
    }
    public String getSolution(){
        return this.solution;
    }

    public List<String> getOptions(){
        return options;
    }
}
