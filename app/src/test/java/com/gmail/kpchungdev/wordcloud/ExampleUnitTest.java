package com.gmail.kpchungdev.wordcloud;

import android.graphics.Paint;
import android.graphics.Rect;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void pseduoRandomGenerator() {
        Random random = new Random();
        random.setSeed(100);

        String s = "";
        for (int i = 0; i < 10; i++) {
            s = s + random.nextInt(100) + " ";
        }

        System.out.println("numbers: " + s);
    }

    @Test
    public void testExpression() {

        Expression textSizeExpression = new ExpressionBuilder("" +
                "10 * rep")
                .build();

        double result = textSizeExpression.evaluate();
        System.out.println("result: " + result + " expected: " + 0);


    }

}