package com.mastercoding.thequizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mastercoding.thequizapp.databinding.ActivityMainBinding;
import com.mastercoding.thequizapp.model.Question;
import com.mastercoding.thequizapp.model.QuestionList;
import com.mastercoding.thequizapp.viewmodel.QuizViewModel;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private static final long START_TIME_IN_MILLIS = 10000; // 10 giây
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

   ActivityMainBinding binding;
   QuizViewModel quizViewModel;
   List<Question> questionList;

   static int result = 0;
   static int totalQuestions = 0;
   int  i =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Data Binding
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );

        // Resetting the Scores:
        result = 0;
        totalQuestions = 0;

        // Creating an instance of 'QuizViewModel'
        quizViewModel = new ViewModelProvider(this)
                .get(QuizViewModel.class);

        // Displaying the First Question:
        DisplayFirstQuestion();
        startTimer();

    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                updateProgressBar();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                // Tự động chuyển sang câu hỏi tiếp theo
                DisplayNextQuestions();
            }
        }.start();
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        binding.timerTextView.setText(timeFormatted);
    }
    private void updateProgressBar() {
        int progress = (int) ((timeLeftInMillis / (double) START_TIME_IN_MILLIS) * 100);
        binding.circularProgressBar.setProgress(progress);
    }

    public void DisplayFirstQuestion(){
        // Observing LiveData from a ViewModel
        quizViewModel.getQuestionListLiveData().observe(
                this,
                new Observer<QuestionList>() {
                    @Override
                    public void onChanged(QuestionList questions) {
                        // Called when the data inside LIVEDATA changes
                        questionList = questions;

                        binding.txtQuestion.setText("Question 1: "+questions.get(0).getQuestion());
                        binding.radio1.setText(questions.get(0).getOption1());
                        binding.radio2.setText(questions.get(0).getOption2());
                        binding.radio3.setText(questions.get(0).getOption3());
                    }
                }
        );
    }

    public void DisplayNextQuestions(){

        totalQuestions = questionList.size();

        // Displaying the question
        int selectedOption = binding.radioGroup.getCheckedRadioButtonId();
        boolean isAnswered = selectedOption != -1;

        if (isAnswered) {
            RadioButton radioButton = findViewById(selectedOption);

            // Kiểm tra nếu tùy chọn đã chọn là đúng
            if (radioButton.getText().toString().equals(questionList.get(i).getCorrectOption())) {
                result++;
                Log.d("CorrectResult", "Correct Result: " + questionList.get(i).getCorrectOption());
                binding.txtResult.setText("Correct Answers: " + result);
            }
        }

        // Tăng chỉ số câu hỏi
        i++;

        // Kiểm tra có còn câu hỏi để hiển thị không?
        if (i < questionList.size()) {
            // Hiển thị câu hỏi tiếp theo
            binding.txtQuestion.setText("Question " + (i + 1) + " : " +
                    questionList.get(i).getQuestion());

            binding.radio1.setText(questionList.get(i).getOption1());
            binding.radio2.setText(questionList.get(i).getOption2());
            binding.radio3.setText(questionList.get(i).getOption3());

            // Kiểm tra nếu đây là câu hỏi cuối cùng
            if (i == (questionList.size() - 1)) {
                //binding.btnNext.setText("Finish");
            }

            binding.radioGroup.clearCheck();

            // Khởi động lại đếm ngược
            timeLeftInMillis = START_TIME_IN_MILLIS;
            startTimer();
        } else {
            // Kết thúc bài kiểm tra
            finishQuiz();
       }

    }
    private void finishQuiz() {
        binding.txtResult.setText("Quiz Finished! Correct Answers: " + result);
        // Thêm các thao tác khác khi kết thúc bài kiểm tra nếu cần
        Intent i = new Intent(MainActivity.this, ResultsActivity.class);
        startActivity(i);
        finish();

    }
}