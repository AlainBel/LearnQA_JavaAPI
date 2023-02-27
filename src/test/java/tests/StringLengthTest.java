package tests;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringLengthTest {

    private static final int EXPECTED_STRING_LENGTH = 15;

    private final String line = "Restassured tests";

    public String getTestString() {
        return line;
    }

    @Test
    public void StringLength() {
        assertTrue(
                getTestString().length() > EXPECTED_STRING_LENGTH,
                "String length 15 characters or less"
        );
    }

}
