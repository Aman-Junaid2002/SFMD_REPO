package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button prevButton, nextButton, showAnswerButton;
    private TextView scoreText, timerText;
    private List<QuizQuestion> questions;
    private int currentIndex = 0;
    private int score = 0;
    private boolean[] answeredQuestions; // Array to track answered questions
    private boolean[] answerShown; // Array to track if the answer has been shown
    private int[] correctAnswerIndices; // Array to store the correct answer indices
    private CountDownTimer countDownTimer;
    private boolean hasAnsweredCorrectly = false; // Track if the user answered correctly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        // Initialize your questions list
        questions = Arrays.asList(
                new QuizQuestion("What is the minimum legal driving age in Pakistan?", new String[]{"16", "18", "21", "Any age if you have the money"}, 3),
                new QuizQuestion("Does this assignment deserve full marks?", new String[]{"Yes", "No", "Maybe", ">:("}, 0),
                new QuizQuestion("What is Batman's true identity?", new String[]{"Bruce Wayne", "Clark Kent", "Barry Allen", "Aman Junaid"}, 3),
                new QuizQuestion("How many cylinders in the engine of a Lexus LFA?", new String[]{"8", "12", "10", "16"}, 2),
                new QuizQuestion("Which superhero movie trilogy is the best?", new String[]{"Iron Man", "Batman", "Captain America", "Thor"}, 1),
                new QuizQuestion("What is the name of the fastest production car?", new String[]{"Bugatti Chiron", "Pagani Zonda R", "Koenigsegg Jesko Absolut", "Devel Sixteen"}, 2),
                new QuizQuestion("Which author is regarded as the father of cosmic horror?", new String[]{"Harlan Ellison", "H.P. Lovecraft", "Edgar Allan Poe",  "Stephen King"}, 1),
                new QuizQuestion("which of these is a fruit?", new String[]{"Potato", "Lemon", "Tomato", "Carrot"}, 2),
                new QuizQuestion("which brand's opening in Russia symbolized the end of the cold war?", new String[]{"KFC", "House of Prime Rib", "Mc Donald", "Wendy's"}, 2),
                new QuizQuestion("Whats the Darkest Version of Black?", new String[]{"Void", "Vanta", "Abyssal", "Catatonic"}, 1),
                new QuizQuestion("What is the biggest brand in the world in terms of revenue?", new String[]{"Nvidea", "Apple", "Google", "Facebook"}, 1),
                new QuizQuestion("What is the name of the american president that abolished slavery?", new String[]{"Abraham Lincoln", "Ronald Raegan", "George Washington", "John F. Kennedy"}, 0),
                new QuizQuestion("What is the oldest living animal on earth?", new String[]{"Lobster", "Great Tortoise", "Greenland Shark", "Blue Whale"}, 2),
                new QuizQuestion("How many people died in the Rawandan genocide?", new String[]{"85,000", "1,000,000", "650,000", "0. it was a ploy to gain sympathy from the UN"}, 1),
                new QuizQuestion("What is the most expensive production car on earth?", new String[]{"Lamborghini Veneno", "Bugatti La Voitre Noir", "Pagani Utopia", "Rolls Royce Boattail"}, 3),
                new QuizQuestion("Which of the following diseases have the highest mortality rate?", new String[]{"Cancer", "Rabies", "AIDS", "Tetanus"}, 1),
                new QuizQuestion("What is the name of the biggest star ever discovered?", new String[]{"Stephenson 2-18", "UV Scuti", "VY Canis Majoris", "TON-618"}, 1),
                new QuizQuestion("Which individual has had the most assassination attempts in recorded history?", new String[]{"Genghis Khan", "Adolf Hitler", "Fidel Castro", "Alexander the Great"}, 2),
                new QuizQuestion("How many years are in a lightyear?", new String[]{"1", "320", "320,000", "6.248"}, 0),
                new QuizQuestion("Who is hailed as the inventor of algebra?", new String[]{"Ibn-e-Sina", "Plato", "Al-Khwarizmi", "Frederick Neitzche"}, 2),
                new QuizQuestion("Congratulations on finishing the quiz, how was your experience?", new String[]{"Great :)", "it was alright :|", "Boring :(", "I hated it >:("}, 0),
                new QuizQuestion("Dummy Question", new String[]{"x", "y", "z"}, 1)
        );

        // Initialize the arrays
        answeredQuestions = new boolean[questions.size()];
        answerShown = new boolean[questions.size()];
        correctAnswerIndices = new int[questions.size()];

        for (int i = 0; i < questions.size(); i++) {
            correctAnswerIndices[i] = questions.get(i).getCorrectAnswerIndex();
        }

        // Initialize and start the countdown timer
        startTimer(5 * 60 * 1000); // 30 minutes in milliseconds

        // Display the first question
        updateQuestion();

        // Setup button listeners
        prevButton.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateQuestion();
            }
        });

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (currentIndex >= questions.size() - 2) {
                // Ensure that the sum of answered and shown answers is equal to number of questions - 1
                int answeredCount = 0;
                int shownCount = 0;
                for (int i = 0; i < questions.size(); i++) {
                    if (answeredQuestions[i]) {
                        answeredCount++;
                    }
                    if (answerShown[i]) {
                        shownCount++;
                    }
                }

                if (currentIndex == questions.size() - 2 && (answeredCount + shownCount) < (questions.size() - 1)) {
                    Toast.makeText(QuizActivity.this, "Please answer or reveal the answers for all questions before proceeding.", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentIndex >= questions.size() - 2) {
                        // Show completion screen when the last question is answered
                        showCompletionScreen();
                    } else {
                        currentIndex++;
                        updateQuestion();
                    }
                }
            } else {
                currentIndex++;
                updateQuestion();
            }
        });



        showAnswerButton.setOnClickListener(v -> {
            showAnswer();
        });
    }

    private void showCompletionScreen() {
        // Stop the timer if it's still running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Clear the question and options
        questionText.setText("Congratulations on finishing the quiz!");

        // Hide the radio buttons and other buttons
        optionsGroup.setVisibility(View.GONE);
        prevButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        showAnswerButton.setVisibility(View.GONE);

        // Display the final score
        scoreText.setText("Your final score: " + score);

        // Create a "Finish" button to exit the quiz
        Button finishButton = new Button(this);
        finishButton.setText("Finish");

        // Set the finish button layout parameters and add it to the layout
        finishButton.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));

        // Position the button below the scoreText view
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) finishButton.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.scoreText);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        finishButton.setLayoutParams(params);

        // Add the finish button to the root layout (RelativeLayout)
        ((RelativeLayout) findViewById(R.id.rootLayout)).addView(finishButton);

        // Set onClick listener for finish button
        finishButton.setOnClickListener(v -> {
            finish();  // Close the current activity
        });
    }



    private void startTimer(long millisInFuture) {
        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Time: %02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                timerText.setText("Time: 00:00");
                endQuiz(); // End the quiz when the timer finishes
            }
        }.start();
    }

    private void endQuiz() {
        // Stop the timer
        if (countDownTimer != null) {
            Toast.makeText(this, "Timer canceled", Toast.LENGTH_SHORT).show();  // Add this line for debugging
            countDownTimer.cancel();
        }

        // Disable all options and buttons
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false); // Added this to disable option4
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        showAnswerButton.setEnabled(false);

        // Show the final score and a completion message
        Toast.makeText(this, "Quiz Completed! Your final score is: " + score, Toast.LENGTH_LONG).show();
    }

    private void updateQuestion() {
        QuizQuestion currentQuestion = questions.get(currentIndex);
        questionText.setText(currentQuestion.getQuestion());

        // Set options
        String[] options = currentQuestion.getOptions();
        option1.setText(options[0]);
        option2.setText(options[1]);
        option3.setText(options[2]);
        option4.setText(options[3]);

        // Clear previous selection
        optionsGroup.clearCheck();

        // Reset colors and enable/disable options
        resetOptionColors();
        boolean isAnswered = answeredQuestions[currentIndex];
        option1.setEnabled(!isAnswered);
        option2.setEnabled(!isAnswered);
        option3.setEnabled(!isAnswered);
        option4.setEnabled(!isAnswered);

        // Highlight correct answer and user's previous selection if the question has been answered
        if (isAnswered) {
            highlightAnswer();
        }

        // Disable show answer button if the question has been answered or answer was already shown
        showAnswerButton.setEnabled(!isAnswered && !answerShown[currentIndex]);

        // Enable/Disable buttons based on the current index
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < questions.size() - 1);

        // Update the score display
        updateScoreDisplay();
    }

    private void checkAnswer() {
        // Get the ID of the selected radio button
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            // Find the selected RadioButton
            RadioButton selectedOption = findViewById(selectedId);
            // Get the index of the selected option
            int selectedOptionIndex = optionsGroup.indexOfChild(selectedOption);

            // Check if the selected answer is correct
            QuizQuestion currentQuestion = questions.get(currentIndex);
            if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
                score += 5;
                hasAnsweredCorrectly = true;
            } else {
                score--;
                hasAnsweredCorrectly = false;
            }

            // Mark the current question as answered
            answeredQuestions[currentIndex] = true;

            // Highlight the correct answer and the wrong answer if applicable
            highlightAnswer();
        }
    }

    private void showAnswer() {
        // Show the correct answer
        highlightCorrectAnswer();

        // Disable all options
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false); // Added this to disable option4

        // Deduct 1 mark from the score for showing the answer
        score--;

        // Mark the current question as answered and the answer as shown
        answeredQuestions[currentIndex] = true;
        answerShown[currentIndex] = true;

        // Update the score display
        updateScoreDisplay();

        // Disable the "Show Answer" button
        showAnswerButton.setEnabled(false);
    }

    private void highlightAnswer() {
        // Reset all options to default color
        resetOptionColors();

        // Get the index of the correct answer for the current question
        int correctAnswerIndex = correctAnswerIndices[currentIndex];
        RadioButton correctOption = (RadioButton) optionsGroup.getChildAt(correctAnswerIndex);

        // Highlight the correct answer in green
        correctOption.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        if (!hasAnsweredCorrectly) {
            // Highlight the selected wrong answer in red if applicable
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedOption = findViewById(selectedId);
                if (optionsGroup.indexOfChild(selectedOption) != correctAnswerIndex) {
                    selectedOption.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        }
    }

    private void highlightCorrectAnswer() {
        // Get the correct answer index for the current question
        int correctAnswerIndex = correctAnswerIndices[currentIndex];

        // Find and enable the correct answer RadioButton
        RadioButton correctOption = (RadioButton) optionsGroup.getChildAt(correctAnswerIndex);
        correctOption.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    private void resetOptionColors() {
        option1.setTextColor(getResources().getColor(android.R.color.black));
        option2.setTextColor(getResources().getColor(android.R.color.black));
        option3.setTextColor(getResources().getColor(android.R.color.black));
        option4.setTextColor(getResources().getColor(android.R.color.black)); // Added this to reset option4
    }

    private void updateScoreDisplay() {
        scoreText.setText("Score: " + score);
    }
}
