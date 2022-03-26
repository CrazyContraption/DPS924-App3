package dps924.assignment3;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {
    private ArrayList<Answer> m_Responses;

    public Result(ArrayList<Answer> l_Responses) {
        if (m_Responses != null)
            m_Responses.clear();
        else
            m_Responses = new ArrayList<Answer>();

        l_Responses.forEach(element -> {
            m_Responses.add(m_Responses.size(), new Answer(element));
        });
    }

    public int getTotalQuestions() {
        return m_Responses.size();
    }

    public int getCorrectQuestions() {
        return (int) m_Responses.stream().filter(Answer::wasCorrect).count();
    }
}
