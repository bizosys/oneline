/*
* Copyright 2010 The Apache Software Foundation
*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.bizosys.oneline.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class StringUtils {
	  
	 public static final String Empty = ""; 
	 public static final String FatalPrefix = "\n\n\n*****";
	 
	 public static final char SEPARATOR_FIELD_WORD = '~';
	 public static final String SEPARATOR_FIELD_WORD_STR = "~";
	 public static final char SEPARAOTOR_FIELD = ';';
	 public static final String SEPARAOTOR_FIELD_STR = ";";
	 public static final char SEPARATOR_RECORD = ':';
	 public static final String SEPARATOR_RECORD_STR = ":";
	 public static final char PADDING_CHAR = '_';
	 
	 public static final List<String> EMPTY_LIST = new ArrayList<String>();
	 public static final Set<String> EMPTY_SET = new HashSet<String>();
	 public static final String[] EmptyArray = new String[]{};
	
	  /**
	   * Make a string representation of the exception.
	   * @param e The exception to stringify
	   * @return A string with exception name and call stack.
	   */
	  public static String stringifyException(Throwable e) {
	    StringWriter stm = new StringWriter();
	    PrintWriter wrt = new PrintWriter(stm);
	    e.printStackTrace(wrt);
	    wrt.close();
	    return stm.toString();
	  }

	  /**
	   * Checks if a string is empty
	   * @param text String value to check
	   * @return true/false.
	   */
	  public static final boolean isEmpty (String text) {
	    if (null == text ) return true;
	    if ( text.length() == 0 ) return true;
	    else return false;
	  }
	  
	  public static final boolean isNonEmpty (String text) {
		    if (null == text ) return false;
		    if ( text.length() == 0 ) return false;
		    return true;
	  }	  

	  /**
	   * Given an array of strings, return a comma-separated list of its elements.
	   * @param strs Array of strings
	   * @return Empty string if strs.length is 0, comma separated list of strings
	   * otherwise
	   */
	  
	  public static String arrayToString(String[] strs) {
	    return arrayToString(strs, ',');
	  }
	  
	  public static String getLineSeaprator() {
		  String seperator = System.getProperty ("line.seperator");
		  if ( null == seperator) return "\n";
		  else return seperator;
	  }
	  
	  public static String arrayToString(String[] strs, char delim) {
	    if (strs == null || strs.length == 0) { return ""; }
	    StringBuffer sbuf = new StringBuffer();
	    sbuf.append(strs[0]);
	    for (int idx = 1; idx < strs.length; idx++) {
	      sbuf.append(delim);
	      sbuf.append(strs[idx]);
	    }
	    return sbuf.toString();
	 }
	  
	  public static String listToString(List<String> strs, char delim) {
		    if (strs == null) return ""; 
		    int len = strs.size();
		    if ( len == 0 )return ""; 
		    
		    StringBuffer sbuf = new StringBuffer();
		    sbuf.append(strs.get(0));
		    for (int idx = 1; idx < len; idx++) {
		      sbuf.append(delim);
		      sbuf.append(strs.get(idx));
		    }
		    return sbuf.toString();
	  }
	  	  

	  /**
	   * returns an arraylist of strings  
	   * @param str the comma seperated string values
	   * @return the arraylist of the comma seperated string values
	   */
	  public static String[] getStrings(String str){
		  return getStrings(str, ",");
	  }

	  /**
	   * returns an arraylist of strings  
	   * @param str the delimiter seperated string values
	   * @param delimiter
	   * @return the arraylist of the comma seperated string values
	   */
	  public static String[] getStrings(String str, String delimiter){
	    if (isEmpty(str)) return null;
	    StringTokenizer tokenizer = new StringTokenizer (str,delimiter);
	    List<String> values = new ArrayList<String>();
	    while (tokenizer.hasMoreTokens()) {
	      values.add(tokenizer.nextToken());
	    }
	    return (String[])values.toArray(new String[values.size()]);
	  }

	  public static String[] getStrings (String line, char delim) {
		  String[] result = new String[]{ line, null };
		  if (line == null) return result;
		  
		  int splitIndex = line.indexOf(delim);
		  if ( -1 != splitIndex) {
				result[0] = line.substring(0,splitIndex);
				if ( line.length() > splitIndex )
				result[1] = line.substring(splitIndex+1);
		  }
		  return result;
	  }
	  
	  public static List<String> fastSplit(final String text, char separator) {
		  if (isEmpty(text)) return null;

		  final List<String> result = new ArrayList<String>();
		  int index1 = 0;
		  int index2 = text.indexOf(separator);
		  String token = null;
		  while (index2 >= 0) {
			  token = text.substring(index1, index2);
			  result.add(token);
			  index1 = index2 + 1;
			  index2 = text.indexOf(separator, index1);
		  }
	            
		  if (index1 < text.length() - 1) {
			  result.add(text.substring(index1));
		  }
		  return result;
	  }
	  
	  public static String firstTokens(final String text, char separator, int tokens) {
		  if (isEmpty(text)) return null;

		  int index1 = 0;
		  int index2 = text.indexOf(separator);
		  StringBuilder sb = new StringBuilder();
		  String token = null;
		  int loop = 0;
		  while (index2 >= 0 && loop < tokens) {
			  token = text.substring(index1, index2);
			  sb.append(token).append(' ');
			  loop++;
			  index1 = index2 + 1;
			  index2 = text.indexOf(separator, index1);
		  }
	            
		  if ((index1 < text.length() - 1) && loop < tokens) {
			  sb.append(token);
		  }
		  return sb.toString();
	  }
	  
	  
	  public static List<String> arrayToList(String[] strA){
		  if ( null == strA) return null;
		  List<String> strL = new ArrayList<String>(strA.length);
		  for (String aStr : strA) {
			  strL.add(aStr);
		  }
		  return strL;
	  }
	  
	  
	  /**
	   * returns an arraylist of strings  
	   * @param str the comma seperated string values
	   * @return the arraylist of the comma seperated string values
	   */
	  public static Set<String> getUniqueStrings(String str){
	    if (str == null)
	      return EMPTY_SET;

	    StringTokenizer tokenizer = new StringTokenizer (str,",");
	    Set<String> values = new HashSet<String>();
	    while (tokenizer.hasMoreTokens()) {
	      values.add(tokenizer.nextToken());
	    }
	    return values;
	  }
	  
	  /**
	   * 
	   * @param input - The original String
	   * @param padding - The padding character
	   * @param finalLength - Final length we want to have 
	   * @return
	   */
	  public static String pad(String input, char padding, int finalLength) {
		  int length = 0;
		  if ( null != input ) length = input.length();
		  else input = "";
		  
		  if ( finalLength > length ) {
		  		StringBuilder sb = new StringBuilder(finalLength);
		  		sb.append(input);
		  		for ( int i=length; i< finalLength; i++ ) sb.append(padding);
		  		return sb.toString();
		  } else {
			  return input.substring(0,finalLength);
		  } 
	  }
	  
	  public static String encodeXml(String text) {
		  text = text.replaceAll("<", "&lt;");
		  text = text.replaceAll(">", "&gt;");
		  text = text.replaceAll("&", "&amp;");
		  return text;
	  }
	  
	  public static String removeNonBreakingSpaces(String text)
	  {
		  if (StringUtils.isEmpty(text)) return StringUtils.Empty;
		  
		  StringBuilder sb = new StringBuilder(text.length());
		  for (char ch : text.toCharArray())
		  {
			  if ((int)ch != 160) sb.append(ch);
		  }
		  return sb.toString();
	  }
	  
	  /**
	   * Removes lucene special characters from the text.
	   * Known speical characters are: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
	   * @param text
	   * @return
	   */
	  public static String escapeLucene(String text)
	  {
		  if (StringUtils.isEmpty(text)) return StringUtils.Empty;
		  
		  StringBuilder sb = new StringBuilder(text.length());
		  for (char ch : text.toCharArray())
		  {
			  switch(ch)
			  {
			  	case '+': break;
			  	case '-': break;
			  	case '&': break;
			  	case '|': break;
			  	case '!': break;
			  	case '{': break;
			  	case '}': break;
			  	case '(': break;
			  	case ')': break;
			  	case '[': break;
			  	case ']': break;
			  	case '^': break;
			  	case '"': break;
			  	case '~': break;
			  	case '*': break;
			  	case '?': break;
			  	case ':': break;
			  	case '\\': break;
			  	default:
			  		if ((int)ch != 160) sb.append(ch);
			  }
		  }
		  return sb.toString();
	  }
	  
	  /**
	   * Removes special characters from the text, fit for a filename.
	   * Known speical characters are: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
	   * @param text
	   * @return
	   */
	  public static String escapeFilename(String text)
	  {
		  if (StringUtils.isEmpty(text)) return StringUtils.Empty;
		  
		  StringBuilder sb = new StringBuilder(text.length());
		  for (char ch : text.toCharArray())
		  {
			  switch(ch)
			  {
			  	case '+': break;
			  	case '&': break;
			  	case '|': break;
			  	case '!': break;
			  	case '{': break;
			  	case '}': break;
			  	case '(': break;
			  	case ')': break;
			  	case '[': break;
			  	case ']': break;
			  	case '^': break;
			  	case '"': break;
			  	case '~': break;
			  	case '*': break;
			  	case '?': break;
			  	case ':': break;
			  	case '\\': break;
			  	case '/': break;
			  	case '\'': break;
			  	case '`': break;
			  	case ' ': 
			  		sb.append('_');
			  		break;
			  	default:
			  		if ((int)ch != 160) sb.append(ch);
			  }
		  }
		  return sb.toString();
	  }
	  
	  public static String decode(String text) {
		if ( null == text) return null;
		char[] arrText = text.toCharArray();
		int arrTextT = arrText.length;
		StringBuilder sb = new StringBuilder(arrTextT);
		char first,second,third,forth;
		for ( int i=0; i<arrTextT; i++ ) {
			first = arrText[i];
			if ( first == '-' ) {
				if ( i+3 < arrTextT) {
					forth = arrText[i+3];
					if ( forth == '-') {
						second = arrText[i+1];
						third = arrText[i+2];
						
						if ( second == 'a' && third == 'm') {
							sb.append("&"); i = i + 3;continue;
						} else if ( second == 'e' && third == 'q') {
							sb.append('='); i = i + 3;continue;
						} else if ( second == 'p' && third == 'l') {
							sb.append('+'); i = i + 3;continue;
						}else if ( second == 'n' && third == 'l') {
							sb.append("<BR />"); i = i + 3;continue;
						} else if ( second == 'c' && third == 'o') {
							sb.append(':'); i = i + 3;continue;
						} else if ( second == 't' && third == 'l') {
							sb.append('~'); i = i + 3;continue;
						} else if ( second == 'l' && third == 't') {
							sb.append("<"); i = i + 3;continue;
						} else if ( second == 'g' && third == 't') {
							sb.append(">"); i = i + 3;continue;
						}
					}
				}
			}
			sb.append(first);
		}
		String replacedMsg = sb.toString().replace("/\\\'", "'");
		return replacedMsg;
	  }
	  
	  /**
	   * from \/ to .
	   * c:\\\abhinash//karan/haha..ram.to
	   * c:...abinash..karan.haha..ram.to
	   * @param str
	   * @param from
	   * @param to
	   * @return
	   */
	  public static String replaceMultipleCharsToAnotherChar(String str, char[] replacables, char replaceBy) {
		  StringBuilder sb = new StringBuilder(str.length());
		  char[] chars = str.toCharArray();
		  boolean replaced = false;
		  
		  for (char c : chars) {
			  
			  replaced = false;
			  for (char replacable : replacables ) {
				if ( c == replacable) {
					sb.append(replaceBy);
					replaced = true;
					break;
				}
			  }
			  if ( replaced ) continue;
			  
			  sb.append(c);
		  }
		  return sb.toString();
	  }
	  
	  public static int totalSighings(String str, char find) {
		  if ( null == str) return 0;
		  char[] chars = str.toCharArray();
		  int counter = 0;
		  for (char c : chars) {
			  if ( c == find) counter++; 
		  }
		  return counter;
	  }
	  
	  public static String encodeText(String text, String encoding)
	  {
		  try
		  {
			  return URLEncoder.encode(text, encoding);
		  } 
		  catch (UnsupportedEncodingException e)
		  {
			  return text;
		  }
	  }
	  
	  public static String escapeXml(String text)
	  {
		  return StringEscapeUtils.escapeXml(text);
	  }
	  
	  public static String encodeText(String text)
	  {
		  if (StringUtils.isEmpty(text)) return text;
		  return text.replace(" ", "%20");
	  }
	  
	  public static String padWithQuotes(String text)
	  {
		  if (isEmpty(text)) return text;
		  StringBuilder sb = new StringBuilder(text.length() + 2);
		  sb.append("\"").append(text).append("\"");
		  return sb.toString();
	  }
	  
	  private static Pattern pattern = Pattern.compile("\\s+");
	  public static String stripExtraSpace(String input)
	  {
		  Matcher matcher = pattern.matcher(input);
		  return matcher.replaceAll(" ");
	  }
	  
	  public static String getId(String text) {
		  if (isEmpty(text)) return text;
		  return new Integer(text.hashCode()).toString();
	  }
	  
	  public static String[] toStringArray(Object[] array) {
		  String[] newArray = new String[array.length];

		  for (int i = 0; i < array.length; i++) {
			  newArray[i] = String.valueOf(array[i]);
		  }
		  return newArray;
	}	  
}
