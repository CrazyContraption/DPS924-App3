package dps924.assignment3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import dps924.assignment3.ui.main.QuizFragment;

public class QuizView extends AppCompatActivity {

    public static DataService DataManager;
    private static QuizView m_QuizView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_QuizView = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view);
        if (savedInstanceState == null)
            DataManager = new DataService();
        updateQuiz();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context t_Context = m_QuizView.getContext();
        Resources t_Resources = t_Context.getResources();

        switch (item.getItemId()) {
            case R.id.get_average:
                QuizView.DataManager.showSimpleModal("Previous Results", QuizView.DataManager.getOverallAverage(), null);
                return true;
            case R.id.set_questions:
                QuizView.DataManager.askMaxQuestions();
                QuizView.updateQuiz();
                return true;
            case R.id.reset_results:
                QuizView.DataManager.clearSavedResults();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void updateQuiz() {
        ProgressBar t_ProgressBar = m_QuizView.findViewById(R.id.progressBar);
        t_ProgressBar.setMax(QuizView.DataManager.getMaxQuestions());
        t_ProgressBar.setProgress(QuizView.DataManager.getAnsweredQuestions());
        Fragment r_NewFragment = new QuizFragment();
        m_QuizView.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, r_NewFragment)
                .commitNow();
    }

    public static Context getContext() {
        return m_QuizView;
    }

    public void handleInput(View l_View) {
        if (l_View.getId() == R.id.none)
            return;
        Button l_Button = (Button)l_View;
        boolean success = false;
        switch (l_Button.getText().toString().toLowerCase()) {
            case "true":
            case "vero":
                success = QuizView.DataManager.submitCurrentQuestion(true);
                break;
            case "false":
            case "falso":
                success = QuizView.DataManager.submitCurrentQuestion(false);
            break;
            default:
                System.err.println("INVALID BUTTON TEXT: " + l_Button.getText().toString().toLowerCase());
                QuizView.DataManager.m_Toaster.showToast(this, "ERROR!");
                break;
        }
        QuizView.DataManager.m_Toaster.showToast(this, (success ? getString(R.string.quiz_correct) : getString(R.string.quiz_incorrect)));
        updateQuiz();
    }
}