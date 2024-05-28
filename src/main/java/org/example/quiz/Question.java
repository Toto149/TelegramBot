package org.example.quiz;

import java.util.List;

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
        StringBuilder optionsString = new StringBuilder("");
        for(int i=0; i<options.size(); i++){
            optionsString.append(Character.valueOf((char)((char) 65+i)))
                    .append( ") " )
                    .append(options.get(i))
                    .append("\n");
        }
        return this.text + "\n\n"
                            + optionsString.toString();
    }

    public String getSolution(){
        return this.solution;
    }

    public List<String> getOptions(){
        return options;
    }

}
