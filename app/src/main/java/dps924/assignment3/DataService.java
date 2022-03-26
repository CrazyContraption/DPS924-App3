package dps924.assignment3;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.text.InputType;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DataService extends Service {

    private ArrayList<Question> m_UnreadQuestions;
    private ArrayList<Answer> m_ReadQuestions;
    private int m_MaxQuestions;

    private Result m_LastQuiz;

    public static ToastController m_Toaster;

    public DataService() {
        m_UnreadQuestions = new ArrayList<>();
        m_ReadQuestions = new ArrayList<>();
        m_LastQuiz = null;

        Resources t_Resources = QuizView.getContext().getResources();

        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_01), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_02), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_03), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_04), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_05), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_06), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_07), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_08), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_09), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_10), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_11), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_12), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_13), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_14), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_15), true));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_16), false));
        m_UnreadQuestions.add(new Question(t_Resources.getString(R.string.question_17), false));

        m_MaxQuestions = (int)(m_UnreadQuestions.size() / 2);

        shuffleQuestions();

        m_Toaster = new ToastController();
    }

    @Override
    public IBinder onBind(Intent intent) { throw new UnsupportedOperationException("Not yet implemented"); }

    public int getRemainingQuestions() {
        return m_MaxQuestions - getAnsweredQuestions();
    }

    public int getTotalQuestions() {
        return m_UnreadQuestions.size() + m_ReadQuestions.size();
    }

    public int getMaxQuestions() {
        return m_MaxQuestions;
    }

    public void askMaxQuestions() {
        Context t_Context = QuizView.getContext();
        Resources t_Resources = t_Context.getResources();
        int t_Capacity = getTotalQuestions();
        QuizView.DataManager.showModal(
            t_Resources.getString(R.string.quiz_update),
            t_Resources.getString(R.string.quiz_set_questions) + " " + t_Capacity,
            t_Resources.getString(R.string.quiz_save),
            null,
            tmp -> {
                int l_NewMax;
                try {
                    l_NewMax = Integer.parseInt(tmp);
                    m_MaxQuestions = (l_NewMax > t_Capacity ? t_Capacity : l_NewMax);
                    showSimpleModal(t_Resources.getString(R.string.success) + "!",t_Resources.getString(R.string.set_questions) + ": " + m_MaxQuestions,null);
                    QuizView.updateQuiz();
                } catch (Exception e) { }
                return null;
            },
            null,
            true
        );


    }

    public int getAnsweredQuestions() {
        return m_ReadQuestions.size();
    }

    public String getCurrentQuestion() throws IndexOutOfBoundsException {
        if (m_UnreadQuestions.size() > 0 && getRemainingQuestions() > 0)
            return m_UnreadQuestions.get(0).getQuestion();
        m_LastQuiz = new Result(m_ReadQuestions);
        shuffleQuestions();
        throw new IndexOutOfBoundsException(m_LastQuiz.getCorrectQuestions() + "/" + m_LastQuiz.getTotalQuestions());
    }

    public AlertDialog showSimpleModal(String l_Title, String l_Message, String l_OkayText) {
        return QuizView.DataManager.showModal(
            l_Title,
            l_Message,
            l_OkayText,
            "",
            null,
            null,
            false
        );
    }

    public AlertDialog showModal(String l_Title, String l_Message, String l_OkayText, String l_CancelText, Function<String,String> l_OkayFunction, Function<String,String> l_CancelFunction, boolean l_ProvideInput) {
        Context t_Context = QuizView.getContext();
        Resources t_Resources = t_Context.getResources();

        if (l_OkayFunction == null)
            l_OkayFunction = tmp -> null;

        if (l_CancelFunction == null)
            l_CancelFunction = tmp -> null;

        if (l_OkayText == null)
            l_OkayText = t_Resources.getString(R.string.button_okay);

        if (l_CancelText == null)
            l_CancelText = t_Resources.getString(R.string.button_cancel);

        // DecimalFormat f = new DecimalFormat("0.00");
        AlertDialog.Builder builder = new AlertDialog.Builder(t_Context);

        EditText input = null;
        if (l_ProvideInput) {
            input = new EditText(t_Context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            builder.setView(input);
        }
        EditText final_Input = input;

        Function<String, String> l_FinalOkayFunction = l_OkayFunction;

        builder.setPositiveButton(l_OkayText, (dialog, whichButton) -> {
            l_FinalOkayFunction.apply((final_Input == null ? null : String.valueOf(final_Input.getText())));
        });
        Function<String, String> l_FinalCancelFunction = l_CancelFunction;
        builder.setNegativeButton(l_CancelText, (dialog, whichButton) -> {
            l_FinalCancelFunction.apply(null);
        });
        builder.setOnDismissListener( (dialog) -> {
            l_FinalCancelFunction.apply(null);
        });

        builder.setMessage(l_Message).setTitle(l_Title);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public boolean submitCurrentQuestion(boolean l_Answer) {
        Question t_Question = m_UnreadQuestions.get(0);
        m_ReadQuestions.add(new Answer(m_UnreadQuestions.remove(0), l_Answer));
        return t_Question.compareAnswer(l_Answer);
    }

    private void shuffleQuestions() {
        while (m_ReadQuestions.size() > 0)
            m_UnreadQuestions.add(m_ReadQuestions.remove(0));
        Collections.shuffle(m_UnreadQuestions);
    }

    public String getOverallAverage() {
        ArrayList<Result> t_AllResults = getPreviousResults();
        AtomicInteger t_Correct = new AtomicInteger(0);
        AtomicInteger t_Questions = new AtomicInteger(0);
        if (t_AllResults != null)
            t_AllResults.forEach( element -> {
                t_Correct.addAndGet(element.getCorrectQuestions());
                t_Questions.addAndGet(element.getTotalQuestions());
            });
        String t_StringBuilder = t_Correct + "/" + t_Questions;
        if (t_Questions.intValue() > 0)
            t_StringBuilder += "\n" + (t_Correct.doubleValue() / t_Questions.doubleValue() * 100) + "%";
        return t_StringBuilder;
    }

    public void saveLastResult() {
        Context t_Context = QuizView.getContext();
        String filename = t_Context.getResources().getString(R.string.save_key);

        ArrayList<Result> t_AllResults = getPreviousResults();
        if (t_AllResults == null)
            t_AllResults = new ArrayList<>();

        t_AllResults.add(t_AllResults.size(),m_LastQuiz);

        try (FileOutputStream fos = t_Context.openFileOutput(filename, Context.MODE_PRIVATE);) {
            fos.write(convertToBytes(t_AllResults));
        } catch (FileNotFoundException e) {
            System.err.println("File missing... recreating as: " + filename);
            new File(t_Context.getFilesDir(), filename);
            saveLastResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Result> getPreviousResults() {
        Context t_Context = QuizView.getContext();
        String filename = t_Context.getResources().getString(R.string.save_key);

        try (FileInputStream fis = t_Context.openFileInput(filename)) {
            byte[] bytes = new byte[100000];
            fis.read(bytes);
            Object l_Data = convertFromBytes(bytes);
            if (l_Data.getClass() == ArrayList.class) {
                ArrayList<Result> t_AllResults = (ArrayList<Result>)l_Data;
                return t_AllResults;
            }
            else
                throw new IOException("'" + l_Data.getClass() + "' mismatched saved class type with expected type 'ArrayList<Result>'");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } catch (IOException e) {
            showSimpleModal(t_Context.getResources().getString(R.string.oops) + "...", t_Context.getResources().getString(R.string.corruption), null);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearSavedResults() {
        Context t_Context = QuizView.getContext();
        Resources t_Resources = t_Context.getResources();
        String filename = t_Resources.getString(R.string.save_key);
        if(new File(t_Context.getFilesDir(), filename).exists()) {
             QuizView.DataManager.showModal(
                 t_Context.getResources().getString(R.string.confirm_delete) + "?",
                 t_Context.getResources().getString(R.string.confirm_delete_prompt),
                 t_Context.getResources().getString(R.string.button_delete),
                null,
                tmp -> {
                    new File(t_Context.getFilesDir(), filename).delete();
                    return null;
                },
                null,
                false
            );
        } else
            showSimpleModal(t_Context.getResources().getString(R.string.oops) + "!", t_Context.getResources().getString(R.string.nothing_to_erase) + "!", null);
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.close();
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}