import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.nio.file.*;

public class Client1 {
	Socket requestSocket; 	// Socket to request chunks from Server
	Socket requestSocket2;	// Socket to request chunks from Client5

	public static int noofparts = 0; // total number of chunks to be received in the end

	public void Client1() {
	}

	public int selfchunks = 0;
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
					selfchunks++;
				}
			} catch (IOException ex) {
				System.out.println("All Chunks corresponding to Client1 have been received from Server");
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

	void receivefromclient5() // to receive chunks from Client5
	{
		while (true) {
			try {
				int x = 1;
				requestSocket2 = new Socket("localhost", 9005);
				if (requestSocket2 != null) {
					System.out.println("Connected to localhost Client 5 in port 9005 for 1st session.");
					while (x <= (noofparts - selfchunks)) {
						InputStream input = requestSocket2.getInputStream();
						receiveChunk(input);
						x++;
					}
					break;
				}

			} catch (ConnectException e) {
				System.err.println("Waiting for Client 5 to start.");
			}

			catch (UnknownHostException unknownHost) {
				System.err.println("You are trying to connect to an unknown host!");
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		try {
			requestSocket2.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public static int sendtoclient2() {
		return noofparts; // to know number of chunks
	}

	private static final int cPort = 9001; // The Client1 will be listening on this port number for request from Client2

	public static void main(String args[]) throws Exception {
		Client1 client = new Client1();
		client.receivefromserver();
		ServerSocket listener = new ServerSocket(cPort);
		System.out.println("The Client 1 is listening for request from Client 2. Please start Client 2.");
		int parts = client.sendtoclient2();
		int ClientNum = 1;
		String fileName = Name;
		try {

			new Handler1(listener.accept(), ClientNum, parts, Name).start();
			ClientNum++;
			client.receivefromclient5();
			new Handler1(listener.accept(), ClientNum, parts, Name).start();
			ClientNum++;

		} finally {
			listener.close();
		}
		
		System.out.println("All " + parts + " chunks received by Client1. Now starting to merge these chunks.");
		String currentdir = new java.io.File(".").getCanonicalPath();
		List<File> l1 = new LinkedList<File>();
		String Name3 = Name.substring(0, Name.lastIndexOf('.'));
		File myfile = new File(currentdir, Name3);
		l1 = listOfFilesToMerge(myfile);
		mergeFiles(l1, myfile);
		System.out.println("Merging of all Chunks Complete.");
		System.out.println("You can find the merged file in Client1 Folder");

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
				System.out.println("Client 2 Connected");

				try {
					int partCounter = 1;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 2");
						partCounter = partCounter + 5;
					}

				} catch (IOException ex) {
					System.out.println("Error, Client1 cannot send ");
				}

				try {
					connection.close();
					System.out.println("Client 2 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 2");
				}

			}

			if (this.no == 2) {
				System.out.println("Client 2 Connected again for Session 2");

				try {
					int partCounter = 3;
					while (partCounter < (noofparts + 1)) {
						File chunk = new File(fileName);
						String Name2 = chunk.getName();
						String Name = Name2.substring(0, Name2.lastIndexOf('.'));
						OutputStream dataout = connection.getOutputStream();
						File sendfile = new File(chunk.getParent(), Name + "." + String.format("%03d", partCounter));
						sendChunk(sendfile, dataout, noofparts);
						System.out.println("Chunk " + partCounter + " sent to Client 2");
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
						System.out.println("Chunk " + partCounter + " sent to Client 2");
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
						System.out.println("Chunk " + partCounter + " sent to Client 2");
						partCounter = partCounter + 5;
					}

				} catch (IOException ex) {
					System.out.println("Error, Client1 cannot send ");
				}

				try {
					connection.close();
					System.out.println("Client 2 disconneted.");
				} catch (IOException ioException) {
					System.out.println("Could not disconnect with Client 2");
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
