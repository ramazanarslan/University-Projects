/*

NAME			:	RAMAZAN ARSLAN
STUDENT NO      :	2016400345

NAME			:	AHMET SEMÝH ARI
STUDENT NO      :	2016400315

CMPE 230 PROJECT-1



 */

import java.util.*;
import java.io.*;

public class MainCompiler
	{

		public static BufferedReader in;
		public static BufferedWriter out;
		public static final String ERR = "ERROR";
		public static List<String> lineLIST, variableLIST, expressionLIST,
				SettedVariableList, OtherVariableList;
		public static String[] variablesList, expressionList, variableUnique;

		public static void main(String[] args) throws IOException
			{

				// The name of the file to open.
				lineLIST = new ArrayList<String>();
				SettedVariableList = new ArrayList<String>();
				OtherVariableList = new ArrayList<String>();

				variablesList = new String[100];
				expressionList = new String[100];
				variableUnique = new String[100];

				String fileName = "C:\\Users\\RamazanArslan\\Desktop\\example.txt";

				ReadTxt(fileName);

				for (int i = 0; i < lineLIST.size(); i++)
					{
						defineVariables(i);
						System.out.println("DDD: " + SettedVariableList.get(i));
					}
				Output();
				// WriteTxt(fileName);

			}

		public static void defineVariables(int counter)
			{

				String letters = "abcdefghijklmnopqrstuvwxyz_";
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
														if (!OtherVariableList
																.contains(var)
																&& !SettedVariableList
																		.contains(var))
															{
																OtherVariableList
																		.add(var);
															}
													}
												else if (!OtherVariableList
														.contains(var)
														&& !SettedVariableList
																.contains(var))
													{
														SettedVariableList
																.add(var);
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
												if (!OtherVariableList
														.contains(var)
														&& !SettedVariableList
																.contains(var))
													{
														OtherVariableList
																.add(var);
													}
											}
										else if (!OtherVariableList
												.contains(var)
												&& !SettedVariableList
														.contains(var))
											{
												SettedVariableList.add(var);
											}

										var = "";
										varStarted = false;
									}

							}

					}

			}

		public static void Output()
			{
				System.out.println("\n" + "code segment");

				for (int counter = 0; counter < lineLIST.size(); counter++)
					{

						System.out.println("\n" + ";;;;"
								+ lineLIST.get(counter));

						func_LineSplit(counter, lineLIST.get(counter));

						System.out.println("pop ax" + "\n" + "pop bx" + "\n"
								+ "mov [bx],ax");
					}
				System.out.println("int 20h");
				System.out.println("code ends" + "\n");
			}

		public static void ReadTxt(String fileName)
			{

				try
					{
						FileReader reader = new FileReader(fileName);
						BufferedReader bufferedReader = new BufferedReader(
								reader);

						String lineinput;

						while ((lineinput = bufferedReader.readLine()) != null)
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

		public static void ExpressionControl(int counter,
				String expressionString, boolean IsIncludeDoubleExp)
			{

				expressionString = expressionString.trim();
				if (expressionString.startsWith("not"))
					{
						func_NOT(counter, expressionString);
					}
				else if (expressionString.startsWith("xor"))
					{
						func_XOR(counter, expressionString);
					}
				else if (expressionString.startsWith("ls"))
					{
						func_LS(counter, expressionString);

					}
				else if (expressionString.startsWith("rs"))
					{
						func_RS(counter, expressionString);
					}
				else if (expressionString.startsWith("lr"))
					{
						func_LR(counter, expressionString);
					}
				else if (expressionString.startsWith("rr"))
					{
						func_RR(counter, expressionString);

					}
				else
					{

						if (Character.isDigit(expressionString.charAt(0)))
							{
								func_StartWithOnly_DIGIT(expressionString);

							}
						else
							{

								if (expressionString.indexOf(0) == '(')
									{
										ExpressionControl(
												counter,
												parenthesisControl(expressionString),
												false);
									}
								else
									{

										if (expressionString.indexOf('|') != -1)
											{

												func_OR(counter,
														expressionString);

											}
										else if (expressionString.indexOf('&') != -1)
											{

												func_AND(counter,
														expressionString);
											}

										else if (expressionString.indexOf(',') != -1)
											{
												func_COMMA(counter,
														expressionString);

											}
										else
											{
												if (expressionString.charAt(0) == '$')
													{
														func_DOLLAR(expressionString);
													}
												else
													{

														func_CH(counter,
																expressionString);

													}
											}

									}

							}

					}

			}

		private static void func_CH(int counter, String expressionString)
			{
				System.out.println("push 0" + expressionString + "h");
			}

		private static void func_DOLLAR(String expressionString)
			{
				System.out.println("push w v" + expressionString.substring(1));
			}

		private static void func_COMMA(int counter, String expressionString)
			{
				String[] tokens = expressionString.split(",");
				ExpressionControl(counter, tokens[0], false);
				ExpressionControl(counter, tokens[1], false);
			}

		private static void func_AND(int counter, String expressionString)
			{

				String[] tokens = expressionString.split("&");
				ExpressionControl(counter, tokens[0], false);
				ExpressionControl(counter, tokens[1], false);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "and ax,bx" + "\n" + "push ax");
			}

		private static void func_OR(int counter, String expressionString)
			{

				String[] tokens = expressionString.split("\\|");

				ExpressionControl(counter, tokens[0], false);
				ExpressionControl(counter, tokens[1], false);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "or ax,bx" + "\n" + "push ax");
			}

		private static void func_StartWithOnly_DIGIT(String expressionString)
			{
				expressionString = "push " + expressionString + "h";

				System.out.println(expressionString);
			}

		private static void func_XOR(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), true);
				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "xor ax,bx" + "\n" + "push ax");

			}

		private static void func_LS(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), true);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "shl ax,bx" + "\n" + "push ax");

			}

		private static void func_RS(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), true);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "shr ax,bx" + "\n" + "push ax");

			}

		private static void func_RR(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), true);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "shr ax,bx" + "\n" + "push ax");

			}

		public static void func_LineSplit(int counter, String line)
			{

				if (line.indexOf('=') == -1)
					{
						String variableString = "push offset v"
								+ line.substring(1);

						System.out.println(variableString);

					}

				else
					{

						String[] split = line.split("=");
						variablesList[counter] = split[0];

						variablesList[counter] = "push offset v"
								+ variablesList[counter].substring(1);
						System.out.println(variablesList[counter]);

						expressionList[counter] = split[1];

						ExpressionControl(counter, expressionList[counter],
								false);

					}

			}

		private static void func_LR(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), true);

				System.out.println("pop ax" + "\n" + "pop bx" + "\n"
						+ "shr ax,bx" + "\n" + "push ax");

			}

		private static void func_NOT(int counter, String expressionString)
			{

				ExpressionControl(counter,
						parenthesisControl(expressionString), false);

				System.out.println("pop ax" + "\n" + "not ax" + "\n"
						+ "push ax");

			}

		private static String parenthesisControl(String expressionString)
			{
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

						String answer = expressionString.substring(
								expOpenedBracketFirstIndex + 1,
								expClosedBracketLastIndex);
						return answer;

					}
				else
					{
						System.out.println("syntax error: Parenthesis");

						return ERR;
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

	}
