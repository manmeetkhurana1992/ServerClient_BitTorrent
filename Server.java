import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	public static int splitFile(File f) throws IOException {
		int partCounter = 1; // To get the number of chunks generated
		int sizeOfFiles = 1024 * 100; // 100kb
		byte[] buffer = new byte[sizeOfFiles];

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
			String name = f.getName();
			int tmp = 0;
			while ((tmp = bis.read(buffer)) > 0) // write each chunk of data into separate file with different number in name
			{
				File newFile = new File(f.getParent(), name + "." + String.format("%03d", partCounter++));
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, tmp);
				}
			}
			partCounter--;
			System.out.println("After splitFile, number of chunks(100kb each) created = " + partCounter);
			return partCounter;
		}
	}

	private static final int sPort = 9050; // The server will be listening on this port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		Server s = new Server();
		System.out.println("Copy the Demo file to the Server Folder and then enter its name with path.");
		System.out.println("Enter the filename with path. Example : C:\\Users\\Desktop\\CN_Project\\Server\\Demo.pdf ");
		Scanner input = new Scanner(System.in);
		String name = input.nextLine();
		System.out.println("You entered: " + name);
		System.out.println("Splitting the File to create Chunks Now.");
		File filetobesplit = new File(name);
		int chunks = splitFile(filetobesplit);

		ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
		try {
			while (clientNum < 6) { // To listen until connection from 5 clients
				new Handler(listener.accept(), clientNum, chunks, filetobesplit).start();
				clientNum++;
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private Socket connection;
		private int no; // The index number of the client
		private int noofchunks;
		private File filename;

		public Handler(Socket connection, int no, int chunks, File filename) {
			this.connection = connection;
			this.no = no;
			this.noofchunks = chunks;
			this.filename = filename;

			if ((this.no) == 1) {
				System.out.println("Client 1 is connected to Server!");
				try {
					int partCounter = 1;
					while (partCounter < (noofchunks + 1)) {
						String Name = filename.getName();
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(filename.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofchunks);
						System.out.println("Chunk " + partCounter + " sent to Client 1");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Server cannot send ");
				}
				try {
					connection.close();
					System.out.println("Client 1 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 1");
				}
			}

			if ((this.no) == 2) {
				System.out.println("Client 2 is connected to Server!");
				try {
					int partCounter = 2;
					while (partCounter < (noofchunks + 1)) {
						String Name = filename.getName();
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(filename.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofchunks);
						System.out.println("Chunk " + partCounter + " sent to Client 2");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Server cannot send ");
				}
				try {
					connection.close();
					System.out.println("Client 2 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 2");
				}
			}

			if ((this.no) == 3) {
				System.out.println("Client 3 is connected to Server!");
				try {
					int partCounter = 3;
					while (partCounter < (noofchunks + 1)) {
						String Name = filename.getName();
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(filename.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofchunks);
						System.out.println("Chunk " + partCounter + " sent to Client 3");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Server cannot send");
				}
				try {
					connection.close();
					System.out.println("Client 3 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 3");
				}
			}

			if ((this.no) == 4) {
				System.out.println("Client 4 is connected to Server!");
				try {
					int partCounter = 4;
					while (partCounter < (noofchunks + 1)) {
						String Name = filename.getName();
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(filename.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofchunks);
						System.out.println("Chunk " + partCounter + " sent to Client 4");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Server cannot send ");
				}
				try {
					connection.close();
					System.out.println("Client 4 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 4");
				}
			}

			if ((this.no) == 5) {
				System.out.println("Client 5 is connected to Server!");
				try {
					int partCounter = 5;
					while (partCounter < (noofchunks + 1)) {
						String Name = filename.getName();
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(filename.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofchunks);
						System.out.println("Chunk " + partCounter + " sent to Client 5");
						partCounter = partCounter + 5;
					}
				} catch (IOException ex) {
					System.out.println("Error, Server cannot send ");
				}
				try {
					connection.close();
					System.out.println("Client 5 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 5");
				}
			}
		}
	}

	public static void sendChunk(File file, OutputStream out, int noparts) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		dos.writeUTF(file.getName());
		dos.writeInt(noparts);
		dos.writeInt((int) file.length());
		int count;
		byte[] buffer = new byte[1024 * 100];
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		while ((count = in.read(buffer)) >= 0) {
			out.write(buffer, 0, count);
			out.flush();
		}
		in.close();
	}
}