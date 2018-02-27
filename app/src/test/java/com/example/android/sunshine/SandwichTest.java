package com.example.android.sunshine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ParameterizedRobolectricTestRunner.class)
public class SandwichTest {
    public enum KeyCode {
        NUM_0 (0),
        NUM_1 (1),
        NUM_2 (2),
        NUM_3 (3),
        NUM_4 (4),
        NUM_5 (5),
        NUM_6 (6),
        NUM_7 (7),
        NUM_8 (8),
        NUM_9 (9),

        DASH (10),
        ENTER (11);

        private final int code;

        private static final KeyCode[] codes = {
                NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9, DASH, ENTER
        };

        KeyCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static KeyCode createFromInteger(int keyCode) {
            if (keyCode >= 0 && keyCode < codes.length) {
                return codes[keyCode];
            }
            return null;
        }
    }

    private final int value;
    private final KeyCode keyCode;

    public static Object[][] data = new Object[][] {
            {0, KeyCode.NUM_0},
            {1, KeyCode.NUM_1},
            {2, KeyCode.NUM_2},
            {3, KeyCode.NUM_3},
            {4, KeyCode.NUM_4},
            {5, KeyCode.NUM_5},
            {6, KeyCode.NUM_6},
            {7, KeyCode.NUM_7},
            {8, KeyCode.NUM_8},
            {9, KeyCode.NUM_9},
            {10, KeyCode.DASH},
            {11, KeyCode.ENTER},
            {12, null},
            {-1, null},
            {999, null},
    };

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][] {
                        {0},
                        {1},
                        {2},
                        {3},
                        {4},
                        {5},
                        {6},
                        {7},
                        {8},
                        {9},
                        {10},
                        {11},
                        {12},
                        {13},
                        {14},
                }
        );
    }

    public SandwichTest(int index) {
        this.value = (Integer)data[index][0];
        this.keyCode = (KeyCode)data[index][1];
    }

    @Test
    @Config(manifest = Config.NONE)
    public void testGetKeyCodeFromInt() {
        Assert.assertEquals(keyCode, KeyCode.createFromInteger(value));
    }

}
