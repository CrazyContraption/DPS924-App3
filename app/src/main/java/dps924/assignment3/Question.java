package dps924.assignment3;

import java.io.Serializable;

public class Question implements Serializable {
    protected String m_Question;
    protected boolean m_Answer;

    public Question (String l_Question, boolean l_Answer) {
        m_Question = l_Question;
        m_Answer = l_Answer;
    }

    public String getQuestion() {
        return m_Question;
    }

    public boolean compareAnswer(boolean l_Answer) {
        return l_Answer == m_Answer;
    }
}
