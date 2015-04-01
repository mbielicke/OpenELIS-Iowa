package org.openelis.utils;
/**
  * A  class that represents an incrementable String of the format 'AAA###' with
  * a variable number of letters in front and digits at the end.
  */
import java.text.DecimalFormat;

public class StringInt implements Cloneable {
    char   leftSide[];
    int    rightSide, rightMax;
    String rightFormat;
    
    public StringInt() {
        leftSide = new char[]{'A', 'A', 'A'};
        rightSide = 0;
        rightMax = 1000;
        rightFormat = "000";
    }
    
    public StringInt(int letters, int digits) {
        leftSide = new char[letters];
        for (int i = 0; i < letters; i++)
            leftSide[i] = 'A';
        
        rightSide = 0;
        rightMax = (int) Math.pow(10, digits);
        
        rightFormat = "";
        for (int i = 0; i < digits; i++)
            rightFormat += "0";
    }
    
    public StringInt(int letters, int digits, String value) throws Exception {
        this(letters, digits);
        
        for (int i = 0; i < leftSide.length; i++) {
            if (Character.isLetter(value.charAt(i)))
                leftSide[i] = value.charAt(i);
            else
                throw new Exception("The specified value does not match the specified format.");
        }

        try {
            rightSide = Integer.parseInt(value.substring(letters, letters + digits));
        } catch (NumberFormatException numE) {
            throw new Exception("The specified value does not match the specified format.");
        }
    }
    
    public StringInt(StringInt newObject) {
        leftSide = (char[]) newObject.leftSide.clone();
        rightSide = newObject.rightSide;
        rightMax = newObject.rightMax;
        rightFormat = newObject.rightFormat.toString();
    }
    
    public void add(int value) throws Exception {
        char tempLeftSide[];
        int  tempRightSide;
        
        tempLeftSide  = (char[]) leftSide.clone();
        tempRightSide = rightSide;
        if (value > 0) {
            while (value > 0) {
                if (++rightSide >= rightMax) {
                    rightSide = 0;
                    for (int i = leftSide.length - 1; i >= 0; i--) {
                        if(++leftSide[i] <= 'Z') {
                            break;
                        } else {
                            leftSide[i] = 'A';
                            if (i == 0) {
                                leftSide  = tempLeftSide;
                                rightSide = tempRightSide;
                                throw new Exception("Cannot exceed maximum value allowed by this StringInt.");
                            }
                        }
                    }
                }
                value--;
            }
        } else if (value < 0) {
            while (value < 0) {
                if (--rightSide < 0) {
                    rightSide = rightMax - 1;
                    for (int i = leftSide.length - 1; i >= 0; i--) {
                        if(--leftSide[i] >= 'A') {
                            break;
                        } else {
                            leftSide[i] = 'Z';
                            if (i == 0) {
                                leftSide  = tempLeftSide;
                                rightSide = tempRightSide;
                                throw new Exception("Value of this StringInt cannot be negative.");
                            }
                        }
                    }
                }
                value++;
            }
        }
    }
    
    public String toString() {
        StringBuffer  buffer;
        DecimalFormat format;
        
        buffer = new StringBuffer();
        
        for (int i = 0; i < leftSide.length; i++)
            buffer.append(leftSide[i]);
        
        format = new DecimalFormat(rightFormat);
        buffer.append(format.format(rightSide));
        
        return buffer.toString();
    }
    
    public Object clone() {
        StringInt newObject;
        
        newObject = new StringInt();
        newObject.leftSide = (char[]) leftSide.clone();
        newObject.rightSide = rightSide;
        newObject.rightMax = rightMax;
        newObject.rightFormat = rightFormat.toString();
        
        return newObject;
    }
}
