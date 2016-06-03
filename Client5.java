import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.file.*;

public class Client5 {
	Socket requestSocket; 	// Socket to request chunks from Server
	Socket requestSocket2; 	// Socket to request chunks from Client4

	public static int noofparts = 0; // total number of chunks to be received in the end

	public void Client5() {
	}

	public static String Name = null;

	void receivefromserver() // to receive chunks from Server
	{
		try {
			// create a socket to connect to the server
			requestSocket = new Socket("localhost", 9050);
			System.out.println("Connected to localhost Server in port 9050");

			try {
				while (true) {
					InputStream input = requestSocket.getInputStream();
					noofparts = receiveChunk(input);
				}
			} catch (IOException ex) {
				System.out.println("All Chunks corresponding to Client5 have been received from Server");
			}

		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a server first.");
		}

		catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	void receivefromclient4() // to receive chunks from Client4
	{
		try {
			// create a socket to connect to the Client4
			requestSocket2 = new Socket("localhost", 9004);
			System.out.println("Connected to localhost Client 4 in port 9004 for 1st session.");

			try {
				while (true) {
					InputStream input = requestSocket2.getInputStream();
					receiveChunk(input);
				}
			} catch (IOException ex) {
				System.out.println("Chunks received from Client4");
			}

		} catch (ConnectException e) {
			System.err.println("Connection refused. You need to initiate a Client 4 first.");
		}

		catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			// Close connections
			try {
				requestSocket2.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public static int sendtoclient1() {
		return noofparts; // to know number of chunks
	}

	private static final int cPort = 9005; // The Client5 will be listening on this port number for request from Client1

	public static void main(String args[]) throws Exception {
		Client5 client = new Client5();
		client.receivefromserver();
		client.receivefromclient4();

		ServerSocket listener = new ServerSocket(cPort);
		System.out.println("The Client 5 is listening for request from Client 1.");
		int parts = client.sendtoclient1();
		int ClientNum = 1;
		try {
			while (ClientNum < 2) {
				new Handler1(listener.accept(), ClientNum, parts, Name).start();
				ClientNum++;
			}
		} finally {
			listener.close();
		}

		System.out.println("All " + parts + " chunks received by Client5. Now starting to merge these chunks.");
		String currentdir = new java.io.File(".").getCanonicalPath();
		List<File> l1 = new LinkedList<File>();
		String Name3 = Name.substring(0, Name.lastIndexOf('.'));
		File myfile = new File(currentdir, Name3);
		l1 = listOfFilesToMerge(myfile);
		mergeFiles(l1, myfile);
		System.out.println("Merging of all Chunks Complete.");
		System.out.println("You can find the merged file in Client5 Folder");

	}

	private static class Handler1 extends Thread {
		private Socket connection;
		private int no;
		private int noofparts;
		private String fileName;

		public Handler1(Socket connection, int no, int parts, String fileName) {
			this.connection = connection;
			this.noofparts = parts;
			this.no = no;
			this.fileName = fileName;

			if (this.no == 1) {
				System.out.println("Client 1 Connected");

				try {

					int partCounter = 2;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 1");
						partCounter = partCounter + 5;
					}

					partCounter = 3;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 1");
						partCounter = partCounter + 5;
					}

					partCounter = 4;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 1");
						partCounter = partCounter + 5;
					}

					partCounter = 5;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 1");
						partCounter = partCounter + 5;
					}

				} catch (IOException ex) {
					System.out.println("Error, Client5 cannot send ");
				}

				try {
					connection.close();
					System.out.println("Client 1 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 1");
				}

			}
		}
	}

	public static int receiveChunk(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);
		String fileName = dis.readUTF();
		int parts = dis.readInt();
		int fileSize = dis.readInt();
		OutputStream out = new FileOutputStream(fileName);
		byte[] buffer = new byte[1024 * 100];
		int count;
		while (fileSize > 0 && (count = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
			out.write(buffer, 0, count);
			fileSize -= count;
		}
		System.out.println("Received Chunk: " + fileName);
		out.close();
		Name = fileName;
		return parts;
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

	public static List<File> listOfFilesToMerge(File oneOfFiles) throws Exception {
		String tmpName = oneOfFiles.getName();
		File[] files = oneOfFiles.getParentFile()
				.listFiles((File dir, String name) -> name.matches(tmpName + "[.]\\d+"));
		Arrays.sort(files);
		return Arrays.asList(files);
	}

	public static void mergeFiles(List<File> files, File into) throws IOException {
		try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(into))) {
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
			}
		}
	}
}