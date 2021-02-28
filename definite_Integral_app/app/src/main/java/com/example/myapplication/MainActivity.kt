package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private var safeToGo = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Hide the Title bar of App
        supportActionBar?.hide();

        val calc_button : Button = findViewById(R.id.calculate_button);
        //val progress_bar : ProgressBar = findViewById(R.id.progress);
        val output_text : TextView = findViewById(R.id.output);

        //progress_bar.isIndeterminate = false

        calc_button.setOnClickListener{
            calc_button.isEnabled = false
            //progress_bar.isIndeterminate = true
            val integrand_expression : TextInputEditText = findViewById(R.id.integrand_input)

            val expression: String = integrand_expression.text.toString()

            //Now here parse the expression

            //Parse the expression will return the list of string which will contain <Integrand, variable, lowerLimit, upperLimit>
            var parsedList : List<String> = parsing(expression)

            //Debug::

            //return from the function

            safeToGo = true

            val safeCheck = -18941782349874298432974.024
            var upperLimit = safeCheck
            var lowerLimit = safeCheck
            try {
                upperLimit = evaluateExpression().evaluate(parsedList[3])
                lowerLimit = evaluateExpression().evaluate(parsedList[3])
            }finally {
                //nothing
                if(upperLimit==safeCheck || lowerLimit==safeCheck) safeToGo=false
            }

            var result : Double = safeCheck;
            try {
                result = integrate_algo(parsedList[0], parsedList[1], lowerLimit, upperLimit);
            } finally {
                if(result==safeCheck) safeToGo=false
            }
            println(result)
            // display in output

            if (safeToGo) {
                var output_round = ""
                val resultString = result.toString()
                var counter = 0
                for (i in 0..resultString.length-1){
                    if(counter>0 || resultString[i]=='.'){
                        counter+=1
                    }
                    output_round+=resultString[i].toString()
                    if(counter>=5) break;
                }
                output_text.text = output_round
            }
            else{
                output_text.text = "Error!"
            }
            //release calc_button
            calc_button.isEnabled = true
            //progress_bar.isIndeterminate = false
        }

    }

    //integration_via_simpson_
    fun integrate_algo(expression: String, variable: String, lowerLimit: Double,upperLimit: Double) : Double{

        //val variableIndexList : List<Int> = returnIndexOfVariable(expression,variable)
        val INCREMENT = 0.00001
        var currentPoint = INCREMENT;
        var iterations = 0;
        var ans = 0.0;

        //empty the variables from the expression

//        println(getExpression(expression,variable,"0.453"))
        while(currentPoint < upperLimit){
            if(iterations%2==0){
                ans += 2*evaluateExpression().evaluate(getExpression(expression,variable,currentPoint.toString()))}
            else{
                ans += 4*evaluateExpression().evaluate(getExpression(expression,variable,currentPoint.toString()))
            }
            iterations +=1
            currentPoint += INCREMENT
        }

        ans += evaluateExpression().evaluate(getExpression(expression,variable,upperLimit.toString()))
        ans += evaluateExpression().evaluate(getExpression(expression,variable,lowerLimit.toString()))

        ans *=INCREMENT;
        ans /=3

        println("::Done!")

        return ans;
    }

    fun getExpression(expression: String, variable: String, current: String) : String{
        var new_expression = ""

        for(i in 0..expression.length-1){
            if(expression[i]==variable[0]) new_expression+=current.toString()
            else{
                new_expression+=expression[i]
            }
            //println(new_expression)
        }

        return new_expression
    }



    //convert to function the integrand

//Parsing function is here to return <integrand, variable, lowerLimit, upperLimit
    private fun parsing (expression : String) : List<String>{
        var integrand : String ="";
    var variable : String = "";
    var upperLimit : String ="";
    var lowerLimit: String ="";
    var firstHalf = true;
    var dummy: Int = expression.length;
    var dummyBool = false;
    for (i in 0..dummy-1) {
        if(expression[i]==','){
            firstHalf=false;
            continue;
        }
        if(firstHalf){
            integrand +=expression[i].toString();
        }else{
            if(expression[i]=='='){
//                print("lowerLimit ")
//                println(lowerLimit);
                for (j in i+1..expression.length-1){
                    if(dummyBool==true && expression[j]==' ') {dummy=j+1;break;}
                lowerLimit +=expression[j].toString()
                    dummyBool = true
//                    print("lowerLimit ")
//                    println(lowerLimit);
                }
//                print("dummy ");
//                println(dummy);
                dummyBool=false;
                for (j in (i-1) downTo 0){
                    if(dummyBool==true && expression[j]==' ') break;
                    variable +=expression[j].toString()
                    dummyBool=true;
                }
                variable = variable.reversed();
//                print("variable ");
//                println(variable);
            }
        }
    }
    println(dummy);
    for (i in dummy..expression.length-1){
        upperLimit +=expression[i].toString();
//        print("upperLimit ")
//        println(upperLimit);
    }

    //Now further filter:

    //lowerLimit filteration
    lowerLimit = lowerLimit.filter { !it.isWhitespace() }

    //upperLimit filter:
    upperLimit = upperLimit.filter{!it.isWhitespace()}


    if(upperLimit.length >= 2){
        var _upperLimit = upperLimit;
        if(_upperLimit[0].toLowerCase()=='t' && _upperLimit[1].toLowerCase()=='o') {
            upperLimit = ""
            for (i in 2..(_upperLimit.length-1)){
                upperLimit += _upperLimit[i];
            }
        }
    }

    //further filteration
    integrand = integrand.toLowerCase();
    integrand = integrand.filter { !it.isWhitespace() }

    variable = variable.filter{!it.isWhitespace()};
    variable = variable.toLowerCase()

    upperLimit = upperLimit.toLowerCase()
    lowerLimit = lowerLimit.toLowerCase();

    var result = listOf(integrand, variable, lowerLimit, upperLimit)
        return result
    }

}

