package dps924.assignment3.ui.main;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dps924.assignment3.QuizView;
import dps924.assignment3.R;

public class QuizFragment extends Fragment {

    private String m_QuestionText = "";
    private int m_QuestionColor = 0;

    public static QuizFragment newInstance() {
        return new QuizFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater l_Inflater, @Nullable ViewGroup l_Container, @Nullable Bundle l_savedInstanceState) {

        final View t_ThisView = l_Inflater.inflate(R.layout.quiz_fragment, l_Container, false);
        TextView t_QuestionBox = (TextView) t_ThisView.findViewById(R.id.quizQuestion);

        if (l_savedInstanceState != null) {
            m_QuestionText = l_savedInstanceState.getString(getString(R.string.bundle_question));
            m_QuestionColor = l_savedInstanceState.getInt(getString(R.string.bundle_color));
        }

        if (m_QuestionText == "")
            try {
                m_QuestionText = QuizView.DataManager.getCurrentQuestion();
                if (m_QuestionColor == 0)
                    m_QuestionColor = Color.argb(
                            255,
                            (byte) (Math.random() * 100),
                            (byte) (Math.random() * 100),
                            (byte) (Math.random() * 80)
                    );
            } catch (IndexOutOfBoundsException e) {
                m_QuestionText = "";
                m_QuestionColor = Color.argb(0, 255, 255, 255);
                AlertDialog t_AlertModal = QuizView.DataManager.showModal(
                    getString(R.string.quiz_complete_title),
                    getString(R.string.quiz_complete_description) + "\n" + e.getMessage(),
                    getString(R.string.quiz_save),
                    getString(R.string.quiz_complete_ignore),
                    tmp -> {QuizView.DataManager.saveLastResult();QuizView.updateQuiz();return null;},
                    tmp -> {QuizView.updateQuiz();return null;},
                    false
                );
            }
        t_QuestionBox.setText(m_QuestionText);
        t_QuestionBox.setBackgroundColor(m_QuestionColor);

        return t_ThisView;
    }

    @NonNull
    @Override
    public void onSaveInstanceState(Bundle r_SavedInstanceState) {
        r_SavedInstanceState.putString(getString(R.string.bundle_question), m_QuestionText);
        r_SavedInstanceState.putInt(getString(R.string.bundle_color), m_QuestionColor);
    }
}