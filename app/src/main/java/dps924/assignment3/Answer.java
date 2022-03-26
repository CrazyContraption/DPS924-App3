package dps924.assignment3;

import java.io.Serializable;
import java.util.ArrayList;

public class Answer extends Question implements Serializable {
    public boolean m_Response;

    public Answer(Question l_Question, boolean l_Answer) {
        super(l_Question.m_Question, l_Question.m_Answer);
        m_Response = l_Answer;
    }

    public Answer(Answer l_Answer) {
        super(l_Answer.m_Question, l_Answer.m_Answer);
        m_Response = l_Answer.m_Response;
    }

    public String getQuestion() {
        return m_Question;
    }

    public boolean wasCorrect() {
        return m_Answer == m_Response;
    }
}
