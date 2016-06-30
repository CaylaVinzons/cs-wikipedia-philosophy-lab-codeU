package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	 public static boolean isValid(Node node) {
	 		List<Node> siblings = node.siblingNodes();
	 		Node parent = node.parent();
	 		int siblingIndex = node.siblingIndex();
	 		int indexLeft = -1, indexRight = -1;

	 		String attr = node.attr("href");
	 		if (attr.charAt(0) == '#') return false;

	 		if (parent instanceof Element) {
	 			String tagName = ((Element)parent).tagName();
	 			if (tagName != "p") return false;
	 		}

	 		for (Node n: siblings) {
	 			if (n instanceof TextNode && n.siblingIndex() > siblingIndex) {
	 				int currentIndexLeft = ((TextNode)n).text().indexOf("(");
	 				if (currentIndexLeft != -1) {
	 					indexLeft = n.siblingIndex();
	 					break;
	 				}
	 			}
	 		}

	 		for (Node n: siblings) {
	 			if (n instanceof TextNode && n.siblingIndex() > siblingIndex) {
	 				int currentIndexRight = ((TextNode)n).text().indexOf(")");
	 				if (currentIndexRight != -1) {
	 					indexRight = n.siblingIndex();
	 					if (indexLeft == indexRight) {
	 						indexLeft = ((TextNode)n).text().indexOf("(");
	 						indexRight = ((TextNode)n).text().indexOf(")");
	 					}
	 					break;
	 				}
	 			}
	 		}


	 		if (indexLeft != -1 && indexRight != -1 && indexRight < indexLeft) {
	 			return false;
	 		} else if (indexLeft == -1 && indexRight > 0) {
	 			return false;
	 		} else {
	 			return true;
	 		}
	 	}
	 private static boolean isSuccess(String url, String philosophy) {
		 return url.equals(philosophy);
	 }
	 private static boolean isFailure(List<String> visited, String url) {
		 if (visited.contains(url)) return true;
		 return false;
	 }

	public static void main(String[] args) throws IOException {

        // some example code to get you started
		List<String> visited = new ArrayList<String>();
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String philosophy = "https://en.wikipedia.org/wiki/Philosophy";

		while (!isSuccess(url, philosophy) || !isFailure(visited,url)) {
			visited.add(url);
			Elements paragraphs = wf.fetchWikipedia(url);
			visited.add(url);
			Element firstPara = paragraphs.get(0);
			Iterable<Node> iter = new WikiNodeIterable(firstPara);

			for (Node node: iter) {
				if (node.hasAttr("href")) {
					if (!isValid(node)) continue;
					String firstlink = node.attr("abs:href");
					if (isFailure(visited,firstlink) || isSuccess(url,philosophy)) break;
					else
						System.out.print("continuing...");
						url = firstlink;
						System.out.println(url);
						break;
				}
			}
		}

		if (isSuccess(url, philosophy)) {
			System.out.println("Success!");
		} else if (isFailure(visited,url)) {
			System.out.println("Failure...");
		} else {
			System.out.println("idk what the fuck");
		}
	}
}
