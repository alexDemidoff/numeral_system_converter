package converter;

import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final String RADIX_PATTERN = "^([1-9]|1[0-9]|2[0-9]|3[0-6])$";
    private static final Scanner scanner = new Scanner(System.in);
    private static State currentState = State.ACTIVE;

    private static int sourceRadix;
    private static String sourceNumberStr;
    private static int targetRadix;

    private static String numberPattern;

    static {
        scanner.useLocale(Locale.ENGLISH);
    }

    public static void main(String[] args) {
        while (currentState == State.ACTIVE) {
            getUserInput();

            if (currentState == State.ACTIVE) {
                System.out.printf("\n%s in base %d = %s in base %d\n", sourceNumberStr,
                        sourceRadix,
                        convert(sourceNumberStr, sourceRadix, targetRadix),
                        targetRadix);
            }
        }
    }

    private static void getUserInput() {
        do {
            System.out.println("Enter the source base: ");
        } while (getSourceRadix() == State.ERROR);

        if (currentState == State.ACTIVE) {
            do {
                System.out.println("Enter the number: ");
            } while (getSourceNumber() == State.ERROR);
        }

        if (currentState == State.ACTIVE) {
            do {
                System.out.println("Enter the target base: ");
            } while (getTargetRadix() == State.ERROR);
        }
    }

    private static State getTargetRadix() {
        String targetRadixStr = scanner.nextLine();

        if (targetRadixStr.equals("exit")) {
            currentState = State.EXIT;
            return State.EXIT;
        }

        if (targetRadixStr.matches(RADIX_PATTERN)) {
            targetRadix = Integer.parseInt(targetRadixStr);
            return State.SUCCESS;
        } else {
            System.out.println("Wrong target radix!");
            return State.ERROR;
        }
    }

    private static State getSourceNumber() {
        sourceNumberStr = scanner.nextLine();

        if (sourceNumberStr.equals("exit")) {
            currentState = State.EXIT;
            return State.EXIT;
        }

        if (sourceNumberStr.matches(numberPattern)) {
            return State.SUCCESS;
        } else {
            System.out.println("Wrong number format!");
            return State.ERROR;
        }
    }

    private static State getSourceRadix() {
        String sourceRadixStr = scanner.nextLine();

        if (sourceRadixStr.equals("exit")) {
            currentState = State.EXIT;
            return State.EXIT;
        }

        if (sourceRadixStr.matches(RADIX_PATTERN)) {
            sourceRadix = Integer.parseInt(sourceRadixStr);

            int rightBorder;
            if (sourceRadix == 1) {
                numberPattern = "^[1]+$";
            } else if (sourceRadix <= 10) {
                rightBorder = sourceRadix - 1;
                numberPattern = String.format("^([0-%1$d]+|[0-%1$d]+.[0-%1$d]+|.[0-%1$d]+)$", rightBorder);
            } else {
                rightBorder = 'a' + sourceRadix - 11; //ASCII code
                numberPattern = String.format("^([0-9a-%1$s]+|[0-9a-%1$s]+.[0-9a-%1$s]+|.[0-9a-%1$s]+)$", (char) rightBorder);
            }

            return State.SUCCESS;
        } else {
            System.out.println("Wrong radix!");
            return State.ERROR;
        }
    }

    public static String convert(String sourceNumber, int sourceRadix, int targetRadix) {
        if (isFraction(sourceNumber)) {
            return convertFraction(sourceNumber, sourceRadix, targetRadix);
        } else {
            return convertInteger(sourceNumber, sourceRadix, targetRadix);
        }
    }

    private static boolean isFraction(String number) {
        return number.contains(".");
    }

    private static String convertInteger(String sourceNumber, int sourceRadix, int targetRadix) {
        int decimalNumber = convertIntegerToDecimal(sourceNumber, sourceRadix);

        return convertIntegerToTargetRadix(decimalNumber, targetRadix);
    }

    // Converts the source number if it is not decimal
    private static int convertIntegerToDecimal(String number, int sourceRadix) {
        // Converting number manually as Java doesn't work with base-1 system
        if (sourceRadix == 1) {
            return number.length();
        } else {
            return Integer.parseInt(number, sourceRadix);
        }
    }

    private static String convertIntegerToTargetRadix(int decimalNumber, int targetRadix) {
        // Converting number manually as Java doesn't work with base-1 system
        if (targetRadix == 1) {
            return "1".repeat(decimalNumber);
        } else {
            return Integer.toString(decimalNumber, targetRadix);
        }
    }

    private static String convertFraction(String sourceNumber, int sourceRadix, int targetRadix) {
        String[] parts = sourceNumber.split("\\.");

        // If a number is without leading zero, like '.256'
        int decimalIntegerPart;
        if (sourceNumber.charAt(0) == '.') {
            decimalIntegerPart = 0;
        } else {
            decimalIntegerPart = convertIntegerToDecimal(parts[0], sourceRadix);
        }

        double decimalFractionalPart = convertFractionalPartToDecimal(parts[1], sourceRadix);
        double decimalFractionalNumber = decimalIntegerPart + decimalFractionalPart;

        return convertFractionToTargetRadix(decimalFractionalNumber, targetRadix);
    }

    private static double convertFractionalPartToDecimal(String fractionalPart, int sourceRadix) {
        double result = 0;

        for (int i = 0; i < fractionalPart.length(); i++) {
            if (Character.isDigit(fractionalPart.charAt(i))) {
                result += (fractionalPart.charAt(i) - '0') / Math.pow(sourceRadix, i + 1); //ASCII code
            } else {
                result += (fractionalPart.charAt(i) - 'a' + 10) / Math.pow(sourceRadix, i + 1); //ASCII code
            }
        }

        return result;
    }

    private static String convertFractionToTargetRadix(double decimalFractionalNumber, int targetRadix) {
        int integerPart = (int) decimalFractionalNumber;
        double fractionalPart = decimalFractionalNumber - integerPart;
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int digit = (int) (fractionalPart * targetRadix);
            fractionalPart = fractionalPart * targetRadix - digit;
            result.append(convertIntegerToTargetRadix(digit, targetRadix));
        }

        return convertIntegerToTargetRadix(integerPart, targetRadix) + "." + result;
    }

}
