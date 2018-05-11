package com.rpg.logic.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordsFilter {

	/**
	 * 敏感词ROOT节点
	 */
	private static Node rootNode = null;
	/**
	 * 用来替换敏感词的符号
	 */
	private final static char FILTER = '*';

	/**
	 * 用*号替换所有敏感词
	 * @param content
	 * @return
	 */
	public static String filterWords(String content) {
		int a = 0;
		char[] chars = (new String(content)).toLowerCase().toCharArray();
		char[] sourceChars = content.toCharArray();
		Node node = rootNode;
		List<String> word = new ArrayList<String>();
		while (a < chars.length) {
			node = findNode(node, chars[a]);
			if (node == null) {
				node = rootNode;
				a = a - word.size();
				word.clear();
			} else if (node.flag == 1) {
				word.add(String.valueOf(chars[a]));
				for (int i = 0; i < word.size(); i++) {
					sourceChars[a - i] = FILTER;
				}
				a = a - word.size() + 1;
				word.clear();
				node = rootNode;
			} else {
				word.add(String.valueOf(chars[a]));
			}
			a++;
		}
		return String.valueOf(sourceChars);
	}

	/**
	 * 判断是否含有敏感词
	 * @param content
	 * @return
	 */
	public static boolean hasBadWords(String content) {
		int a = 0;
		char[] chars = content.toLowerCase().toCharArray();
		Node node = rootNode;
		List<String> word = new ArrayList<String>();
		while (a < chars.length) {
			node = findNode(node, chars[a]);
			if (node == null) {
				node = rootNode;
				a = a - word.size();
				word.clear();
			} else if (node.flag == 1) {
				return true;
			}
			a++;
		}
		return false;
	}

	/**
	 * 重新加载敏感词汇表
	 * @param path 
	 */
	public static void load() {
		List<String> badwords = new ArrayList<String>();
		rootNode = new Node('R');
		try {
			InputStream is = WordsFilter.class
					.getResourceAsStream("/" + LanguageType.get().getLoaclPath() + FileConstants.BAD_WORDS);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				badwords.add(line);
			}
			is.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String str : badwords) {
			if (str != null && str.length() > 0) {
				char[] chars = str.toCharArray();
				if (chars.length > 0)
					insertNode(rootNode, chars, 0);
			}
		}
	}

	private static void insertNode(Node node, char[] cs, int index) {
		Node n = findNode(node, cs[index]);
		if (n == null) {
			n = new Node(cs[index]);
			node.nodes.add(n);
		}
		if (index == (cs.length - 1))
			n.flag = 1;

		index++;
		if (index < cs.length)
			insertNode(n, cs, index);
	}

	private static Node findNode(Node node, char c) {
		List<Node> nodes = node.nodes;
		Node rn = null;
		for (Node n : nodes) {
			if (n.c == c) {
				rn = n;
				break;
			}
		}
		return rn;
	}

	private static class Node {
		public char c;
		public int flag;
		public List<Node> nodes = new ArrayList<Node>();

		public Node(char c) {
			this.c = c;
			this.flag = 0;
		}
		// public Node(char c, int flag) {
		// this.c = c;
		// this.flag = flag;
		// }
	}
}
