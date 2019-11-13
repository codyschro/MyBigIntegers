public class MyBigIntegers {

    //create value and positive flag property
    String value;
    boolean positiveFlag;


    //constructor, returns a string representation of the integer for printing and verification, adds negative sign if applicable
    public String ToString(){
        if(!positiveFlag)
            return "-".concat(value);
        return value;
    }

    //constructor, takes in a string argument of a decimal integer and checks if positive or not, sets value
    public MyBigIntegers(String num){
        positiveFlag = true;
        if(num.charAt(0) == '-'){
            positiveFlag = false;
            num = num.substring(1);
        }
        value = num;
    }

    //initialize to zero constructor
    public MyBigIntegers(){
        value = "0";
        positiveFlag = true;
    }

    //returns a new BigInteger with value of this.value + num.value
    public MyBigIntegers Plus (MyBigIntegers num){

        //initialize value strings, result string, sum, and carry
        String firstNum = this.value;
        String secondNum = num.value;
        StringBuilder result = new StringBuilder();
        long carry =  0;

        //check if first value negative, uses minus method
        if(!this.positiveFlag && num.positiveFlag){
            MyBigIntegers thirdNum = new MyBigIntegers(this.value);
            thirdNum.positiveFlag = true;
            return num.Minus(thirdNum);
        }

        //check if second value is negative, uses minus method
        if(this.positiveFlag && !num.positiveFlag)
            return this.Minus(num);

        //make sure firstNum is bigger than secondNum for easier calculations, swap strings
        if(secondNum.length() > firstNum.length()){
            String tmp = secondNum;
            secondNum = firstNum;
            firstNum = tmp;
        }

        //loop starting at right ends
        for(int i = 1; i <= firstNum.length(); i++ ){
            //add each digit and take care of carries, subtract ascii for correct results
            if (i <= secondNum.length()) {
                long firstDigit = firstNum.charAt(firstNum.length() - i) - '0';
                long secondDigit = secondNum.charAt(secondNum.length() - i) - '0';
                long sum = firstDigit + secondDigit + carry;
                result.insert(0, sum % 10);
                //handle carry
                if (sum > 9)
                    carry = 1;
                else
                    carry = 0;
            }
            //there are extra numbers, lengths are uneven
            else{
                long sum = firstNum.charAt(firstNum.length() - i) - '0' + carry;
                result.insert(0, sum%10);
                if(sum > 9)
                    carry = 1;
                else
                    carry = 0;
            }
        }

        //add one to beginning to get correct result if carry is not 0
        if (carry != 0)
            result.insert(0, "1");

        //create answer as new string, ensure there is negative sign if needed
        MyBigIntegers answer = new MyBigIntegers(result.toString());
        if(!this.positiveFlag && !num.positiveFlag)
            answer.positiveFlag = false;

        return answer;

    }

    //returns a new BigInteger value of this.value * num.value, got help from here: https://www.programcreek.com/2014/05/leetcode-multiply-strings-java/
    public MyBigIntegers Times (MyBigIntegers num){

        //initialize strings
        String firstNum = this.value;
        String secondNum = num.value;
        StringBuilder result = new StringBuilder();

        //make sure neither num is zero, if so return 0
        if(firstNum.charAt(0) == '0')
            return new MyBigIntegers("0");
        else if (secondNum.charAt(0) == '0')
            return new MyBigIntegers("0");

        //create array in reverse order, size of both num comibined
        long [] digit = new long[firstNum.length()+secondNum.length()];
        //keep track of where position is at
        int firstPos = 0;

        //start at end of both nums using 2 loops
        for(int i = firstNum.length() - 1; i >= 0; i--){
            int secondPos = 0;
            long carry = 0;
            long x = firstNum.charAt(i) - '0';
            for(int j = secondNum.length() - 1; j >= 0; j--){
                //get digit to multiply
                long y = secondNum.charAt(j) - '0';
                //multiply, add carry
                long sum = (x * y) + digit[firstPos + secondPos] + carry;
                //update carry
                carry = sum / 10;
                digit[firstPos + secondPos] = sum % 10;
                //increase position
                secondPos++;
            }

            //manage carries, move first position
            if(carry > 0)
                digit[firstPos + secondPos] += carry;
            firstPos++;
        }
        //determine whether to add numbers
        for(int i = digit.length - 1; i >= 0; i--){
            if(result.length() == 0 && digit[i] == 0){
            }
            else
                result.append(digit[i]);
        }

        MyBigIntegers answer = new MyBigIntegers(result.toString());

        //decide if answer is negative or not using exclusive or
        if(this.positiveFlag ^ num.positiveFlag)
            answer.positiveFlag = false;

        return answer;

    }

    public MyBigIntegers Minus(MyBigIntegers num){

        //initialize strings, flag, carry
        String firstNum = this.value;
        String secondNum = num.value;
        boolean negative = false;
        long carry = 0;
        StringBuilder result = new StringBuilder();

        //handle negative minus a negative using plus method
        if(!num.positiveFlag && !this.positiveFlag){
            MyBigIntegers thirdNum = new MyBigIntegers(num.value);
            thirdNum.positiveFlag = true;
            return this.Plus(thirdNum);
        }

        //handle negative minus positive using plus method
        if(num.positiveFlag && !this.positiveFlag){
            MyBigIntegers thirdNum = new MyBigIntegers(num.value);
            thirdNum.positiveFlag = false;
            return this.Plus(thirdNum);
        }

        //handle positive minus negative using plus method
        if(!num.positiveFlag && this.positiveFlag){
            MyBigIntegers thirdNum = new MyBigIntegers(num.value);
            thirdNum.positiveFlag = true;
            return this.Plus(thirdNum);
        }

        //use helper to test which is smaller of the two
        if(smaller(firstNum, secondNum)){
            String tmp = firstNum;
            firstNum = secondNum;
            secondNum = tmp;
            negative = true;
        }

        long difference = firstNum.length() - secondNum.length();

        //start at end of strings, do grade school subtraction
        for(int i = secondNum.length() - 1; i >= 0; i--){
            //find the difference of each digit, mind the carry
            long digit = ((firstNum.charAt((int)(difference + i)) - '0') - (secondNum.charAt(i) - '0') - carry);
            if(digit < 0){
                digit += 10;
                carry = 1;
            }
            else
                carry = 0;
            result.insert(0, digit);
        }

        for (int i = firstNum.length() - secondNum.length() - 1; i >= 0; i--){
            if(carry > 0 && firstNum.charAt(i) == '0'){
                result.insert(0, "9");
                continue;
            }
            //get rid of zeroes
            long digit = ((firstNum.charAt(i) - '0') - carry);
            if(i > 0 || digit > 0)
                result.insert(0, digit);
            carry = 0;
        }

        MyBigIntegers answer = new MyBigIntegers(result.toString());
        //check if negative
        if(negative)
            answer.positiveFlag = false;

        return answer;
    }

    //helper function to determine which num is smallest
    static boolean smaller(String x, String y){
        //find lengths
        long len1 = x.length();
        long len2 = y.length();

        if(len1 < len2)
            return true;
        if (len1 > len2)
            return false;

        for(int i = 0; i < len1; i++){
            if(x.charAt(i) < y.charAt(i))
                return true;
            else if(x.charAt(i) > y.charAt(i))
                return false;
        }
        return false;
    }

    //implement faster times version using karatsuba helper function
    public MyBigIntegers TimesFaster(MyBigIntegers num){

        MyBigIntegers answer = Karatsuba(this, num);
        //check if one of the numbers is negative using exclusive or
        if(this.positiveFlag ^ num.positiveFlag)
            answer.positiveFlag = false;
        return answer;
    }

    //helper function implementing Karatsuba algorithm
    private static MyBigIntegers Karatsuba(MyBigIntegers firstHalf, MyBigIntegers secondHalf){

        //check that numbers are not small, if they are use regular multiply function
        if(firstHalf.value.length() < 10 || secondHalf.value.length() < 10)
            return firstHalf.Times(secondHalf);
        //determine which string is smaller, find middle
        int middle = Integer.min(firstHalf.value.length(), secondHalf.value.length());
        middle = Math.floorDiv(middle, 2);
        //fragment strings
        MyBigIntegers first = new MyBigIntegers(firstHalf.value.substring(0,firstHalf.value.length() - middle));
        MyBigIntegers second = new MyBigIntegers(firstHalf.value.substring(firstHalf.value.length()- middle));
        MyBigIntegers third = new MyBigIntegers(secondHalf.value.substring(0, secondHalf.value.length() - middle));
        MyBigIntegers fourth = new MyBigIntegers(secondHalf.value.substring(secondHalf.value.length() - middle));
        //recurse each portion
        MyBigIntegers x = Karatsuba(second,fourth);
        MyBigIntegers y = Karatsuba(first.Plus(second), third.Plus(fourth));
        MyBigIntegers z = Karatsuba(first, third);
        //set y
        y = y.Minus(x).Minus(z);
        //left shift y and z, add zeroes
        for(int i = 0; i < middle; i++){
            y.value = y.value.concat("0");
            z.value = z.value.concat("00");
        }
        //calculate answer using Plus method
        MyBigIntegers answer = x.Plus(y).Plus(z);
        return answer;
    }



    //Verification of methods
    public static void main(String [] args){

        //create two stings for comparison
        String answer;
        String correctAnswer;

        //declare two big integers
        MyBigIntegers first = new MyBigIntegers("9999");
        MyBigIntegers second = new MyBigIntegers("11111");

        //check to see if answers are same
        //answer = first.Plus(second).ToString();
        //correctAnswer = Long.toString(9999+11111);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Plus test with small numbers");
        //else
            //System.out.println("Answers not same for Plus test with small numbers");

        //first.value = "501";
        //second.value = "500";
        //answer = first.Plus(second).ToString();
        //correctAnswer = Long.toString(501+500);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Plus test with small numbers");
        //else
            //System.out.println("Answers not same for Plus test with small numbers");

        //repeat for other methods
        //first.value = "98765";
        //second.value = "2323";
        //answer = first.Times(second).ToString();
        //correctAnswer = Long.toString(98765*2323);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Times test with small numbers");
        //else
            //System.out.println("Answers not same for Times test with small numbers");

        //first.value = "123456";
        //second.value = "9876";
        //answer = first.Times(second).ToString();
        //correctAnswer = Long.toString(123456*9876);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Times test with small numbers");
        //else
            //System.out.println("Answers not same for Times test with small numbers");

        //first.value = "852";
        //second.value = "12345";
        //answer = first.TimesFaster(second).ToString();
        //correctAnswer = Long.toString(852*12345);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for TimesFaster test with small numbers");
        //else
            //System.out.println("Answers not same for TimesFaster test with small numbers");

        //first.value = "654";
        //second.value = "363636";
        //answer = first.TimesFaster(second).ToString();
        //correctAnswer = Long.toString(654*363636);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for TimesFaster test with small numbers");
        //else
            //System.out.println("Answers not same for TimesFaster test with small numbers");

        //first.value = "10202";
        //second.value = "7894";
        //answer = first.Minus(second).ToString();
        //correctAnswer = Long.toString(10202-7894);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Minus test with small numbers");
        //else
            //System.out.println("Answers not same for Minus test with small numbers");

        //first.value = "2525";
        //second.value = "7878";
        //answer = first.Minus(second).ToString();
        //correctAnswer = Long.toString(2525-7878);
        //if(answer.compareTo(correctAnswer) == 0)
            //System.out.println("Answers same for Minus test with small numbers");
        //else
            //System.out.println("Answers not same for Minus test with small numbers");

        //test with numbers where result is obvious
        //first.value = "9999999999999999999999999999999999";
        //second.value = "1";
        //answer = first.Plus(second).ToString();
        //System.out.println("Plus: 9999999999999999999999999999999999 + 1 = " + answer);

        //first.value = "22222222222222222222222222222222222222";
        //second.value = "6666666666666666666666666666";
        //answer = first.Times(second).ToString();
        //System.out.println("Times: 22222222222222222222222222222222222222 * 6666666666666666666666666666 = " + answer);

        first.value = "22222222222222222222222222222222222222";
        second.value = "6666666666666666666666666666";
        answer = first.TimesFaster(second).ToString();
        System.out.println("TimesFaster: 22222222222222222222222222222222222222 * 6666666666666666666666666666 = " + answer);

        first.value = "88888888888888888888888888888888888";
        second.value = "44444444444444444444444444444444444";
        answer = first.Minus(second).ToString();
        System.out.println("Minus: 88888888888888888888888888888888888 - 44444444444444444444444444444444444 = " + answer);




    }
}
