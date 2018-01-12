package com.kansus.kstrainer.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class with logging utility methods.
 * 
 * @author Charles Nascimento
 */
public class Log {

	private static BufferedWriter writer;

	/**
	 * Sets a file writer so that all the messages written to the log are saved
	 * to a file as well.
	 * 
	 * @param bufferedWriter The buffered writer.
	 */
	public static void setWriter(BufferedWriter bufferedWriter) {
		writer = bufferedWriter;
	}

    public static void setFile(File file) throws IOException {
        writer = new BufferedWriter(new FileWriter(file));
    }

	/**
	 * Closes the file writer. This must be called for the file to be properly
	 * written to the disk.
	 * 
	 * @throws IOException
	 */
	public static void saveFile() throws IOException {
		if (writer != null) {
			writer.close();
			writer = null;
		}
	}

	/**
	 * Logs a message to the console and to a file, if a writer is set.
	 * 
	 * @param message The message to be logged.
	 */
	public static void writeln(String message) {
		System.out.println(message);

		if (writer != null) {
			try {
				writer.append(message);
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Logs an empty new line.
	 */
	public static void newLine() {
		System.out.println();

		if (writer != null) {
			try {
				writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Logs a message to the console and to a file, if a writer is set.
	 *
	 * @param message The message to be logged.
	 */
	public static void write(String message) {
		System.out.print(message);

		if (writer != null) {
			try {
				writer.append(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
