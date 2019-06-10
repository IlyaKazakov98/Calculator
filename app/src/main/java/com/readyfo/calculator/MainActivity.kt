package com.readyfo.calculator

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class MainActivity : AppCompatActivity(){

    // Используем тип BigDecimal вместо Double для того, чтобы избежать ответа в стиле 2 - 1.1 = 0.89999999999999991
    private  var answer = BigDecimal(0.0)
    private var mutableList = mutableListOf<String>()
    private var clickEquals = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_text_view.layoutManager = LinearLayoutManager(this)
    }

    fun onClickAC(v: View){
        // функция обнуления всех значений калькулятора
        calc_view.text = "0"
        calc_answer.text = ""
        mutableList.clear()
        recycler_text_view.adapter = Adapter(mutableList)
        answer = BigDecimal(0.0)
        clickEquals = false
    }

    fun onClickDel(v: View){
        if (!clickEquals) {
            val strCalcView: String = calc_view.text.toString()
            // Функция удаления последнего элемента строки
            when {
                // Если поле calc_view равно "0", то calc_answer становится равной "= 0"
                strCalcView == "0" -> calc_answer.text = "= 0"
                // Если поле calc_view не пустое, то удаляем последний элемент строки.
                strCalcView.isNotEmpty() -> {
                    calc_view.text = strCalcView.substring(0, strCalcView.length - 1)
                    // После удаления элемента проверяем поле на пустоту.
                    if (calc_view.text.isNotEmpty()) {
                        // Если не пустое,то пересчитываем ответ
                        operations(calc_view.text.toString())
                    }
                    // В противном случае
                    else {
                        // Есле mutableList не пустой.
                        if (mutableList.isNotEmpty()) {
                            // Записываем последний элемент в поле calc_view.
                            calc_view.text = mutableList[mutableList.size - 1]
                            // Удаляем его.
                            mutableList.removeAt(mutableList.size - 1)
                            // И звоним адаптеру, чтобы тот удалил этот же элемент из recycler_text_view.
                            recycler_text_view.adapter = Adapter(mutableList)
                            // Прокручиваем recycler_text_view до последнего элемента, чтобы последнее действие всегда было перед глазами
                            recycler_text_view.smoothScrollToPosition(mutableList.size)
                        }
                        // В противном случае в поле calc_view записываем значение "0"
                        else {
                            calc_view.text = "0"
                            calc_answer.text = "= 0"
                        }
                    }
                }
            }
        }
        else ifInt()
    }

    // Не обращаем внимание на то, что имя функции не выделяется. Так произошло потому что, она объявляется на прямую из кнопки в XML
    // Её объявляение записанно в стиле, который в свою очередь применён к кнопке. Также и с функцией onClickOperators.
    @SuppressLint("SetTextI18n")
    fun onClickNumber (v: View){
        val strCalcView = calc_view.text.toString()
        val button = v as Button
        val numBtn = button.text.toString()

        if (!clickEquals) {
            // Вводим число
            // Если содежимое поля calc_view не равно "0", то добавляем к строке цифру
            calc_view.text = if (strCalcView != "0")
                "$strCalcView$numBtn"
            // В противном случае заменяем "0" на число
            else numBtn
            // Пересчитываем ответ после каждого ввода числа
            operations(calc_view.text.toString())
        }
        else ifInt()
    }

    fun onClickDouble (v: View){
        val strCalcView = calc_view.text.toString()
        val button = v as Button
        val numBtn = button.text.toString()

        if (!clickEquals) {
            // Ввод точки для создания числа типа Double
            // Если последний элемент строки уже является точкой, то возвращаем строку без изменений
            calc_view.text = if (strCalcView[strCalcView.length - 1] == '.')
                strCalcView
            // В противном случае добавляем точку
            else "$strCalcView$numBtn"
        }
        else ifInt()
    }

    fun onClickOperators (v: View){
        val strCalcView = calc_view.text.toString()
        val button = v as Button
        val operatorBtn = button.text.toString()
        if (!clickEquals) {
            // Если содежимое поля calc_view является оператором, то выходим из функции.
            if (strCalcView == "/" || strCalcView == "*" || strCalcView == "-" || strCalcView == "+") {
                calc_view.text = operatorBtn
            }
            else {
                // Добавляем в mutableList содержимое поля calc_view
                mutableList.add(strCalcView)
                // После делаем звонок адаптеру на добавление его в recycler_text_view
                recycler_text_view.adapter = Adapter(mutableList)
                // Прокручиваем recycler_text_view до последнего элемента, чтобы последнее действие всегда было перед глазами
                recycler_text_view.smoothScrollToPosition(mutableList.size)
            }
            calc_view.text = operatorBtn
        }
        else ifInt()
    }

    fun onClickEqual (v: View){
        val strCalcView = calc_view.text.toString()

        if (!clickEquals) {
            // Добавляем в mutableList содержимое поля calc_view
            mutableList.add(strCalcView)
            // После делаем звонок адаптеру на добавление его в recycler_text_view
            recycler_text_view.adapter = Adapter(mutableList)
            // Прокручиваем recycler_text_view до последнего элемента, чтобы последнее действие всегда было перед глазами
            recycler_text_view.smoothScrollToPosition(mutableList.size)
            // Присваиваем true глобальной переменной clickEquals, чтобы остальные методы были в курсе того,
            // что равно уже нажато и никаких опереций больше выполнять не нужно.
            clickEquals = true
            ifInt()
        }
        else ifInt()
    }

    // Функция расчёта ответа
    private fun operations (s: String){
        // Создаём условие для расчёта ответа
        // Если в истории вычислений уже что-то есть(mutableList.isNotEmpty()), то фикируем его размер
        if (mutableList.isNotEmpty()){
            val listSize = mutableList.size
            var i = 1
            // Если размер mutableList больше 1, то за входные принимаем его первый элемент и перебираем остальную часть
            // mutableList, в каждой из которых находится математическая операция.
            if (listSize > 1){
                answer = BigDecimal(mutableList[0])
                while (listSize > i){
                    operationsWhen(mutableList[i])
                    i++
                }
                // Так же не забываем проверить наличие математической операции в поле ввода(s)
                if (s.length > 1)
                    operationsWhen(s)
            }
            // Если размер mutableList равен 1, то за входные принимаем его первый и единственный элемент и производим
            // Математическую операцию с тем, что находится в поле ввода(s)
            else if (listSize == 1){
                if (s.length > 1)
                    operationsWhen(s)
                else {
                    answer = BigDecimal(mutableList[0])
                    ifInt()
                }
            }
        }
        else {
            answer = BigDecimal(calc_view.text.toString())
            ifInt()
        }
    }

    private fun operationsWhen (s: String){
        when (s[0]) {
            '/' -> {
                answer = answer.divide(BigDecimal(s.substring(1)), 15, RoundingMode.CEILING)
                ifInt()
            }
            '*' -> {
                answer = answer.multiply(BigDecimal(s.substring(1)))
                ifInt()
            }
            '-' -> {
                answer = answer.subtract(BigDecimal(s.substring(1)))
                ifInt()
            }
            '+' -> {
                answer = answer.add(BigDecimal(s.substring(1)))
                ifInt()
            }
        }
    }

    // Проверяем ответ на целочисленность и записываем его в ответ.
    @SuppressLint("SetTextI18n")
    private fun ifInt (){
        // Проверяем ответ на целочисленность перед выводом в поле ответа
        val ifIntAnswer = answer.stripTrailingZeros().scale() <= 0
        val localAnswerInt: BigInteger
        // Если кнопка равно не нажималась, то выводим ответ в calc_answer и продолжаем вычисления
        if (!clickEquals) {
            // Если целочисленное, то выводим в типе Int
            if (ifIntAnswer) {
                localAnswerInt = answer.toBigInteger()
                calc_answer.text = "= $localAnswerInt"
            }
            // В противном случае в Double
            else
                // Убираем нули после запятой с помошью stripTrailingZeros()
                calc_answer.text = "= ${answer.stripTrailingZeros()}"
            Log.d("answer", "$answer")
        }
        // В противном случае выводим в calc_view и заканчиваем вычисления
        else{
            if (ifIntAnswer) {
                localAnswerInt = answer.toBigInteger()
                calc_view.text = "= $localAnswerInt"
            }
            else calc_view.text = "= ${answer.stripTrailingZeros()}"

            calc_answer.text = ""
        }
    }
}
