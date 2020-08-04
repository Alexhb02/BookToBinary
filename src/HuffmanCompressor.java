import java.util.*;
import java.io.*;

public class HuffmanCompressor {
	/*This method will take the inputFile name specified, find the file, and call the createHuffmanNodesList method to
	 *turn it into an Arraylist of nodes each representing a unique character. After that, it will create a duplicate
	 *Arraylist of huffman nudes. Then it will assemble a huffman tree using the assembleTree Huffman method, write
	 *ont a file the specifics regarding the frequencies of each character and other information using the toEncodedString
	 *method, and will finally call on the encodeFile method to write onto a file the huffman encoding of the text given
	 *as well as the calculated space savings. If there's an error, it will catch it and print out that it does not work,
	 *otherwise it will return "ok".
	 */
	public static String huffmanCoder(String inputFileName, String outputFileName) throws IOException {
		try {
			String userDirectory = System.getProperty("user.dir");
			FileWriter tableWriter = new FileWriter(userDirectory + "\\src\\" + "Untitled2");
			ArrayList<HuffmanNode> editedNodes = createHuffmanNodesList(inputFileName);
			ArrayList<HuffmanNode> uneditedNodes = new ArrayList<>();
			for(HuffmanNode node : editedNodes) {
				uneditedNodes.add(node);
			}
			HuffmanNode huffmanTree = assembleTree(editedNodes);
			tableWriter.write(toEncodedString(huffmanTree, uneditedNodes));
			tableWriter.close();
			encodeFile(inputFileName, outputFileName);
			return "ok!";
			}
		catch(Exception error) {
			return "Does not work :(";
		}
	}
	/*
	 * I created a Comparator class for frequency, so that I can use the class to order my ArrayList from
	 * minimum to maxmimum frequency when creating the huffman tree. The compare method will later sort
	 * Huffman Nodes from least to greatest according to their frequency.
	 */
	public static class FrequencyComparator implements Comparator<HuffmanNode>{
		public int compare(HuffmanNode firstNode, HuffmanNode secondNode) {
			return (firstNode.getFrequency() > secondNode.getFrequency()? -1:1);
		}
	}
	/*
	 * This method will read the input file using FileReader. It will also create a hashtable, which will 
	 * have characters as keys, and the values are the frequencies that correspond with the characters. 
	 * The Filereader will then loop through each character; if the character already corresponds to a 
	 * frequency value, then it will replace the frequency value of the character key by 1, otherwise it
	 * will create a new value key pairing for that character (with a frequency value of 1) and will add
	 * the node to an Arraylist of HuffmanNodes, each corresponding to a different character. Then, it will
	 * find the space savings by adding all the unique characters by their relative frequencies by 8 (8-bytes)
	 * and will also set each node's frequency in the aforementioned Arraylist to the frequency found in the 
	 * hashtable for the corresponding character. It will then assemble the huffman tree using a helper method,
	 * huffman encode the file with the help of the huffman tree and a helper method, will write this encoded
	 * String onto a file using a filewriter, and then will write the total number of space savings onto a 
	 * seperate file using a different filewriter. It will return the space savings as an int
	 */
	public static int encodeFile(String inputFileName, String outputFileName) throws IOException{
		String userDirectory = System.getProperty("user.dir");
		FileReader fr = new FileReader(userDirectory + "\\src\\" + inputFileName);
		FileWriter writer = new FileWriter(userDirectory + "\\src\\" + outputFileName);
		FileWriter spaceSavingsWriter = new FileWriter(userDirectory + "\\src\\" + "SpaceSavings");
		Hashtable<Character, Integer> huffmanHashtable = new Hashtable<Character, Integer>();
		ArrayList<HuffmanNode> editedNodes = new ArrayList<>();
		int spaceSavingsCount = 0;
		int i;
		while((i=fr.read())!=-1) {
			Character letter = (Character)(char)i;
			if(huffmanHashtable.containsKey(letter)) {
				Integer newFrequency = (Integer)huffmanHashtable.get(letter)+1;
				huffmanHashtable.replace(letter, newFrequency);
			}
			else {
				huffmanHashtable.put(letter, (Integer)1);
				HuffmanNode newNode = new HuffmanNode(letter, 1, null, null);
				editedNodes.add(newNode);
			}
		}
		for(HuffmanNode currentNode: editedNodes) {
			currentNode.setFrequency((Integer) huffmanHashtable.get(currentNode.getChar()));
			spaceSavingsCount += currentNode.getFrequency() * 8;
		}
		ArrayList<HuffmanNode> uneditedNodes = new ArrayList<>();
		for(HuffmanNode node : editedNodes) {
			uneditedNodes.add(node);
		}
		HuffmanNode huffmanTree = assembleTree(editedNodes);
		String encodedString = encodingString(huffmanTree, uneditedNodes);
		spaceSavingsWriter.write("Calculated space savings: " + spaceSavingsCount);
		writer.write(encodedString);
		writer.close();
		fr.close();
		spaceSavingsWriter.close();
		return spaceSavingsCount;
	}
	/* This method will read the input file using FileReader. It will also create a hashtable, which will 
	 * have characters as keys, and the values are the frequencies that correspond with the characters. 
	 * The Filereader will then loop through each character; if the character already corresponds to a 
	 * frequency value, then it will replace the frequency value of the character key by 1, otherwise it
	 * will create a new value key pairing for that character (with a frequency value of 1) and will add
	 * the node to an Arraylist of HuffmanNodes, each corresponding to a different character. Then, it will
	 * set each node's frequency in the aforementioned Arraylist to the frequency found in the 
	 * hashtable for the corresponding character, which it will then return.
	 */
	public static ArrayList<HuffmanNode> createHuffmanNodesList(String inputFileName) throws IOException{
		String userDirectory = System.getProperty("user.dir");
		FileReader fr = new FileReader(userDirectory + "\\src\\" + inputFileName);
		Hashtable<Character, Integer> huffmanHashtable = new Hashtable<Character, Integer>();
		ArrayList<HuffmanNode> nodes = new ArrayList<>();
		int i;
		while((i=fr.read())!=-1) {
			Character letter = (Character)(char)i;
			if(huffmanHashtable.containsKey(letter)) {
				Integer newFrequency = (Integer)huffmanHashtable.get(letter)+1;
				huffmanHashtable.replace(letter, newFrequency);
			}
			else {
				huffmanHashtable.put(letter, (Integer)1);
				HuffmanNode newNode = new HuffmanNode(letter, 1, null, null);
				nodes.add(newNode);
			}
		}
		for(HuffmanNode currentNode: nodes) {
			currentNode.setFrequency((Integer) huffmanHashtable.get(currentNode.getChar()));
		}
		fr.close();
		return nodes;
	}
	/*This method will create an empty Huffman Tree. Then, with the Arraylist of Huffman nodes
	 *created in the above method, it will loop through beginning at the end of the list (where
	 *the minimum frequency Node is), and merge it with the second minimum frequency node and add
	 *it to the list again. Then, it will sort the list according to frequency using the Comparator
	 *object (the class for which is above), and it will work all the way up until it merges all the 
	 *nodes together. It will return the root of this new Huffman tree.
	 */
	public static HuffmanNode assembleTree(ArrayList<HuffmanNode> list) {
		HuffmanNode huffmanTree = null;
		int counter = list.size()-1;
		while(list.size()>1) {
			HuffmanNode firstNode = list.get(counter);
			HuffmanNode secondNode = list.get(counter-1);
			list.remove(counter);
			list.remove(counter-1);
			huffmanTree = mergeNodes(firstNode, secondNode);
			list.add(huffmanTree);
			counter--;
			Collections.sort(list, new FrequencyComparator());
		}
		return huffmanTree;
	}
	
