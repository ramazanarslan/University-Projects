/*

NAME			:	RAMAZAN ARSLAN
STUDENT NO      :	2016400345

NAME			:	AHMET SEMÝH ARI
STUDENT NO      :	2016400315

CMPE 230 PROJECT-1



 */

import java.security.acl.NotOwnerException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import javax.security.auth.x500.X500Principal;
import javax.swing.Popup;

import org.w3c.dom.css.Counter;

public class MainCompiler
	{

		public static BufferedReader in;
		public static BufferedWriter out;
		public static final String ERR = "ERROR";
		public static boolean IsItFirstSituation = false;

		public static int labelout, labeltest, nofv;
		public static String[] line, variables, expression, variablesOutput,
				expressionOutput, variableWithoutEq;
		public static Integer[] expOpenedBracketNum, expClosedBracketNum,
				expOpenedBracketFirstIndex, expClosedBracketLastIndex;

		public static void main(String[] args) throws IOException
			{

				// The name of the file to open.

				variables = new String[100];
				expression = new String[100];
				variablesOutput = new String[100];
				expressionOutput = new String[100];
				variableWithoutEq = new String[100];
				expClosedBracketNum = new Integer[100];
				expOpenedBracketNum = new Integer[100];
				expOpenedBracketFirstIndex = new Integer[100];
				expClosedBracketLastIndex = new Integer[100];
				line = new String[100];

				String fileName = "C:\\Users\\RamazanArslan\\Desktop\\example.txt";

				ReadTxt(fileName);
				// WriteTxt(fileName);

				// program();

			}

		public static void ExpressionControl(int counter,
				String expressionString)
			{

				// System.out.println("GELENLER : " + expressionString);
				expClosedBracketLastIndex[counter] = 0;
				expOpenedBracketFirstIndex[counter] = 0;
				if (expressionString == "first_situation"
						&& !IsItFirstSituation)
					{
						expressionString = "" + expression[counter];
						expressionString = expressionString.substring(1);
						IsItFirstSituation = true;
					}

				// Check start with 'NOT'

				if (expressionString.startsWith("not"))
					{

						IsItFirstSituation = false;

						// not exp
						OperationNOTCheck(counter, expressionString);

					}

				// Check start with 'XOR'

				else if (expressionString.startsWith("xor"))
					{
						OperationXORCheck(counter, expressionString);

					}

				else if (expressionString.startsWith("ls"))
					{
					}
				else if (expressionString.startsWith("rs"))
					{
					}
				else if (expressionString.startsWith("lr"))
					{
					}
				else if (expressionString.startsWith("rr"))
					{

					}
				else
					{

						if (Character.isDigit(expressionString.charAt(0)))
							{
								expressionString = "push " + expressionString
										+ "h";

								System.out.println(expressionString);

							}
						else
							{

								if (IsItFirstSituation)
									{
										expressionString = "push 0"
												+ expressionString + "h";
										System.out.println(expressionString);
										expressionOutput[counter] = expressionString;
										IsItFirstSituation = false;
									}
								else
									{

										expressionOutput[counter] = expressionString;

										System.out.println("push 0"
												+ expressionString + "h");

									}

							}

					}

			}

		private static void OperationXORCheck(int counter,
				String expressionString)
			{
				System.out.println("\n" + "XOR MAIN : " + expressionString);
				ParenthesisControl(counter, expressionString);

				ParenthesisSyntaxControlAndExtractExpression(counter,
						expressionString);

			}

		private static void OperationNOTCheck(int counter,
				String expressionString)
			{

				// System.out.println("\n" + "NOT MAINDEKI : " +
				// expressionString);

				ParenthesisControl(counter, expressionString);

				ParenthesisSyntaxControlAndExtractExpression(counter,
						expressionString);

				ExpressionControl(
						counter,
						ParenthesisSyntaxControlAndExtractExpression(counter,
								expressionString));

			}

		public static String ParenthesisSyntaxControlAndExtractExpression(
				int counter, String expressionString)
			{
				if (expOpenedBracketNum[counter] + expClosedBracketNum[counter] == 0)
					{

						String answer = expressionString.substring(
								expOpenedBracketFirstIndex[counter] + 1,
								expClosedBracketLastIndex[counter]);
						// System.out.println("PARENTEZ OUT:  " + answer);

						return answer;

					}
				else
					{
						System.out.println("systax error :D about using NOT : "
								+ expOpenedBracketNum[counter] + "  "
								+ expClosedBracketNum[counter]);

						return ERR;
					}
			}

		private static void ParenthesisControl(int counter,
				String expressionString)
			{

				int i = 0;
				expOpenedBracketNum[counter] = 0;
				expClosedBracketNum[counter] = 0;

				int runForOnlyOne = 0;

				while (i < expressionString.length())
					{
						if (expressionString.charAt(i) == '(')
							{
								expOpenedBracketNum[counter]++;

								if (runForOnlyOne == 0)
									{
										expOpenedBracketFirstIndex[counter] = i;
										runForOnlyOne++;
									}

							}
						if (expressionString.charAt(i) == ')')
							{
								expClosedBracketLastIndex[counter] = i;
								expClosedBracketNum[counter]--;
							}
						i++;
					}
			}

		public static void WriteTxt(String fileName)
			{
				try
					{
						FileWriter writer = new FileWriter(fileName, false);
						BufferedWriter bufferedWriter = new BufferedWriter(
								writer);
						bufferedWriter.write("Hello World");
						bufferedWriter.newLine();
						bufferedWriter.write("Hello semih");

						bufferedWriter.close();
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
			}

		public static void ReadTxt(String fileName)
			{
				try
					{
						FileReader reader = new FileReader(fileName);
						BufferedReader bufferedReader = new BufferedReader(
								reader);

						System.out
								.println("\n" + "///////////////////////////");
						System.out.println("\n" + "code segment");

						String lineinput;

						int counter = 0;

						while ((lineinput = bufferedReader.readLine()) != null)
							{

								line[counter] = lineinput;
								System.out.println("\n" + ";;;;"
										+ line[counter]);

								// Check is include ' = '
								CheckIsIncludeEqualCh(counter);
								// System.out.println(line[counter]);
								counter++;
							}
						reader.close();
						System.out.println("pop ax" + "\n" + "pop bx" + "\n"
								+ "mov [bx],ax");
						System.out.println("int 20h");
						System.out.println("code ends" + "\n");
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
			}

		public static void CheckIsIncludeEqualCh(int counter)
			{

				if (line[counter].indexOf('=') == -1)
					{
						variableWithoutEq[counter] = "push offset v"
								+ line[counter].substring(1);
						System.out.println(variableWithoutEq[counter]);
					}

				else
					{

						String[] tokens = line[counter].split("=");
						variables[counter] = tokens[0];
						variablesOutput[counter] = "push offset v"
								+ variables[counter].substring(1);
						expression[counter] = tokens[1];
						System.out.println(variablesOutput[counter]);

						ExpressionControl(counter, "first_situation");

					}

			}

		public static void program() throws IOException
			{

				System.out.println("\n" + "///////////////////////////");
				System.out.println("\n" + "code segment");

				for (int i = 0; i < 11; i++)
					{

						if (line[i] != null)
							{
								System.out.println("\n" + ";;" + line[i]);

							}

					}
				System.out.println("int 20h");
				System.out.println("code ends" + "\n");

			}
	}
