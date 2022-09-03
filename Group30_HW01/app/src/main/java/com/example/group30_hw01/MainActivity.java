
/*
    Homework 01

    MainActivity.java

    Group:
    Matthew Carroll
    Conor Derhammer
 */

package com.example.group30_hw01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // For Tagging and Debugging
    AppCompatActivity activity = this;
    final String TAG = "bac";

    // For calculations
    final double CONST_F = 0.66;
    final double CONST_M = 0.73;
    double bodyWeight;
    boolean isFemale;
    boolean hasSetWG;
    ArrayList<Drink> drinks;
    double bac;

    // Top third
    EditText editWeight;
    RadioButton radioButtonFemale;
    RadioButton radioButtonMale;
    Button buttonSetWeight;

    // Middle Third
    TextView labelWeightGenderDisplay;
    RadioButton radioButtonOneOunce;
    RadioButton radioButtonFiveOunce;
    RadioButton radioButtonTwelveOunce;
    SeekBar seekBarAlcoholPercent;
    TextView labelCurrAlcoholPercent;

    // Bottom third
    Button buttonReset;
    Button buttonAddDrink;
    TextView labelNumDrinks;
    TextView labelBACLevel;
    TextView labelCurrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize variables
        bodyWeight = 0.0;
        hasSetWG = false;
        drinks = new ArrayList<>();
        bac = 0.0;

        /*
            INSTANTIATE VIEWS FOR FIRST PART
         */
        editWeight = findViewById(R.id.editWeight);
        buttonSetWeight = findViewById(R.id.buttonSetWeight);

        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        radioButtonMale = findViewById(R.id.radioButtonMale);

        labelWeightGenderDisplay = findViewById(R.id.labelWeightGenderDisplay);


        /*
            BUTTON - SET WEIGHT - EVENT LISTENER

            When set weight button is pressed, grab weight and gender
            and display it beneath the button

            If no weight is entered or if it is incorrectly entered,
            throw a Toast
         */
        buttonSetWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Grab weight using edit text field
                Double weight = getEnteredWeight();

                // Check if the weight came back fine
                if (weight != null) {

                    // Set body weight and gender from current views
                    bodyWeight = weight;
                    isFemale = radioButtonFemale.isChecked();

                    // Reset views to neutral state
                    clearViews();

                    // Update weight and gender display based on values found
                    String gender = isFemale ? "Female" : "Male";
                    String displayText = bodyWeight + " lbs (" + gender + ")";
                    labelWeightGenderDisplay.setText(displayText);

                    // Record that weight and gender have been set
                    hasSetWG = true;

                    // Reset fields
                    drinks.clear();
                    bac = 0.0;

                }
                // Else throw a Toast to the screen
                else {
                    Toast.makeText(activity, "Enter a positive number for weight", Toast.LENGTH_SHORT).show();
                }
            }

            // Return weight as a double parse from text
            public Double getEnteredWeight(){
                String text = editWeight.getText().toString();
                double weight;

                try {
                    weight = Double.parseDouble(text);
                    if (weight < 0)
                        return null;
                    return weight;

                } catch(NumberFormatException e){
                    return null;
                }
            }
        });


        /*
            INSTANTIATE VIEWS FOR SECOND PART
         */
        radioButtonOneOunce = findViewById(R.id.radioButtonOneOunce);
        radioButtonFiveOunce = findViewById(R.id.radioButtonFiveOunce);
        radioButtonTwelveOunce = findViewById(R.id.radioButtonTwelveOunce);

        seekBarAlcoholPercent = findViewById(R.id.seekBarAlcoholPercent);
        labelCurrAlcoholPercent = findViewById(R.id.labelCurrAlcoholPercent);

        buttonReset = findViewById(R.id.buttonReset);
        buttonAddDrink = findViewById(R.id.buttonAddDrink);

        labelNumDrinks = findViewById(R.id.labelNumDrinks);
        labelBACLevel = findViewById(R.id.labelBACLevel);
        labelCurrStatus = findViewById(R.id.labelCurrStatus);

        // Initialize number of drinks and bac display to show 0 and 0.000
        String numDrinksText = getString(R.string.textLabelDrinks) + " " +  getString(R.string.textLabelDefaultNumDrinks);
        labelNumDrinks.setText(numDrinksText);
        String bacText = getString(R.string.textLabelBAC) + " " +  getString(R.string.textLabelDefaultBAC);
        labelBACLevel.setText(bacText);


        /*
            SEEKBAR - ALCOHOL PERCENT - EVENT LISTENER

            Whenever the seekbar is moved, the percentage next to it should show
            the current value
         */
        seekBarAlcoholPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String percentText = i + "%";
                labelCurrAlcoholPercent.setText(percentText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        /*
            BUTTON - ADD DRINK - EVENT LISTENER

            If weight and gender have been set, add a drink to the list
            and update the BAC level and status
         */
        buttonAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if weight and gender have been set
                if (hasSetWG){

                    // Create new drink using selected size and percent
                    int size;
                    if (radioButtonOneOunce.isChecked())
                        size = 1;
                    else if (radioButtonFiveOunce.isChecked())
                        size = 5;
                    else
                        size = 12;

                    int percent = seekBarAlcoholPercent.getProgress();

                    Drink drink = new Drink(size, percent);
                    drinks.add(drink);

                    // Update drink counter
                    String numDrinksText = getString(R.string.textLabelDrinks) + " " + drinks.size();
                    labelNumDrinks.setText(numDrinksText);
                    Log.d(TAG, "drink added: " + drink);

                    // Calculate and update BAC level
                    bac = getBAC();
                    double roundedBAC = (Math.round(bac * 1000)) / 1000.0;
                    String bacText = getString(R.string.textLabelBAC) + " " + roundedBAC;
                    labelBACLevel.setText(bacText);

                    // Update status
                    if (bac < 0.08){
                        labelCurrStatus.setText(R.string.textLabelStatusGreen);
                        labelCurrStatus.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    else if (bac < 0.2){
                        labelCurrStatus.setText(R.string.textLabelStatusOrange);
                        labelCurrStatus.setBackgroundColor(getResources().getColor(R.color.orange));
                    }
                    else {
                        labelCurrStatus.setText(R.string.textLabelStatusRed);
                        labelCurrStatus.setBackgroundColor(getResources().getColor(R.color.red));

                        if (bac > 0.25) {
                            buttonAddDrink.setEnabled(false);
                            Toast.makeText(activity, "No more drinks for you!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(activity, "Please enter your weight and gender", Toast.LENGTH_SHORT).show();
                }
            }

            // Calculate current BAC
            public double getBAC(){

                double totalContent = 0.0;
                double W = 5.14;
                double R = isFemale ? CONST_F : CONST_M;

                for (Drink drink : drinks){
                    totalContent += drink.getAlcoholContent();
                }

                return totalContent * W / (bodyWeight * R);
            }
        });


        /*
            BUTTON - RESET - EVENT LISTENER

            When the reset button is clicked, clear drinks, reset UI
            and re-enable the add drink button
         */
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reset all forms to the neutral state
                clearViews();

                //Reset fields
                hasSetWG = false;
                bac = 0.0;
                drinks.clear();

                //Enable add drink button
                buttonAddDrink.setEnabled(true);

            }
        });
    }

    // Clear the views that need to be reset
    public void clearViews(){

        //Reset labels and selections
        editWeight.setText("");
        labelWeightGenderDisplay.setText("");
        radioButtonFemale.setChecked(true);
        radioButtonOneOunce.setChecked(true);
        seekBarAlcoholPercent.setProgress(15);
        String numDrinksText = getString(R.string.textLabelDrinks) + " " + getString(R.string.textLabelDefaultNumDrinks);
        labelNumDrinks.setText(numDrinksText);
        String bacText = getString(R.string.textLabelBAC) + " " + getString(R.string.textLabelDefaultBAC);
        labelBACLevel.setText(bacText);

        //Reset status
        labelCurrStatus.setText(getString(R.string.textLabelStatusGreen));
        labelCurrStatus.setBackgroundColor(getResources().getColor(R.color.green));
    }
}