	/*This main method will simply call the huffmanCoder method with the respective text files,
	 *which will begin the huffman encoding process.
	 */
	public static void main(String[] args) throws IOException {
		try {
		if(args.length == 0)
			System.out.println(HuffmanCompressor.huffmanCoder("84-0.txt", "encoded.txt"));
		else if(args.length == 2)
			System.out.println(HuffmanCompressor.huffmanCoder(args[0], args[1]));
		}
		catch(Exception error) {
			
		}
	}
	/*This method will take two nodes as input, and make a new node with their added frequencies
	 * and make this node's right and left children the other two nodes. Then it will return
	 * the node.
	 */
	public static HuffmanNode mergeNodes(HuffmanNode firstNode, HuffmanNode secondNode) {
		return new HuffmanNode(null, firstNode.getFrequency()+secondNode.getFrequency(), firstNode.getFrequency()>secondNode.getFrequency()? secondNode: firstNode, firstNode.getFrequency()>secondNode.getFrequency()? firstNode: secondNode);
	}
	/*
	 * This method will calculate the height of the Huffman tree in a recursive manner, and will
	 * return the height of the tree.
	 */
	public static int findHeight(HuffmanNode root) {
		if(root == null)
			return 0;
		return findHeight(root.getRight()) > findHeight(root.getLeft())? 1 + findHeight(root.getRight()): 1 + findHeight(root.getLeft());
	}
	/*
	 * This method will encode the file using huffman encoding using the encoding helper method by looping through each node
	 * in the nodes arraylist (nodes with characters in them) and will 
	 */
	public static String encodingString(HuffmanNode root, ArrayList<HuffmanNode> nodes) {
		String encodedString = "";
		for(HuffmanNode node : nodes) {
			encodedString += encoding(root, node, "");
		}
		return encodedString;
	}
	/*
	 * This method will take the huffman tree and the character, and will recursively loop
	 * through all possible path of the huffman tree (where 1 is in the right direction and
	 * 0 the left), until it reaches all of the leaf nodes of the huffman tree, at which
	 * point it will check if each one is equal to the input node and will return the path
	 * taken to that node (essentially the Huffman encoding of the character contained in the
	 * node).
	 */
	public static String encoding(HuffmanNode root, HuffmanNode node, String path) {
		if(!root.hasChar()) {
			return encoding(root.getRight(), node, "1" + path) + encoding(root.getLeft(), node, "0" + path);
		}
		else if(root.getChar().equals(node.getChar()))
			return path;
		else
			return "";
	}
	/*
	 * This method will take return a string with height of tree, number of leaves on tree, and tree balance
	 * factor based off of helper methods. Then, the method will loop through each node in the input arraylist
	 * of nodes and add onto the string specific information about each character's frequency and huffman encoding
	 * representation using a helper method.
	 */
	public static String toEncodedString(HuffmanNode root, ArrayList<HuffmanNode> nodes) {
		String printCode = "";
		printCode += "Height of tree: " + findHeight(root) + "\n";
		printCode += "Number of leaves on tree: " + findNumLeaves(nodes) + "\n";
		printCode += "Tree balance factor: " + findTreeBalance(root);
		for(HuffmanNode node: nodes) {
			printCode += makeTable(root,node);
		}
		return printCode;
	}
	/*
	 * This method will find the balance factor of the tree by returning the subtraction of the 
	 * height of the right subtree of the root from the height of the left subtree of the root
	 * of the huffman tree.
	 */
	public static int findTreeBalance(HuffmanNode root) {
		return findHeight(root.getRight()) - findHeight(root.getLeft());
	}
	/*
	 * This will return the number of leaf nodes of the huffman tree by returning the number of 
	 * nodes in the ArrayList of Huffman nodes. The huffman tree is structured in such a way
	 * that only the nodes containing characters are leaf nodes, and by therefore calculating 
	 * the size of the list containing all the nodes with characters in them, you are finding
	 * the number of leaves on the Huffman tree.
	 */
	public static int findNumLeaves(ArrayList<HuffmanNode> nodes) {
		return nodes.size();
	}
	/*
	 * This method will recursively go through each leaf node of the huffman tree and will
	 * print out the character, it's respective frequency, and it's huffman encoding triple
	 * using helper methods.
	 */
	public static String makeTable(HuffmanNode root, HuffmanNode node) {
			return "\n" + node.getChar() + ":" + node.getFrequency() + ":" + encoding(root, node, "");
	}
}