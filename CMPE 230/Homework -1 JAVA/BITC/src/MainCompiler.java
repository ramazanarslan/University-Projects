/*

NAME			:	RAMAZAN ARSLAN
STUDENT NO      :	2016400345

NAME			:	AHMET SEMÝH ARI
STUDENT NO      :	2016400315

CMPE 230 PROJECT-1



 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.border.EtchedBorder;

import org.omg.CORBA.ORB;

public class MainCompiler
	{
	// first of all
		// $x = not($y)
		// i define in my code
		// Variable -> "$x"
		// Operations -> "="
		// Expression -> "not($y)

		
		public static String newline = System.getProperty("line.separator"); // for \n																																					// "\n"
		public static BufferedReader in;
		public static BufferedWriter out;
		public static final String ERR = "ERROR";

		public static List<String> lineLIST, variableLIST, expressionLIST,
				SettedVariableList, OtherVariableList, variableUniqueList;

		public static String[] variablesList, expressionList;
		boolean errorOccured = false;

		public static void main(String[] args) throws IOException
			{
				

				if (!args[0].matches("(.*)\\.bc"))
					{
						// Only accepts files with .my extension
						out.write("ERROR  : file extention must be -> *.bc  ");
						return;
					}
				String name;

				name = args[0].replace(".bc", ".asm");

				// define some variables
				lineLIST = new ArrayList<String>();
				SettedVariableList = new ArrayList<String>();
				OtherVariableList = new ArrayList<String>();
				variableUniqueList = new ArrayList<String>();

				variablesList = new String[100];
				expressionList = new String[100];

				ReadTxt(args[0]);// Read file line by line from arg[0]

				out = new BufferedWriter(new FileWriter(name)); // creating an output

				Output();
				out.close();

			}

		public static void findAllVariablesToDefineInTheEnd() throws IOException
			{
				// This func created to define variable in the end of the assembly code
				// for example vx dw 0
				for (int i = 0; i < variableUniqueList.size(); i++)
					{
						out.write("v" + variableUniqueList.get(i) + " dw 0" + newline);

					}
			}

		public static void defineVariables(int counter) throws IOException
			{

				// Check unique variable to define end

				String letters = "abcdefghijklmnopqrstuvwxyz_"; // after $, these
																												// letters must come
				boolean varStarted = false;
				boolean equalFound = false;
				String ch = "";
				String var = "";

				for (int i = 0; i < lineLIST.get(counter).length(); i++)
					{
						ch = "" + lineLIST.get(counter).charAt(i);

						if (ch.equals("="))
							{
								equalFound = true;
							}

						if (ch.equals("$"))
							{
								varStarted = true;
							}

						if (letters.contains(ch))
							{
								if (varStarted)
									{
										var = var + ch;

										if (i == lineLIST.get(counter).length() - 1)
											{
												if (equalFound)
													{
														if (!OtherVariableList.contains(var)
																&& !SettedVariableList.contains(var))
															{
																OtherVariableList.add(var);
															}
													}
												else if (!OtherVariableList.contains(var)
														&& !SettedVariableList.contains(var))
													{
														SettedVariableList.add(var);
													}
											}
									}

							}
						else
							{
								if (var.length() > 0 && varStarted)
									{
										if (equalFound)
											{
												if (!OtherVariableList.contains(var)
														&& !SettedVariableList.contains(var))
													{
														OtherVariableList.add(var);
													}
											}
										else if (!OtherVariableList.contains(var)
												&& !SettedVariableList.contains(var))
											{
												SettedVariableList.add(var);
											}

										var = "";
										varStarted = false;
									}

							}

					}

			}

		public static void Output() throws IOException
			{

				out.write("code segment" + newline);

				for (int counter = 0; counter < lineLIST.size(); counter++)
					{

						out.write(";;;;" + lineLIST.get(counter) + newline);

						func_LineSplit(counter, lineLIST.get(counter));

						out.write("pop ax" + newline + "pop bx" + newline + "mov [bx],ax"
								+ newline);
					}
				out.write("int 20h ;;exits to operating system" + newline);
				findAllVariablesToDefineInTheEnd();

				out.write("code ends");

			}

		public static void ReadTxt(String argument) throws IOException
			{

				try
					{
						FileReader reader = new FileReader(argument);
						BufferedReader in = new BufferedReader(reader);

						String lineinput;

						while ((lineinput = in.readLine()) != null)
							{
								lineLIST.add(lineinput);

							}
						reader.close();

					}
				catch (IOException e)
					{
						e.printStackTrace();
					}

			}

		public static void ExpressionControl(int counter, String expressionString,
				boolean IsIncludeDoubleExp) throws IOException
			{
				expressionString = expressionString.trim();
				// check not(a) -> one expression ls(a,b) -> double expression
				if (!IsIncludeDoubleExp)
					{
						expressionString = parenthesisControl(expressionString);
					}
				boolean isError = false;
				boolean isCOMMA = false;
				int indexofCOMMA = -1;

				// these method control comma
				if (IsIncludeDoubleExp)
					{
						int a = 0;
						int expOpenedBracketCount = 0;
						int expClosedBracketCount = 0;
						while (a < expressionString.length())
							{

								if (expressionString.charAt(a) == '(')
									{
										expOpenedBracketCount++;

									}
								if (expressionString.charAt(a) == ')')
									{
										expClosedBracketCount--;
									}

								// syntax of Parenthesis control
								if (expOpenedBracketCount + expClosedBracketCount == 0)
									{

										if (expressionString.charAt(a) == ',')
											{
												if (expressionString.charAt(a - 1) == ',')
													{
														isError = true;
														break;

													}

												else if (expressionString.charAt(a) == ',')
													{
														isCOMMA = true;
														indexofCOMMA = a;
														break;
													}
											}
									}
								else if (expOpenedBracketCount + expClosedBracketCount == 0)
									{

									}

								a++;

							}
					}

				// Check expression have " AND" and "OR"
				int indexofAND_OR = -1;
				boolean isOR = false;
				boolean isAND = false;
				int i = 0;
				int expOpenedBracketCount = 0;
				int expClosedBracketCount = 0;

				while (i < expressionString.length())
					{
						// Counting All Parenthesis

						if (expressionString.charAt(i) == '(')
							{
								expOpenedBracketCount++;

							}
						if (expressionString.charAt(i) == ')')
							{
								expClosedBracketCount--;
							}

						if (expOpenedBracketCount + expClosedBracketCount == 0)
							{

								if (expressionString.charAt(i) == '|'
										|| expressionString.charAt(i) == '&')
									{
										if (expressionString.charAt(i - 1) == '|'
												|| expressionString.charAt(i - 1) == '&')
											{
												isError = true;

											}
										else if (expressionString.charAt(i) == '|')
											{
												isOR = true;
												indexofAND_OR = i;
												break;
											}
										else if (expressionString.charAt(i) == '&')
											{
												isAND = true;
												indexofAND_OR = i;
												break;
											}
									}
							}

						i++;
					}

				if (isError)
					{
						out.write("Syntax error about AND OR");
					}
				else
					{
						if (isOR)
							{
								// invoke OR function
								func_OR(counter, expressionString, indexofAND_OR);

							}
						else if (isAND)
							{
								// invoke AND function

								func_AND(counter, expressionString, indexofAND_OR);

							}
						else if (isCOMMA)
							{
								// invoke COMMA function

								func_COMMA(counter, expressionString, indexofCOMMA);

							}

						else if (expressionString.startsWith("not"))
							{
								// invoke NOT function

								func_NOT(counter, expressionString);
							}
						else if (expressionString.startsWith("xor"))
							{
								// invoke XOR function

								func_XOR(counter, expressionString);
							}
						else if (expressionString.startsWith("ls"))
							{
								// invoke Ls function

								func_LS(counter, expressionString);

							}
						else if (expressionString.startsWith("rs"))
							{
								// invoke RS function

								func_RS(counter, expressionString);
							}
						else if (expressionString.startsWith("lr"))
							{
								// invoke LR function

								func_LR(counter, expressionString);
							}
						else if (expressionString.startsWith("rr"))
							{
								// invoke RR function

								func_RR(counter, expressionString);

							}
						else
							{
								// check expression start with number

								if (Character.isDigit(expressionString.charAt(0)))
									{
										func_StartWithOnly_DIGIT(expressionString);

									}
								else
									{
										// check expression start with $

										if (expressionString.charAt(0) == '$')
											{
												func_DOLLAR(expressionString);
											}
										else
											{
												// check expression start with Charecter forEx: abcd

												func_CH(counter, expressionString);

											}

									}

							}
					}

			}

		public static void func_LineSplit(int counter, String line)
				throws IOException
			{

				// split if line has "EQUAL" or not
				if (line.indexOf('=') == -1)
					{
						// if line do not contains EQUAL
						String variableString = "push offset v" + line.substring(1);
						if (!variableUniqueList.contains(line.substring(1)))
							{
								variableUniqueList.add(line.substring(1));
							}
						out.write(variableString + newline);

					}

				else
					{
						// if line contains EQUAL

						String[] split = line.split("=");
						variablesList[counter] = split[0].trim();
						if (!variableUniqueList.contains(variablesList[counter]
								.substring(1)))
							{
								variableUniqueList.add(variablesList[counter].substring(1));
							}
						variablesList[counter] = "push offset v"
								+ variablesList[counter].substring(1);
						out.write(variablesList[counter] + newline);

						expressionList[counter] = split[1];

						// send expression control
						ExpressionControl(counter, expressionList[counter], false);

					}

			}

		private static void func_CH(int counter, String expressionString)
				throws IOException
			{
				// convert to hexamal for character
				out.write("push 0" + expressionString + "h" + newline);
			}

		private static void func_DOLLAR(String expressionString) throws IOException
			{
				// DOLLAR control

				if (!variableUniqueList.contains(expressionString.substring(1))) // for
																																					// define
																																					// variables
																																					// in
																																					// the
																																					// end
					{
						variableUniqueList.add(expressionString.substring(1));
					}
				if (expressionString.contains("(") || expressionString.contains(")"))
					{
						out.write("ERROR about Variables" + expressionString.substring(1)
								+ newline);
					}
				else
					{
						out.write("push w v" + expressionString.substring(1) + newline);
					}

			}

		private static void func_COMMA(int counter, String expressionString,
				int indexComma) throws IOException
			{

				String strings1 = expressionString.substring(0, indexComma);
				String strings2 = expressionString.substring(indexComma + 1);
				strings1 = strings1.trim();
				strings2 = strings2.trim();
				ExpressionControl(counter, strings1, false);
				ExpressionControl(counter, strings2, false);

			}

		private static void func_AND(int counter, String expressionString,
				int indexOR) throws IOException
			{
				String strings1 = expressionString.substring(0, indexOR - 1);
				String strings2 = expressionString.substring(indexOR + 1);
				strings1 = strings1.trim();
				strings2 = strings2.trim();
				ExpressionControl(counter, strings1, false);
				ExpressionControl(counter, strings2, false);

				out.write("pop ax" + newline + "pop bx" + newline + "and ax,bx"
						+ newline + "push ax" + newline);
			}

		private static void func_OR(int counter, String expressionString,
				int indexOR) throws IOException
			{
				String strings1 = expressionString.substring(0, indexOR);
				String strings2 = expressionString.substring(indexOR + 1);
				strings1 = strings1.trim();
				strings2 = strings2.trim();

				ExpressionControl(counter, strings1, false);
				ExpressionControl(counter, strings2, false);

				out.write("pop ax" + newline + "pop bx" + newline + "or ax,bx"
						+ newline + "push ax" + newline);
			}

		private static void func_StartWithOnly_DIGIT(String expressionString)
				throws IOException
			{
				expressionString = "push " + expressionString + "h";

				out.write(expressionString + newline);
			}

		private static void func_XOR(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), true);
				out.write("pop ax" + newline + "pop bx" + newline + "xor ax,bx"
						+ newline + "push ax" + newline);

			}

		private static void func_LS(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), true);

				out.write("pop ax" + newline + "pop bx" + newline + "shl ax,bx"
						+ newline + "push ax" + newline);

			}

		private static void func_RS(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), true);

				out.write("pop ax" + newline + "pop bx" + newline + "shr ax,bx"
						+ newline + "push ax" + newline);

			}

		private static void func_RR(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), true);

				out.write("pop ax" + newline + "pop bx" + newline + "shr ax,bx"
						+ newline + "push ax" + newline);

			}

		private static void func_LR(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), true);

				out.write("pop ax" + newline + "pop bx" + newline + "shr ax,bx"
						+ newline + "push ax" + newline);

			}

		private static void func_NOT(int counter, String expressionString)
				throws IOException
			{

				ExpressionControl(counter, parenthesisExtract(expressionString), false);

				out.write("pop ax" + newline + "not ax" + newline + "push ax" + newline);

			}

		private static String parenthesisControl(String expressionString)
				throws IOException
			{
				// this method controls expression has equal opened bracket'(' and has
				// closed bracket ')

				int i = 0;
				int expOpenedBracketCount = 0;
				int expClosedBracketCount = 0;
				int expOpenedBracketFirstIndex = -1;
				int expClosedBracketLastIndex = 0;

				while (i < expressionString.length())
					{
						if (expressionString.charAt(i) == '(')
							{
								expOpenedBracketCount++;

								if (expOpenedBracketFirstIndex == -1)
									{
										expOpenedBracketFirstIndex = i;
									}

							}
						if (expressionString.charAt(i) == ')')
							{
								expClosedBracketLastIndex = i;
								expClosedBracketCount--;
							}

						i++;
					}

				if (expOpenedBracketCount + expClosedBracketCount == 0)
					{
						int a = 0;
						boolean isClosedOccured = false;
						boolean isError = false;
						while (a < expressionString.length())
							{

								if (isClosedOccured)
									{
										if (!(expressionString.charAt(a) == ' '
												|| expressionString.charAt(a) == ')'
												|| expressionString.charAt(a) == '|'
												|| expressionString.charAt(a) == '&' || expressionString
													.charAt(a) == ','))
											{
												isError = true;
												break;
											}
										else
											{
												isClosedOccured = false;
											}

									}
								if (expressionString.charAt(a) == ')')
									{
										isClosedOccured = true;
									}

								a++;
							}
						if (isError)
							{
								return "ERROR";
							}
						if (expOpenedBracketFirstIndex == 0
								&& expClosedBracketLastIndex == expressionString.length() - 1)
							{
								String answer = expressionString.substring(
										expOpenedBracketFirstIndex + 1, expClosedBracketLastIndex);

								return answer;

							}
						else
							return expressionString;

					}
				else
					{
						out.write("syntax error: Parenthesis" + newline);

						return ERR;
					}

			}

		private static String parenthesisExtract(String expressionString)
				throws IOException
			{

				// if expression come like this "(abcd)" or "(not(abcd))
				// these method extract parenthesis
				int i = 0;
				int expOpenedBracketCount = 0;
				int expClosedBracketCount = 0;
				int expOpenedBracketFirstIndex = -1;
				int expClosedBracketLastIndex = 0;

				while (i < expressionString.length())
					{
						if (expressionString.charAt(i) == '(')
							{
								expOpenedBracketCount++;

								if (expOpenedBracketFirstIndex == -1)
									{
										expOpenedBracketFirstIndex = i;
									}

							}
						if (expressionString.charAt(i) == ')')
							{
								expClosedBracketLastIndex = i;
								expClosedBracketCount--;
							}
						i++;
					}

				if (expOpenedBracketCount + expClosedBracketCount == 0)
					{

						if (expOpenedBracketFirstIndex == 0
								&& expClosedBracketLastIndex == expressionString.length() - 1)
							{
								String answer = expressionString.substring(
										expOpenedBracketFirstIndex + 1, expClosedBracketLastIndex);

								return answer;

							}

						else if (expOpenedBracketFirstIndex != 0)
							{
								String answer = expressionString.substring(
										expOpenedBracketFirstIndex + 1, expClosedBracketLastIndex);
								return answer;

							}
						return expressionString;

					}
				else
					{
						out.write("syntax error: Parenthesis" + newline);

						return ERR;
					}

			}

	}
