package com.example.makos.calculator;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Pattern;
import bsh.EvalError;
import bsh.Interpreter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    boolean backPressedDouble = false;
    @Override
    public void onBackPressed(){
        if(backPressedDouble){
            super.onBackPressed();
            return;
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator()) {
            v.vibrate(100);
        }
        backPressedDouble = true;
        final Toast toast = Toast.makeText(this, "Press Back again to quit.", Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedDouble = false;
                toast.cancel();
            }
        }, 2000);
    }

    public void numberClicked(View btn) {
        EditText result = (EditText) findViewById(R.id.result);
        EditText answer = (EditText) findViewById(R.id.answer);
        Button btn1 = (Button) btn;
        String currentResult = result.getText().toString()+btn1.getText().toString();
        String[] splitter = currentResult.split("\\u00F7|\\u2212|\\u00D7|\\u002B");
        if(splitter[splitter.length-1].replaceAll("\\.[0-9]*","").length()>9){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                v.vibrate(200);
            }
            final Toast toast = Toast.makeText(this, "Number out of range!", Toast.LENGTH_SHORT);
            toast.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
            return;
        }
        if(Pattern.matches("^[0][^.]", currentResult)){
            result.setText(btn1.getText().toString());
            answer.setText(btn1.getText().toString());
        }else{
            result.setText(currentResult);
            answer.setText(updateAnswer(currentResult));
            int focus = result.getText().length();
            result.setSelection(focus);
        }
    }

    private String updateAnswer(String currentResult) {
        Interpreter evaluateCurrentResult = new Interpreter();
        currentResult = currentResult.replaceAll("\\u00F7","/");
        currentResult = currentResult.replaceAll("\\u00D7","*");
        currentResult = currentResult.replaceAll("\\u2212","-");
        currentResult = currentResult.replaceAll("\\u002B","+");
        try{
            return evaluateCurrentResult.eval(currentResult).toString();
        }catch(EvalError e){
            return "";
        }
    }

    public void dotClicked(View btn){
        EditText result = (EditText) findViewById(R.id.result);
        EditText answer = (EditText) findViewById(R.id.answer);
        Button btn1 = (Button) btn;
        String currentResult = result.getText().toString()+btn1.getText().toString();
        int count;

        if(currentResult.substring(currentResult.length()-2,currentResult.length()-1).matches("\\u00F7|\\u2212|\\u00D7|\\u002B")){
            currentResult = currentResult.substring(0,currentResult.length()-2)+btn1.getText().toString();
        }
        String[] splitter = currentResult.split("\\u00F7|\\u2212|\\u00D7|\\u002B");
        count = splitter[splitter.length-1].length() - splitter[splitter.length-1].replaceAll("\\.","").length();
        if(count>1){
            return;
        }
        result.setText(currentResult);
        if(!currentResult.contains("\u00F7|\u2212|\u00D7|\u002B")) {
            answer.setText(currentResult);
        }
        int focus = result.getText().length();
        result.setSelection(focus);
    }

    public void signClicked(View btn){
        EditText result = (EditText) findViewById(R.id.result);
        Button btn1 = (Button) btn;
        String currentResult = result.getText().toString()+btn1.getText().toString();

        if(currentResult.substring(currentResult.length()-2,currentResult.length()-1).matches("\\u00F7|\\u2212|\\u00D7|\\u002B|\\.")){
            result.setText(getString(R.string.substring1,currentResult.substring(0,currentResult.length()-2),btn1.getText()));
        }else{
            result.setText(currentResult);
        }
        int focus = result.getText().length();
        result.setSelection(focus);
    }

    public void deleteClicked(View btn){
        EditText result = (EditText) findViewById(R.id.result);
        EditText answer = (EditText) findViewById(R.id.answer);
        String currentResult = result.getText().toString();
        if(currentResult.length()==1){
            result.setText("0");
            answer.setText("0");
        }else{
            result.setText(currentResult.substring(0,currentResult.length()-1));
            answer.setText(updateAnswer(currentResult.substring(0,currentResult.length()-1)));
        }
        int focus = result.getText().length();
        result.setSelection(focus);
    }
}
